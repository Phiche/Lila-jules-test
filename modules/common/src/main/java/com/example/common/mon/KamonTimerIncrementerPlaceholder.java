package com.example.common.mon;

public class KamonTimerIncrementerPlaceholder {
    private final String name;
    public KamonTimerIncrementerPlaceholder(String name) { this.name = name; }
    public void increment() {
        System.out.println("[MONITOR INCREMENT] " + name);
    }
}
