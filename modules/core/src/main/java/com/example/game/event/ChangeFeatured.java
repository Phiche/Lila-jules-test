package com.example.game.event;
import com.example.common.JsonData; // Placeholder for JsObject
public class ChangeFeatured {
    private final JsonData mgs; // Was JsObject, meaning unclear, using raw JsonData
    public ChangeFeatured(JsonData mgs) { this.mgs = mgs; }
    public JsonData getMgs() { return mgs; }
}
