package nz.bradley.neil.cpu;

import nz.bradley.neil.cpu.api.MMIO;
import nz.bradley.neil.cpu.api.MMIOException;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <p>
 *     <b>Memory</b>
 * </p>
 * <p>
 *     <b>Overview</b>
 *     <br>
 *     This is the normal memory for the system. It includes
 *     Memory-Mapped Input/Output handling also.
 * </p>
 * <p>
 *     <b>MMIO</b>
 *     <br>
 *     MMIO units can be registered with the Memory controller, using
 *     {@link Memory#registerMMIO(MMIO)}.  Once registered, MMIO units
 *     can be accessed through the normal memory calls:
 *     <br>
 *     <ul>
 *         <li>{@link Memory#readByte(int)}</li>
 *         <li>{@link Memory#readInteger(int)}</li>
 *         <li>{@link Memory#writeByte(int, byte)}</li>
 *         <li>{@link Memory#writeInteger(int, int)}</li>
 *     </ul>
 *     <br>
 *     MMIO units can request a range of memory addresses, but they must
 *     be unique - an address cannot match to multiple MMIO units.
 * </p>
 */
public class Memory {

    private byte[] memory;
    private final Map<Integer, Entry<MMIO, Integer>> MMIOMap;

    public Memory() {
        MMIOMap = new HashMap<>();
    }

    public Memory configure(String config) {
        int loc = config.indexOf("size=");
        int size = 4096;
        if (loc >= 0) {
            size = Integer.parseInt(config.substring(loc + 5));
        }
        memory = new byte[size];
        return this;
    }

    public void registerMMIO(MMIO mmio) throws MMIOException {
        var mapping = mmio.registerMapping();
        for (int i = 0; i < mapping.getValue(); i++) {
            int key = mapping.getKey() + i;
            if (MMIOMap.containsKey(key)) {
                throw new MMIOException("Invalid mapping: address " + key + " already mapped to " + MMIOMap.get(key).getKey().getName());
            } else {
                MMIOMap.put(key, Map.entry(mmio, i));
            }
        }
    }

    public byte readByte(int address) {
        if (MMIOMap.containsKey(address)) {
            var mmio = MMIOMap.get(address);
            return mmio.getKey().readByte(mmio.getValue());
        } else {
            return memory[address];
        }
    }

    public int readInteger(int address) {
        if (MMIOMap.containsKey(address)) {
            var mmio = MMIOMap.get(address);
            return mmio.getKey().readInteger(mmio.getValue());
        } else {
            byte[] temp = Arrays.copyOfRange(memory, address, address + 4);
            return ByteBuffer.wrap(temp).getInt();
        }
    }

    public void writeByte(int address, byte b) {
        if (MMIOMap.containsKey(address)) {
            var mmio = MMIOMap.get(address);
            mmio.getKey().writeByte(mmio.getValue(), b);
        } else {
            memory[address] = b;
        }
    }

    public void writeInteger(int address, int i) {
        if (MMIOMap.containsKey(address)) {
            var mmio = MMIOMap.get(address);
            mmio.getKey().writeInteger(mmio.getValue(), i);
        } else {
            memory[address    ] = (byte)(i >>> 24       );
            memory[address + 1] = (byte)(i >>> 16 & 0xFF);
            memory[address + 2] = (byte)(i >>>  8 & 0xFF);
            memory[address + 3] = (byte)(i        & 0xFF);
        }
    }

    public void bootstrap(byte[] bootloader) {
        System.arraycopy(bootloader,0,memory, 0,bootloader.length);
    }
}
