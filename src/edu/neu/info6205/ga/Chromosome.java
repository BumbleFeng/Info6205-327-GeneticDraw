/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.neu.info6205.ga;

import static edu.neu.info6205.ga.GeneticAlgorithm.height;
import static edu.neu.info6205.ga.GeneticAlgorithm.width;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author BumbleBee
 */
public class Chromosome implements Comparable<Chromosome> {
    
    private static final Logger logger = LogManager.getLogger(Chromosome.class.getName());
    private int num;
    private int pointNum;
    public Gene[] genes;
    protected double difference;
    private Random random;

    public Chromosome(int num, int pointNum) {
        this.num = num;
        this.pointNum = pointNum;
        genes = new Gene[num];
        for (int i = 0; i < num; i++) {
            genes[i] = new Gene(pointNum);
        }
        Arrays.sort(genes);
        random = new Random(System.nanoTime());
        logger.info("Create Chromosome: " + this);
    }

    public void exprssion(Graphics2D g) {
        logger.info("Draw Chromosome: " + this);
        g.clearRect(0, 0, width, height);
        for (Gene gene : genes) {
            g.setColor(gene.color);
            g.fillPolygon(gene.polygon);
        }
    }

    public Chromosome copy(double p) {
        logger.info("Copy Chromosome: " + this);
        Chromosome c = new Chromosome(this.num, this.pointNum);
        for (int i = 0; i < this.num; i++) {
            c.genes[i] = this.genes[i].copy(p);
        }
        Arrays.sort(c.genes);
        return c;
    }
    
    @Override
    public Chromosome clone(){
        logger.info("Clone Chromosome: " + this);
        Chromosome c = new Chromosome(this.num, this.pointNum);
        for (int i = 0; i < this.num; i++) {
            c.genes[i] = this.genes[i].clone();
        }
        c.difference = this.difference;
        return c;
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
        Chromosome that = (Chromosome) o;

        return this.num == that.num
                && Arrays.equals(this.genes, that.genes);
    }
    
    @Override
    public int hashCode(){
        return 31*num
                +31*Arrays.hashCode(genes);
    }
    
    @Override
    public int compareTo(Chromosome that) {
        return Double.compare(this.difference, that.difference);
    }

    public double getDifference() {
        return difference;
    }

    public void setDifference(double difference) {
        this.difference = difference;
    }
    
}
