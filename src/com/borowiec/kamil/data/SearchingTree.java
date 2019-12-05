package com.borowiec.kamil.data;

import com.borowiec.kamil.data.figures.Point;
import com.borowiec.kamil.data.figures.Segment;
import com.borowiec.kamil.data.figures.Trapezoid;
import com.borowiec.kamil.data.nodes.Leaf;
import com.borowiec.kamil.data.nodes.Node;
import com.borowiec.kamil.data.nodes.XNode;
import com.borowiec.kamil.data.nodes.YNode;
import com.borowiec.kamil.gui.Scene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SearchingTree {
    private Node root;
    private List<Scene> scenes = new ArrayList<>();

    public SearchingTree(Segment[] segs, int lx, int rx, int ly, int ry) {
        int minX = Math.min(lx, Arrays. stream(segs).mapToInt(Segment::getMinX).min().orElse(Integer.MAX_VALUE));
        int maxX = Math.max(rx, Arrays.stream(segs).mapToInt(Segment::getMaxX).max().orElse(Integer.MIN_VALUE));
        int minY = Math.min(ly, Arrays.stream(segs).mapToInt(Segment::getMinY).min().orElse(Integer.MAX_VALUE));
        int maxY = Math.max(ry, Arrays.stream(segs).mapToInt(Segment::getMaxY).max().orElse(Integer.MIN_VALUE));

        // INIT FIRST ELEMENT
        this.root = new Leaf(new Trapezoid(new Point(minX, minY), new Point(maxX, maxY),
                new Segment(new Point(minX, maxY), new Point(maxX, maxY)),
                new Segment(new Point(minX, minY), new Point(maxX, minY))));

        // RANDOM SEQUENCE OF LINES
        List<Segment> segmentsList = Arrays.asList(segs);
        Collections.shuffle(segmentsList);
        Segment[] segments = (Segment[]) segmentsList.toArray();

        // CREATE TRAPEZOID MAP
        for (int i = 0; i < segments.length && segments[i] != null; i++) {
            Leaf[] list = followSegment(segments[i]);

            // ADD CONSIDERED LINE
            List<Segment> iterationSceneLines = new ArrayList<>(Arrays.asList(Arrays.copyOfRange(segments, 0, i)));

            scenes.add(new Scene(Collections.emptyList(), new ArrayList<>(iterationSceneLines), Collections.emptyList()));

            if (list.length == 1) {
                Trapezoid old = list[0].getData();
                Trapezoid leftTrapezoid = new Trapezoid(old.getLeftBound(), segments[i].getLeftEndPoint(), old.getUpperBound(), old.getLowerBound());
                Trapezoid rightTrapezoid = new Trapezoid(segments[i].getRightEndPoint(), old.getRightBound(), old.getUpperBound(), old.getLowerBound());
                Trapezoid topTrapezoid = new Trapezoid(segments[i].getLeftEndPoint(), segments[i].getRightEndPoint(), old.getUpperBound(), segments[i]);
                Trapezoid bottomTrapezoid = new Trapezoid(segments[i].getLeftEndPoint(), segments[i].getRightEndPoint(), segments[i], old.getLowerBound());

                scenes.add(new Scene(Collections.emptyList(),
                        new ArrayList<>(iterationSceneLines),
                        Arrays.asList(leftTrapezoid, rightTrapezoid, topTrapezoid, bottomTrapezoid)));

                XNode lelftXNode = new XNode(segments[i].getLeftEndPoint());
                XNode rightXNode = new XNode(segments[i].getRightEndPoint());
                YNode yNode = new YNode(segments[i]);

                Leaf leftLeaf = new Leaf(leftTrapezoid);
                Leaf rightLeaf = new Leaf(rightTrapezoid);
                Leaf topLeaf = new Leaf(topTrapezoid);
                Leaf bottomLeaf = new Leaf(bottomTrapezoid);

                if (!(leftTrapezoid.hasZeroWidth() || rightTrapezoid.hasZeroWidth())) {
                    lelftXNode.setChildren(leftLeaf, rightXNode);
                    rightXNode.setChildren(yNode, rightLeaf);
                    yNode.setChildren(topLeaf, bottomLeaf);
                    connectNodesToOldStructure(list[0], lelftXNode);
                    lowerLink(leftTrapezoid, bottomTrapezoid);
                    lowerLink(old.getLowerLeftNeighbor(), leftTrapezoid);
                    upperLink(leftTrapezoid, topTrapezoid);
                    upperLink(old.getUpperLeftNeighbor(), leftTrapezoid);
                    lowerLink(rightTrapezoid, old.getLowerRightNeighbor());
                    lowerLink(bottomTrapezoid, rightTrapezoid);
                    upperLink(rightTrapezoid, old.getUpperRightNeighbor());
                    upperLink(topTrapezoid, rightTrapezoid);
                } else if (leftTrapezoid.hasZeroWidth() && !rightTrapezoid.hasZeroWidth()) {
                    rightXNode.setChildren(yNode, rightLeaf);
                    yNode.setChildren(topLeaf, bottomLeaf);
                    connectNodesToOldStructure(list[0], rightXNode);
                    lowerLink(old.getLowerLeftNeighbor(), bottomTrapezoid);
                    upperLink(old.getUpperLeftNeighbor(), topTrapezoid);
                    lowerLink(rightTrapezoid, old.getLowerRightNeighbor());
                    lowerLink(bottomTrapezoid, rightTrapezoid);
                    upperLink(rightTrapezoid, old.getUpperRightNeighbor());
                    upperLink(topTrapezoid, rightTrapezoid);
                } else if (rightTrapezoid.hasZeroWidth() && !leftTrapezoid.hasZeroWidth()) {
                    lelftXNode.setChildren(leftLeaf, yNode);
                    yNode.setChildren(topLeaf, bottomLeaf);
                    connectNodesToOldStructure(list[0], lelftXNode);
                    lowerLink(leftTrapezoid, bottomTrapezoid);
                    lowerLink(old.getLowerLeftNeighbor(), leftTrapezoid);
                    upperLink(leftTrapezoid, topTrapezoid);
                    upperLink(old.getUpperLeftNeighbor(), leftTrapezoid);
                    lowerLink(bottomTrapezoid, old.getLowerRightNeighbor());
                    upperLink(topTrapezoid, old.getUpperRightNeighbor());
                } else {
                    yNode.setChildren(topLeaf, bottomLeaf);
                    connectNodesToOldStructure(list[0], yNode);
                    lowerLink(old.getLowerLeftNeighbor(), bottomTrapezoid);
                    lowerLink(bottomTrapezoid, old.getLowerRightNeighbor());
                    upperLink(old.getUpperLeftNeighbor(), topTrapezoid);
                    upperLink(topTrapezoid, old.getUpperRightNeighbor());
                }

            } else {
                Trapezoid[] topTrs = new Trapezoid[list.length];
                Trapezoid[] botTrs = new Trapezoid[list.length];

                for (int j = 0; j < list.length; j++) {
                    if (j == 0) {
                        Point rtP = isPointAboveLine(list[j].getData().getRightBound(), segments[i]) ? list[j].getData().getRightBound() : null;
                        topTrs[j] = new Trapezoid(segments[i].getLeftEndPoint(), rtP, list[j].getData().getUpperBound(), segments[i]);
                    } else if (j == list.length - 1) {
                        Point ltP = isPointAboveLine(list[j].getData().getLeftBound(), segments[i]) ? ltP = list[j].getData().getLeftBound() : null;
                        topTrs[j] = new Trapezoid(ltP, segments[i].getRightEndPoint(), list[j].getData().getUpperBound(), segments[i]);
                    } else {
                        Point rtP = isPointAboveLine(list[j].getData().getRightBound(), segments[i]) ? list[j].getData().getRightBound() : null;
                        Point ltP = isPointAboveLine(list[j].getData().getLeftBound(), segments[i]) ? list[j].getData().getLeftBound() : null;
                        topTrs[j] = new Trapezoid(ltP, rtP, list[j].getData().getUpperBound(), segments[i]);
                    }

                    if (j == 0) {
                        Point rtP = !isPointAboveLine(list[j].getData().getRightBound(), segments[i]) ? list[j].getData().getRightBound() : null;
                        botTrs[j] = new Trapezoid(segments[i].getLeftEndPoint(), rtP, segments[i], list[j].getData().getLowerBound());
                    } else if (j == list.length - 1) {
                        Point ltP = !isPointAboveLine(list[j].getData().getLeftBound(), segments[i]) ? list[j].getData().getLeftBound() : null;
                        botTrs[j] = new Trapezoid(ltP, segments[i].getRightEndPoint(), segments[i], list[j].getData().getLowerBound());
                    } else {
                        Point rtP = !isPointAboveLine(list[j].getData().getRightBound(), segments[i]) ? list[j].getData().getRightBound() : null;
                        Point ltP = !isPointAboveLine(list[j].getData().getLeftBound(), segments[i]) ? list[j].getData().getLeftBound() : null;
                        botTrs[j] = new Trapezoid(ltP, rtP, segments[i], list[j].getData().getLowerBound());
                    }
                }

                // MERGE TRAPEZOIDS
                int aTop = 0, aBot = 0;
                for (int j = 0; j < list.length; j++) {
                    if (topTrs[j].getRightBound() != null) {
                        Trapezoid tempMerge = new Trapezoid(topTrs[aTop].getLeftBound(), topTrs[j].getRightBound(), topTrs[aTop].getUpperBound(), segments[i]);
                        for (int k = aTop; k <= j; k++) {
                            topTrs[k] = tempMerge;
                        }
                        aTop = j + 1;
                    }
                    if (botTrs[j].getRightBound() != null) {
                        Trapezoid tempMerge = new Trapezoid(botTrs[aBot].getLeftBound(), botTrs[j].getRightBound(), segments[i], botTrs[aBot].getLowerBound());
                        for (int k = aBot; k <= j; k++) {
                            botTrs[k] = tempMerge;
                        }
                        aBot = j + 1;
                    }
                }

                List<Trapezoid> trapezoids = new ArrayList<>();
                trapezoids.addAll(Arrays.asList(topTrs));
                trapezoids.addAll(Arrays.asList(botTrs));

                scenes.add(new Scene(Collections.emptyList(),
                        new ArrayList<>(iterationSceneLines), trapezoids));

                // LINK NEIGHBOURS
                for (int j = 0; j < list.length; j++) {
                    if (j != 0) {
                        if (topTrs[j] != topTrs[j - 1]) {
                            lowerLink(topTrs[j - 1], topTrs[j]);
                        }
                        Trapezoid temp2 = list[j].getData().getUpperLeftNeighbor();
                        if (!list[j - 1].getData().equals(temp2)) {
                            upperLink(temp2, topTrs[j]);
                        }
                        if (botTrs[j] != botTrs[j - 1]) {
                            upperLink(botTrs[j - 1], botTrs[j]);
                        }
                        temp2 = list[j].getData().getLowerLeftNeighbor();
                        if (!list[j - 1].getData().equals(temp2)) {
                            lowerLink(temp2, botTrs[j]);
                        }
                    }
                }

                for (int j = 0; j < list.length; j++) {
                    if (j != topTrs.length - 1) {
                        if (topTrs[j] != topTrs[j + 1]) {
                            lowerLink(topTrs[j], topTrs[j + 1]);
                        }
                        Trapezoid temp2 = list[j].getData().getUpperRightNeighbor();
                        if (!list[j + 1].getData().equals(temp2)) {
                            upperLink(topTrs[j], temp2);
                        }
                        if (botTrs[j] != botTrs[j + 1]) {
                            upperLink(botTrs[j], botTrs[j + 1]);
                        }
                        temp2 = list[j].getData().getLowerRightNeighbor();
                        if (!list[j + 1].getData().equals(temp2)) {
                            lowerLink(botTrs[j], temp2);
                        }
                    }
                }

                // POSSIBLE MOST LEFT/RIGHT TRAPEZOIDS
                Trapezoid mostLeft = null;
                Trapezoid mostRight = null;
                Trapezoid oldLeft = list[0].getData();
                Trapezoid oldRight = list[list.length - 1].getData();

                if (!segments[i].getLeftEndPoint().equals(oldLeft.getLeftBound())) {
                    mostLeft = new Trapezoid(oldLeft.getLeftBound(), segments[i].getLeftEndPoint(),
                            oldLeft.getUpperBound(), oldLeft.getLowerBound());
                }

                if (!segments[i].getRightEndPoint().equals(list[list.length - 1].getData().getRightBound())) {
                    mostRight = new Trapezoid(segments[i].getRightEndPoint(), oldRight.getRightBound(),
                            oldRight.getUpperBound(), oldRight.getLowerBound());
                }

                if (mostLeft != null) {
                    lowerLink(oldLeft.getLowerLeftNeighbor(), mostLeft);
                    upperLink(oldLeft.getUpperLeftNeighbor(), mostLeft);

                    lowerLink(mostLeft, botTrs[0]);
                    upperLink(mostLeft, topTrs[0]);
                } else {
                    if (oldLeft.getUpperBound().getLeftEndPoint().equals(oldLeft.getLeftBound())) {
                        lowerLink(oldLeft.getLowerLeftNeighbor(), botTrs[0]);
                    } else if (oldLeft.getLowerBound().getLeftEndPoint().equals(oldLeft.getLeftBound())) {
                        upperLink(oldLeft.getUpperLeftNeighbor(), topTrs[0]);
                    } else {
                        lowerLink(oldLeft.getLowerLeftNeighbor(), botTrs[0]);
                        upperLink(oldLeft.getUpperLeftNeighbor(), topTrs[0]);
                    }
                }

                if (mostRight != null) {
                    lowerLink(mostRight, oldRight.getLowerRightNeighbor());
                    upperLink(mostRight, oldRight.getUpperRightNeighbor());

                    lowerLink(botTrs[botTrs.length - 1], mostRight);
                    upperLink(topTrs[topTrs.length - 1], mostRight);
                } else {
                    if (oldRight.getUpperBound().getRightEndPoint().equals(oldRight.getRightBound())) {
                        lowerLink(botTrs[botTrs.length - 1], oldRight.getLowerRightNeighbor());
                    } else if (oldRight.getLowerBound().getRightEndPoint().equals(oldRight.getRightBound())) {
                        upperLink(topTrs[topTrs.length - 1], oldRight.getUpperRightNeighbor());
                    } else {
                        lowerLink(botTrs[botTrs.length - 1], oldRight.getLowerRightNeighbor());
                        upperLink(topTrs[topTrs.length - 1], oldRight.getUpperRightNeighbor());
                    }
                }

                // UPDATE TREE STRUCTURE
                Leaf[] topLeaf = new Leaf[topTrs.length];
                Leaf[] botLeaf = new Leaf[botTrs.length];
                for (int j = 0; j < topLeaf.length; j++) {
                    if (j == 0 || topTrs[j] != topTrs[j - 1]) {
                        topLeaf[j] = new Leaf(topTrs[j]);
                    } else {
                        topLeaf[j] = topLeaf[j - 1];
                    }
                    if (j == 0 || botTrs[j] != botTrs[j - 1]) {
                        botLeaf[j] = new Leaf(botTrs[j]);
                    } else {
                        botLeaf[j] = botLeaf[j - 1];
                    }
                }

                Node[] newStructures = new Node[list.length];
                for (int j = 0; j < list.length; j++) {
                    Node y = new YNode(segments[i]);
                    if (j == 0 && mostLeft != null) {
                        XNode x = new XNode(segments[i].getLeftEndPoint());
                        x.setChildren(new Leaf(mostLeft), y);
                        newStructures[j] = x;
                    } else if (j == newStructures.length - 1 && mostRight != null) {
                        XNode x = new XNode(segments[i].getRightEndPoint());
                        x.setChildren(y, new Leaf(mostRight));
                        newStructures[j] = x;
                    } else {
                        newStructures[j] = y;
                    }

                    y.setChildren(topLeaf[j], botLeaf[j]);

                    for (Node parent : list[j].getParentNodes()) {
                        if (parent.getLeftChildNode() == list[j]) {
                            parent.setLeftChildNode(newStructures[j]);
                        } else {
                            parent.setRightChildNode(newStructures[j]);
                        }
                    }
                }
            }
        }
    }

    private void lowerLink(Trapezoid left, Trapezoid right) {
        if (left != null) {
            left.setLowerRightNeighbor(right);
        }
        if (right != null) {
            right.setLowerLeftNeighbor(left);
        }
    }

    private void upperLink(Trapezoid left, Trapezoid right) {
        if (left != null) {
            left.setUpperRightNeighbor(right);
        }
        if (right != null) {
            right.setUpperLeftNeighbor(left);
        }
    }

    private Leaf[] followSegment(Segment s) {
        ArrayList<Leaf> list = new ArrayList<>();
        Leaf previous = findPoint(s.getLeftEndPoint(), s);
        list.add(previous);
        while (s.getRightEndPoint().compareTo(previous.getData().getRightBound()) > 0) {
            previous = isPointAboveLine(previous.getData().getRightBound(), s) ?
                    previous.getData().getLowerRightNeighbor().getLeaf() : previous.getData().getUpperRightNeighbor().getLeaf();
            list.add(previous);
        }
        Leaf[] arr = new Leaf[list.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

    // SEARCHING METHODS :
    private Leaf findPoint(Point p, Segment s) {
        Node current = root;
        while (!(current instanceof Leaf)) {
            if (current instanceof XNode) {
                current = p.compareTo(((XNode) current).getData()) < 0 ? current.getLeftChildNode() : current.getRightChildNode();
            } else if (current instanceof YNode) {
                if (s == null) {
                    current = isPointAboveLine(p, ((YNode) current).getData()) ? current.getLeftChildNode() : current.getRightChildNode();
                } else {
                    current = isPointAboveLine(p, ((YNode) current).getData(), s) ? current.getLeftChildNode() : current.getRightChildNode();
                }
            }
        }
        return ((Leaf) current);
    }

    public List<Node> findTrace(Point p) {
        List<Node> result = new ArrayList<>();
        Node current = root;
        while (!(current instanceof Leaf)) {
            result.add(current);
            if (current instanceof XNode) {
                current = p.compareTo(((XNode) current).getData()) < 0 ? current.getLeftChildNode() : current.getRightChildNode();
            } else if (current instanceof YNode) {
                current = isPointAboveLine(p, ((YNode) current).getData()) ? current.getLeftChildNode() : current.getRightChildNode();
            }
        }
        return result;
    }

    public Trapezoid findPointTrapezoid(Point p) {
        return findPoint(p, null).getData();
    }

    private static boolean isPointAboveLine(Point p, Segment s) {
        int x = p.getX();
        int y = p.getY();
        return (x - s.getLeftEndPoint().getX()) * s.getRightEndPoint().getY()
                + (s.getRightEndPoint().getX() - x) * s.getLeftEndPoint().getY()
                < y * (s.getRightEndPoint().getX() - s.getLeftEndPoint().getX());
    }

    private static boolean isPointAboveLine(Point p, Segment old, Segment pseg) {
        if (p.equals(old.getLeftEndPoint())) {
            long x1 = p.getX();
            long x2 = old.getRightEndPoint().getX();
            long x3 = pseg.getRightEndPoint().getX();
            long y1 = p.getY();
            long y2 = old.getRightEndPoint().getY();
            long y3 = pseg.getRightEndPoint().getY();
            return (x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1) > 0;
        }
        return isPointAboveLine(p, old);
    }

    public List<Scene> getScenes() {
        return this.scenes;
    }

    private void connectNodesToOldStructure(Leaf leaf, Node eventualNewRoot) {
        if (leaf.getParentNode() == null) {
            root = eventualNewRoot;
        } else {
            for (Node tempParent : leaf.getParentNodes()) {
                if (tempParent.getLeftChildNode() == leaf) {
                    tempParent.setLeftChildNode(eventualNewRoot);
                } else {
                    tempParent.setRightChildNode(eventualNewRoot);
                }
            }
        }
    }
}
