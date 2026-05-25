// WARNING: Intentionally insecure. Local security education / review testing ONLY.

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * Defects (2):
 * 1) CWE-327 Use of Broken / Risky Crypto — MD5 for password “hashing”, no salt.
 * 2) CWE-502 Deserialization of Untrusted Data — ObjectInputStream on attacker-controlled bytes.
 */
public final class VulnerableTokenService {

    // [VULN] MD5 without salt — fast to brute-force / rainbow tables.
    public String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        return Base64.getEncoder().encodeToString(md.digest(password.getBytes()));
    }

    // [VULN] Deserializing untrusted blob can lead to RCE gadget chains.
    public Object restoreSession(byte[] blob) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(blob))) {
            return ois.readObject();
        }
    }
}
