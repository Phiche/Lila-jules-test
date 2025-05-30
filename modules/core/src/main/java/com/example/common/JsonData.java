package com.example.common;

import java.util.Map; // Example representation

/**
 * Placeholder for Play JSON's JsObject / JsValue.
 * Actual implementation will depend on the chosen JSON library for Java.
 * This could be Jackson's JsonNode, Gson's JsonElement, or a simple Map.
 */
public class JsonData {
    private final Object value; // Could be Map<String, Object> for JsObject, or any type for JsValue

    public JsonData(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public static JsonData fromMap(Map<String, Object> map) {
        return new JsonData(map);
    }
    // Add methods to get as String, Int, navigate, etc., as needed
}
