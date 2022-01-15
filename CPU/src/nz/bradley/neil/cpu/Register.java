package nz.bradley.neil.cpu;

public class Register {

    private long work;
    private boolean c, v;

    public Register() {
        reset();
    }

    public void reset() {
        set(0);
    }

    public void set(long value) {
        work = value;
        c = v = false;
    }

    public int get() {
        return (int)work;
    }

    public boolean isZero() {
        return work == 0L;
    }

    public boolean isNegative() {
        return work < 0L;
    }

    public boolean hadCarry() {
        return c;
    }

    public boolean hadOverflow() {
        return v;
    }

    public void addTo(long value) {
        work += value;
        c = work > Integer.MAX_VALUE || work < Integer.MIN_VALUE;
        v = false;
    }

    public void subtract(long value) {
        addTo(-value);
    }

    public void multiplyBy(long value) {
        work *= value;
        c = false;
        v = work > Integer.MAX_VALUE || work < Integer.MIN_VALUE;
    }

    public void divideBy(long value) {
        if (value != 0) {
            work /= value;
            c = false;
            v = work > Integer.MAX_VALUE || work < Integer.MIN_VALUE;
        } else {
            c = true;
            v = true;
        }
    }

    public void complement() {
        work = ~work;
        c = false;
        v = false;
    }

    public void negate() {
        work = -work;
        c = false;
        v = false;
    }

    public void rotateLeft(long value) {
        value %= 32;
        int w = (int)work;
        int hi = w >>> (32 - value);
        int lo = w << value;
        work = hi | lo;
        c = false;
        v = false;
    }

    public void shiftLeft(long value) {
        work <<= value;
        c = false;
        v = false;
    }

    public void shiftRight(long value) {
        work >>>= value;
        c = false;
        v = false;
    }

    public void signedShiftRight(long value) {
        work >>= value;
        c = false;
        v = false;
    }

    public void and(long value) {
        work &= value;
        c = false;
        v = false;
    }

    public void or(long value) {
        work |= value;
        c = false;
        v = false;
    }

    public void xor(long value) {
        work ^= value;
        c = false;
        v = false;
    }
}
