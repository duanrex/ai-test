// WARNING: Intentionally insecure. Local security education / review testing ONLY.

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Two defects:
 * 1) CWE-22 Path Traversal — user-controlled filename appended to export directory.
 * 2) CWE-732 Incorrect Permission Assignment for Critical Resource — world-readable/writable temp file.
 */
public final class VulnerableExportWriter {

    private static final String EXPORT_ROOT = "/var/exports/";

    // [VULN-1] Path traversal via fileName, e.g. "../../etc/cron.d/evil"
    public void saveExport(String fileName, String body) throws IOException {
        File f = new File(EXPORT_ROOT + fileName);
        try (FileWriter w = new FileWriter(f)) {
            w.write(body);
        }
    }

    // [VULN-2] Predictable path + default permissions often allow other local users to read secrets.
    public File createTempSecretsFile(String userId) throws IOException {
        File f = new File(System.getProperty("java.io.tmpdir"), "secrets-" + userId + ".txt");
        if (!f.createNewFile()) {
            // ignore race for demo
        }
        f.setWritable(true, false); // writable by everyone on many systems
        return f;
    }
}
