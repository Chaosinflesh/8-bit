package nz.bradley.neil.cpu.api;

import nz.bradley.neil.cpu.testing.annotations.Should;

/**
 * <p>
 *      <b>Neil Bradley's 8-bit CPU Instruction Set</b>
 * </p>
 * <p>
 *     <b>Overview</b>
 *     <br>
 *     The CPU is a single-cycle CPU, with 16 32-bit registers, capable
 *     of addressing 4GB of RAM.  Instructions may include register
 *     addresses, or small int values, as part of the instruction.  See
 *     each instruction for details.
 * </p>
 * <p>
 *     <b>Registers</b>
 *     <br>
 *     Registers are identified by 4-bit addresses, MSB left.  The final
 *     register is referenced as $RETURN, and whilst it can be used, its
 *     value may be overwritten by interrupt return addresses.  The CPU
 *     also has the concept of a $TARGET register, which is what ALU and
 *     certain other instructions operate on.
 * </p>
 * <p>
 *     <b>Values</b>
 *     <br>
 *     Values are either 4-bit, MSB left, or 32-bit, post-instruction.
 * </p>
 */
public enum Instruction {

    /**
     * <p>
     *     <b>SEL $r</b>
     *     <br>
     *     <i>Select $TARGET Register</i>
     * </p>
     * <p>
     *     <code>0x0r</code>
     *     <br>
     *     Select which register to use as <code>$TARGET</code>.
     * </p>
     */
    SEL((byte)0x00),

    /**
     * <p>
     *     <b>LDR $r</b>
     *     <br>
     *     <i>Load from Address in Register</i>
     * </p>
     * <p>
     *     <code>0x1r</code>
     *     <br>
     *     Loads from the memory address specified in register
     *     <code>$r</code> to <code>$TARGET</code>.
     * </p>
     */
    LDR((byte)0x10),

    /**
     * <p>
     *     <b>STR $r</b>
     *     <br>
     *     <i>Store to Address in Register</i>
     * </p>
     * <p>
     *     <code>0x2r</code>
     *     <br>
     *     Stores the data from <code>$TARGET</code> to the memory
     *     address specified in register <code>$r</code>.
     * </p>
     */
    STR((byte)0x20),

    /**
     * <p>
     *     <b>JR $r</b>
     *     <br>
     *     <i>Jump to Address in Register</i>
     * </p>
     * <p>
     *     <code>0x3r</code>
     *     <br>
     *     Jumps to the memory address specified in register
     *     <code>$r</code>.
     * </p>
     */
    JR((byte)0x30),

    /**
     * <p>
     *     <b>CP $r</b>
     *     <br>
     *     <i>Copy $TARGET to Register</i>
     * </p>
     * <p>
     *     <code>0x4r</code>
     *     <br>
     *     Copies the data from <code>$TARGET</code> to register
     *     <code>$r</code>.  This sets Z and N flags as appropriately,
     *     and clears C and V flags.
     * </p>
     */
    CP((byte)0x40),

    /**
     * <p>
     *     <b>AND $r</b>
     *     <br>
     *     <i>And from Register</i>
     * </p>
     * <p>
     *     <code>0x5r</code>
     *     <br>
     *     ANDs the value in register <code>$r</code> to
     *     <code>$TARGET</code>, saving the result in
     *     <code>$TARGET</code>.
     * </p>
     */
    AND((byte)0x50),

    /**
     * <p>
     *     <b>OR $r</b>
     *     <br>
     *     <i>OR from Register</i>
     * </p>
     * <p>
     *     <code>0x6r</code>
     *     <br>
     *     ORs the value in register <code>$r</code> with
     *     <code>$TARGET</code>, saving the result in
     *     <code>$TARGET</code>.
     * </p>
     */
    OR((byte)0x60),

    /**
     * <p>
     *     <b>XOR $r</b>
     *     <br>
     *     <i>XOR from Register</i>
     * </p>
     * <p>
     *     <code>0x7r</code>
     *     <br>
     *     XORs the value in register <code>$r</code> with
     *     <code>$TARGET</code>, saving the result in
     *     <code>$TARGET</code>.
     * </p>
     */
    XOR((byte)0x70),

    /**
     * <p>
     *     <b>ADD $r</b>
     *     <br>
     *     <i>Add from Register</i>
     * </p>
     * <p>
     *     <code>0x8r</code>
     *     <br>
     *     Adds the value in register <code>$r</code> to
     *     <code>$TARGET</code>, saving the result in
     *     <code>$TARGET</code>.
     * </p>
     */
    ADD((byte)0x80),

    /**
     * <p>
     *     <b>SUB %i</b>
     *     <br>
     *     <i>Subtract from Register</i>
     * </p>
     * <p>
     *     <code>0x9i</code>
     *     <br>
     *     Subracts the value in register <code>$r</code> from
     *     <code>$TARGET</code>.
     * </p>
     */
    SUB((byte)0x90),

    /**
     * <p>
     *     <b>MUL $r</b>
     *     <br>
     *     <i>Multiply from Register</i>
     * </p>
     * <p>
     *     <code>0x9r</code>
     *     <br>
     *     Multiplies <code>$TARGET</code> by <code>$r</code>, saving
     *     the result in <code>$TARGET</code>.
     * </p>
     */
    MUL((byte)0xA0),

    /**
     * <p>
     *     <b>DIV $r</b>
     *     <br>
     *     <i>Divide from Register</i>
     * </p>
     * <p>
     *     <code>0xAr</code>
     *     <br>
     *     Divides the <code>$TARGET</code> by <code>$r</code>, saving
     *     the result in <code>$TARGET</code>.
     * </p>
     */
    DIV((byte)0xB0),

    /**
     * <p>
     *     <b>SHL %i</b>
     *     <br>
     *     <i>Shift Left</i>
     * </p>
     * <p>
     *     <code>0xCi</code>
     *     <br>
     *     Shifts <code>$TARGET</code> left by %i bits.
     * </p>
     */
    SHL((byte)0xC0),

    /**
     * <p>
     *     <b>SHR %i</b>
     *     <br>
     *     <i>Shift Right</i>
     * </p>
     * <p>
     *     <code>0xDi</code>
     *     <br>
     *     Unsigned shifts right <code>$TARGET</code> by %i bits.
     * </p>
     */
    SHR((byte)0xD0),

    /**
     * <p>
     *     <b>SSR %i</b>
     *     <br>
     *     <i>Signed Shift Right</i>
     * </p>
     * <p>
     *     <code>0xEi</code>
     *     <br>
     *     Shifts <code>$TARGET</code> right by %i bits. The shifted in
     *     bits will all be set as as the original highest bit.
     * </p>
     */
    SSR((byte)0xE0),

    /**
     * <p>
     *     <b>LDN !d</b>
     *     <br>
     *     <i>Load Next</i>
     * </p>
     * <p>
     *     <code>0xF0</code>
     *     <br>
     *     Loads the next four bytes into <code>$TARGET</code>, MSB
     *     left, big-endian.  This will set Z and N as appropriate, and
     *     clear C and V.
     * </p>
     */
    LDN((byte)0xF0),

    /**
     * <p>
     *     <b>STN !d</b>
     *     <br>
     *     <i>Store Next</i>
     * </p>
     * <p>
     *     <code>0xF1</code>
     *     <br>
     *     Stores the value from <code>$TARGET</code> into the address
     *     specified as <code>~d</code>.
     * </p>
     */
    STN((byte)0xF1),

    /**
     * <p>
     *     <b>J !d</b>
     *     <br>
     *     <i>Jump</i>
     * </p>
     * <p>
     *     <code>0xF2</code>
     *     <br>
     *     Jumps to the address specified as <code>!d</code>.
     * </p>
     */
    J((byte)0xF2),

    /**
     * <p>
     *     <b>JZ!d</b>
     *     <br>
     *     <i>Jump if Zero</i>
     * </p>
     * <p>
     *     <code>0xF3</code>
     *     <br>
     *     Jumps to the address specified as <code>!d</code>, if
     *     <code>$TARGET</code> is zero.
     * </p>
     */
    JZ((byte)0xF3),

    /**
     * <p>
     *     <b>JN !d</b>
     *     <br>
     *     <i>Jump if Negative</i>
     * </p>
     * <p>
     *     <code>0xF4</code>
     *     <br>
     *     Jumps to the address specified as <code>!d</code>, if
     *     <code>$TARGET</code> is negative.
     * </p>
     */
    JN((byte)0xF4),

    /**
     * <p>
     *     <b>JC !d</b>
     *     <br>
     *     <i>Jump if Carry</i>
     * </p>
     * <p>
     *     <code>0xF5</code>
     *     <br>
     *     Jumps to the address specified as <code>!d</code>, if the
     *     last calculation performed on <code>$TARGET</code> resulted
     *     in a carry.
     * </p>
     */
    JC((byte)0xF5),

    /**
     * <p>
     *     <b>JV !d</b>
     *     <br>
     *     <i>Jump if Overflow</i>
     * </p>
     * <p>
     *     <code>0xF6</code>
     *     <br>
     *     Jumps to the address specified as <code>!d</code>, if the
     *     last calculation performed on <code>$TARGET</code> resulted
     *     in an overflow.
     * </p>
     */
    JV((byte)0xF6),

    /**
     * <p>
     *     <b>COM</b>
     *     <br>
     *     <i>Complement $TARGET</i>
     * </p>
     * <p>
     *     <code>0xF7</code>
     *     <br>
     *     Complements <code>$TARGET</code>. This is a one's complement.
     * </p>
     */
    COM((byte)0xF7),

    /**
     * <p>
     *     <b>NEG</b>
     *     <br>
     *     <i>Negate $TARGET</i>
     * </p>
     * <p>
     *     <code>0xF8</code>
     *     <br>
     *     Negates <code>$TARGET</code>. This is a two's complement.
     * </p>
     */
    NEG((byte)0xF8),

    /**
     * <p>
     *     <b>INC</b>
     *     <br>
     *     <i>Increment $TARGET</i>
     * </p>
     * <p>
     *     <code>0xF9</code>
     *     <br>
     *     Increment <code>$TARGET</code> by <code>1</code>.
     * </p>
     */
    INC((byte)0xF9),

    /**
     * <p>
     *     <b>DEC</b>
     *     <br>
     *     <i>Decrement $TARGET</i>
     * </p>
     * <p>
     *     <code>0xFA</code>
     *     <br>
     *     Decrement <code>$TARGET</code> by <code>1</code>.
     * </p>
     */
    DEC((byte)0xFA),

    /**
     * <p>
     *     <b>CLR</b>
     *     <br>
     *     <i>Clear</i>
     * </p>
     * <p>
     *     <code>0xFB</code>
     *     <br>
     *     Sets <code>$TARGET</code> to <code>0</code>.
     * </p>
     */
    CLR((byte)0xFB),

    /**
     * <p>
     *     <b>UNO</b>
     *     <br>
     *     <i>Set to 1</i>
     * </p>
     * <p>
     *     <code>0xFC</code>
     *     <br>
     *     Sets <code>$TARGET</code> to <code>1</code>.
     * </p>
     */
    UNO((byte)0xFC),

    /**
     * <p>
     *     <b>NOP</b>
     *     <br>
     *     <i>No Operation</i>
     * </p>
     * <p>
     *     <code>0xFD</code>
     *     <br>
     *     An empty instruction.
     * </p>
     */
    NOP((byte)0xFD),

    /**
     * <p>
     *     <b>RST</b>
     *     <br>
     *     <i>Reset</i>
     * </p>
     * <p>
     *     <code>0xFE</code>
     *     <br>
     *     Hard reset, clearing all registers, resetting all
     *     {@link MMIO}s, and setting <code>$PC</code> to
     *     <code>0</code>.
     * </p>
     */
    RST((byte)0xFE),

    /**
     * <p>
     *     <b>HLT</b>
     *     <br>
     *     <i>Halt</i>
     * </p>
     * <p>
     *     <code>0xFF</code>
     *     <br>
     *     The CPU stops executing.
     * </p>
     */
    HLT((byte)0xFF);


    private final byte value;

    Instruction(byte value) {
        this.value = value;
    }

    public final byte getValue() {
        return this.value;
    }

    public static Instruction ofValue(byte value) {
        // Look for exact match first.
        for (var i: Instruction.values()) {
            if (i.value == value) {
                return i;
            }
        }
        // Inexact matches second.
        value &= 0xF0;
        for (var i: Instruction.values()) {
            if (i.value == value) {
                return i;
            }
        }
        throw new InstructionException("Could not match the specified instruction value: " + value);
    }

}
