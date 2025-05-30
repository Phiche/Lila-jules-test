package com.example.i18n; // Using a new package for the translated coreI18n files

import com.example.common.play.PlayLang; // Placeholder
import com.example.scalalibmodel.Language; // Placeholder

public final class I18nConstants {
    private I18nConstants() {}

    public static final int MAX_LANGS = 128;
    public static final Language DEFAULT_LANGUAGE = new Language("en");
    public static final PlayLang EN_LANG = new PlayLang("en", "GB"); // Corresponds to Lang("en", "GB")
    public static final PlayLang DEFAULT_LANG = EN_LANG;
}
