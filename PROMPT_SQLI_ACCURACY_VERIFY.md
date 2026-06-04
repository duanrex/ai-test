# PROMPT_SQLI_ACCURACY_VERIFY

用于对照 **ai-review-assistant** 中 `app/prompt.py` 的 **`_SQL_INJECTION_ACCURACY`**、合并摘要里「勿把仅日志当 SQL 注入」等规则是否生效。

## 前置条件

- 助手已部署包含上述提示词改动的版本（多 Skill 流水线不变）。

## 验收点（看 PR 评论 JSON / 正文）

1. **`sample-project/service/UserService.java` — `logLookupHint`**  
   实现仅为 `System.out.println(...)`，**没有**把字符串交给 JDBC / ORM 执行。  
   **预期：** issues / summary 中 **不出现**「SQL 注入」「SQL injection」针对该方法。  
   **若仍提及该方法：** 措辞应为日志侧风险（如敏感信息进日志、log injection），**不是** SQLi。

2. **`sample-project/service/ConfusingNames.java` — `waitMsBusy`**  
   纯 CPU 忙等、循环内无 I/O。  
   **预期：** 性能类严重度多为 **MEDIUM**；不应默认标成 **HIGH**（与 `_PERFORMANCE_SEVERITY_HINT` 一致）。

## 与本文件一起开 PR

本文件为 **`.md`**，chunker 会 **跳过** 不送审；与 `sample-project/**/*.java` 同 PR 时，日志中应仍出现对本文档的 **`[skip] no Qwen: … documentation file (ignored)`**，与 `DOC_SKIP_VERIFY` 行为一致。

**Tag:** `PROMPT_SQLI_ACCURACY_VERIFY`
