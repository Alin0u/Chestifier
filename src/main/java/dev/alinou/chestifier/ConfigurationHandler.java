package dev.alinou.chestifier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ConfigurationHandler {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Config config = new Config();
    private static File configFile;

    public static void load() {
        configFile = FabricLoader.getInstance().getConfigDir().resolve("chestifier.json").toFile();
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                Config loaded = GSON.fromJson(reader, Config.class);
                if (loaded != null) {
                    config = loaded;
                    // fill in any missing/invalid fields with defaults
                    if (!isValidHexColor(config.highlightColor)) config.highlightColor = "4000ff00";
                }
            } catch (Exception e) {
                Chestifier.LOGGER.warn("Failed to load config, backing up and using defaults: {}", e.getMessage());
                backupCorruptConfig();
                config = new Config();
            }
        }
    }

    private static void backupCorruptConfig() {
        try {
            Files.copy(configFile.toPath(), configFile.toPath().resolveSibling("chestifier.json.bak"),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Chestifier.LOGGER.warn("Failed to back up corrupt config: {}", e.getMessage());
        }
    }

    public static void save() {
        if (configFile == null) return;
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            Chestifier.LOGGER.warn("Failed to save config: {}", e.getMessage());
        }
    }

    public static boolean allowExtraLargeChests()   { return config.extraLargeChests; }
    public static void setAllowExtraLargeChests(boolean v) { config.extraLargeChests = v; save(); }

    public static boolean halfSizeButtons()          { return config.halfSizeButtons; }
    public static void setHalfSizeButtons(boolean v) { config.halfSizeButtons = v; save(); }

    public static boolean toneDownButtons()          { return config.toneDownButtons; }
    public static void setToneDownButtons(boolean v) { config.toneDownButtons = v; save(); }

    public static boolean enableSearch()             { return config.enableSearch; }
    public static void setEnableSearch(boolean v)    { config.enableSearch = v; save(); }

    public static boolean enableRowButtons()         { return config.enableRowButtons; }
    public static void setEnableRowButtons(boolean v){ config.enableRowButtons = v; save(); }

    public static boolean enableColumnButtons()      { return config.enableColumnButtons; }
    public static void setEnableColumnButtons(boolean v){ config.enableColumnButtons = v; save(); }

    public static String getHighlightColor()         { return config.highlightColor; }
    public static void setHighlightColor(String v)   {
        if (!isValidHexColor(v)) return;
        config.highlightColor = v;
        save();
    }

    private static boolean isValidHexColor(String v) {
        return v != null && v.matches("[0-9a-fA-F]{1,8}");
    }

    public static void toggleSearchBox() {
        setEnableSearch(!config.enableSearch);
    }

    private static class Config {
        boolean extraLargeChests  = false;
        boolean halfSizeButtons   = false;
        boolean toneDownButtons   = true;
        boolean enableSearch      = true;
        boolean enableRowButtons  = true;
        boolean enableColumnButtons = true;
        String  highlightColor    = "4000ff00";
    }
}
