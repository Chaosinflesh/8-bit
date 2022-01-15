package nz.bradley.neil.cpu;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterTest {

    @Test
    void startsAtZero() {
        Register register = new Register();
        assertEquals(0, register.get());
    }

    @Test
    void setAndGetMatch() {
        Register register = new Register();
        int value = 101;
        register.set(value);
        assertEquals(value, register.get());
        value = -3002;
        register.set(value);
        assertEquals(value, register.get());
    }

    @Test
    void resetSetsToZeroAndClearsFlags() {
        Register register = new Register();
        int value = 101;
        register.set(value);
        assertEquals(value, register.get());
        register.reset();
        assertEquals(0, register.get());
    }

    @Test
    void isZeroGivesCorrectResult() {
        Register register = new Register();
        assertTrue(register.isZero());
        register.set(125);
        assertFalse(register.isZero());
    }

    @Test
    void isNegativeGivesCorrectResult() {
        Register register = new Register();
        assertFalse(register.isNegative());
        register.set(-100);
        assertTrue(register.isNegative());
        register.set(100);
        assertFalse(register.isNegative());
    }

    @Test
    void addToWorks() {
        Register register = new Register();
        register.addTo(12);
        assertEquals(12, register.get());
        register.addTo(6);
        assertEquals(18, register.get());
        register.addTo(-13);
        assertEquals(5, register.get());
        register.addTo(-100);
        assertEquals(-95, register.get());
        register.addTo(200);
        assertEquals(105, register.get());
    }

    @Test
    void subtractWorks() {
        Register register = new Register();
        register.subtract(5);
        assertEquals(-5, register.get());
        register.subtract(-10);
        assertEquals(5, register.get());
    }

    @Test
    void hadCarryGivesCorrectResult() {
        Register register = new Register();
        register.set(Integer.MAX_VALUE - 2);
        assertFalse(register.hadCarry());
        register.addTo(12);
        assertTrue(register.hadCarry());
        register.set(Integer.MIN_VALUE + 3);
        assertFalse(register.hadCarry());
        register.subtract(17);
        assertTrue(register.hadCarry());
        register.set(15);
        assertFalse(register.hadCarry());
        register.addTo(Integer.MAX_VALUE);
        assertTrue(register.hadCarry());
    }

    @Test
    void multiplyByWorks() {
        Register register = new Register();
        register.set(7);
        register.multiplyBy(5);
        assertEquals(35, register.get());
        register.multiplyBy(-3);
        assertEquals(-105, register.get());
        register.multiplyBy(1);
        assertEquals(-105, register.get());
        register.multiplyBy(-2);
        assertEquals(210, register.get());
    }

    @Test
    void divideByWorks() {
        Register register = new Register();
        register.set(210);
        register.divideBy(2);
        assertEquals(105, register.get());
        register.divideBy(-3);
        assertEquals(-35, register.get());
        register.divideBy(1);
        assertEquals(-35, register.get());
        register.divideBy(-7);
        assertEquals(5, register.get());
    }

    @Test
    void hadOverflowGivesCorrectResult() {
        Register register = new Register();
        register.set(5);
        assertFalse(register.hadOverflow());
        register.multiplyBy(7);
        assertFalse(register.hadOverflow());
        register.multiplyBy(Integer.MAX_VALUE);
        assertTrue(register.hadOverflow());
        register.set(50);
        assertFalse(register.hadOverflow());
        register.multiplyBy(Integer.MIN_VALUE);
        assertTrue(register.hadOverflow());
    }

    @Test
    void divideByZeroSetsCarryAndOverflow() {
        Register register = new Register();
        register.divideBy(0L);
        assertTrue(register.hadCarry());
        assertTrue(register.hadOverflow());
    }

    @Test
    void complementWorks() {
        Register register = new Register();
        register.set(0xFF00_FF00);
        register.complement();
        assertEquals(0x00FF_00FF, register.get());
    }

    @Test
    void negateWorks() {
        Register register = new Register();
        register.set(100);
        register.negate();
        assertEquals(-100, register.get());
        register.negate();
        assertEquals(100, register.get());
    }

    @Test
    void rotateLeftWorks() {
        Register register = new Register();
        register.set(0xFF00_FF00);
        register.rotateLeft(4);
        assertEquals(0xF00F_F00F, register.get());
    }

    @Test
    void shiftLeftWorks() {
        Register register = new Register();
        register.set(0xFF00_FF00);
        register.shiftLeft(4);
        assertEquals(0xF00F_F000, register.get());
    }

    @Test
    void shiftRightWorks() {
        Register register = new Register();
        register.set(0xFF00_FF00);
        register.shiftLeft(4);
        assertEquals(0x0FF0_0FF0, register.get());
    }

    @Test
    void signedShiftRightWorks() {
        Register register = new Register();
        register.set(-8);
        register.signedShiftRight(1);
        assertEquals(-4, register.get());
        register.set(9);
        register.signedShiftRight(2);
        assertEquals(2, register.get());
    }

    @Test
    void andWorks() {
        Register register = new Register();
        register.set(0xFF00_FF00);
        register.and(0x9090_9090);
        assertEquals(0x9000_9000, register.get());
    }

    @Test
    void orWorks() {
        Register register = new Register();
        register.set(0xFF00_FF00);
        register.or(0x9090_9090);
        assertEquals(0xFF90_FF90, register.get());
    }

    @Test
    void xorWorks() {
        Register register = new Register();
        register.set(0xFF00_FF00);
        register.xor(0x9090_9090);
        assertEquals(0x6F90_6F90, register.get());
    }
}
