package com.example.i18n;

import com.example.common.play.PlayLang;
import com.example.common.scalatags.RawFrag; // Placeholder
import java.util.List;

public interface TranslatorFrag {
    // Seq[Matchable] -> List<Object>; Matchable is any type in Scala 3
    RawFrag literal(I18nKey key, List<Object> args, PlayLang lang);
    RawFrag plural(I18nKey key, long count, List<Object> args, PlayLang lang);
}
