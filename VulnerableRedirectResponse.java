// WARNING: Intentionally insecure. Local security education / review testing ONLY.

/**
 * Two defects:
 * 1) CWE-601 URL Redirection to Untrusted Site ("Open Redirect") — return URL fully controlled by caller.
 * 2) CWE-79 Cross-site Scripting — HTML built by string concatenation without encoding.
 */
public final class VulnerableRedirectResponse {

    // [VULN-1] Open redirect: next may be https://evil.com
    public String buildLoginRedirect(String next) {
        return next;
    }

    // [VULN-2] Reflected XSS: name may contain <script>…
    public String helloPage(String name) {
        return "<!DOCTYPE html><html><body>Hello, " + name + "!</body></html>";
    }
}
