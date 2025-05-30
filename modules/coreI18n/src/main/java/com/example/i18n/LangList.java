package com.example.i18n;

import com.example.common.play.PlayFormMapping; // Placeholder
import com.example.common.play.PlayLang; // Placeholder
import com.example.scalalibmodel.Language; // Placeholder
import com.example.scalalibmodel.LangTag; // Placeholder
import com.example.common.Pair; // Assuming Pair exists in com.example.common
import java.util.List;
import java.util.Map;

public interface LangList {
    Map<PlayLang, String> getAll(); // Was Map[Lang, String]
    List<Language> getAllLanguages();
    List<Language> getPopularLanguages();
    List<PlayLang> getPopularNoRegion(); // Was List[Lang]
    String getNameByLanguage(Language l);
    String getName(PlayLang tag); // Was name(tag: Lang)
    String getName(LangTag tag);  // Was name(tag: LangTag)

    interface LangForm {
        List<Pair<Language, String>> getChoices(); // Pair from com.example.common
        PlayFormMapping<Language> getMapping();
    }

    LangForm getAllLanguagesForm();
    LangForm getPopularLanguagesForm();
}
// Ensure com.example.common.Pair is available
