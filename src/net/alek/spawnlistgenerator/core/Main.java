package net.alek.spawnlistgenerator.core;

import com.formdev.flatlaf.FlatDarculaLaf;
import net.alek.spawnlistgenerator.util.AssetsLoader;
import net.alek.spawnlistgenerator.util.RegistryReader;
import net.alek.spawnlistgenerator.util.GUIHandler;

import javax.swing.*;

public class Main {
    public static final String VERSION = "1.1.0";

    public static void main(String[] args) {
        Logger.Log.info("Starting App... (Version "+VERSION+")");

        AssetsLoader.load();
        RegistryReader.findGModInstallPath();

        SwingUtilities.invokeLater(() -> {
            FlatDarculaLaf.setup();
            GUIHandler.setupGUI();
            GUIHandler.repaint();
        });
    }
}