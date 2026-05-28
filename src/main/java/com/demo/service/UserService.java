package com.demo.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Demo dependency for import-level context retrieval testing.
 * Intentionally vulnerable: SQL built via string concatenation.
 */
public class UserService {

    public void save(String username) throws Exception {
        String sql = "INSERT INTO users (name) VALUES ('" + username + "')";
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test", "sa", "");
        try (Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
        }
    }
}
