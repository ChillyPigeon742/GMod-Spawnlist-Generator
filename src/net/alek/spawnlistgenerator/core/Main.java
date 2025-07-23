package net.alek.spawnlistgenerator.core;

import com.formdev.flatlaf.FlatDarculaLaf;
import net.alek.spawnlistgenerator.util.AssetsLoader;
import net.alek.spawnlistgenerator.util.InstallPathFinder;
import net.alek.spawnlistgenerator.util.GUIHandler;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
    public static String version = "null";

    public static void main(String[] args) {
        String resourcePath = "/assets/config/Maven.properties";
        try (InputStream in = Main.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                System.err.println("Resource not found: " + resourcePath);
            } else {
                Properties props = new Properties();
                props.load(in);
                version = props.getProperty("app.version", "null");
            }
        } catch (IOException e) {
            System.err.println("Failed to read version tag!");
            System.err.println(e.getMessage() != null ? e.getMessage() : e.toString());
        }

        Logger.Log.info("Starting App... (Version "+version+")");

        AssetsLoader.load();
        InstallPathFinder.findGModInstallPath();

        SwingUtilities.invokeLater(() -> {
            FlatDarculaLaf.setup();
            GUIHandler.setupGUI();
            GUIHandler.repaint();
        });
    }
}