package pe.flyingcat.shizukubot.beans;

/**
 *
 * @author FlyingCat
 */
public class Settings {

    private String botToken;
    private String language;
    private String prefix;

    public Settings() {
    }

    public Settings(String botToken, String language, String prefix) {
        this.botToken = botToken;
        this.language = language;
        this.prefix = prefix;
    }

    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
