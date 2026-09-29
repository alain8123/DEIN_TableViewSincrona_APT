package alain.holamundo.table.util;

import java.util.Locale;
import java.util.ResourceBundle;

public final class I18n {

    private static final String BASE_NAME = "alain.holamundo.table.i18n.messages";
    private static Locale locale = Locale.forLanguageTag("es");

    private I18n() {}

    public static ResourceBundle bundle() {
        return ResourceBundle.getBundle(BASE_NAME, locale);
    }

    public static void setLocale(String languageTag) {
        locale = Locale.forLanguageTag(languageTag);
        // Hace que el DatePicker (y otros formatos) también usen el idioma elegido
        Locale.setDefault(Locale.Category.FORMAT, locale);
    }

    public static Locale getLocale() {
        return locale;
    }
}