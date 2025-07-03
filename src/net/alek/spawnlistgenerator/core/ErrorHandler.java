package net.alek.spawnlistgenerator.core;

import net.alek.spawnlistgenerator.util.AssetsLoader;
import net.alek.spawnlistgenerator.util.initGUI;

import javax.swing.*;

public class ErrorHandler {
    public static String getCallerInfo() {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();
            if (!className.equals(ErrorHandler.class.getName())) {
                String simpleClassName = className.substring(className.lastIndexOf('.') + 1);
                return simpleClassName + ":" + element.getLineNumber();
            }
        }
        return "UnknownCaller";
    }

    private static void handleException(String typeOfException){
        String caller = getCallerInfo();

        Logger.Log.error(typeOfException+"/"+caller+"/The Program has Suffered an "+typeOfException+"!");

        Logger.Log.error("Unloading Assets...");
        initGUI.window.setIconImage(AssetsLoader.getMissingIcon().getImage());
        AssetsLoader.unload();

        Logger.Log.error("Unloading Themes...");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            throw new StackOverflowError();
        }
        SwingUtilities.updateComponentTreeUI(JFrame.getFrames()[0]);
        JFrame.getFrames()[0].dispose();

        JOptionPane.showMessageDialog(null, typeOfException+"/"+caller+"\n\n" +
                "The Program has Suffered an "+typeOfException+"!");
    }

    public static void IOException() {
        handleException("IOException");
    }

    public static void InterruptedException() {
        handleException("InterruptedException");
    }

    public static void UnsupportedFlavorException() {
        handleException("UnsupportedFlavorException");
    }

    public static void LineUnavailableException() {
        handleException("LineUnavailableException");
    }
}