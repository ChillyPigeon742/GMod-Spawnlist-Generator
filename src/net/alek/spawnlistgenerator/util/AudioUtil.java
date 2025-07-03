package net.alek.spawnlistgenerator.util;

import net.alek.spawnlistgenerator.core.ErrorHandler;

import javax.sound.sampled.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.function.Supplier;

public class AudioUtil {
    public static Thread audioThread(Supplier<AudioInputStream> streamSupplier){
        return new Thread(() -> {
            try (AudioInputStream audioInputStream = streamSupplier.get()) {
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();

                clip.addLineListener(event -> {
                    if(event.getType()==LineEvent.Type.STOP){
                        clip.close();
                        clip.drain();
                        clip.flush();
                        System.gc();
                    }
                });
            } catch (LineUnavailableException ex) {
                ErrorHandler.LineUnavailableException();
            } catch (IOException ex) {
                ErrorHandler.IOException();
            }
        });
    }

    public static MouseAdapter buttonSounds(){
        return new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                audioThread(AssetsLoader::getClick).start();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                audioThread(AssetsLoader::getHover).start();
            }
        };
    }
}