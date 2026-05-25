// WARNING: Intentionally insecure. Local security education / review testing ONLY.

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Defects (2):
 * 1) CWE-89 SQL Injection — user input concatenated into SQL.
 * 2) CWE-798 Hard-coded Credentials — database password embedded in source.
 */
public final class VulnerableUserLookup {

    private static final String DB_URL = "jdbc:h2:mem:demo;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "sa";
    // [VULN] Hard-coded password in repository (never do this in production).
    private static final String DB_PASSWORD = "SuperSecret123!";

    public String findUserEmail(String userId) throws Exception {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        Statement st = conn.createStatement();
        // [VULN] SQL injection: attacker can pass  "' OR '1'='1"  style input.
        String sql = "SELECT email FROM users WHERE id = '" + userId + "'";
        ResultSet rs = st.executeQuery(sql);
        return rs.next() ? rs.getString(1) : null;
    }
}
