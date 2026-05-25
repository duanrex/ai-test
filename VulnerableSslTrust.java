// WARNING: Intentionally insecure. Local security education / review testing ONLY.

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

/**
 * Two defects:
 * 1) CWE-295 Improper Certificate Validation — trust manager accepts any certificate.
 * 2) CWE-297 Improper Validation of Certificate with Host Mismatch — hostname verification disabled.
 */
public final class VulnerableSslTrust {

    // [VULN-1] Trust-all X509 (MITM possible).
    public static SSLContext naiveSslContext() throws Exception {
        TrustManager[] trustAll =
                new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {}

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {}

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
                };
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, trustAll, new java.security.SecureRandom());
        return ctx;
    }

    // [VULN-2] Accept any hostname (certificate CN/SAN ignored for host match).
    public static HostnameVerifier allowAllHosts() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }

    /** Example wiring (do not use). */
    public static void applyTo(HttpsURLConnection conn) throws Exception {
        conn.setSSLSocketFactory(naiveSslContext().getSocketFactory());
        conn.setHostnameVerifier(allowAllHosts());
    }
}
