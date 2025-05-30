package com.example.common.http;
public class Crawler {
    private final boolean value;
    public Crawler(boolean value) { this.value = value; }
    public boolean isCrawler() { return value; }
    public boolean no() { return !value; } // For isHuman check
    public boolean yes() { return value; } // For isCrawler(req).yes
}
