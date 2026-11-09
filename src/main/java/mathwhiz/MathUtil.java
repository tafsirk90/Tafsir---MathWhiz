package mathwhiz;

/** Reusable mathematical methods. Every game answer is computed with these. */
public final class MathUtil {

    private MathUtil() { }

    /** Prime check by trial division up to sqrt(n). */
    public static boolean isPrime(int n) {
        if (n < 2) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        for (int i = 3; (long) i * i <= n; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }

    /** Remainder of a divided by b. */
    public static int modulus(int a, int b) {
        if (b == 0) throw new IllegalArgumentException("Divisor cannot be zero");
        return a % b;
    }

    /** sin of an angle in degrees, rounded to 2 decimal places. */
    public static double sine(double degrees) {
        return round2(Math.sin(Math.toRadians(degrees)));
    }

    /** cos of an angle in degrees, rounded to 2 decimal places. */
    public static double cosine(double degrees) {
        return round2(Math.cos(Math.toRadians(degrees)));
    }

    public static double round2(double v) {
        double r = Math.round(v * 100.0) / 100.0;
        return r == 0.0 ? 0.0 : r;   // avoid -0.0
    }
}
