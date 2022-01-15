package nz.bradley.neil.cpu.timer;

import nz.bradley.neil.cpu.api.MMIO;
import nz.bradley.neil.cpu.impl.MMIOBase;

public class Timer extends MMIOBase {

    private int count, freq;
    private boolean interrupted;
    private boolean enabled;

    private int interrupt;

    @Override
    public MMIO configure(String config) {
        super.configure(config);
        String[] configs = config.split("[\\s=]");
        for (int i = 0; i < configs.length; i += 2) {
            switch (configs[i]) {
                case "interrupt" -> interrupt = Integer.parseInt(configs[i + 1]);
                case "freq" -> freq = Integer.parseInt(configs[i + 1]);
                case "enabled" -> enabled = Boolean.parseBoolean(configs[i + 1]);
            }
        }
        return this;
    }

    @Override
    public Integer getInterruptAddress() {
        interrupted = false;
        return interrupt;
    }

    @Override
    public boolean hasInterrupt() {
        return interrupted;
    }

    @Override
    public byte readByte(int offset) {
        return 0;
    }

    @Override
    public int readInteger(int offset) {
        return count;
    }

    @Override
    public void writeByte(int offset, byte data) {
    }

    @Override
    public void writeInteger(int offset, int data) {
        // TODO: This may be used to modify frequency/reset timer.
    }

    @Override
    public void sendClock() {
        if (enabled) {
            count++;
            if (count == freq) {
                interrupted = true;
                count = 0;
            }
        }
    }

    @Override
    public void sendReset() {
        count = 0;
        interrupted = false;
        enabled = false;
    }
}
