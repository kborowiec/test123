package com.borowiec.kamil.gui;

import com.borowiec.kamil.data.figures.Point;
import com.borowiec.kamil.data.figures.Segment;
import com.borowiec.kamil.data.figures.Trapezoid;

import java.util.ArrayList;
import java.util.List;

public class Scene {
    private List<Point> points;
    private List<Segment> segments;
    private List<Segment> trapezoids;

    public Scene(List<Point> points, List<Segment> segments, List<Trapezoid> trapezoids) {
        this.points = points;
        this.segments = segments;
        this.trapezoids = trapezoidsToSegments(trapezoids);
    }

    private List<Segment> trapezoidsToSegments(List<Trapezoid> trapezoids) {
        List<Segment> result = new ArrayList<>();
        for (Trapezoid trapezoid : trapezoids) {
            result.add(trapezoid.getLowerBound());
            result.add(trapezoid.getUpperBound());
            result.add(new Segment(trapezoid.getLowerBound().getLeftEndPoint(), trapezoid.getUpperBound().getLeftEndPoint()));
            result.add(new Segment(trapezoid.getLowerBound().getRightEndPoint(), trapezoid.getUpperBound().getRightEndPoint()));
        }
        return result;
    }

    public List<Point> getPoints() {
        return points;
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public List<Segment> getTrapezoids() {
        return trapezoids;
    }
}
