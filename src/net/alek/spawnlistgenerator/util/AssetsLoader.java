package net.alek.spawnlistgenerator.util;

import net.alek.spawnlistgenerator.core.ErrorHandler;
import net.alek.spawnlistgenerator.core.Logger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;

public class AssetsLoader {
    private static ImageIcon appIcon;
    private static ImageIcon missing;
    private static byte[] clickData;
    private static byte[] hoverData;

    public static void load() {
        Logger.Log.info("Loading assets...");

        Logger.Log.info("Loading images...");
        appIcon = new ImageIcon(Objects.requireNonNull(AssetsLoader.class.getResource("/assets/image/icon.png")));
        missing = new ImageIcon(Objects.requireNonNull(AssetsLoader.class.getResource("/assets/image/missing.png")));

        Logger.Log.info("Loading audio data...");
        try {
            clickData = Objects.requireNonNull(AssetsLoader.class.getResourceAsStream("/assets/audio/ui_click.wav")).readAllBytes();
            hoverData = Objects.requireNonNull(AssetsLoader.class.getResourceAsStream("/assets/audio/ui_hover.wav")).readAllBytes();
        } catch (IOException e) {
            ErrorHandler.IOException();
        }
    }

    public static void unload() {
        appIcon = null;
        clickData = null;
        hoverData = null;

        System.gc();
    }

    public static ImageIcon getAppIcon() {
        return appIcon;
    }

    public static ImageIcon getMissingIcon() {
        return missing;
    }

    public static AudioInputStream getClick() {
        try {
            return AudioSystem.getAudioInputStream(new ByteArrayInputStream(clickData));
        } catch (UnsupportedAudioFileException | IOException e) {
            ErrorHandler.IOException();
            return null;
        }
    }

    public static AudioInputStream getHover() {
        try {
            return AudioSystem.getAudioInputStream(new ByteArrayInputStream(hoverData));
        } catch (UnsupportedAudioFileException | IOException e) {
            ErrorHandler.IOException();
            return null;
        }
    }
}