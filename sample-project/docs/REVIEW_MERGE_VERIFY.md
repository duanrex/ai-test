# Merge / normalize 验证说明（故意与 Java 缺陷分文件）

本 Markdown 在 PR 里通常会作为**单独 chunk** 被评审。模型有时会把「下面提到的 Java 问题」错误地记在 **本 `.md` 文件** 的 `file` 字段上。

**部署了 `issue_normalize` + `merge_reviews` 去重后，预期：**

1. **`Issues Found` 里的 `file`**  
   若 `message` 里写明了真实路径（例如 `repository/UserRepository.java`、`auth/AuthService.java`），则合并后 **`file` 应被改成该 `.java` 路径**，而不是长期停留在 `*.md`。

2. **`Summary`**  
   若某个 chunk 的 summary 写「仅是文档、无执行代码、无严重问题」，而其它 chunk 对 **`.java`** 报了 HIGH，则合并后的总 summary **不应再保留那段与 HIGH 矛盾的文档套话**（`_coalesce_summaries`）。

3. **仍应出现的真实风险（在源码里，不在本文）**  

| 路径 | 说明 |
|------|------|
| `repository/UserRepository.java` | 拼接 SQL、`anyNameExists` N+1 |
| `auth/AuthService.java` | 硬编码密钥、`loadExportByName` 路径拼接、`pingPartner` URL |

推送 PR 后看 **最终 PR 评论**：对照上表检查 `file` 与 summary 是否与助手版本一致。

**Tag:** `MERGE_NORMALIZE_VERIFY`
