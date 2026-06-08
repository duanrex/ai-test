# 企业闭环验证（Feedback / Metrics / File risk / Reflection）

对应 **ai-review-assistant** 已实现能力，本文件说明如何用 **`C:\Git\ai-test`** 开 PR 做 **端到端验证**。

## 前置（助手侧）

1. 部署的助手代码包含：`review_feedback`、GitHub `issue_comment` 处理、`file_risk`、`REFLECTION_ENABLED`、`/api/feedback` 等。
2. **数据库**：在助手使用的库执行 **`ai-review-assistant` 仓库** 内  
   `docs/sql/review_feedback.sql`（表 `review_feedback`；依赖已有 `review_history`）。
3. **`.env`**：`DATABASE_URL`、`GITHUB_TOKEN` 已配置。
4. **GitHub Webhook**：同一 URL 除 **`pull_request`** 外，勾选 **`issue_comment`**（否则开发者回复无法入库）。

---

## A. 第一次 PR（产生评审 + 指纹）

1. 将 **`C:\Git\ai-test`** 推 GitHub，开 **PR**（改 `sample-project/**/*.java` 即可，已有 **WEBHOOK_SMOKE_VERIFY** 靶子）。
2. 等助手在 PR 上发 **AI Code Review** 评论。
3. 在评论里展开 **「Fingerprints (expand to copy)」**，复制某一行 **64 位十六进制**（及对应 `filename`，如 `sample-project/service/UserService.java`）。

---

## B. 开发者反馈（GitHub → `review_feedback`）

在 **同一 PR** 下用**个人账号**（非 Bot）发一条新评论，**每行一条**：

```text
/ai-review-feedback FALSE_POSITIVE <粘贴64位hex> sample-project/service/UserService.java
```

可选类型：`ACCEPTED`、`REJECTED`、`FALSE_POSITIVE`、`IGNORED`。

**预期：** 助手日志或接口无报错；Webhook 返回 JSON 含 `feedback_saved: 1`（若你在网关/日志里能看到）。

**注意：** `issue_comment` 由 **GitHub 发往助手**；助手需对 **`ai-test` 所在仓库** 配置了该 Webhook。

---

## C. 查 Skill 指标（助手 HTTP）

在能访问助手 API 的环境执行：

```http
GET http://<助手主机>:8000/api/feedback/skill-quality
```

或：

```http
GET http://<助手主机>:8000/api/metrics/skill-feedback
```

**预期：** `rows` 中出现 `skill_source: merged`、`feedback_type: FALSE_POSITIVE`、`count >= 1`。

---

## D. File risk 注入（需已有历史）

1. **同一文件路径** 再开 **第二次 PR**（或同一 PR 再 push 触发重新评审），仍改 `sample-project/service/UserService.java` 一类路径，使 **`review_history.filename`** 与第一次一致。
2. 助手在 **`DATABASE_URL` 已配置** 时会对 **general** 评审拼入 **Historical file signal** 段落（近 30 天高严重度次数 + 误报反馈次数）。

**预期：** 模型总结/ issues 可能更谨慎（非硬性断言，仅作上下文）。

---

## E. Reflection 反思节点

1. 助手环境设置 **`REFLECTION_ENABLED=1`**（或 `true`），重启服务。
2. 再触发一次 PR 评审。

**预期：** 日志中 pipeline 路径含 **`reflection`**；总 token 多一次合并后的 LLM 调用（费用上升）。默认 **`REFLECTION_ENABLED=0`** 时不走反思。

---

## F. 不经过 GitHub，仅本地 API 写反馈

需助手已启动且 **`DATABASE_URL` 可用**。先算指纹（在 **ai-review-assistant** 根目录、已激活 venv）：

```powershell
cd C:\Git\AI\ai-review-assistant
.\.venv\Scripts\python.exe -c "from app.feedback.fingerprint import issue_fingerprint; print(issue_fingerprint('sample-project/service/UserService.java', '与 PR 评论里某条 issue 的 message 完全一致'))"
```

再 **POST**（把 `repo`、`pr_number`、`issue_fingerprint` 换成真实值）：

```powershell
curl -s -X POST "http://127.0.0.1:8000/api/feedback" -H "Content-Type: application/json" -d "{\"repo\":\"owner/repo\",\"pr_number\":1,\"filename\":\"sample-project/service/UserService.java\",\"issue_fingerprint\":\"<64位小写hex>\",\"feedback_type\":\"FALSE_POSITIVE\",\"skill_source\":\"merged\"}"
```

**预期：** `{"id":..., "status":"ok"}`。

---

## G. 离线 golden（ai-review-assistant 仓库内）

在 **ai-review-assistant** 根目录：

```powershell
python -m unittest discover -s tests/evaluation -p "test_*.py" -v
```

**Tag:** `VERIFY_ENTERPRISE_LOOP`
