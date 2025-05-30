package com.example.chess;
// Basic placeholder, Uci can be a complex hierarchy (Move, Drop)
public interface Uci {
    // Placeholder, specific methods would depend on Uci.Move or Uci.Drop
    String getKeys(); // For Uci.Move
    Square getSquare(); // For Uci.Drop

    // Nested placeholder classes for Move and Drop if needed for type discrimination
    class Move implements Uci {
        private String keys;
        public Move(String orig, String dest, String promotion) { this.keys = orig + dest + (promotion != null ? promotion : "");}
        @Override public String getKeys() { return keys; }
        @Override public Square getSquare() { return null; } // Not applicable for Move
    }
    class Drop implements Uci {
        private Square square;
        public Drop(String role, String squareKey) {this.square = new Square(squareKey);}
        @Override public String getKeys() { return null; } // Not applicable for Drop
        @Override public Square getSquare() { return square; }
    }
}
