package com.example.i18n;

import com.example.common.play.PlayLang; // Placeholder
import com.example.common.play.PlayRequestHeader; // Placeholder
import com.example.scalalibmodel.Language; // Placeholder
import java.util.List;
import java.util.Optional;

public interface LangPicker {
    List<Language> preferedLanguages(PlayRequestHeader req, PlayLang prefLang);
    PlayLang byStrOrDefault(Optional<String> str);
}
