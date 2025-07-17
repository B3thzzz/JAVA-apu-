package src;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class FileManager {
    public static List<String> readAllLines(String filename) {
        try {
            return Files.readAllLines(Paths.get(filename));
        } catch (IOException e) {
            System.out.println("Error reading file: " + filename);
            return new ArrayList<>();
        }
    }

    public static void writeAllLines(String filename, List<String> lines) {
        try {
            Files.write(Paths.get(filename), lines);
        } catch (IOException e) {
            System.out.println("Error writing file: " + filename);
        }
    }

    public static void appendLine(String filename, String line) {
        try {
            Files.write(Paths.get(filename), Arrays.asList(line), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Error appending to file: " + filename);
        }
    }
} 