package com.borowiec.kamil.data.nodes;

import com.borowiec.kamil.data.figures.Segment;

public class YNode extends Node {
    private Segment data;
    
    public YNode(Segment s) {
        super();
        data = s;
    }

    public Segment getData() {
        return data;
    }
}
