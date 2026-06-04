package auth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import repository.UserRepository;

/**
 * V2 review test: login delegates to repository (cross-file SQL / auth reasoning).
 * Intentionally vulnerable patterns for local review-tool testing only.
 */
public class AuthService {

    private final UserRepository userRepository = new UserRepository();

    public boolean login(String username, String password) throws Exception {
        return userRepository.existsWithCredentials(username, password);
    }

    /**
     * Hot loop calling DB each iteration — performance smell for priority-ordered review.
     */
    public void warmCacheBadly(String prefix) throws Exception {
        for (int i = 0; i < 500; i++) {
            if (userRepository.existsByName(prefix + i)) {
                System.out.println("hit");
            }
        }
    }

    /** Legacy inline SQL (still dangerous if copy-pasted without repository). */
    public boolean legacyCheck(String username) throws Exception {
        String sql = "SELECT 1 FROM users WHERE name = '" + username + "' LIMIT 1";
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:auth", "sa", "");
        try (Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            return rs.next();
        }
    }
}
