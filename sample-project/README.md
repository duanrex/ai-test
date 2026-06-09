# sample-project

用于 **Method Symbol Resolution** 与 **Rule Engine V2**（优先级 / Profile）本地验证的小型 Java 布局（不保证 `javac` 通过，仅给 tree-sitter / 字符串解析用）。

| 路径 | 作用 |
|------|------|
| `service/UserService.java` | `save(User)`、`UserRepository` 委托；风格噪声方法 `x`；**MULTI_SKILL_VERIFY**：`buildAuditTrail`；**JAVA_PATCH_VERIFY**：`publishTagsWithPause`、`logLookupHint`；**WEBHOOK_SMOKE_VERIFY**：`webhookSmokeTag` |
| `service/User.java` | 占位类型；**JAVA_PATCH_VERIFY**：`sameName`（{@code ==} 比较字符串）；**WEBHOOK_SMOKE_VERIFY**：`smokeRunRef` |
| `service/ConfusingNames.java` | `saveUser` / `saveAll` / `saveConfig` — 验证查找 `save` 时**不误命中**；**JAVA_PATCH_VERIFY**：`waitMsBusy`；**WEBHOOK_SMOKE_VERIFY**：`smokeIdentity` |
| `repository/UserRepository.java` | `insert` / `existsWithCredentials` / `existsByName` — **拼接 SQL**；**MULTI_SKILL_VERIFY**：`anyNameExists`（N+1）；**JAVA_PATCH_VERIFY**：`listUserNamesOrdered`；**WEBHOOK_SMOKE_VERIFY**：`logFilterPreview`（仅 println） |
| `auth/AuthService.java` | `login`、跨文件仓库；**MULTI_SKILL_VERIFY**：密钥 / 路径 / URL / `warmCacheBadly` / `legacyCheck`；**MERGE_NORMALIZE_VERIFY** 注释；**JAVA_PATCH_VERIFY**：`deleteUsersByRole`；**WEBHOOK_SMOKE_VERIFY**：`SMOKE_VERIFY_REVISION` |
| `docs/REVIEW_MERGE_VERIFY.md` | **验收说明**（当前 **`.md` 已被 chunker 忽略**，不送审；文内描述如何看日志与 Java 靶子） |
| `../DOC_SKIP_VERIFY.md`（仓库根，与 `sample-project/` 同级） | 用于在日志里确认 **`[skip] no Qwen: … documentation file (ignored)`** |
| `../PROMPT_SQLI_ACCURACY_VERIFY.md`（仓库根） | **SQLi 术语 / 忙等严重度** 验收清单（`.md` 同样被 chunker 忽略） |
| `../VERIFY_ENTERPRISE_LOOP.md`（仓库根） | **Feedback**（`/ai-review-feedback TYPE #N` / 短前缀 / 64hex）、**Metrics**、**File risk**、**Reflection**（需助手 DB + **`GITHUB_TOKEN`** + Webhook `issue_comment`） |

仓库根目录 **`MULTI_SKILL_VERIFY.md`**：多 Skill 流水线 + **文档跳过 / 合并** 验收步骤。  
**`sample-project/docs/REVIEW_MERGE_VERIFY.md`**：与上同步的说明（**md 本身不再产生 AI chunk**）。

仓库根目录 `test2.java`：极简「坏命名 / 长变量」片段，便于 **`RULE_PROFILE=frontend`** 时观察模型更偏 **style**、且 **import/method retrieval 关闭** 下的输出差异。

## V2 测试建议（ai-review-assistant）

1. **`payment`（默认 `review-profiles.yaml` 的 `active_profile`）**  
   推包含本目录改动的 PR，预期：优先报 **SQL 注入 / 凭证拼接** 等安全问题；Java 文件会走 **import + method retrieval**（若 webhook 指向该仓库）。

2. **`frontend`**  
   在助手侧设置环境变量 **`RULE_PROFILE=frontend`** 后重启，再跑同一 PR：预期更关注 **style**，且检索关闭、token 更省。

## 合并后验收（issue_normalize + merge）与文档忽略

- **`.md` 等**：由助手 **chunker 直接跳过**；PR 里改 README / 本目录下 md 时，日志里应有 **`[skip] no Qwen: … documentation file (ignored)`**，且 **不会** 为这些文件跑多 Skill。  
- **仅 Java chunk 合并时**：`Issues` 里 **`file`** 应为 **`*.java`**；若历史/模型仍写出 doc `file`，**`normalize_merged_review`** 会按正文里的源码路径纠正。  
- **`Summary`**：多 Java chunk 时，**`merge`** 会去掉与已有 issues 矛盾的文档式套话（见 `docs/REVIEW_MERGE_VERIFY.md`）。

验证命令见仓库根目录 `docs/METHOD_RESOLUTION_VERIFICATION.md`（若存在）。
