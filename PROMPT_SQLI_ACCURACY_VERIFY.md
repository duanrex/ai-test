# PROMPT_SQLI_ACCURACY_VERIFY

用于对照 **ai-review-assistant** 中 `app/prompt.py` 的 **`_SQL_INJECTION_ACCURACY`**、合并摘要里「勿把仅日志当 SQL 注入」等规则是否生效。

## 前置条件

- 助手已部署包含上述提示词改动的版本（多 Skill 流水线不变）。提示词还应包含 **`_ISSUE_LIST_HYGIENE`**（N+1 术语、同一方法同一根因不重复多条 / 不双严重度）。

## 验收点（看 PR 评论 JSON / 正文）

1. **`sample-project/service/UserService.java` — `logLookupHint`**  
   实现仅为 `System.out.println(...)`，**没有**把字符串交给 JDBC / ORM 执行。  
   **预期：** issues / summary 中 **不出现**「SQL 注入」「SQL injection」针对该方法。  
   **若仍提及该方法：** 措辞应为日志侧风险（如敏感信息进日志、log injection），**不是** SQLi。

2. **`sample-project/service/ConfusingNames.java` — `waitMsBusy`**  
   纯 CPU 忙等、循环内无 I/O。  
   **预期：** 性能类严重度多为 **MEDIUM**；不应默认标成 **HIGH**（与 `_PERFORMANCE_SEVERITY_HINT` 一致）。

3. **`sample-project/auth/AuthService.java` — `legacyCheck`**  
   **预期：** 不应将「外部多次调用该方法」误称为 **N+1**；N+1 指「先加载 N 条再在循环里每条多查一次」一类模式（见 `_ISSUE_LIST_HYGIENE`）。

4. **`sample-project/service/UserService.java` — `publishTagsWithPause`**  
   **预期：** 对「循环 + `Thread.sleep`」同一根因，**不应**同时出现 **MEDIUM** 与 **HIGH** 两条独立 issue；合并后宜为 **一条**、**单一**严重度。

## 与本文件一起开 PR

本文件为 **`.md`**，chunker 会 **跳过** 不送审；与 `sample-project/**/*.java` 同 PR 时，日志中应仍出现对本文档的 **`[skip] no Qwen: … documentation file (ignored)`**，与 `DOC_SKIP_VERIFY` 行为一致。

**Tag:** `PROMPT_SQLI_ACCURACY_VERIFY`
