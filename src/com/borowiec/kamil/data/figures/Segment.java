package com.borowiec.kamil.data.figures;

import java.awt.geom.Line2D;

public class Segment {
    private Point leftEndpoint;
    private Point rightEndpoint;
    private Line2D.Double line;

    public Segment(Point p1, Point p2) {
        leftEndpoint = p1.compareTo(p2) <= 0 ? p1 : p2;
        rightEndpoint = p1.compareTo(p2) <= 0 ? p2: p1;
        line = new Line2D.Double(leftEndpoint.getX(), leftEndpoint.getY(), rightEndpoint.getX(), rightEndpoint.getY());
    }

    public Point getLeftEndPoint() {
        return leftEndpoint;
    }

    public Point getRightEndPoint() {
        return rightEndpoint;
    }

    public int getMinX() {
        return leftEndpoint.getX();
    }

    public int getMaxX() {
        return rightEndpoint.getX();
    }

    public int getMinY() {
        return Math.min(leftEndpoint.getY(), rightEndpoint.getY());
    }

    public int getMaxY() {
        return Math.max(leftEndpoint.getY(), rightEndpoint.getY());
    }

    public Line2D.Double getLine() {
        return line;
    }

    @Override
    public boolean equals(Object s) {
        if (!(s instanceof Segment)) {
            return false;
        }
        Segment seg = (Segment) s;
        return seg.leftEndpoint.equals(this.leftEndpoint) && seg.rightEndpoint.equals(this.rightEndpoint);
    }

    public Point intersect(int x) {
        if (leftEndpoint.getX() != rightEndpoint.getX()) {
            long sum = ((long) (x - leftEndpoint.getX())) * ((long) rightEndpoint.getY()) +
                    ((long) (rightEndpoint.getX() - x)) * ((long) leftEndpoint.getY());
            double y = (double)sum / (rightEndpoint.getX() - leftEndpoint.getX());
            return new Point(x, (int) y);
        } else {
            return new Point(leftEndpoint.getX(), leftEndpoint.getY());
        }
    }

    private double getA() {
        if (isVertical()) {
            return 0;
        }
        return (rightEndpoint.getY() - leftEndpoint.getY()) / ((double) (rightEndpoint.getX() - leftEndpoint.getX()));
    }

    private boolean isVertical() {
        return (rightEndpoint.getX() == leftEndpoint.getX());
    }

    public boolean doInterselect(Segment seg) {
        if (seg.leftEndpoint.getX() > this.rightEndpoint.getX() || seg.rightEndpoint.getX() < this.leftEndpoint.getX()) {
            return false;
        }

        if (this.isVertical() && seg.isVertical()) {
            return this.getMaxY() > seg.getMinY() && this.getMinY() < seg.getMaxY();
        } else if (this.isVertical()) {
            Point p = seg.intersect(this.leftEndpoint.getX());
            return (p.getY() > this.getMinY()) && (p.getY() < this.getMaxY());
        } else {
            double a1 = this.getA();
            double a2 = seg.getA();
            double b00 = this.leftEndpoint.getY() - this.leftEndpoint.getX() * a1;
            double b01 = seg.leftEndpoint.getY() - seg.leftEndpoint.getX() * a1;
            double b02 = seg.rightEndpoint.getY() - seg.rightEndpoint.getX() * a1;
            double b10 = seg.leftEndpoint.getY() - seg.leftEndpoint.getX() * a2;
            double b11 = this.leftEndpoint.getY() - this.leftEndpoint.getX() * a2;
            double b12 = this.rightEndpoint.getY() - this.rightEndpoint.getX() * a2;
            if (((b01 <= b00 && b00 <= b02) || (b01 >= b00 && b00 >= b02))
                    && ((b11 <= b10 && b10 <= b12) || b11 >= b10 && b10 >= b12)) {
                return this.equals(seg)
                        || !(this.leftEndpoint.equals(seg.leftEndpoint)
                        || this.leftEndpoint.equals(seg.rightEndpoint)
                        || this.rightEndpoint.equals(seg.leftEndpoint)
                        || this.rightEndpoint.equals(seg.rightEndpoint));
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return leftEndpoint + "  " + rightEndpoint;
    }
}
