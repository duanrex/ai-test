package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import service.User;

/**
 * V2 review test: string-concat SQL for security + cross-file import context.
 * Do not use in production.
 */
public class UserRepository {

    public void insert(User user) throws Exception {
        String sql = "INSERT INTO users (name) VALUES ('" + user.name + "')";
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:repo", "sa", "");
        try (Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
        }
    }

    public boolean existsWithCredentials(String username, String password) throws Exception {
        String sql =
                "SELECT 1 FROM users WHERE username='"
                        + username
                        + "' AND password='"
                        + password
                        + "' LIMIT 1";
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:repo", "sa", "");
        try (Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            return rs.next();
        }
    }

    public boolean existsByName(String name) throws Exception {
        String sql = "SELECT 1 FROM users WHERE name='" + name + "' LIMIT 1";
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:repo", "sa", "");
        try (Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            return rs.next();
        }
    }

    /**
     * MULTI_SKILL_VERIFY: N+1 — one DB round-trip per list element (PerformanceSkill).
     */
    public boolean anyNameExists(java.util.List<String> names) throws Exception {
        for (String n : names) {
            if (existsByName(n)) {
                return true;
            }
        }
        return false;
    }
}
