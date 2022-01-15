package nz.bradley.neil.cpu.api;

import java.util.Map;

/**
 * <p>
 *     Memory-Mapped Input/Output Interface
 * </p>
 * <p>
 *     Provides a mechanism by which external IO may be mapped into
 *     memory space.  The MMIO device will declare how many bytes of
 *     memory it will require, and these will be allocated to it within
 *     the the address range 0xFFFF_0000 - 0xFFFF_FFFF.
 * </p>
 */
public interface MMIO {

    /**
     * Gets a human-readable name for the MMIO unit.
     *
     * @return The name allocated to this MMIO unit when configured.
     */
    String getName();

    /**
     * Assign the MMIO unit relevant information.
     *
     * @param config    The configuration parameters for the MMIO.
     * @return          Should return itself.
     */
    MMIO configure(String config);

    /**
     * Notifies the caller of the starting address, and range of this
     * MMIO unit's memory-mapped addresses.
     *
     * @return  &lt;start, length&gt; address range for this MMIO unit.
     */
    Map.Entry<Integer, Integer> registerMapping();

    /**
     * Gets the jump address for the MMIO unit's interrupt handler, if
     * it has one.  This method should also clear the MMIO unit's
     * internal interrupt flag.
     *
     * @return The jump address for the MMIO interrupt, or null if it
     *         doesn't have an interrupt.
     */
    Integer getInterruptAddress();

    /**
     * Notifies the CPU if an interrupt event has occurred. The CPU will
     * check for interrupts before incrementing $PC.  Note that checking
     * for the interrupt happens at the CPU's discretion, so the MMIO
     * unit will need to handle the possibility of a delay between
     * setting the flag, and the interrupt being serviced.
     *
     * @return  {@link Boolean#TRUE} if interrupted awaiting handling,
     *          {@link Boolean#FALSE} otherwise.
     */
    boolean hasInterrupt();

    byte readByte(int offset);

    int readInteger(int offset);

    void writeByte(int offset, byte data);

    void writeInteger(int offset, int data);

    void sendClock();

    void sendReset();
}
