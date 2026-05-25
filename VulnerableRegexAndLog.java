// WARNING: Intentionally insecure. Local security education / review testing ONLY.

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Two defects:
 * 1) CWE-1333 Inefficient Regular Expression Complexity (ReDoS) — user-supplied pattern may be catastrophic backtracking.
 * 2) CWE-117 Improper Output Neutralization for Logs — log forged via embedded newlines in user input.
 */
public final class VulnerableRegexAndLog {


    // [VULN-1] ReDoS: attacker supplies evil regex + long string to match.
    public boolean matchesUserPattern(String text, String userRegex) {
        try {
            return Pattern.compile(userRegex).matcher(text).matches();
        } catch (PatternSyntaxException e) {
            return false;
        }
    }

    // [VULN-2] Log injection: userName may contain "\n[FAKE] admin login success\n".
    public void auditLogin(String userName, boolean ok) {
        System.out.println("[AUDIT] user=" + userName + " success=" + ok);
    }
}
