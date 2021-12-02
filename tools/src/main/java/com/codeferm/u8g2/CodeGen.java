/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2;

import static com.codeferm.u8g2.CodeGen.ListType.CONSTANTS;
import static com.codeferm.u8g2.CodeGen.ListType.ENUMS;
import static com.codeferm.u8g2.CodeGen.ListType.FONT_SWITCH;
import static com.codeferm.u8g2.CodeGen.ListType.I2C_SWITCH;
import static com.codeferm.u8g2.CodeGen.ListType.METHODS;
import static com.codeferm.u8g2.CodeGen.ListType.SPI_SWITCH;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Code generator for U8g2 to HawtJNI Java methods. Also generates code for Display.java.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Command(name = "codegen", mixinStandardHelpOptions = true, version = "codegen 1.0.0",
        description = "Generate Java code")
public class CodeGen implements Callable<Integer> {

    /**
     * Logger.
     */
    private final org.apache.logging.log4j.Logger logger = LogManager.getLogger(CodeGen.class);
    /**
     * u8g2.h file.
     */
    @Option(names = {"-u", "--u8g2"}, description = "File u8g2.h to process, ${DEFAULT-VALUE} by default.")
    private String u8g2FileName = "../u8g2/src/main/native-package/src/u8g2.h";
    /**
     * u8x8.h file.
     */
    @Option(names = {"-x", "--u8x8"}, description = "File u8x8.h to process, ${DEFAULT-VALUE} by default.")
    private String u8x8FileName = "../u8g2/src/main/native-package/src/u8x8.h";

    /**
     * Add display types here and in setup method.
     */
    public enum ListType {
        METHODS,
        ENUMS,
        I2C_SWITCH,
        SPI_SWITCH,
        CONSTANTS,
        FONT_SWITCH;
    }

    /**
     * C to Java mapping.
     */
    private final Map<String, String> typeMap;

    public CodeGen() {
        // These are C to Java type mappings
        typeMap = Stream.of(new String[][]{
            {"u8g2_uint_t", "int"},
            {"size_t", "int"},
            {"uint8_t", "int"},
            {"void", "void"},
            {"int8_t", "int"},
            {"u8g2_t", "long"},
            {"u8g2_cb_t", "long"},
            {"uint16_t", "int"},
            {"int16_t", "int"},
            {"char", "String"},
            {"u8g2_draw_ll_hvline_cb", "long"},
            {"u8log_t", "long"},
            {"u8g2_kerning_t", "long"},
            {"...", "long"},
            {"*", "long"},
            {"u8x8_msg_cb", "long"},}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    /**
     * Convert string to camelCase.
     *
     * @param str Input string.
     * @return Output string.
     */
    public String camelCase(final String str) {
        final var sb = new StringBuilder();
        for (final var s : str.split("_")) {
            sb.append(Character.toUpperCase(s.charAt(0)));
            sb.append(s.substring(1, s.length()).toLowerCase());
        }
        // Make first character lower case
        sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
        return sb.toString();
    }

    /**
     * Read file into list.
     *
     * @param fileName File name to read.
     * @return String List.
     */
    public List<String> readFile(final String fileName) {
        List<String> result;
        try (Stream<String> lines = Files.lines(Paths.get(fileName))) {
            result = lines.collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * Parse C constants and make the HawtJNI constants.
     *
     * @param header C header file.
     * @return Java code.
     */
    public Map<ListType, List<String>> generateConstants(final List<String> header) {
        final var map = new HashMap<ListType, List<String>>();
        final var code = new ArrayList<String>();
        final var enums = new ArrayList<String>();
        final var fontSwitch = new ArrayList<String>();
        for (final String string : header) {
            // Split on white space
            final var split = string.split("\\s+");
            if (split.length > 0) {
                switch (split[0]) {
                    case "extern":
                        // Only deal with font extern const
                        if (split.length == 5) {
                            final var font = split[3].split("\\[")[0];
                            code.add("    @JniField(flags = {CONSTANT})");
                            code.add(String.format("    public static long %s;", font));
                            if (font.startsWith("u8g2_font_")) {
                                final var name = font.replace("u8g2_", "");
                                enums.add(String.format("        %s,", name.toUpperCase()));
                                fontSwitch.add(String.format("            case %s:", name.toUpperCase()));
                                fontSwitch.add(String.format("                fontSel = Fonts.%s;",font));
                                fontSwitch.add("                break;");
                            }
                        }
                        break;
                    case "#define":
                        if (split.length == 3 || (split.length > 3 && split[3].contains("/*"))) {
                            // Pointer constant starts with (&
                            if (split[2].startsWith("(&")) {
                                code.add("    @JniField(flags = {CONSTANT})");
                                code.add(String.format("    public static long %s;", split[1]));
                                // int constant starts with 0..9 or "(", but not "(u8g2)"
                            } else if ((split[2].matches("^[0-9].*$") || split[2].startsWith("(")) && !split[2].contains("(u8g2)")) {
                                code.add("    @JniField(flags = {CONSTANT})");
                                code.add(String.format("    public static int %s;", split[1]));
                            }
                        }
                        break;
                }
            }
        }
        map.put(CONSTANTS, code);
        map.put(ENUMS, enums);
        map.put(FONT_SWITCH, fontSwitch);
        return map;
    }

    /**
     * Parse C functions and make the HawtJNI methods.
     *
     * @param header C header file.
     * @return Map of String Lists.
     */
    public Map<ListType, List<String>> generateMethods(final List<String> header) {
        final var map = new HashMap<ListType, List<String>>();
        final var code = new ArrayList<String>();
        final var enums = new ArrayList<String>();
        final var i2cSwitch = new ArrayList<String>();
        final var spiSwitch = new ArrayList<String>();
        for (var string : header) {
            final var term = string.indexOf(";");
            // Remove anything after line terminator
            if (term >= 0) {
                string = string.substring(0, term);
            }
            // Split on white space
            final var split = string.split("\\s+");
            if (split.length > 0) {
                if (typeMap.get(split[0]) != null) {
                    // Get just function name
                    final var function = split[1].split("\\(")[0];
                    String method = function.replace("u8g2_", "");
                    method = method.substring(0, 1).toLowerCase() + method.substring(1);
                    // Handle camelCase
                    if (method.contains("_")) {
                        method = camelCase(method);
                    }
                    // Make enums for full beffer setup functions
                    if (function.startsWith("u8g2_Setup_") && function.endsWith("_f")) {
                        final var enumStr = function.substring(0, function.length() - 2).replaceFirst("u8g2_Setup_", "").toUpperCase();
                        enums.add(String.format("        %s,", enumStr));
                        if (enumStr.contains("_I2C_")) {
                            i2cSwitch.add(String.format("            case %s:", enumStr));
                            i2cSwitch.add(String.format("                U8g2.%s(u8g2, rotation, byteCb, gpioAndDelayCb);", method));
                            i2cSwitch.add("                break;");
                        } else {
                            spiSwitch.add(String.format("            case %s:", enumStr));
                            spiSwitch.add(String.format("                U8g2.%s(u8g2, rotation, byteCb, gpioAndDelayCb);", method));
                            spiSwitch.add("                break;");
                        }
                    }
                    // Grab all arguments of function
                    final var leftParen = string.indexOf("(");
                    final var rightParen = string.lastIndexOf(")");
                    final var allArgs = string.substring(leftParen + 1, rightParen);
                    var args = allArgs.trim().split(",");
                    // Only one arg
                    if (args.length == 0) {
                        args = new String[1];
                        args[0] = allArgs;
                    }
                    String javaArgs = "";
                    // Map args
                    for (int i = 0; i < args.length; i++) {
                        final var typeList = new ArrayList<String>();
                        typeList.addAll(Arrays.asList(args[i].trim().split("\\s+")));
                        // Remove const, etc.
                        while (typeList.size() > 2) {
                            typeList.remove(0);
                        }
                        // Check for type and name
                        if (typeList.size() == 2) {
                            // Handle pointer reference except for char
                            if (typeList.get(1).contains("*") && !typeList.get(0).equals("char")) {
                                javaArgs += String.format("final long %s", camelCase(typeList.get(1).trim().replace(")", "").
                                        replace("*", "")));
                            } else {
                                // Use map to determine type
                                javaArgs += String.format("final %s %s", typeMap.get(typeList.get(0).trim()), camelCase(
                                        typeList.get(1).trim().replace(")", "").replace("*", "")));
                            }
                            // If not last argument then add comma
                            if (i < args.length - 1) {
                                javaArgs += ", ";
                            }
                        } else if (typeList.get(0).equals("...")) {
                            // Handle ellipsis with pointer
                            javaArgs += String.format("%s %s", typeMap.get(typeList.get(0).trim()), "args");
                        }
                    }
                    // Filter out u8g2_d_memory.c stuff
                    if (!function.startsWith("*")) {
                        code.add(String.format("    @JniMethod(accessor = \"%s\")", function));
                        code.add(String.format("    public static native %s %s(%s);", typeMap.get(split[0]), method, javaArgs));
                    }
                }
            }
        }
        map.put(ListType.METHODS, code);
        map.put(ListType.ENUMS, enums);
        map.put(ListType.I2C_SWITCH, i2cSwitch);
        map.put(ListType.SPI_SWITCH, spiSwitch);
        return map;
    }

    /**
     * Generate code to stdout.
     *
     * @return Exit code.
     * @throws InterruptedException Possible exception.
     */
    @Override
    public Integer call() throws InterruptedException {
        var exitCode = 0;
        // Constant generation
        System.out.println("\n// u8x8.h constants\n\n");
        final var u8x8List = readFile(u8x8FileName);
        final var u8x8Constants = generateConstants(u8x8List);
        final var u8Constants = u8x8Constants.get(CONSTANTS);
        u8Constants.forEach(string -> {
            System.out.println(string);
        });
        final var u8g2List = readFile(u8g2FileName);
        final var u8g2Constants = generateConstants(u8g2List);
        final var u2Constants = u8g2Constants.get(CONSTANTS);
        final var fontEnums = u8g2Constants.get(ENUMS);
        final var fontSwitch = u8g2Constants.get(FONT_SWITCH);
        // These are in u8g2port.h
        u2Constants.add("    @JniField(flags = {CONSTANT})");
        u2Constants.add("    public static long u8x8_arm_linux_gpio_and_delay;");
        u2Constants.add("    @JniField(flags = {CONSTANT})");
        u2Constants.add("    public static long u8x8_byte_arm_linux_hw_i2c;");
        u2Constants.add("    @JniField(flags = {CONSTANT})");
        u2Constants.add("    public static long u8x8_byte_arm_linux_hw_spi;");
        // Not writing parser for these two
        u2Constants.add("    @JniField(flags = {CONSTANT})");
        u2Constants.add("    public static long u8x8_u8toa;");
        u2Constants.add("    @JniField(flags = {CONSTANT})");
        u2Constants.add("    public static long u8x8_u16toa;");
        System.out.println("\n// u8g2.h font enums\n\n");
        fontEnums.forEach(string -> {
            System.out.println(string);
        });
        System.out.println("\n// u8g2.h font switch\n\n");
        fontSwitch.forEach(string -> {
            System.out.println(string);
        });
        System.out.println("\n// u8g2.h constants\n\n");
        u2Constants.forEach(string -> {
            System.out.println(string);
        });
        // Method generation
        final var map = generateMethods(u8g2List);
        System.out.println("\n// u8g2.h methods\n\n");
        final var methods = map.get(METHODS);
        methods.forEach(string -> {
            System.out.println(string);
        });
        final var enums = map.get(ENUMS);
        System.out.println("\n// u8g2.h setup enums\n\n");
        enums.forEach(string -> {
            System.out.println(string);
        });
        final var i2c = map.get(I2C_SWITCH);
        System.out.println("\n// u8g2.h I2C setup switch\n\n");
        i2c.forEach(string -> {
            System.out.println(string);
        });
        final var spi = map.get(SPI_SWITCH);
        System.out.println("\n// u8g2.h SPI setup switch\n\n");
        spi.forEach(string -> {
            System.out.println(string);
        });
        return exitCode;
    }

    /**
     * Main parsing, error handling and handling user requests for usage help or
     * version help are done with one line of code.
     *
     * @param args Argument list.
     */
    public static void main(String... args) {
        System.exit(new CommandLine(new CodeGen()).execute(args));
    }
}
