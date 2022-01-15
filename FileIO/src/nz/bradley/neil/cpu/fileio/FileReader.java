package nz.bradley.neil.cpu.fileio;

import nz.bradley.neil.cpu.impl.MMIOBase;

public class FileReader extends MMIOBase {

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

    }

    @Override
    public void sendClock() {

    }

    @Override
    public void sendReset() {

    }
}
