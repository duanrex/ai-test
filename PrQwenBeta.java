// WARNING: Intentionally bad patterns for local PR / Qwen review testing only. Do not use in production.

import java.net.URL;

/** 用于触发 Qwen：两处问题 + 方法之间有空行。 */
public final class PrQwenBeta {

    public byte[] fetchAny(String urlString) throws Exception {

        return new URL(urlString).openStream().readAllBytes();
    }


    public boolean unsafeSecretEquals(String guess, String secret) {

        return secret.equals(guess);
    }
}
