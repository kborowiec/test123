package com.borowiec.kamil.data.figures;

import com.borowiec.kamil.data.nodes.Leaf;

import java.awt.Polygon;

public final class Trapezoid {
    private Trapezoid upperLeftNeighbour;
    private Trapezoid lowerLeftNeighbour;
    private Trapezoid upperRightNeighbour;
    private Trapezoid lowerRightNeighbour;
    private Leaf leaf;

    private Point leftPoint;
    private Point rightPoint;
    private Segment topSegment;
    private Segment bottomSegment;
    private Polygon polygon;

    public Trapezoid(Point left, Point right, Segment top, Segment bottom) {
        leftPoint = left;
        rightPoint = right;
        topSegment = top;
        bottomSegment = bottom;

        upperLeftNeighbour = lowerLeftNeighbour = upperRightNeighbour = lowerRightNeighbour = null;
        leaf = null;
        if (left != null && right != null) {
            polygon = this.toBoundaryPolygon(left, right, top, bottom);
        }
    }

    private Polygon toBoundaryPolygon(Point left, Point right, Segment top, Segment bottom) {
        Point tl = top.intersect(left.getX());
        Point tr = top.intersect(right.getX());
        Point bl = bottom.intersect(left.getX());
        Point br = bottom.intersect(right.getX());
        int[] xs = {tl.getX(), tr.getX(), br.getX(), bl.getX()};
        int[] ys = {tl.getY(), tr.getY(), br.getY(), bl.getY()};
        return new Polygon(xs, ys, 4);
    }

    public Polygon getBoundaryPolygon() {
        return polygon;
    }

    public boolean hasZeroWidth() {
        return leftPoint.equals(rightPoint);
    }

    public Point getLeftBound() {
        return leftPoint;
    }

    public Point getRightBound() {
        return rightPoint;
    }

    public Segment getLowerBound() {
        return bottomSegment;
    }

    public Segment getUpperBound() {
        return topSegment;
    }

    public Trapezoid getLowerLeftNeighbor() {
        return lowerLeftNeighbour;
    }

    public Trapezoid getUpperLeftNeighbor() {
        return upperLeftNeighbour;
    }

    public Trapezoid getLowerRightNeighbor() {
        return lowerRightNeighbour;
    }

    public Trapezoid getUpperRightNeighbor() {
        return upperRightNeighbour;
    }

    public void setLowerLeftNeighbor(Trapezoid t) {
        lowerLeftNeighbour = t;
    }

    public void setUpperLeftNeighbor(Trapezoid t) {
        upperLeftNeighbour = t;
    }

    public void setLowerRightNeighbor(Trapezoid t) {
        lowerRightNeighbour = t;
    }

    public void setUpperRightNeighbor(Trapezoid t) {
        upperRightNeighbour = t;
    }

    public void setLeaf(Leaf l) {
        leaf = l;
    }

    public Leaf getLeaf() {
        return leaf;
    }

    @Override
    public boolean equals(Object t) {
        if (!(t instanceof Trapezoid)) {
            return false;
        }
        Trapezoid trapezoid = (Trapezoid) t;
        return (this.topSegment == trapezoid.topSegment) && (this.bottomSegment == trapezoid.bottomSegment);
    }
}
