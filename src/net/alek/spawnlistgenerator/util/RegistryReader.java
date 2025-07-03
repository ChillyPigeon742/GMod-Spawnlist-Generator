package net.alek.spawnlistgenerator.util;

import net.alek.spawnlistgenerator.core.ErrorHandler;
import net.alek.spawnlistgenerator.core.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RegistryReader {
    public static String installPath;

    public static void findGModInstallPath() {
        Logger.Log.info("Determining GMod installation path...");

        String[] registryPaths = {
                "HKLM\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Steam App 4000",
                "HKLM\\SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Steam App 4000"
        };

        for (String regPath : registryPaths) {
            installPath = queryRegistryForInstallLocation(regPath);
            if (installPath != null) {
                initGUI.gmodPathField.setText(installPath);
                break;
            }
        }
    }

    private static String queryRegistryForInstallLocation(String regKey) {
        try {
            ProcessBuilder builder = new ProcessBuilder("reg", "query", regKey, "/v", "InstallLocation");
            builder.redirectErrorStream(true);
            Process process = builder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("InstallLocation")) {
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

        return null;
    }
}