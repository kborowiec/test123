package com.borowiec.kamil.gui;

import com.borowiec.kamil.data.SearchingTree;
import com.borowiec.kamil.data.figures.Point;
import com.borowiec.kamil.data.figures.Segment;
import com.borowiec.kamil.data.figures.Trapezoid;
import com.borowiec.kamil.data.nodes.Node;
import com.borowiec.kamil.data.nodes.XNode;
import com.borowiec.kamil.data.nodes.YNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

public class DisplayPanel extends JPanel implements Runnable {

    private static final int DELAY = 500;
    private int xPos, yPos;
    private Segment[] segments;
    private SearchingTree searchingTree;

    private boolean showSegments = true;
    private boolean findPoint = false;

    private List<Point> pointsCollection = null;
    private Color pointsColor = Color.magenta;

    private List<Segment> linesCollection = null;
    private Color linesColor = Color.yellow;

    private List<Segment> trapezoidLinesCollection = null;
    private Color trapezoidColor = Color.red;

    private Trapezoid result = null;

    public DisplayPanel(Segment[] segments, SearchingTree searchingTree) {
        this.segments = segments;
        this.searchingTree = searchingTree;
        setBackground(Color.lightGray);
        setDoubleBuffered(true);
        MouseAdapter m = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                xPos = e.getX();
                yPos = e.getY();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                xPos = e.getX();
                yPos = e.getY();
                if (!findPoint) {
                    if (result == null) {
                        findPoint = true;
                    } else {
                        result = null;
                        pointsCollection = null;
                        linesCollection = null;
                    }
                }
            }
        };
        addMouseListener(m);
        addMouseMotionListener(m);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        new Thread(this).start();
    }

    @Override
    public void run() {
        //this.drawTrapezoidMapCreation(searchingTree.getScenes());
        while (true) {
            threadAction();
            sleep(10);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.black);
        g2d.setStroke(new BasicStroke((float) 5.0));
        if (segments != null && showSegments) {
            for (Segment segment : segments) {
                if (segment != null) {
                    g2d.draw(segment.getLine());
                }
            }
        }

        if (result != null) {
            g2d.setStroke(new BasicStroke((float) 4.0));
            g2d.setPaint(Color.green);
            displayAll(g2d, result, new ArrayList<>());
        }

        if (linesCollection != null) {
            linesCollection.forEach(line -> {
                g2d.setPaint(this.linesColor);
                g2d.draw(line.getLine());
            });
        }

        if (pointsCollection != null) {
            pointsCollection.forEach(point -> {
                g2d.setPaint(this.pointsColor);
                g2d.draw(new Ellipse2D.Double(point.getX(), point.getY(), 6, 6));
            });
        }

        if(trapezoidLinesCollection != null) {
            trapezoidLinesCollection.forEach(line -> {
                g2d.setPaint(this.trapezoidColor);
                g2d.draw(line.getLine());
            });
        }

        if (!findPoint) {
            g2d.setColor(Color.BLUE);
            g2d.setStroke(new BasicStroke((float) 5.0));
            g2d.draw(new Ellipse2D.Double(xPos - 4, yPos - 4, 8, 8));
        }

        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }

    private void displayAll(Graphics2D g2d, Trapezoid t, ArrayList<Polygon> pp) {
        displayAll(g2d, t, pp, 0);
    }

    private void displayAll(Graphics2D g2d, Trapezoid t, ArrayList<Polygon> pp, int i) {
        if (i >= 10 || t == null) {
            return;
        }
        Polygon p = t.getBoundaryPolygon();
        if (!pp.contains(p)) {
            pp.add(p);
            g2d.fill(p);
            displayAll(g2d, t.getLowerLeftNeighbor(), pp, i);
            displayAll(g2d, t.getLowerRightNeighbor(), pp, i);
            displayAll(g2d, t.getUpperLeftNeighbor(), pp, i);
            displayAll(g2d, t.getUpperRightNeighbor(), pp, i);
        }
    }

    private void threadAction() {
        if (findPoint) {
            Point p = new Point(xPos, yPos);
            this.drawTrace(p);
            this.drawResult(p);
            findPoint = false;
        }
    }

    private void drawTrapezoidMapCreation(List<Scene> scenes) {
        this.pointsCollection = new ArrayList<>();
        this.linesCollection = new ArrayList<>();
        this.trapezoidLinesCollection = new ArrayList<>();
        this.showSegments = false;
        this.linesColor = Color.black;
        for (Scene scene : scenes) {
            this.pointsCollection.clear();
            this.linesCollection.clear();
            this.trapezoidLinesCollection.clear();
            this.pointsCollection.addAll(scene.getPoints());
            this.linesCollection.addAll(scene.getSegments());
            this.trapezoidLinesCollection.addAll(scene.getTrapezoids());
            sleep(DELAY);
        }
        this.showSegments = true;
        this.linesCollection.clear();
        this.pointsCollection.clear();
        this.trapezoidLinesCollection.clear();
        this.linesColor = Color.yellow;
    }

    private void drawTrace(Point point) {
        List<Node> trace = searchingTree.findTrace(point);
        pointsCollection = new ArrayList<>();
        linesCollection = new ArrayList<>();
        for (Node n : trace) {
            if (n instanceof XNode) pointsCollection.add(((XNode) n).getData());
            if (n instanceof YNode) linesCollection.add(((YNode) n).getData());
            sleep(DELAY);
        }
    }

    private void drawResult(Point point) {
        result = searchingTree.findPointTrapezoid(point);
        sleep(DELAY * 5);
    }

    private void sleep(int ms) {
        repaint();
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            System.err.println("Interrupted repaint thread");
        }
    }
}
