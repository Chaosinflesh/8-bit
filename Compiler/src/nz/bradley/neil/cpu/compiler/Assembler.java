package nz.bradley.neil.cpu.compiler;

import nz.bradley.neil.cpu.api.Instruction;
import nz.bradley.neil.cpu.api.InstructionException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Assembler {

    public static void main(String[] args) {
        if (args.length < 2) {
            usage();
            return;
        }
        List<String> inFiles = List.of(Arrays.copyOf(args, args.length - 1));
        String outFile = args[args.length - 1];
        if (inFiles.isEmpty() || outFile == null || outFile.isBlank()) {
            usage();
            return;
        }

        assemble(concatenateFiles(inFiles), outFile);
    }


    private static void usage() {
        System.err.println("Usage: java Assembler source1 [source2 ...] output");
    }


    private static List<String> concatenateFiles(List<String> files) {
        List<String> lines = new LinkedList<>();
        files.forEach(file -> {
            try {
                lines.addAll(Files.readAllLines(Path.of(file)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return lines;
    }


    private static final Map<String, byte[]> symbols = new HashMap<>();
    private static final Map<String, List<Integer>> symbolReferences = new HashMap<>();
    private static final Map<String, byte[]> labels = new HashMap<>();
    private static final Map<String, List<Integer>> labelReferences = new HashMap<>();
    private static int count;
    private static int index;
    private static byte[] bytes;

    private static void assemble(List<String> lines, String outFile) {
        try {
            count = 0;
            index = 0;
            bytes = new byte[lines.size() * 5];  // Worst case scenario
            // is all jumps.
            for (String line: lines) {
                // Strip comments.
                if (line.contains(";")) {
                    line = line.substring(0, line.indexOf(";"));
                }
                line = line.strip();

                if (!line.isBlank()) {
                    if (line.charAt(0) == '.') {
                        processData(line.substring(1));
                    } else if (line.charAt(0) == ':') {
                        processLabel(line.substring(1));
                    } else if (line.charAt(0) == '_') {
                        processAddress(line.substring(1));
                    } else {
                        processInstruction(line);
                    }
                }
                count++;
            }
            linkData();
            linkLabels();
            Files.write(Path.of(outFile), Arrays.copyOf(bytes, index));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processData(String line) {
        String[] parts = line.split("\\s+", 3);
        String symbol = parts[1].replaceAll("[$%!]", "");
        byte[] value = null;
        ByteBuffer buffer;

        switch (parts[0]) {
            case "int" -> {
                buffer = ByteBuffer.allocate(4);
                value = buffer.putInt(HexFormat.fromHexDigits(parts[2])).array();
            }
            case "raw" -> {
                buffer = ByteBuffer.allocate(parts[2].length() / 2);
                for (int i = 0; i < parts[2].length(); i += 2) {
                    buffer.put((byte)HexFormat.fromHexDigits(parts[2], i, i + 2));
                }
                value = buffer.array();
            }
            case "text" -> value = parts[2].getBytes(StandardCharsets.UTF_8);
        }

        if (value == null) {
            throw new RuntimeException("Invalid data type found at " + count + ": " + line);
        }
        if (symbols.containsKey(symbol)) {
            throw new RuntimeException("Duplicate symbol found at " + count + ": " + line);
        }
        symbols.put(symbol, value);
    }

    private static void processLabel(String label) {
        if (labels.containsKey(label)) {
            throw new RuntimeException("Duplicate label detected at " + count + ": " + label);
        }
        labels.put(label, ByteBuffer.allocate(4).putInt(index).array());
    }

    private static void processAddress(String line) {
        int address = HexFormat.fromHexDigits(line.replace("0x", "").replace(":", ""));
        if (address < index) {
            throw new RuntimeException("Address below index at " + count + ": " + line);
        }
        byte[] nop = compile(Instruction.NOP);
        while (index < address) {
            System.arraycopy(nop, 0, bytes, index, nop.length);
            index += nop.length;
        }
    }

    private static void processInstruction(String line) {
        String[] parts = line.split("\\s+", 2);
        Instruction instruction = Instruction.valueOf(parts[0]);
        int param = 0;
        if (parts.length > 1) {
            // Allow for readability, but ignore otherwise.
            parts[1] = parts[1].replaceAll("[$%!]", "");
            if (Character.isDigit(parts[1].charAt(0))) {
                param = Integer.parseInt(parts[1]);
            } else {
                switch (instruction) {
                    case J, JZ, JN, JC, JV -> {
                        labelReferences.putIfAbsent(parts[1], new LinkedList<>());
                        labelReferences.get(parts[1]).add(index + 1);
                    }
                    case LDN, STN -> {
                        symbolReferences.putIfAbsent(parts[1], new LinkedList<>());
                        symbolReferences.get(parts[1]).add(index + 1);
                    }
                }
            }
        }
        byte[] built = buildInstruction(instruction, param);
        System.arraycopy(built, 0, bytes, index, built.length);
        index += built.length;
    }

    private static void linkData() {
        for (var symbol: symbolReferences.keySet()) {
            if (!symbols.containsKey(symbol)) {
                throw new RuntimeException("Symbol not found: " + symbol);
            }
            for (var reference: symbolReferences.get(symbol)) {
                var pos = ByteBuffer.allocate(4).putInt(index).array();
                var data = symbols.get(symbol);
                // Might need to extend the array here.
                if (index + data.length > bytes.length) {
                    bytes = Arrays.copyOf(bytes, bytes.length * 2 + data.length);
                }
                System.arraycopy(data, 0, bytes, index, data.length);
                System.arraycopy(pos, 0, bytes, reference, 4);
                index += data.length;
            }
        }
    }

    private static void linkLabels() {
        for (var label: labelReferences.keySet()) {
            if (!labels.containsKey(label)) {
                throw new RuntimeException("Label not found: " + label);
            }
            for (var reference: labelReferences.get(label)) {
                System.arraycopy(labels.get(label), 0, bytes,reference, 4);
            }
        }
    }

    public static byte[] buildInstruction(
            final Instruction instruction,
            final int param
    ) {
        return switch (instruction) {
            case SEL, LDR, STR, JR, CP, AND, OR, XOR, ADD, SUB, MUL, DIV, SHL, SHR, SSR -> compileWithValue(instruction, param);
            case LDN, STN, J, JZ, JN, JC, JV -> compileWithInteger(instruction, param);
            default -> compile(instruction);
        };
    }

    private static byte[] compile(final Instruction instruction) {
        return new byte[]{instruction.getValue()};
    }

    private static byte[] compileWithValue(
            final Instruction instruction,
            final int value
    ) {
        checkValue(value);
        return new byte[]{(byte)(instruction.getValue() | value)};
    }

    private static byte[] compileWithInteger(
            final Instruction instruction,
            final int i
    ) {
        return ByteBuffer
            .allocate(5)
            .put(instruction.getValue())
            .putInt(i)
            .array();
    }

    private static void checkValue(final int value) {
        if (value < 0 || value > 15) {
            throw new InstructionException("Value " + value + " out of range.");
        }
    }

}
