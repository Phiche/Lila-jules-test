package com.example.chess;
public enum Color { WHITE, BLACK;
    public Color unary_!(){ return this == WHITE ? BLACK : WHITE; } // For negation like !color
}
