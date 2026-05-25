// WARNING: Intentionally insecure. Local security education / review testing ONLY.

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * Three defects:
 * 1) CWE-22  Path Traversal — user-controlled path segment.
 * 2) CWE-78  OS Command Injection — shell command built from user input.
 * 3) CWE-918 Server-Side Request Forgery (SSRF) — fetches arbitrary URL from user.
 */
public final class VulnerableSystemBridge {

    private static final String BASE = "/data/uploads/";

    // [VULN-1] Path traversal: e.g. name = "../../etc/passwd"
    public String readUserFile(String name) throws Exception {
        Path p = Path.of(BASE + name);
        return Files.readString(p, StandardCharsets.UTF_8);
    }

    // [VULN-2] Command injection via unvalidated host.
    public String traceRoute(String host) throws Exception {
        Process proc = Runtime.getRuntime().exec("tracert " + host);
        try (BufferedReader br =
                new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
            return br.readLine();
        }
    }

    // [VULN-3] SSRF: attacker supplies file://, http://169.254.169.254/, internal IPs, etc.
    public byte[] fetchRemote(String urlString) throws Exception {
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        return conn.getInputStream().readAllBytes();
    }
}
