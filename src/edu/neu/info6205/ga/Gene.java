/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.neu.info6205.ga;

import static edu.neu.info6205.ga.GeneticAlgorithm.height;
import static edu.neu.info6205.ga.GeneticAlgorithm.width;
import java.awt.Color;
import java.awt.Polygon;
import java.util.Arrays;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author BumbleBee
 */
public class Gene implements Comparable<Gene> {

    private static final Logger logger = LogManager.getLogger(Gene.class.getName());
    private int pointNum;
    protected Polygon polygon;
    private int[] c;
    protected Color color;
    protected int z;
    private Random random;

    public Gene(int pointNum) {
        random = new Random(System.nanoTime());
        this.pointNum = pointNum;
        int[] x = new int[pointNum];
        int[] y = new int[pointNum];
        for (int i = 0; i < pointNum; i++) {
            x[i] = random.nextInt(width);
            y[i] = random.nextInt(height);
        }
        polygon = new Polygon(x, y, pointNum);
        c = new int[4];
        for (int i = 0; i < 4; i++) {
            c[i] = random.nextInt(256);
        }
        color = new Color(c[0], c[1], c[2], c[3]);
        z = random.nextInt(1000);
        logger.info("Create Gene: " + this);
    }

    public Gene copy(double p) {
        logger.info("Copy Gene: " + this);
        Gene g = this.clone();
        if (random.nextDouble() < p) {
            g.mutate();
        }
        return g;
    }

    @Override
    public Gene clone() {
        logger.info("Clone Gene: " + this);
        Gene g = new Gene(this.pointNum);
        int[] x = this.polygon.xpoints.clone();
        int[] y = this.polygon.ypoints.clone();
        g.polygon = new Polygon(x, y, pointNum);
        g.c = this.c.clone();
        g.color = new Color(c[0], c[1], c[2], c[3]);
        g.z = this.z;
        return g;
    }

    //mutate should not be drastic
    public void mutate() {
        logger.info("Mutate Gene: " + this);
        if (random.nextBoolean()) {
            //mutate color
            switch (random.nextInt(4)) {
                case 0:
                    c[0] = clamp(c[0] + random.nextInt(100) - 50, 0, 255);
                    break;
                case 1:
                    c[1] = clamp(c[1] + random.nextInt(100) - 50, 0, 255);
                    break;
                case 2:
                    c[2] = clamp(c[2] + random.nextInt(100) - 50, 0, 255);
                    break;
                case 3:
                    c[3] = clamp(c[3] + random.nextInt(100) - 50, 0, 255);
                    break;
            }
            color = new Color(c[0], c[1], c[2], c[3]);
        } else {
            if (random.nextBoolean()) {
                //mutate position
                int p = random.nextInt(pointNum);
                int w = width / 10;
                polygon.xpoints[p] = clamp(polygon.xpoints[p] + random.nextInt(w * 2) - w, 0, width - 1);
                int h = height / 10;
                polygon.ypoints[p] = clamp(polygon.ypoints[p] + random.nextInt(h * 2) - h, 0, width - 1);
            } else {
                // mutate stacking
                z = random.nextInt(1000);
            }
        }
        logger.info("         To: " + this);
    }

    public int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(val, max));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        Gene that = (Gene) o;

        return this.pointNum == that.pointNum
                && Arrays.equals(this.polygon.xpoints, that.polygon.xpoints)
                && Arrays.equals(this.polygon.ypoints, that.polygon.ypoints)
                && this.z == that.z
                && Arrays.equals(this.c, that.c);
    }

    @Override
    public int hashCode() {
        return 31 * pointNum
                + 31 * Arrays.hashCode(polygon.xpoints)
                + 31 * Arrays.hashCode(polygon.ypoints)
                + 31 * z
                + 31 * Arrays.hashCode(c);

    }

    @Override
    public String toString() {
        return pointNum
                + Arrays.toString(polygon.xpoints)
                + Arrays.toString(polygon.ypoints)
                + z
                + Arrays.toString(c);
    }

    @Override
    public int compareTo(Gene that) {
        return Integer.compare(this.z, that.z);
    }
}
