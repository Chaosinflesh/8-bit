package nz.bradley.neil.cpu;

import nz.bradley.neil.cpu.api.Instruction;
import nz.bradley.neil.cpu.api.MMIO;
import nz.bradley.neil.cpu.impl.MMIOBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CPU extends MMIOBase {

    private static final int REGISTER_COUNT = 16;
    private static final int RETURN = REGISTER_COUNT - 1;

    private final Clock clock;
    private final Memory memory;
    private final Set<MMIO> mmios;
    private final PC pc;

    private final List<Register> registers = new ArrayList<>(16);
    private Register target;

    public CPU(Clock clock, Memory memory, PC pc, Set<MMIO>mmios) {
        this.clock = clock;
        clock.registerCPU(this);
        this.memory = memory;
        this.pc = pc;

        // Add parts to MMIOs.
        this.mmios = mmios;
        this.mmios.add(this);

        // Setup registers.
        for (int i = 0; i < REGISTER_COUNT; i++) {
            registers.add(new Register());
        }
        this.target = registers.get(0);
    }

    @Override
    public MMIO configure(String config) {
        super.configure(config);
        mmios.forEach(memory::registerMMIO);
        registers.forEach(Register::reset);
        return this;
    }

    @Override
    public byte readByte(int offset) {
        offset %= REGISTER_COUNT;
        return (byte)registers.get(offset).get();
    }

    @Override
    public int readInteger(int offset) {
        offset %= REGISTER_COUNT;
        return registers.get(offset).get();
    }

    @Override
    public void writeByte(int offset, byte data) {
        offset %= REGISTER_COUNT;
        registers.get(offset).set(data);
    }

    @Override
    public void writeInteger(int offset, int data) {
        offset %= REGISTER_COUNT;
        registers.get(offset).set(data);
    }

    @Override
    public void sendClock() {
        // 1. Send clock through to all peripherals. Note that the program
        //    counter gets incremented as part of this.
        mmios.forEach(mmio -> {
            if (mmio != this) {
                mmio.sendClock();
            }
        });

        // 2. Check for interrupts. Note that the first interrupt triggered is
        //    the one handled.
        //    TODO: In the future we need to make it so interrupts can't
        //          interrupt each other.
        for (MMIO mmio: mmios) {
            if (mmio.hasInterrupt()) {
                // We incremented $PC, but haven't executed it yet, so save it
                // as the next target (which the IR will have to call: `JR $RETURN`).
                registers.get(RETURN).set(pc.readInteger(0));
                pc.writeInteger(0, mmio.getInterruptAddress());
                return;
            }
        }

        // 2. Execute instruction.
        processInstruction(memory.readByte(pc.readInteger(0)));
    }

    @Override
    public void sendReset() {
        // Clear registers.
        // Clear PC.
        // Not sure about reloading the program file (where am I doing
        // that right now anyway?).
    }

    private void processInstruction(byte instruction) {
        Instruction instr = Instruction.ofValue(instruction);
        int param = instruction & 0x0F;
        switch (instr) {
            case SEL -> target = registers.get(param);
            case LDR -> target.set(memory.readInteger(registers.get(param).get()));
            case STR -> memory.writeInteger(registers.get(param).get(), target.get());
            case JR -> pc.writeInteger(0, registers.get(param).get());
            case CP -> registers.get(param).set(target.get());
            case AND -> target.and(registers.get(param).get());
            case OR -> target.or(registers.get(param).get());
            case XOR -> target.xor(registers.get(param).get());
            case ADD -> target.addTo(registers.get(param).get());
            case SUB -> target.subtract(registers.get(param).get());
            case MUL -> target.multiplyBy(registers.get(param).get());
            case DIV -> target.divideBy(registers.get(param).get());
            case SHL -> target.shiftLeft(param);
            case SHR -> target.shiftRight(param);
            case SSR -> target.signedShiftRight(param);
            case LDN -> {
                target.set(memory.readInteger(pc.readInteger(0) + 1));
                pc.writeInteger(0, pc.readInteger(0) + 5);
            }
            case STN -> {
                memory.writeInteger(memory.readInteger(pc.readInteger(0) + 1), target.get());
                pc.writeInteger(0, pc.readInteger(0) + 5);
            }
            case J -> jump(true);
            case JZ -> jump(target.isZero());
            case JN -> jump(target.isNegative());
            case JC -> jump(target.hadCarry());
            case JV -> jump(target.hadOverflow());
            case COM -> target.complement();
            case NEG -> target.negate();
            case INC -> target.addTo(1L);
            case DEC -> target.subtract(1L);
            case CLR -> target.set(0L);
            case UNO -> target.set(1L);
            case RST -> clock.stop();   // TODO.
            case HLT -> clock.stop();
            default -> {}
        }
    }

    private void jump(boolean condition) {
        if (condition) {
            pc.writeInteger(0, memory.readInteger(pc.readInteger(0) + 1));
        }
    }
}
