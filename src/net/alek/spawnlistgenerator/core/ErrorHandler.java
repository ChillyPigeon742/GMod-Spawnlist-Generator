package net.alek.spawnlistgenerator.core;

import net.alek.spawnlistgenerator.util.AssetsLoader;
import net.alek.spawnlistgenerator.util.GUIHandler;

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
        AssetsLoader.unload();

        Logger.Log.error("Unloading Themes...");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            throw new StackOverflowError();
        }
        GUIHandler.repaint();
        JFrame.getFrames()[0].dispose();

        JOptionPane.showOptionDialog(
                null,
                typeOfException + "/" + caller + "\n\nThe program has suffered an " + typeOfException + "!",
                "Error",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE,
                UIManager.getIcon("OptionPane.errorIcon"),
                new Object[]{"OK"},
                "OK"
        );
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