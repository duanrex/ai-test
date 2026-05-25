// WARNING: Intentionally vulnerable. For local education / review-tool testing only. Do not use in production.
import java.sql.*;
import java.io.*;
import java.security.MessageDigest;
import java.util.Base64;

public class BadLoginServlet {

    // 1) SQL 注入：字符串拼接用户输入
    public User findUser(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE name = '" + username + "'";
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test", "sa", "");
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);
        return rs.next() ? new User(rs.getString("name")) : null;
    }


    // 2) 命令注入：把用户输入拼进系统命令
    public String pingHost(String host) throws IOException {
        Process p = Runtime.getRuntime().exec("ping -c 1 " + host);
        return new String(p.getInputStream().readAllBytes());
    }
	

    // 3) 路径遍历：未校验路径
    public byte[] readReport(String name) throws IOException {
        File f = new File("/var/reports/" + name);
        return new FileInputStream(f).readAllBytes();
    }


    // 4) 弱哈希：MD5 + 无盐
    public String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        return Base64.getEncoder().encodeToString(md.digest(password.getBytes()));
    }

    // 5) 反序列化不可信数据（若与 ObjectInputStream 结合使用极危险；此处示意“信任外部字节”）
    public Object restoreSession(byte[] blob) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(blob))) {
            return ois.readObject();
        }
    }

    // 6) 硬编码密钥
    private static final String API_KEY = "sk_live_0123456789abcdef";

    static class User {
        final String name;
        User(String name) { this.name = name; }
    }
}