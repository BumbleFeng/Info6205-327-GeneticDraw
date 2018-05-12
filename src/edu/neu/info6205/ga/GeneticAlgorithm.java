/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.neu.info6205.ga;

import edu.neu.info6205.ui.GAPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Random;
import javax.imageio.ImageIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author BumbleBee
 */
public class GeneticAlgorithm extends GAPanel {

    private static final Logger logger = LogManager.getLogger(GeneticAlgorithm.class.getName());
    private int scale;
    private int num;
    private int pointNum;
    protected Chromosome[] population;
    private PriorityQueue<Chromosome> pq;

    protected int generation;
    protected int maxGen;

    private String path;
    protected static int width;
    protected static int height;
    private int[][] t;
    private int rgb;
    private BufferedImage temple;
    protected BufferedImage bestImage;
    private Graphics2D g;

    protected Chromosome best;
    private ArrayList<Double> bestScore;

    //private double[] pi;
    private int s;
    private double ps;
    private double pc;
    private double pm;

    private Random random;
    public long e = 0;
    public long d = 0;

    public GeneticAlgorithm(String path, int scale, int num, int pointNum, int maxGen, double ps, double pc, double pm) {
        this.path = path;
        this.scale = scale;
        this.num = num;
        this.pointNum = pointNum;
        this.maxGen = maxGen;
        this.ps = ps;
        this.pc = pc;
        this.pm = pm;
        logger.info("Create GeneticAlgorithm (Path: " + path
                + ",Scale: " + scale
                + ",Polygon Number: " + num
                + ",Point Number: " + pointNum
                + ",Max Generation: " + maxGen
                + ",Survival Rate: " + ps
                + ",Crossover Rate: " + pc
                + ",Mutate Rate: " + pm + ")");
    }

    @Override
    public void setGA(String path, int scale, int num, int pointNum, int maxGen, double ps, double pc, double pm) {
        this.path = path;
        this.scale = scale;
        this.num = num;
        this.pointNum = pointNum;
        this.maxGen = maxGen;
        this.ps = ps;
        this.pc = pc;
        this.pm = pm;
        logger.info("Set GeneticAlgorithm (Path: " + path
                + ",Scale: " + scale
                + ",Polygon Number: " + num
                + ",Point Number: " + pointNum
                + ",Max Generation: " + maxGen
                + ",Survival Rate: " + ps
                + ",Crossover Rate: " + pc
                + ",Mutate Rate: " + pm + ")");
    }

    public void read() {
        logger.info("Read Image: " + path);
        File file = new File(path);
        try {
            BufferedImage image = ImageIO.read(file);
            width = image.getWidth();
            height = image.getHeight();
            BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);;
            int[][] gray = new int[width][height];
            t = new int[width][height];
            rgb = 0;
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    t[i][j] = image.getRGB(i, j);
                    rgb += t[i][j];
                    gray[i][j] = getGray(t[i][j]);;
                    int c = new Color(gray[i][j], gray[i][j], gray[i][j]).getRGB();
                    grayImage.setRGB(i, j, c);
                }
            }
            System.out.println(rgb);
            ImageIO.write(grayImage, "jpg", new File("gray.jpg"));
        } catch (IOException ex) {
            ex.printStackTrace();
            logger.error(ex.getMessage());
        }
    }

    public static int getGray(int c) {
        int r = (c >> 16) & 0xFF, g = (c >> 8) & 0xFF, b = c & 0xFF;
        int gray = (int) (0.3 * r + 0.59 * g + 0.11 * b);
        return gray;
    }

    public void init() {
        logger.info("Init GeneticAlgorithm: " + this);
        population = new Chromosome[scale];
        generation = 0;
        //pi = new double[scale];
        for (int i = 0; i < scale; i++) {
            population[i] = new Chromosome(num, pointNum);
        }
        temple = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g = temple.createGraphics();
        bestScore = new ArrayList<Double>();
        s = (int) (scale * ps);
        random = new Random(System.nanoTime());
    }

    public void evaluate() {
        logger.info("Evaluate GeneticAlgorithm: " + this);
        long t;
        pq = new PriorityQueue<>();
        for (int i = 0; i < scale; i++) {
            t = System.nanoTime();
            population[i].exprssion(g);
            e += System.nanoTime() - t;
            t = System.nanoTime();
            population[i].difference = difference(temple);
            d += System.nanoTime() - t;
            pq.add(population[i]);
        }
        best = pq.peek().clone();
        bestScore.add(best.difference);
        System.out.println(Thread.currentThread().getName() + " " + generation + " " + best.difference);
        logger.info(this + " " + generation + " " + best.difference);
        bestImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D bg = bestImage.createGraphics();
        best.exprssion(bg);
        try {
            ImageIO.write(bestImage, "png", new File("best.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
            logger.error(ex.getMessage());
        }
    }

    public double difference(BufferedImage temple) {
        double difference = 0.0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int a = temple.getRGB(i, j);
                int b = t[i][j];//target.getRGB(i, j)
                difference += Math.abs((a & 0xff) - (b & 0xff));
                difference += Math.abs(((a >> 8) & 0xFF) - ((b >> 8) & 0xFF));
                difference += Math.abs(((a >> 16) & 0xFF) - ((b >> 16) & 0xFF));
                difference += Math.abs(((a >> 24) & 0xFF) - ((b >> 24) & 0xFF));
            }
        }
        return difference;
    }

    public void next() {
        logger.info("Next Generation: " + this);
        //Truncation
        Chromosome[] next = new Chromosome[scale];
        Chromosome[] survival = new Chromosome[s];
        for (int i = 0; i < s; i++) {
            survival[i] = pq.poll();
            //System.out.println(survival[i].difference);
        }
        //elitism
        next[0] = best.clone();

        for (int i = 1; i < scale - 1;) {
            if (random.nextDouble() < pc) {
                Chromosome father = select(survival);
                Chromosome mother = select(survival);
                Chromosome[] children = crossover(father, mother);
                next[i++] = children[0];
                next[i++] = children[1];
            } else {
                next[i++] = select(survival).copy(pm);
            }
        }
        if (next[scale - 1] == null) {
            next[scale - 1] = select(survival).copy(pm);
        }
        population = next;
        generation++;
    }

    //Tournament Selector
    public Chromosome select(Chromosome[] list) {
        int size = list.length;
        Chromosome a = list[random.nextInt(size)];
        Chromosome b = list[random.nextInt(size)];
        return (a.difference < b.difference) ? a : b;
    }

    //Uniform Crossover
    public Chromosome[] crossover(Chromosome father, Chromosome mother) {
        logger.info("Crossover: " + father + " and " + mother);
        Chromosome[] children = new Chromosome[2];
        children[0] = new Chromosome(num, pointNum);
        children[1] = new Chromosome(num, pointNum);
        for (int i = 0; i < num; i++) {
            if (random.nextBoolean()) {
                children[0].genes[i] = father.genes[i].copy(pm);
                children[1].genes[i] = mother.genes[i].copy(pm);
            } else {
                children[0].genes[i] = mother.genes[i].copy(pm);
                children[1].genes[i] = father.genes[i].copy(pm);
            }
        }
        Arrays.sort(children[0].genes);
        Arrays.sort(children[1].genes);
        logger.info(" Children: " + children[0] + " and " + children[1]);
        return children;
    }

    public GeneticAlgorithm colonize(int from, int to) {
        logger.info("Colonize: " + this + " population from " + from + " to " + to);
        GeneticAlgorithm ga = new GeneticAlgorithm(this.path, to - from, this.num, this.pointNum, this.maxGen, this.ps, this.pc, this.pm);
        ga.t = this.t;
        ga.init();
        ga.generation = this.generation;
        for (int i = 0; i < ga.scale; i++) {
            ga.population[i] = this.population[i + from].clone();
        }
        return ga;
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
        GeneticAlgorithm that = (GeneticAlgorithm) o;

        return this.path.equals(that.path)
                && this.scale == that.scale
                && this.num == that.num
                && this.pointNum == that.pointNum
                && this.maxGen == that.maxGen
                && this.ps == that.ps
                && this.pc == that.pc
                && this.pm == that.pm
                && Arrays.deepEquals(this.t, that.t)
                && this.generation == that.generation
                && Arrays.equals(this.population, that.population);
    }

    @Override
    public int hashCode() {
        return 31 * path.hashCode()
                + 31 * scale
                + 31 * num
                + 31 * pointNum
                + 31 * maxGen
                + 31 * Double.hashCode(ps)
                + 31 * Double.hashCode(pc)
                + 31 * Double.hashCode(pm)
                + 31 * Arrays.deepHashCode(t)
                + 31 * generation
                + 31 * Arrays.hashCode(population);
    }

    @Override
    public void run() {
        long time = System.nanoTime();
        String name = Thread.currentThread().getName();
        try {
            read();
            init();
            evaluate();
            while (generation < maxGen) {
                next();
                evaluate();
                repaint();
                synchronized (this) {
                    while (suspended) {
                        wait();
                    }
                    if (stopped) {
                        break;
                    }
                }
            }
        } catch (InterruptedException ex) {
            System.out.println(name + " interrupted.");
        }
        time = System.nanoTime() - time;
        System.out.println(time / (1000000000.0 * 60));
        System.out.println("\n" + name + " exiting.");
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Font f = new Font("dialog", Font.PLAIN, 18);
        g.setFont(f);
        g.drawString("Best", 10, 20);
        g.drawImage(bestImage, 10, 30, this);
    }

    public Chromosome[] getPopulation() {
        return population;
    }

    public PriorityQueue<Chromosome> getPq() {
        return pq;
    }

    public Chromosome getBest() {
        return best;
    }

    public static void main(String[] args) {
        GeneticAlgorithm ga = new GeneticAlgorithm("resources/mona_lisa.jpg", 200, 100, 3, 4000, 0.8, 0.8, 0.1);
        ga.read();
        ga.init();
        ga.evaluate();
        long t;
        long e = 0;
        long n = 0;
        while (ga.generation < ga.maxGen) {
            t = System.nanoTime();
            ga.next();
            n += System.nanoTime() - t;
            t = System.nanoTime();
            ga.evaluate();
            e += System.nanoTime() - t;
        }
        System.out.println(ga.e / 1000000000.0);
        System.out.println(ga.d / 1000000000.0);
        System.out.println(e / 1000000000.0 + " " + n / 1000000000.0);
        //ga.run();
    }
}
