package net.alek.spawnlistgenerator.util;

import net.alek.spawnlistgenerator.core.ErrorHandler;
import net.alek.spawnlistgenerator.core.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InstallPathFinder {
    public static String installPath;

    public static void findGModInstallPath() {
        Logger.Log.info("Determining GMod installation path...");

        String steamPath = getSteamInstallPath();
        if (steamPath == null) {
            Logger.Log.error("Steam installation path not found.");
            ErrorHandler.LineUnavailableException();
            return;
        }

        Path gmodDefaultPath = Path.of(steamPath, "steamapps", "common", "GarrysMod");
        if (Files.exists(gmodDefaultPath)) {
            installPath = gmodDefaultPath.toString();
            GUIHandler.gmodPathField.setText(installPath);
            return;
        }

        Path libraryFoldersFile = Path.of(steamPath, "steamapps", "libraryfolders.vdf");
        if (Files.exists(libraryFoldersFile)) {
            try (BufferedReader reader = Files.newBufferedReader(libraryFoldersFile)) {
                Pattern pattern = Pattern.compile("^\\s*\"\\d+\"\\s*\"(.+?)\"\\s*$");
                String line;
                while ((line = reader.readLine()) != null) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        String libraryPath = matcher.group(1).replaceAll("\\\\\\\\", "\\\\");
                        Path potentialGmodPath = Path.of(libraryPath, "steamapps", "common", "GarrysMod");
                        if (Files.exists(potentialGmodPath)) {
                            installPath = potentialGmodPath.toString();
                            GUIHandler.gmodPathField.setText(installPath);
                            return;
                        }
                    }
                }
            } catch (IOException e) {
                ErrorHandler.IOException();
            }
        }

        Logger.Log.error("Garry's Mod installation path not found.");
        ErrorHandler.IOException();
    }

    private static String getSteamInstallPath() {
        String[] regKeys = {
                "HKLM\\SOFTWARE\\Valve\\Steam",
                "HKLM\\SOFTWARE\\WOW6432Node\\Valve\\Steam"
        };

        for (String regKey : regKeys) {
            try {
                ProcessBuilder builder = new ProcessBuilder("reg", "query", regKey, "/v", "InstallPath");
                builder.redirectErrorStream(true);
                Process process = builder.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("InstallPath")) {
                            String[] parts = line.trim().split("\\s{4,}");
                            if (parts.length >= 3) {
                                return parts[2].trim();
                            }
                        }
                    }
                }

                process.waitFor();
            } catch (IOException e) {
                ErrorHandler.IOException();
            } catch (InterruptedException e) {
                ErrorHandler.InterruptedException();
                Thread.currentThread().interrupt();
            }
        }

        return null;
    }
}