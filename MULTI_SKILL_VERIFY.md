# Multi-skill pipeline 验证（ai-review-assistant）

本仓库在 `sample-project/` 下加了 **`MULTI_SKILL_VERIFY`** 注释的代码，用于触发 **ai-review-assistant** 的多段流水线（**LangGraph**：按 `review-profiles.yaml` 中 `security` / `performance` 的 **enabled** 条件执行 **Security / Performance** 节点；`frontend` 等 profile 可能只跑 **review → summary**）。

## 预期各 Skill 能“看见”什么

| 区域 | 文件 | 意图 |
|------|------|------|
| Security | `auth/AuthService.java` | 硬编码密钥、路径拼接、`URL` SSRF 面；**JAVA_PATCH_VERIFY**：`deleteUsersByRole` |
| Security | `repository/UserRepository.java` | 拼接 SQL + 凭证；**JAVA_PATCH_VERIFY**：`listUserNamesOrdered`（ORDER BY 拼接） |
| Security / logs | `service/UserService.java` | **JAVA_PATCH_VERIFY**：`logLookupHint` — 仅 `println`，**不执行 SQL**；助手应 **不误报 SQL 注入**（见 `PROMPT_SQLI_ACCURACY_VERIFY.md`） |
| Performance | `repository/UserRepository.java` | `anyNameExists` 循环里多次 `existsByName`（N+1） |
| Performance | `auth/AuthService.java` | `warmCacheBadly` 大量循环调库 |
| Performance | `service/UserService.java` | `buildAuditTrail` 双重循环 + 字符串拼接；**JAVA_PATCH_VERIFY**：`publishTagsWithPause`（循环 + sleep） |
| Performance | `service/ConfusingNames.java` | **JAVA_PATCH_VERIFY**：`waitMsBusy`（纯 CPU 忙等；预期多为 **MEDIUM**，非 **HIGH**） |
| General / Style | `service/UserService.java` | 方法 `x` 命名差、可维护性 |
| General / Correctness | `service/User.java` | **JAVA_PATCH_VERIFY**：`sameName`；**WEBHOOK_SMOKE_VERIFY**：`smokeRunRef` |

## WEBHOOK_SMOKE_VERIFY（端到端 PR 烟测）

以下方法仅用于 **开 PR 时产生稳定 diff**，便于确认 webhook / LangGraph / 合并逻辑无异常（**不增加**新的高危靶子逻辑）：

| 文件 | 新增 / 标记 |
|------|----------------|
| `service/UserService.java` | `webhookSmokeTag` |
| `auth/AuthService.java` | `SMOKE_VERIFY_REVISION` 常量 |
| `repository/UserRepository.java` | `logFilterPreview`（仅 `println`，非 SQL） |
| `service/ConfusingNames.java` | `smokeIdentity` |
| `service/User.java` | `smokeRunRef` |

## 怎么跑通

1. 将 **`C:\Git\ai-test`** 推到 GitHub，开一个 **PR**（包含上述改动）。
2. 确保 **ai-review-assistant** 已部署当前多 Skill 版本；`GITHUB_TOKEN`、模型 Key 已配置。
3. 触发 **PR webhook**（或本地调用与线上一致的 `review_single_file` 流程）。
4. 观察助手日志：
   - `[pipeline] Multi-skill (LangGraph): <file>.java -- review -> ... -> summary`（路径随 **Rule profile** 变化）
   - `⚡ AI pipeline: <file> (attempt=...)`
5. 看 PR 评论里的 **合并后** `summary` / `issues`：应同时体现安全类与性能类发现（具体措辞因模型而异）。

## 超时与费用

流水线对每个 Java chunk 的 LLM 次数 **随 Rule profile 变化**：`payment` 全开时最多 **4 次**（review + security + performance + summary）；`frontend` 等关闭 security/performance 时可能仅 **2 次**。若 PR 里多个 Java 文件，总耗时与 token 会明显上升。可在助手环境设置 **`SKILL_PIPELINE_TIMEOUT_SEC`**（默认 60）适当加大。

## 仅本地 smoke（不连 GitHub）

在 **ai-review-assistant** 仓库根目录，用与 CI 相同的单测/脚本无法替代完整四段模型调用；要验证端到端仍需一次真实或 mock 的 provider 调用。

## 文档忽略 + 合并（`DOC_SKIP_VERIFY` / `MERGE_NORMALIZE_VERIFY`）

1. 开一个 PR，**同时改** 仓库根 `DOC_SKIP_VERIFY.md`、`sample-project/docs/REVIEW_MERGE_VERIFY.md` 与若干 **`sample-project/**/*.java`**。  
2. **助手日志**：对上述 `.md` 应出现 **`[skip] no Qwen: … — documentation file (ignored)`**（文档 **不进** 多 Skill 流水线）。  
3. **PR 评论**：只应反映 **`.java` chunk** 的合并结果；**Issues** 里 SQL/SSRF/N+1 等 **`file`** 应为对应 **`.java`**。  
4. **Summary**：多 Java chunk 合并时，不应再出现与 **HIGH** 明显矛盾的「纯文档无问题」套话（依赖最新 `merge.py`）。

## PROMPT_SQLI_ACCURACY_VERIFY（`app/prompt.py` 术语收紧）

部署含 **`_SQL_INJECTION_ACCURACY`**、**`_ISSUE_LIST_HYGIENE`**、合并阶段「勿把仅日志当 SQLi」及 **去重 / 统一严重度** 规则的 **ai-review-assistant** 后，用本仓库 PR 再跑一轮 webhook，在 **`UserService.logLookupHint`** 上验收：

| 检查项 | 预期 |
|--------|------|
| `logLookupHint` | **不得**在 issues/summary 中出现 **SQL injection** / **SQL 注入**（因无 `Statement`/`execute*` 路径） |
| 若仍提 `logLookupHint` | 应用 **日志敏感信息**、**log injection** 等表述，且严重度通常 ≤ **MEDIUM** |
| `ConfusingNames.waitMsBusy` | 性能类多为 **MEDIUM**；若仍为 **HIGH** 可再调模型或提示词 |
| `AuthService.legacyCheck` | **不得**误用 **N+1** 描述「仅多次调用、每次单条 SQL」的情形 |
| `UserService.publishTagsWithPause` | 同一根因（循环 + sleep）**不应**拆成 **MEDIUM + HIGH** 两条 |

根目录 **`PROMPT_SQLI_ACCURACY_VERIFY.md`** 为同主题简短清单（改该文件可触发文档 skip 日志，与 Java 同 PR 即可）。
