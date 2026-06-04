# Multi-skill pipeline 验证（ai-review-assistant）

本仓库在 `sample-project/` 下加了 **`MULTI_SKILL_VERIFY`** 注释的代码，用于触发 **Review → Security → Performance → Summary** 四段流水线。

## 预期各 Skill 能“看见”什么

| 区域 | 文件 | 意图 |
|------|------|------|
| Security | `auth/AuthService.java` | 硬编码密钥、路径拼接、`URL` SSRF 面；**JAVA_PATCH_VERIFY**：`deleteUsersByRole` |
| Security | `repository/UserRepository.java` | 拼接 SQL + 凭证；**JAVA_PATCH_VERIFY**：`listUserNamesOrdered`（ORDER BY 拼接） |
| Security | `service/UserService.java` | **JAVA_PATCH_VERIFY**：`logLookupHint`（LIKE 片段日志） |
| Performance | `repository/UserRepository.java` | `anyNameExists` 循环里多次 `existsByName`（N+1） |
| Performance | `auth/AuthService.java` | `warmCacheBadly` 大量循环调库 |
| Performance | `service/UserService.java` | `buildAuditTrail` 双重循环 + 字符串拼接；**JAVA_PATCH_VERIFY**：`publishTagsWithPause`（循环 + sleep） |
| Performance | `service/ConfusingNames.java` | **JAVA_PATCH_VERIFY**：`waitMsBusy`（忙等） |
| General / Style | `service/UserService.java` | 方法 `x` 命名差、可维护性 |
| General / Correctness | `service/User.java` | **JAVA_PATCH_VERIFY**：`sameName` 用 `==` 比字符串 |

## 怎么跑通

1. 将 **`C:\Git\ai-test`** 推到 GitHub，开一个 **PR**（包含上述改动）。
2. 确保 **ai-review-assistant** 已部署当前多 Skill 版本；`GITHUB_TOKEN`、模型 Key 已配置。
3. 触发 **PR webhook**（或本地调用与线上一致的 `review_single_file` 流程）。
4. 观察助手日志：
   - `🔹 Multi-skill pipeline: <file>.java — review → security → performance → summary`
   - `⚡ AI pipeline: <file> (attempt=...)`
5. 看 PR 评论里的 **合并后** `summary` / `issues`：应同时体现安全类与性能类发现（具体措辞因模型而异）。

## 超时与费用

流水线对 **每个 Java chunk 会调用 4 次 LLM**。若 PR 里多个 Java 文件，总耗时与 token 会明显上升。可在助手环境设置 **`SKILL_PIPELINE_TIMEOUT_SEC`**（默认 60）适当加大。

## 仅本地 smoke（不连 GitHub）

在 **ai-review-assistant** 仓库根目录，用与 CI 相同的单测/脚本无法替代完整四段模型调用；要验证端到端仍需一次真实或 mock 的 provider 调用。

## 文档忽略 + 合并（`DOC_SKIP_VERIFY` / `MERGE_NORMALIZE_VERIFY`）

1. 开一个 PR，**同时改** 仓库根 `DOC_SKIP_VERIFY.md`、`sample-project/docs/REVIEW_MERGE_VERIFY.md` 与若干 **`sample-project/**/*.java`**。  
2. **助手日志**：对上述 `.md` 应出现 **`[skip] no Qwen: … — documentation file (ignored)`**（文档 **不进** 多 Skill 流水线）。  
3. **PR 评论**：只应反映 **`.java` chunk** 的合并结果；**Issues** 里 SQL/SSRF/N+1 等 **`file`** 应为对应 **`.java`**。  
4. **Summary**：多 Java chunk 合并时，不应再出现与 **HIGH** 明显矛盾的「纯文档无问题」套话（依赖最新 `merge.py`）。
