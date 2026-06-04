# 文档跳过 + 合并逻辑（验收说明）

## 当前行为（ai-review-assistant `chunker`）

**`.md` / `.txt` / `.rst` / `.adoc` 等文档后缀** 在 **`chunk_diff_files`** 阶段会被 **整文件忽略**：不生成 chunk、**不调用** Review / Security / Performance / Summary 流水线。

因此：本文件即使出现在 PR diff 里，也 **不会** 再产生「文档 chunk 把 `file` 标成 README」之类的问题。

**验证方式：** 部署最新助手后，对包含本文件的 PR 跑 webhook，在助手控制台应看到类似：

```text
[skip] no Qwen: sample-project/docs/REVIEW_MERGE_VERIFY.md — documentation file (ignored)
```

## 仍应用 `issue_normalize` / `merge` 的场景

当 **其它路径**（例如模型仍写出 `file` 为某 `.md`）与 **message 中引用的 `*.java`** 不一致时，**合并后** 仍会尝试把 `file` 改回正文里的源码路径；**多 chunk summary** 仍会去掉与已有 issues 矛盾的文档套话段落。

## 刻意留作靶子的 Java（实际被审）

| 路径 | 说明 |
|------|------|
| `repository/UserRepository.java` | 拼接 SQL、`anyNameExists` N+1、`listUserNamesOrdered`（ORDER BY 拼接） |
| `auth/AuthService.java` | 硬编码密钥、路径拼接读文件、`pingPartner` URL、`warmCacheBadly`、`deleteUsersByRole` |
| `service/UserService.java` | `publishTagsWithPause`、`logLookupHint`（**仅控制台**，不执行 SQL — 用于 **PROMPT_SQLI_ACCURACY_VERIFY**） |
| `service/User.java` / `service/ConfusingNames.java` | **JAVA_PATCH_VERIFY**：`sameName`、`waitMsBusy`（**PROMPT_SQLI_ACCURACY_VERIFY**：忙等预期多为 MEDIUM） |

**Tag:** `DOC_SKIP_VERIFY`
