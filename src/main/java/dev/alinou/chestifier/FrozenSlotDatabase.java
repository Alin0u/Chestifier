package dev.alinou.chestifier;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class FrozenSlotDatabase {

    private static final boolean[] frozenSlots = new boolean[41];
    private static File storageFile;

    public static void init(File configDir) {
        storageFile = new File(configDir, "chestifier-frozen.properties");
        // Freeze hotbar slots 0-4 by default
        for (int i = 0; i < 5; i++) {
            frozenSlots[i] = true;
        }
        load();
    }

    private static void load() {
        if (storageFile == null || !storageFile.exists()) return;
        Properties props = new Properties();
        try (FileReader reader = new FileReader(storageFile)) {
            props.load(reader);
            for (int i = 0; i < frozenSlots.length; i++) {
                String val = props.getProperty("slot" + i);
                if (val != null) frozenSlots[i] = Boolean.parseBoolean(val);
            }
        } catch (IOException ignored) {}
    }

    public static void save() {
        if (storageFile == null) return;
        Properties props = new Properties();
        for (int i = 0; i < frozenSlots.length; i++) {
            props.setProperty("slot" + i, Boolean.toString(frozenSlots[i]));
        }
        try (FileWriter writer = new FileWriter(storageFile)) {
            props.store(writer, "Chestifier frozen slots");
        } catch (IOException ignored) {}
    }

    public static boolean isSlotFrozen(int slot) {
        if (slot < 0 || slot >= frozenSlots.length) return false;
        return frozenSlots[slot];
    }

    public static void setSlotFrozen(int slot, boolean frozen) {
        if (slot < 0 || slot >= frozenSlots.length) return;
        frozenSlots[slot] = frozen;
        save();
    }
}
