# sample-project

用于 **Method Symbol Resolution** 本地验证的小型 Java 布局（不保证 `javac` 通过，仅给 tree-sitter / 字符串解析用）。

| 路径 | 作用 |
|------|------|
| `service/UserService.java` | `save(User)` |
| `service/User.java` | 占位类型 |
| `service/ConfusingNames.java` | `saveUser` / `saveAll` / `saveConfig` — 验证查找 `save` 时**不误命中** |
| `repository/UserRepository.java` | `insert(User)` |
| `auth/AuthService.java` | `login` |

验证命令见仓库根目录 `docs/METHOD_RESOLUTION_VERIFICATION.md`。
