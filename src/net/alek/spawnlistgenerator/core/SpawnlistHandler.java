package net.alek.spawnlistgenerator.core;

import net.alek.spawnlistgenerator.util.GUIHandler;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

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
                    GUIHandler.addModelEntry(file);
                }
            }
        }
    }

    public static void generateSpawnlist() {
        new Thread(() -> {
            File saveFile = GUIHandler.showSpawnlistSaveDialog();
            if (saveFile == null) return;

            String spawnlist = buildSpawnlist();

            try (FileWriter fw = new FileWriter(saveFile)) {
                fw.write(spawnlist);
                JOptionPane.showMessageDialog(GUIHandler.window, "Spawnlist saved:\n" + saveFile.getAbsolutePath());
            } catch (IOException e) {
                ErrorHandler.IOException();
            }
        }).start();
    }

    private static String buildSpawnlist() {
        StringBuilder sb = new StringBuilder();
        sb.append("\"TableToKeyValues\"\n{\n")
                .append("\t\"parentid\"\t\"").append(GUIHandler.parentIdField.getText().trim()).append("\"\n")
                .append("\t\"icon\"\t\"").append(GUIHandler.iconField.getText().trim()).append("\"\n")
                .append("\t\"id\"\t\"").append(GUIHandler.idField.getText().trim()).append("\"\n")
                .append("\t\"contents\"\n\t{\n");

        Map<String, List<String>> headerMap = new LinkedHashMap<>();
        for (int i = 0; i < GUIHandler.tableModel.getRowCount(); i++) {
            String path = GUIHandler.tableModel.getValueAt(i, 0).toString().replace("\\", "/").trim();
            String header = GUIHandler.tableModel.getValueAt(i, 1).toString().trim();

            if (path.isEmpty()) continue;

            int modelIndex = path.toLowerCase().indexOf("models/");
            String cleanPath = modelIndex >= 0 ? path.substring(modelIndex) : path;
            if (!cleanPath.toLowerCase().startsWith("models/")) {
                cleanPath = "models/" + cleanPath;
            }

            cleanPath = cleanPath.replaceAll("(?i)^models/models/", "models/");

            if (!header.isEmpty()) {
                headerMap.computeIfAbsent(header, k -> new ArrayList<>()).add(cleanPath);
            } else {
                headerMap.computeIfAbsent("__no_header__", k -> new ArrayList<>()).add(cleanPath);
            }
        }

        int index = 1;
        for (Map.Entry<String, List<String>> entry : headerMap.entrySet()) {
            if (!entry.getKey().equals("__no_header__")) {
                sb.append("\t\t\"").append(index++).append("\"\n\t\t{\n")
                        .append("\t\t\t\"type\"\t\"header\"\n")
                        .append("\t\t\t\"text\"\t\"").append(entry.getKey()).append("\"\n")
                        .append("\t\t}\n");
            }

            for (String path : entry.getValue()) {
                sb.append("\t\t\"").append(index++).append("\"\n\t\t{\n")
                        .append("\t\t\t\"type\"\t\"model\"\n")
                        .append("\t\t\t\"model\"\t\"").append(path).append("\"\n")
                        .append("\t\t}\n");
            }
        }

        sb.append("\t}\n")
                .append("\t\"name\"\t\"").append(GUIHandler.nameField.getText().trim()).append("\"\n")
                .append("\t\"version\"\t\"3\"\n")
                .append("}");

        return sb.toString();
    }
}
