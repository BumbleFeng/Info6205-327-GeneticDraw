/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.neu.info6205.ga;

import edu.neu.info6205.ui.GAPanel;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author BumbleBee
 */
public class ParallelGA extends GAPanel {

    private static final Logger logger = LogManager.getLogger(ParallelGA.class.getName());
    private int pointNum;
    private int cutoff;
    private GeneticAlgorithm ga;
    protected int gap;
    protected int maxGen;

    public ParallelGA(String path, int scale, int cutoff, int num, int pointNum, int maxGen, int gap, double ps, double pc, double pm) {
        this.pointNum = pointNum;
        this.cutoff = cutoff;
        this.gap = gap;
        this.maxGen = maxGen;
        ga = new GeneticAlgorithm(path, scale, num, pointNum, gap, ps, pc, pm);
        if (maxGen < gap) {
            ga.maxGen = maxGen;
        }
        logger.info("Create ParallelGA (Path: " + path
                + ",Scale: " + scale
                + ",Cutoff: " + cutoff
                + ",Polygon Number: " + num
                + ",Point Number: " + pointNum
                + ",Max Generation: " + maxGen
                + ",Generation Gap: " + gap
                + ",Survival Rate: " + ps
                + ",Crossover Rate: " + pc
                + ",Mutate Rate: " + pm + ")");
    }

    @Override
    public void setPGA(String path, int scale, int cutoff, int num, int pointNum, int maxGen, int gap, double ps, double pc, double pm) {
        this.pointNum = pointNum;
        this.cutoff = cutoff;
        this.gap = gap;
        this.maxGen = maxGen;
        ga = new GeneticAlgorithm(path, scale, num, pointNum, gap, ps, pc, pm);
        logger.info("Set ParallelGA (Path: " + path
                + ",Scale: " + scale
                + ",Cutoff: " + cutoff
                + ",Polygon Number: " + num
                + ",Point Number: " + pointNum
                + ",Max Generation: " + maxGen
                + ",Generation Gap: " + gap
                + ",Survival Rate: " + ps
                + ",Crossover Rate: " + pc
                + ",Mutate Rate: " + pm + ")");
    }

    public void evolve() {
        evolve(ga.population, 0, ga.population.length);
        logger.info("Merge to: " + ga);
        ga.generation = ga.maxGen - 1;
        ga.evaluate();
        repaint();
        ga.next();
        ga.evaluate();
        repaint();
    }

    public void evolve(Chromosome[] population, int from, int to) {
        int size = to - from;
        if (size <= cutoff) {
            logger.info("Evaluate: " + Arrays.toString(population) + " from " + from + " to " + to);
            GeneticAlgorithm g = ga.colonize(from, to);
            g.evaluate();
            //elitism
            //g.best = ga.best.clone(pointNum);
            while (g.generation < g.maxGen - 1) {
                g.next();
                g.evaluate();
                ga.bestImage = g.bestImage;
                repaint();
            }
            for (int i = from; i < to; i++) {
                population[i] = g.population[i - from].clone();
            }
        } else {
            int mid = from + (to - from) / 2;
            logger.info("Separate: " + Arrays.toString(population) + " from " + from + " to " + to + " at " + mid);
            CompletableFuture<Chromosome[]> parga1 = parga(population, from, mid);
            CompletableFuture<Chromosome[]> parga2 = parga(population, mid, to);
            CompletableFuture<Chromosome[]> parga = parga1.
                    thenCombine(parga2, (c1, c2) -> {
                        Chromosome[] result = new Chromosome[c1.length + c2.length];
                        for (int i = 0; i < c1.length; i++) {
                            result[i] = c1[i].clone();
                        }
                        for (int j = 0; j < c2.length; j++) {
                            result[j + c1.length] = c2[j].clone();
                        }
                        logger.info("Merge: " + c1 + " and " + c2 + " to " + result);
                        return result;
                    });

            parga.whenComplete((result, throwable) -> {
                for (int i = 0; i < result.length; i++) {
                    population[i] = result[i].clone();
                }
                logger.info("Return: " + Arrays.toString(result) + " to " + Arrays.toString(population));
            });
            parga.join();
        }
    }

    private CompletableFuture<Chromosome[]> parga(Chromosome[] population, int from, int to) {
        return CompletableFuture.supplyAsync(
                () -> {
                    logger.info("Parga: " + Arrays.toString(population) + " from " + from + " to " + to);
                    Chromosome[] result = new Chromosome[to - from];
                    for (int i = 0; i < result.length; i++) {
                        result[i] = population[from + i].clone();
                    }
                    evolve(result, 0, result.length);
                    return result;
                }
        );
    }

    @Override
    public void run() {
        long time = System.nanoTime();
        String name = Thread.currentThread().getName();
        try {
            ga.read();
            ga.init();
            ga.evaluate();
            evolve();
            for (int i = gap * 2; i <= maxGen; i += gap) {
                ga.maxGen = i;
                evolve();
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
        g.drawImage(ga.bestImage, 10, 30, this);
    }

    public static void main(String[] args) {
        ParallelGA pga = new ParallelGA("resources/mona_lisa.jpg", 400, 100, 100, 3, 20, 10, 0.8, 0.8, 0.1);
        pga.run();
    }
}
