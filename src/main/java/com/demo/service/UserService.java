package com.demo.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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

    /** 按邮箱查重（故意拼接 SQL，供 import retrieval / 跨文件评审演示） */
    public boolean existsByEmail(String email) throws Exception {
        String sql = "SELECT 1 FROM users WHERE email = '" + email + "' LIMIT 1";
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test", "sa", "");
        try (Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            return rs.next();
        }
    }
}
