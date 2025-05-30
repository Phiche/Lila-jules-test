package com.example.chess;
// Basic placeholder
public interface Opening { // Could be an interface or abstract class
    String getName();
    // Nested AtPly class
    class AtPly implements Opening {
        private String name;
        public AtPly(String name) { this.name = name; }
        @Override public String getName() { return name; }
    }
}
