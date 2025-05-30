package com.example.i18n;

import com.example.common.play.PlayLang;

public interface Translator {
    TranslatorTxt getTxt();
    TranslatorFrag getFrag();
    Translate to(PlayLang lang);
    Translate toDefault();
}
