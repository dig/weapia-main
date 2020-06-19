package net.sunken.common.util;

import lombok.Getter;

@Getter
public class Tuple<X, Y> {
    private final X x;
    private final Y y;

    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }
}