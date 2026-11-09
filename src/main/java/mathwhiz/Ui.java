package mathwhiz;

import java.util.List;

/** Small HTML helpers shared by the servlets. */
final class Ui {

    private Ui() { }

    static String head(String title) {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>" + title + "</title><style>"
            + "body{font-family:Arial,sans-serif;max-width:720px;margin:30px auto;padding:0 12px}"
            + "table{border-collapse:collapse;width:100%;margin:12px 0} th,td{border:1px solid #ccc;padding:8px;text-align:left}"
            + "th{background:#f3f4f6} .ok{color:#15803d;font-weight:bold} .bad{color:#b91c1c;font-weight:bold}"
            + ".q{background:#f9fafb;border:1px solid #e5e7eb;border-radius:8px;padding:12px;margin:12px 0}"
            + "input[type=text],input[type=number]{padding:6px} button,a.btn{padding:9px 18px;background:#3b82f6;color:#fff;"
            + "border:0;border-radius:6px;text-decoration:none;cursor:pointer;font-size:15px}"
            + "</style></head><body><h1>Tafsir - MathWhiz</h1>";
    }

    static String foot() {
        return "</body></html>";
    }

    static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    static String leaderboard() {
        StringBuilder sb = new StringBuilder("<h2>Top 10 Leaderboard</h2>");
        try {
            List<Db.Entry> top = Db.top10();
            if (top.isEmpty()) return sb.append("<p>No scores yet. Be the first!</p>").toString();
            sb.append("<table><tr><th>Rank</th><th>Player</th><th>Score</th><th>Date</th></tr>");
            int rank = 1;
            for (Db.Entry e : top) {
                sb.append("<tr><td>").append(rank++).append("</td><td>").append(esc(e.player()))
                  .append("</td><td>").append(e.score()).append(" / 10</td><td>")
                  .append(e.playedAt().toString(), 0, 16).append("</td></tr>");
            }
            sb.append("</table>");
        } catch (Exception ex) {
            sb.append("<p class='bad'>Database error: ").append(esc(ex.getMessage())).append("</p>");
        }
        return sb.toString();
    }
}
