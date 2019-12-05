package com.borowiec.kamil.data.nodes;

import com.borowiec.kamil.data.figures.Trapezoid;

public class Leaf extends Node {
    private Trapezoid data;
    
    public Leaf(Trapezoid t) {
        super();
        data = t;
        t.setLeaf(this);
    }

    public Trapezoid getData() {
        return data;
    }
}
