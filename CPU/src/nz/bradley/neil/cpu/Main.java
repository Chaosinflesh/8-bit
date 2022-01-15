package nz.bradley.neil.cpu;

import nz.bradley.neil.cpu.api.MMIO;
import nz.bradley.neil.cpu.fileio.FileReader;
import nz.bradley.neil.cpu.fileio.FileWriter;
import nz.bradley.neil.cpu.timer.Timer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {

    private static CPU cpu;
    private static Clock clock;
    private static Memory memory;
    private static PC pc;
    private static Set<MMIO> mmios;

    private static boolean DEBUG = false;
    private static boolean STEP = false;
    private static byte[] bootloader = null;

    public static void main(String[] args) {

        // Process arguments.
        Set<Map.Entry<String, String>> configOptions = new HashSet<>();
        for (String arg: args) {
            System.out.println(arg);
            if ("STEP".equals(arg)) {
                STEP = true;
            } else if ("DEBUG".equals(arg)) {
                DEBUG = true;
            } else if (arg.startsWith("CONFIG=")) {
                loadConfigFile(configOptions, arg.substring(arg.indexOf("=") + 1));
            } else if (arg.startsWith("BOOTLOADER=")) {
                loadBootstrap(arg.substring(arg.indexOf("=") + 1));
            } else {
                System.out.println("Unrecognized argument: '" + arg + "'");
            }
        }

        initialize(configOptions);
        if (bootloader != null) {
            memory.bootstrap(bootloader);
        }
        run();
    }

    private static void loadConfigFile(
        Set<Map.Entry<String, String>> configOptions,
        String filename
    ) {
        try {
            List<String> lines = Files.readAllLines(Path.of(filename));
            for (String line: lines) {
                if (!line.startsWith("#") && line.contains(": ")) {
                    String[] parts = line.split(": ");
                    configOptions.add(Map.entry(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadBootstrap(String filename) {
        try {
            bootloader = Files.readAllBytes(Path.of(filename));
        } catch (IOException e) {
            System.err.println("Failed to load boostrap: " + filename);
        }
    }

    private static void initialize(
        Set<Map.Entry<String, String>> configs
    ) {
        // Initialize parts.
        mmios = new HashSet<>();
        String cpuConfig = null;
        for (var config: configs) {
            switch (config.getKey()) {
                case "RAM" -> memory = new Memory().configure(config.getValue());
                case "CLOCK" -> {
                    clock = new Clock();
                    clock.configure(config.getValue());
                    mmios.add(clock);
                }
                case "PC" -> {
                    pc = new PC();
                    pc.configure(config.getValue());
                    mmios.add(pc);
                }
                case "CPU" -> cpuConfig = config.getValue();
                case "TIMER" -> mmios.add(new Timer().configure(config.getValue()));
                case "FILE_WRITER" -> mmios.add(new FileWriter().configure(config.getValue()));
                case "FILE_READER" -> mmios.add(new FileReader().configure(config.getValue()));
            }
        }
        if (clock == null || memory == null || pc == null || cpuConfig == null) {
            throw new IllegalStateException("A required element [clock, memory, or pc] is missing from the config.");
        }

        cpu = new CPU(clock, memory, pc, mmios);
        cpu.configure(cpuConfig);
    }

    private static void run() {
        Scanner scanner = new Scanner(System.in);
        clock.start();
        do {
            if (DEBUG) {
                System.out.println(String.join(
                        System.lineSeparator(),
                        "CYCLE " + clock.readInteger(0) + clock.readInteger(4) + ":",
                        "\t" + pc.getName() + "> " + pc.readInteger(0),
                        String.format("\t%02X", memory.readByte(pc.readInteger(0)))
                ));
            }
            if (STEP) {
                String text = scanner.next();
                if ("QUIT".equals(text)) {
                    clock.stop();
                }
            }
        } while (clock.step());
    }
}
