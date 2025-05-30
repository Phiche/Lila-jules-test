package com.example.i18n;

import com.example.common.play.PlayLang;
// import com.example.common.scalatags.RawFrag; // For commented out frag method
// import java.util.Arrays; // For commented out txt/frag methods

public class Translate {
    private final Translator translator;
    private final PlayLang lang;

    public Translate(Translator translator, PlayLang lang) {
        this.translator = translator;
        this.lang = lang;
    }

    public Translator getTranslator() { return translator; }
    public PlayLang getLang() { return lang; }

    // Methods from I18nKey that use Translate can be added here or in I18nKey itself if Translate is passed
    // For example, if I18nKey.txt(args...) was a thing:
    // public String txt(I18nKey key, Object... args) {
    //    return translator.getTxt().literal(key, Arrays.asList(args), lang);
    // }
    // public RawFrag frag(I18nKey key, Object... args) {
    //    return translator.getFrag().literal(key, Arrays.asList(args), lang);
    // }
}
