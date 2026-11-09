package mathwhiz;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/leaderboard")
public class LeaderboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().println(Ui.head("Tafsir - MathWhiz Leaderboard") + Ui.leaderboard()
            + "<p><a class='btn' href='game'>Play</a></p>" + Ui.foot());
    }
}
