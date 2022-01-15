package nz.bradley.neil.cpu;


import nz.bradley.neil.cpu.impl.MMIOBase;

public class PC extends MMIOBase {

    private int location, nextLocation;

    @Override
    public byte readByte(int offset) {
        return 0;
    }

    @Override
    public int readInteger(int offset) {
        return location;
    }

    @Override
    public void writeByte(int offset, byte data) {
        // Does nothing.
    }

    @Override
    public void writeInteger(int offset, int data) {
        nextLocation = data;
    }

    @Override
    public void sendClock() {
        location = nextLocation;
        nextLocation++;
    }

    @Override
    public void sendReset() {
        location = 0;
        nextLocation = 0;
    }
}
