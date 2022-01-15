package nz.bradley.neil.cpu;

import nz.bradley.neil.cpu.impl.MMIOBase;

public class Clock extends MMIOBase {

    private boolean running;
    private CPU cpu;
    private long cycles;

    public void registerCPU(CPU cpu) {
        this.cpu = cpu;
    }

    public void start() {
        // TODO: Clock thread.
        running = true;
    }

    public boolean step() {
        if (running) {
            cpu.sendClock();
            cycles++;
        }
        return running;
    }

    public void stop() {
        // TODO: Clock thread.
        running = false;
    }


    @Override
    public byte readByte(int offset) {
        return (byte)(cycles >> ((7 - offset) * 8) & 0xFF);
    }

    @Override
    public int readInteger(int offset) {
        if (offset == 4) {
            return (int)(cycles & 0xFFFFFFFFL);
        } else {
            return (int)((cycles >>> 32) & 0xFFFFFFFFL);
        }
    }

    @Override
    public void writeByte(int offset, byte data) {
        // Does nothing, count cannot be written to.
    }

    @Override
    public void writeInteger(int offset, int data) {
        // Does nothing, count cannot be written to.
    }

    @Override
    public void sendClock() {
        // Does nothing, this manages itself.
    }

    @Override
    public void sendReset() {
        // TODO: Clock thread.
        cycles = 0L;
    }
}
