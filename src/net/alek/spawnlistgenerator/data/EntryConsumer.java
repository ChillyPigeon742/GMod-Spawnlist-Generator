package net.alek.spawnlistgenerator.data;

@FunctionalInterface
public interface EntryConsumer {
    void accept(String model, String header);
}