// WARNING: Intentionally bad patterns for local PR / Qwen review testing only. Do not use in production.

import java.nio.file.Files;
import java.nio.file.Path;

/** 用于触发 Qwen：两处问题 + 方法之间有空行。 */
public final class PrQwenAlpha {

    public int unsafeParse(String raw) {

        return Integer.parseInt(raw);
    }


    public String unsafeRead(String name) throws Exception {

        return Files.readString(Path.of("/data/reports/" + name));
    }
}
