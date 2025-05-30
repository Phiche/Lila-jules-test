package com.example.game.event;
import com.example.common.UserId;
import com.example.game.Pov; // Placeholder
public class CorresAlarmEvent {
    private final UserId userId;
    private final Pov pov;
    private final String opponent; // Name of the opponent
    public CorresAlarmEvent(UserId userId, Pov pov, String opponent) {
        this.userId = userId;
        this.pov = pov;
        this.opponent = opponent;
    }
    public UserId getUserId() { return userId; }
    public Pov getPov() { return pov; }
    public String getOpponent() { return opponent; }
}
