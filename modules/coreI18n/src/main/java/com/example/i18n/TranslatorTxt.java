package com.example.i18n;

import com.example.common.play.PlayLang;
import java.util.List;

public interface TranslatorTxt {
    String literal(I18nKey key, List<Object> args, PlayLang lang); // Seq[Any] -> List<Object>
    String plural(I18nKey key, long count, List<Object> args, PlayLang lang); // Count is long
}
