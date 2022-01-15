package nz.bradley.neil.cpu.fileio;

import nz.bradley.neil.cpu.impl.MMIOBase;

public class FileWriter extends MMIOBase {

    @Override
    public byte readByte(int offset) {
        return 0;
    }

    @Override
    public int readInteger(int offset) {
        return 0;
    }

    @Override
    public void writeByte(int offset, byte data) {

    }

    @Override
    public void writeInteger(int offset, int data) {
        System.out.printf("%s\t%08X\t%d\n", Integer.toBinaryString(data), data, data);
    }

    @Override
    public void sendClock() {

    }

    @Override
    public void sendReset() {

    }
}
