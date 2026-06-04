本文件用于验证 **ai-review-assistant** 对文档类后缀的 **忽略**。

预期：PR 包含本文件时，助手日志出现 **`[skip] no Qwen: ... DOC_SKIP_VERIFY.md — documentation file (ignored)`**，且 **不会** 对本文件发起多 Skill 评审。

（Java 靶子仍在 `sample-project/` 下。）

**PROMPT_SQLI_ACCURACY_VERIFY：** 见仓库根目录 `PROMPT_SQLI_ACCURACY_VERIFY.md` 与 `MULTI_SKILL_VERIFY.md` 对应小节。
