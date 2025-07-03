package net.alek.spawnlistgenerator.core;

import net.alek.spawnlistgenerator.util.initGUI;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class SpawnlistHandler {
    public static String capitalize(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public static void addModelsFromFolderDeep(File folder) {
        Queue<File> queue = new LinkedList<>();
        queue.add(folder);
        int maxDepth = 10;

        while (!queue.isEmpty() && maxDepth-- > 0) {
            File current = queue.poll();
            File[] files = current.listFiles();
            if (files == null) continue;

            for (File file : files) {
                if (file.isDirectory()){
                    queue.add(file);
                }else if (file.getName().toLowerCase().endsWith(".mdl")){
                    initGUI.addModelEntry(file);
                }
            }
        }
    }

    public static void generateSpawnlist() {
        if (Stream.of(initGUI.nameField, initGUI.idField, initGUI.iconField, initGUI.parentIdField).anyMatch(f -> f.getText().trim().isEmpty())
                || initGUI.tableModel.getRowCount() == 0) {
            new Thread(() -> JOptionPane.showMessageDialog(initGUI.window, "Fill in all fields and add at least one model.")).start();
            return;
        }

        new Thread(() -> {
            File saveFile = initGUI.showSpawnlistSaveDialog();
            if (saveFile == null) return;

            String spawnlist = buildSpawnlist();

            try (FileWriter fw = new FileWriter(saveFile)) {
                fw.write(spawnlist);
                JOptionPane.showMessageDialog(initGUI.window, "Spawnlist saved:\n" + saveFile.getAbsolutePath());
            } catch (IOException e) {
                ErrorHandler.IOException();
            }
        }).start();
    }

    private static String buildSpawnlist() {
        StringBuilder sb = new StringBuilder();
        sb.append("\"TableToKeyValues\"\n{\n")
                .append("\t\"parentid\"\t\"").append(initGUI.parentIdField.getText().trim()).append("\"\n")
                .append("\t\"icon\"\t\"").append(initGUI.iconField.getText().trim()).append("\"\n")
                .append("\t\"id\"\t\"").append(initGUI.idField.getText().trim()).append("\"\n")
                .append("\t\"contents\"\n\t{\n");

        Map<String, List<String>> headerMap = new LinkedHashMap<>();
        for (int i = 0; i < initGUI.tableModel.getRowCount(); i++) {
            String path = initGUI.tableModel.getValueAt(i, 0).toString().replace("\\", "/");
            String header = initGUI.tableModel.getValueAt(i, 1).toString().trim();
            header = header.isEmpty() ? "Models" : header;
            headerMap.computeIfAbsent(header, k -> new ArrayList<>()).add(path);
        }

        int index = 1;
        for (Map.Entry<String, List<String>> entry : headerMap.entrySet()) {
            sb.append("\t\t\"").append(index++).append("\"\n\t\t{\n")
                    .append("\t\t\t\"type\"\t\"header\"\n")
                    .append("\t\t\t\"text\"\t\"").append(entry.getKey()).append("\"\n")
                    .append("\t\t}\n");

            for (String path : entry.getValue()) {
                int modelIndex = path.toLowerCase().indexOf("models/");
                String relativePath = modelIndex >= 0 ? path.substring(modelIndex) : path;
                sb.append("\t\t\"").append(index++).append("\"\n\t\t{\n")
                        .append("\t\t\t\"type\"\t\"model\"\n")
                        .append("\t\t\t\"model\"\t\"").append(relativePath).append("\"\n")
                        .append("\t\t}\n");
            }
        }

        sb.append("\t}\n")
                .append("\t\"name\"\t\"").append(initGUI.nameField.getText().trim()).append("\"\n")
                .append("\t\"version\"\t\"3\"\n")
                .append("}");

        return sb.toString();
    }
}
