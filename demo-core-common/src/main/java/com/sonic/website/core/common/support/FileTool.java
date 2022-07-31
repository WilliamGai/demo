package com.sonic.website.core.common.support;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by bao on 2017/7/20.
 */
public class FileTool {
    public static String readLine(String fileName) throws Exception {
        return new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);
    }

    /** readLine("xx.txt").split("[\\P{L}]+"); */
    public static List<String> readLines(String fileName) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        return lines;
    }

    public static void writeLine(String fileName, Iterable<? extends CharSequence> lines) throws IOException {
        Files.write(Paths.get(fileName), lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
    }

    public static void forEachLine(String fileName, Consumer<String> action) throws IOException {
        Files.lines(Paths.get(fileName), StandardCharsets.UTF_8).forEach(action);
    }

}
