package nz.bradley.neil.cpu.impl;

import nz.bradley.neil.cpu.api.MMIO;

import java.util.Map;

public abstract class MMIOBase implements MMIO {

    protected String name;
    protected int address, range;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public MMIO configure(String config) {
        String[] configs = config.split("[\\s=]");
        for (int i = 0; i < configs.length; i += 2) {
            switch (configs[i]) {
                case "name" -> name = configs[i + 1];
                case "address" -> address = Integer.parseInt(configs[i + 1]);
                case "range" -> range = Integer.parseInt(configs[i + 1]);
            }
        }
        return this;
    }

    @Override
    public Map.Entry<Integer, Integer> registerMapping() {
        return Map.entry(address, range);
    }

    @Override
    public Integer getInterruptAddress() {
        return null;
    }

    @Override
    public boolean hasInterrupt() {
        return false;
    }

    abstract public byte readByte(int offset);

    abstract public int readInteger(int offset);

    abstract public void writeByte(int offset, byte data);

    abstract public void writeInteger(int offset, int data);

    abstract public void sendClock();

    abstract public void sendReset();
}
