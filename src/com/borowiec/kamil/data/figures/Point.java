package com.borowiec.kamil.data.figures;

public class Point implements Comparable<Point> {
    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    @Override
    public int compareTo(Point p) {
        if (p == null) {
            return 1;
        }
        if (this.x < p.x || (this.x == p.x && this.y < p.y)) {
            return -1;
        } else if ((this.x == p.x) && (this.y == p.y)) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public boolean equals(Object p) {
        if (!(p instanceof Point)) {
            return false;
        }
        Point pp = (Point) p;
        return (this.x == pp.x) && (this.y == pp.y);
    }

    @Override
    public String toString() {
        return x + "  " + y;
    }
}
