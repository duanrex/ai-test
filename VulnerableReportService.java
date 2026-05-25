// WARNING: Intentionally insecure. Local security education / review testing ONLY.

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Defects (2):
 * 1) CWE-22 Path Traversal — user-controlled segment under a fixed prefix without canonicalization.
 * 2) CWE-78 OS Command Injection — shell command built from untrusted input.
 */
public final class VulnerableReportService {

    private static final String REPORT_DIR = "/var/reports/";

    // [VULN] Path traversal via name e.g. "../../etc/passwd"
    public byte[] readReport(String name) throws IOException {
        File f = new File(REPORT_DIR + name);
        try (FileInputStream in = new FileInputStream(f)) {
            return in.readAllBytes();
        }
    }

    // [VULN] Command injection: host may contain shell metacharacters.
    public String pingHost(String host) throws IOException, InterruptedException {
        String cmd = "ping -n 1 " + host;
        Process p = Runtime.getRuntime().exec(cmd);
        p.waitFor();
        return new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}
