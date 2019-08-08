package pe.flyingcat.shizukubot.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author FlyingCat
 */
public class Multilanguage {

    private Locale lang;
    private List<String> listLangs;

    public Multilanguage(String locale) {
        this.lang = new Locale(locale);
        listLangs = new ArrayList<>();
    }

    public Locale getLang() {
        return lang;
    }

    public void setLang(Locale lang) {
        this.lang = lang;
    }

    public String getMessage(String key) {
        return ResourceBundle.getBundle("lang/Messages", lang).getString(key);
    }

    public List<String> getMessagesAvailables() {
        String[] langs = Locale.getISOLanguages();
        listLangs.clear();
        for (String l : langs) {
            URL rb = ClassLoader.getSystemResource("lang/Messages_" + l + ".properties");
            if (rb != null) {
                listLangs.add(rb.toString().split("lang/Messages_")[1].split(".properties")[0]);
            }
        }
        return listLangs;
    }

    public boolean isRegistered(String shortName) {
        return Arrays.stream(getMessagesAvailables().toArray()).anyMatch(shortName::equals);
    }

    public String toLongName(String shortName) {
        return new Locale(shortName).getDisplayName(lang);
    }

}
