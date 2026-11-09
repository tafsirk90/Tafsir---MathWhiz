package mathwhiz;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** JDBC access to PostgreSQL. All queries use PreparedStatement. */
public class Db {

    static final String URL  = env("DB_URL", "jdbc:postgresql://localhost:5432/mathwhiz");
    static final String USER = env("DB_USER", "postgres");
    static final String PASS = env("DB_PASSWORD", "admin123");

    private static volatile boolean ready = false;

    static {
        try { Class.forName("org.postgresql.Driver"); }
        catch (ClassNotFoundException e) { throw new RuntimeException(e); }
    }

    public record Entry(String player, int score, Timestamp playedAt) { }

    private static String env(String k, String d) {
        String v = System.getenv(k);
        return (v == null || v.isBlank()) ? d : v;
    }

    private static Connection get() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    /** Creates the table if it does not exist (the database itself comes from schema.sql). */
    private static synchronized void ensureTable() throws SQLException {
        if (ready) return;
        try (Connection c = get(); Statement st = c.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS scores (" +
                       "id SERIAL PRIMARY KEY, player_name VARCHAR(50) NOT NULL, " +
                       "score INT NOT NULL CHECK (score BETWEEN 0 AND 10), " +
                       "played_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");
        }
        ready = true;
    }

    /** CREATE: insert one score. */
    public static void saveScore(String player, int score) throws SQLException {
        ensureTable();
        try (Connection c = get();
             PreparedStatement ps = c.prepareStatement("INSERT INTO scores(player_name, score) VALUES (?, ?)")) {
            ps.setString(1, player);
            ps.setInt(2, score);
            ps.executeUpdate();
        }
    }

    /** READ: top 10 by score (earlier play wins ties). */
    public static List<Entry> top10() throws SQLException {
        ensureTable();
        List<Entry> list = new ArrayList<>();
        try (Connection c = get();
             PreparedStatement ps = c.prepareStatement(
                 "SELECT player_name, score, played_at FROM scores ORDER BY score DESC, played_at ASC LIMIT 10");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new Entry(rs.getString(1), rs.getInt(2), rs.getTimestamp(3)));
        }
        return list;
    }
}
