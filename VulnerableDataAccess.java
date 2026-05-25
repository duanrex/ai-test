// WARNING: Intentionally insecure. Local security education / review testing ONLY.

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Three defects:
 * 1) CWE-89  SQL Injection — concatenated user input in SQL.
 * 2) CWE-798 Hard-coded Credentials — secrets in source.
 * 3) CWE-532 Insertion of Sensitive Information into Log — password printed to logs.
 */
public final class VulnerableDataAccess {

    private static final String JDBC = "jdbc:h2:mem:x;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    // [VULN-2] Hard-coded DB password.
    private static final String PASS = "P@ssw0rd!";

    public String login(String username, String password) throws Exception {
        // [VULN-3] Sensitive data in logs.
        System.out.println("login attempt user=" + username + " password=" + password);

        Connection c = DriverManager.getConnection(JDBC, USER, PASS);
        Statement st = c.createStatement();
        // [VULN-1] SQL injection via username/password concatenation.
        String sql =
                "SELECT 1 FROM users WHERE name='"
                        + username
                        + "' AND pwd='"
                        + password
                        + "'";
        ResultSet rs = st.executeQuery(sql);
        return rs.next() ? "OK" : "DENY";
    }
}
