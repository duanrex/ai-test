// WARNING: Intentionally insecure. Local security education / review testing ONLY.

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Two defects:
 * 1) CWE-611 Improper Restriction of XML External Entity Reference (XXE) — parser not hardened against external entities.
 * 2) CWE-22 Path Traversal — user-controlled path read from disk without validation.
 */
public final class VulnerableXmlLoad {

    // [VULN-1] XXE: factory defaults may allow external DTD/entities depending on JDK/parser.
    public void parseUserXml(String xml) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        db.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    }

    // [VULN-2] Path traversal: path may be "../../../windows/win.ini" etc.
    public String readConfig(String path) throws IOException {
        return Files.readString(Path.of(path));
    }
}
