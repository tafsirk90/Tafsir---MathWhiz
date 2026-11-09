package mathwhiz;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * GET  /game : shows the 4 questions. Q1 uses a random number kept in the session;
 *              in Q2, Q3 and Q4 the PLAYER types the numbers / angles and their own answer.
 * POST /game : Java calculates the correct answers with MathUtil, compares them with the
 *              player's answers, calculates the score out of 10, saves it and shows the result.
 *
 * Marks: Q1 prime = 2, Q2 modulus = 2, Q3 sine = 3, Q4 cosine = 3  (total 10)
 */
@WebServlet("/game")
public class GameServlet extends HttpServlet {

    private static final int[] MARKS = {2, 2, 3, 3};

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int n = 2 + new Random().nextInt(99);          // Q1 number: 2..100
        req.getSession().setAttribute("n", n);

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println(Ui.head("Tafsir - MathWhiz - Play"));
        out.println("<form method='post' action='game'>");
        out.println("<p>Your name: <input type='text' name='player' maxlength='50' required></p>");

        out.println("<div class='q'><b>Q1 (2 marks).</b> Is <b>" + n + "</b> a prime number?<br>"
            + "<label><input type='radio' name='a1' value='yes' required> Yes</label> "
            + "<label><input type='radio' name='a1' value='no'> No</label></div>");

        out.println("<div class='q'><b>Q2 (2 marks).</b> Choose your own two numbers and give the remainder:<br>"
            + "<input type='number' name='m1' required> mod <input type='number' name='m2' required>"
            + " = <input type='number' name='a2' required></div>");

        out.println("<div class='q'><b>Q3 (3 marks).</b> Choose your own angle (degrees) and give its sine (2 decimals):<br>"
            + "sin(<input type='number' step='any' name='ang3' required>&deg;)"
            + " = <input type='text' name='a3' required></div>");

        out.println("<div class='q'><b>Q4 (3 marks).</b> Choose your own angle (degrees) and give its cosine (2 decimals):<br>"
            + "cos(<input type='number' step='any' name='ang4' required>&deg;)"
            + " = <input type='text' name='a4' required></div>");

        out.println("<button type='submit'>Submit answers</button></form>");
        out.println(Ui.foot());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        Integer n = (Integer) req.getSession().getAttribute("n");
        if (n == null) {                               // no game in progress
            resp.sendRedirect("game");
            return;
        }
        String player = req.getParameter("player") == null ? "" : req.getParameter("player").trim();
        if (player.isEmpty()) player = "Anonymous";
        if (player.length() > 50) player = player.substring(0, 50);

        // Player's inputs and answers
        String a1 = req.getParameter("a1");
        Integer m1 = parseInt(req.getParameter("m1"));
        Integer m2 = parseInt(req.getParameter("m2"));
        Integer a2 = parseInt(req.getParameter("a2"));
        Double ang3 = parseDouble(req.getParameter("ang3"));
        Double a3 = parseDouble(req.getParameter("a3"));
        Double ang4 = parseDouble(req.getParameter("ang4"));
        Double a4 = parseDouble(req.getParameter("a4"));

        // Correct answers, all calculated in Java
        boolean prime = MathUtil.isPrime(n);
        boolean modValid = m1 != null && m2 != null && m2 != 0;
        Integer mod = modValid ? MathUtil.modulus(m1, m2) : null;
        Double sin = ang3 != null ? MathUtil.sine(ang3) : null;
        Double cos = ang4 != null ? MathUtil.cosine(ang4) : null;

        boolean[] ok = {
            a1 != null && a1.equals("yes") == prime,
            mod != null && a2 != null && a2.equals(mod),
            sin != null && a3 != null && Math.abs(a3 - sin) <= 0.01,
            cos != null && a4 != null && Math.abs(a4 - cos) <= 0.01
        };
        int score = 0;
        for (int i = 0; i < 4; i++) if (ok[i]) score += MARKS[i];

        String dbMsg = "";
        try { Db.saveScore(player, score); }
        catch (Exception e) { dbMsg = "<p class='bad'>Score could not be saved: " + Ui.esc(e.getMessage()) + "</p>"; }
        req.getSession().removeAttribute("n");

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println(Ui.head("Tafsir - MathWhiz - Result"));
        out.println("<h2>Result for " + Ui.esc(player) + "</h2>");
        out.println("<table><tr><th>Question</th><th>Your answer</th><th>Correct</th><th>Marks</th></tr>");
        row(out, "Is " + n + " prime?", a1 == null ? "-" : a1, prime ? "yes" : "no", ok[0], MARKS[0]);
        row(out, modValid ? m1 + " mod " + m2 : "invalid numbers (divisor cannot be 0)",
            a2 == null ? "-" : a2.toString(), mod == null ? "-" : mod.toString(), ok[1], MARKS[1]);
        row(out, ang3 == null ? "sin(invalid angle)" : "sin(" + fmt(ang3) + "&deg;)",
            a3 == null ? "-" : a3.toString(), sin == null ? "-" : sin.toString(), ok[2], MARKS[2]);
        row(out, ang4 == null ? "cos(invalid angle)" : "cos(" + fmt(ang4) + "&deg;)",
            a4 == null ? "-" : a4.toString(), cos == null ? "-" : cos.toString(), ok[3], MARKS[3]);
        out.println("</table><h2>Score: " + score + " / 10</h2>" + dbMsg);
        out.println(Ui.leaderboard());
        out.println("<p><a class='btn' href='game'>Play again</a></p>");
        out.println(Ui.foot());
    }

    private void row(PrintWriter out, String question, String yours, String correct, boolean ok, int marks) {
        out.println("<tr><td>" + question + "</td><td>" + Ui.esc(yours) + "</td><td>" + correct + "</td><td class='"
            + (ok ? "ok'>" + marks : "bad'>0") + " / " + marks + "</td></tr>");
    }

    private static String fmt(double d) {
        return d == Math.rint(d) ? String.valueOf((long) d) : String.valueOf(d);
    }

    private static Integer parseInt(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return null; }
    }

    private static Double parseDouble(String s) {
        try {
            double d = Double.parseDouble(s.trim().replace(',', '.'));
            return Double.isFinite(d) ? d : null;
        } catch (Exception e) { return null; }
    }
}
