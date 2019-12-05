package com.borowiec.kamil.data.nodes;

import com.borowiec.kamil.data.figures.Point;

public class XNode extends Node {
    private Point data;
    
    public XNode(Point p) {
        super();
        data = p;
    }

    public Point getData() {
        return data;
    }
}
