// WARNING: 本地 PR / AST 测试用。Tier「高」：+ 行片段应对 tree-sitter 判为 HIGH_SIGNAL → 不应被 ast_java_filter 跳过。
// 另保留「中」风险示例（需 Qwen 看业务/安全），与 highTier 分开。

import java.nio.file.Files;
import java.nio.file.Path;

/** Tier 高：控制流 + 异常。Tier 中：原有不安全 API 用法（送 Qwen）。 */
public final class PrQwenAlpha {

    /**
     * 高：仅看 + 行包进 static{} 时，应出现 if / throw / 调用等 HIGH 节点。
     * 方法内故意留空行，便于 diff 含空行。
     */
    public void highTier(String raw) {

        int n = Integer.parseInt(raw);

        if (n < 0) {

            throw new IllegalArgumentException("negative");
        }
    }


    /* ---------- 中：仍应送 Qwen 的漏洞型写法（非「仅 DEBUG」） ---------- */

    public int unsafeParse(String raw) {

        return Integer.parseInt(raw);
    }


    public String unsafeRead(String name) throws Exception {

        return Files.readString(Path.of("/data/reports/" + name));
    }
}
