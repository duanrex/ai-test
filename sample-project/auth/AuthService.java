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

    // MERGE_NORMALIZE_VERIFY: doc chunk + java chunk merge / file rewrite smoke
    // DOC_SKIP_VERIFY: md files skipped in chunker; this Java file still reviewed

    /** MULTI_SKILL_VERIFY: hardcoded secret — SecuritySkill / Summary merge */
    private static final String INTERNAL_API_KEY = "msk-verify-secret-do-not-ship";

    private final UserRepository userRepository = new UserRepository();

    public boolean login(String username, String password) throws Exception {
        return userRepository.existsWithCredentials(username, password);
    }

    /** MULTI_SKILL_VERIFY: path traversal pattern — SecuritySkill */
    public byte[] loadExportByName(String name) throws Exception {
        java.io.File f = new java.io.File("/var/exports/" + name);
        try (java.io.FileInputStream in = new java.io.FileInputStream(f)) {
            return in.readAllBytes();
        }
    }

    /** MULTI_SKILL_VERIFY: SSRF-style URL from user input — SecuritySkill */
    public void pingPartner(String urlString) throws Exception {
        try (java.io.InputStream in = new java.net.URL(urlString).openStream()) {
            in.readAllBytes();
        }
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

    /**
     * JAVA_PATCH_VERIFY: role-based delete with string-concat SQL (SecuritySkill).
     */
    public int deleteUsersByRole(String role) throws Exception {
        String sql = "DELETE FROM users WHERE role='" + role + "'";
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:auth", "sa", "");
        try (Statement st = conn.createStatement()) {
            return st.executeUpdate(sql);
        }
    }
}
