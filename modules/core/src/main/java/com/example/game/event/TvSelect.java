package com.example.game.event;
import com.example.common.GameId;
import com.example.chess.Speed;
import com.example.common.JsonData; // Placeholder for JsObject
public class TvSelect {
    private final GameId gameId;
    private final Speed speed;
    private final String channel;
    private final JsonData data; // Was JsObject
    public TvSelect(GameId gameId, Speed speed, String channel, JsonData data) {
        this.gameId = gameId;
        this.speed = speed;
        this.channel = channel;
        this.data = data;
    }
    public GameId getGameId() { return gameId; }
    public Speed getSpeed() { return speed; }
    public String getChannel() { return channel; }
    public JsonData getData() { return data; }
}
