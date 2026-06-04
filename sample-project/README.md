# sample-project

用于 **Method Symbol Resolution** 与 **Rule Engine V2**（优先级 / Profile）本地验证的小型 Java 布局（不保证 `javac` 通过，仅给 tree-sitter / 字符串解析用）。

| 路径 | 作用 |
|------|------|
| `service/UserService.java` | `save(User)`、`UserRepository` 委托；风格噪声方法 `x`；**MULTI_SKILL_VERIFY**：`buildAuditTrail` 双重循环与字符串拼接（性能） |
| `service/User.java` | 占位类型 |
| `service/ConfusingNames.java` | `saveUser` / `saveAll` / `saveConfig` — 验证查找 `save` 时**不误命中** |
| `repository/UserRepository.java` | `insert` / `existsWithCredentials` / `existsByName` — **拼接 SQL**；**MULTI_SKILL_VERIFY**：`anyNameExists`（N+1） |
| `auth/AuthService.java` | `login`、跨文件仓库；**MULTI_SKILL_VERIFY**：硬编码密钥、路径拼接读文件、`URL` 打开用户输入（SSRF 面）、`warmCacheBadly` / `legacyCheck` |

仓库根目录 **`MULTI_SKILL_VERIFY.md`**：如何用本仓库开一个 PR 验证 **ai-review-assistant** 的 **Review → Security → Performance → Summary** 四段流水线与日志关键字。

仓库根目录 `test2.java`：极简「坏命名 / 长变量」片段，便于 **`RULE_PROFILE=frontend`** 时观察模型更偏 **style**、且 **import/method retrieval 关闭** 下的输出差异。

## V2 测试建议（ai-review-assistant）

1. **`payment`（默认 `review-profiles.yaml` 的 `active_profile`）**  
   推包含本目录改动的 PR，预期：优先报 **SQL 注入 / 凭证拼接** 等安全问题；Java 文件会走 **import + method retrieval**（若 webhook 指向该仓库）。

2. **`frontend`**  
   在助手侧设置环境变量 **`RULE_PROFILE=frontend`** 后重启，再跑同一 PR：预期更关注 **style**，且检索关闭、token 更省。

验证命令见仓库根目录 `docs/METHOD_RESOLUTION_VERIFICATION.md`（若存在）。
