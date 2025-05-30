package com.example.config;
import java.util.Optional;
public class HostPort {
    private final String host;
    private final int port;
    public HostPort(String host, int port) {
        this.host = host;
        this.port = port;
    }
    public String getHost() { return host; }
    public int getPort() { return port; }
    public String show() { return host + ":" + port; }
    public static Optional<HostPort> read(String str) {
        if (str == null) return Optional.empty();
        String[] parts = str.split(":", 2);
        if (parts.length == 2) {
            try {
                int portNum = Integer.parseInt(parts[1]);
                return Optional.of(new HostPort(parts[0], portNum));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}
