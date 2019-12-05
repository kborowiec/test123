package com.borowiec.kamil;

import com.borowiec.kamil.data.figures.Point;
import com.borowiec.kamil.data.SearchingTree;
import com.borowiec.kamil.data.figures.Segment;
import com.borowiec.kamil.gui.DisplayPanel;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TrapezoidMap {
    public static void main(String[] args) {
        if (args.length > 0) {
            Segment[] arr = getLines(args[0]);
            JFrame f = new JFrame();
            DisplayPanel dp = new DisplayPanel(arr, new SearchingTree(arr, 0, 1200, 0, 800));
            f.add(dp);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(1200, 820);
            f.setLocationRelativeTo(null);
            f.setTitle("Trapezoid map");
            f.setVisible(true);
            f.setResizable(false);
        }
    }

    private static Segment[] getLines(String s) {
        Segment[] arr = null;
        File f = new File(s);
        try {
            Scanner scan = new Scanner(f);
            if (scan.hasNextInt()) {
                int len = scan.nextInt();
                arr = new Segment[len];
                for (int i = 0; i < len && scan.hasNextInt(); i++) {//read in each new line
                    arr[i] = new Segment(new Point(scan.nextInt(), scan.nextInt()), new Point(scan.nextInt(), scan.nextInt()));
                }
            }
        } catch (FileNotFoundException ex) {
            System.err.println("Unable to load segment file");
        }
        return arr;
    }
}
