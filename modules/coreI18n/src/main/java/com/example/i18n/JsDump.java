package com.example.i18n;

import com.example.common.JsonData; // Placeholder for JsObject
import java.util.List;

public interface JsDump {
    // The 'Translate' parameter for keysToObject was implicit in Scala.
    // In Java, it needs to be passed explicitly.
    JsonData keysToObject(List<I18nKey> keys, Translate translate);
}
