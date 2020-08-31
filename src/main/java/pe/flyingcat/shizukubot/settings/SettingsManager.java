package pe.flyingcat.shizukubot.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.dv8tion.jda.api.JDA;
import pe.flyingcat.shizukubot.beans.Settings;
import pe.flyingcat.shizukubot.util.ApplicationExitCodes;
import pe.flyingcat.shizukubot.util.Multilanguage;

/**
 *
 * @author FlyingCat
 */
public class SettingsManager {

    private static SettingsManager instance = null;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Settings settings;
    private JDA discord;
    private final Path configFile = new File(".").toPath().resolve("config.json");
    private final Multilanguage multiLang;

    private SettingsManager() {
        // First run in english
        if (!configFile.toFile().exists()) {
            System.out.println("-- Default language is English. You can change later. --");
            System.out.println("Creating default settings.");
            System.out.println("You will need to edit the config.properties "
                    + "with your login information.");
            System.out.println("Default prefix: sh!");
            this.settings = getDefaultSettings();
            saveSettings();
            System.exit(ApplicationExitCodes.NEWLY_CREATED_CONFIG);
        }
        loadSettings();
        multiLang = new Multilanguage(settings.getLanguage());
    }

    private void loadSettings() {
        try {
            checkBadEscapes(configFile);
            try ( BufferedReader reader = Files.newBufferedReader(configFile, StandardCharsets.UTF_8)) {
                this.settings = gson.fromJson(reader, Settings.class);
            }
            checkOldSettingsFile();
        } catch (IOException e) {
            System.out.println(multiLang.getMessage("APP_SETTINGS_LOADED_ERR"));
        }
    }

    private void checkOldSettingsFile() {
        boolean modified = false;
        Settings defaults = getDefaultSettings();
        if (settings.getBotToken() == null) {
            settings.setBotToken(defaults.getBotToken());
            modified = true;
        }
        if (settings.getLanguage() == null) {
            settings.setLanguage(defaults.getLanguage());
        }
        if (settings.getPrefix() == null) {
            settings.setPrefix(defaults.getPrefix());
        }
        if (modified) {
            saveSettings();
        }
    }

    private Settings getDefaultSettings() {
        Settings newSettings = new Settings();
        newSettings.setBotToken("");
        newSettings.setLanguage("en");
        newSettings.setPrefix("sh!");
        return newSettings;
    }

    private void saveSettings() {
        String jsonOut = gson.toJson(this.settings);
        try {
            try ( BufferedWriter writer = Files.newBufferedWriter(configFile, StandardCharsets.UTF_8)) {
                writer.append(jsonOut);
            }
        } catch (IOException e) {
            e.getMessage();
        }
    }

    public void updateSettings(Settings settings) {
        String jsonOut = gson.toJson(settings);
        try {
            try ( BufferedWriter writer = Files.newBufferedWriter(configFile, StandardCharsets.UTF_8)) {
                writer.append(jsonOut);
            }
        } catch (IOException e) {
            e.getMessage();
        }
        this.settings = settings;
    }

    public static synchronized SettingsManager getInstance() {
        if (instance == null) {
            instance = new SettingsManager();
        }
        return instance;
    }

    public Settings getSettings() {
        return settings;
    }

    public static JDA discord() {
        return getInstance().discord;
    }

    public static void checkBadEscapes(Path filePath) throws IOException {
        final byte FORWARD_SOLIDUS = 47;
        final byte BACKWARDS_SOLIDUS = 92;

        boolean modified = false;
        byte[] bytes = Files.readAllBytes(filePath);
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == BACKWARDS_SOLIDUS) {
                modified = true;
                bytes[i] = FORWARD_SOLIDUS;
            }
        }
        if (modified) {
            Files.write(filePath, bytes);
        }
    }
}
