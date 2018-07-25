/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.neu.info6205.test;

import edu.neu.info6205.ga.Chromosome;
import edu.neu.info6205.ga.Gene;
import edu.neu.info6205.ga.GeneticAlgorithm;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author BumbleBee
 */
@SuppressWarnings("ALL")
public class GeneticAlgorithmTest {

    private GeneticAlgorithm ga;

    public GeneticAlgorithmTest() {
        ga = new GeneticAlgorithm("resources/mona_lisa.jpg", 100, 100, 3, 10, 0.8, 0.8, 0.1);
        ga.read();
        ga.init();
    }

    @Test
    public void geneTest() {
        Gene a = new Gene(3);
        Gene b = new Gene(3);
        assertNotNull(a);
        assertNotNull(b);
        assertFalse(a.equals(b));
        b = a.clone();
        assertTrue(a.equals(b));
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a, b);
        b.mutate();
        assertFalse(a.equals(b));
        b = a.copy(0);
        assertEquals(a, b);
        b = a.copy(1);
        assertFalse(a.equals(b));
    }

    @Test
    public void chromosomeTest() {
        Chromosome a = new Chromosome(100, 3);
        Chromosome b = new Chromosome(100, 3);
        assertNotNull(a);
        assertNotNull(b);
        assertFalse(a.equals(b));
        b = a.clone();
        assertTrue(a.equals(b));
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a, b);
        b = a.copy(1);
        assertFalse(a.equals(b));
        b = a.copy(0);
        assertEquals(a, b);
    }

    @Test
    public void fitnessTest() {
        Chromosome a = new Chromosome(100, 3);
        Chromosome b = new Chromosome(100, 3);
        assertTrue(a.getDifference() == 0.0);
        assertTrue(b.getDifference() == 0.0);
        BufferedImage temple = new BufferedImage(350, 350, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = temple.createGraphics();
        a.exprssion(g);
        a.setDifference(ga.difference(temple));
        b.exprssion(g);
        b.setDifference(ga.difference(temple));
        assertFalse(a.getDifference() == 0.0);
        assertFalse(b.getDifference() == 0.0);
        assertFalse(a.getDifference() == b.getDifference());
        b = a.clone();
        b.exprssion(g);
        b.setDifference(ga.difference(temple));
        assertTrue(a.getDifference() == b.getDifference());
    }

    @Test
    public void sortTest() {
        ga.evaluate();
        double best = ga.getBest().getDifference();
        double a = Integer.MAX_VALUE;
        for (Chromosome c : ga.getPopulation()) {
            a = (c.getDifference() < a) ? c.getDifference() : a;
        }
        assertTrue(a == best);

        Chromosome[] p = new Chromosome[100];
        for (int i = 0; !ga.getPq().isEmpty(); i++) {
            p[i] = ga.getPq().poll();
        }
        for (int j = 0; j < 99; j++) {
            assertTrue(p[j].getDifference() < p[j + 1].getDifference());
        }
    }

    @Test
    public void crossoverTest() {
        Chromosome a = new Chromosome(100, 3);
        Chromosome b = new Chromosome(100, 3);
        Chromosome[] children = ga.crossover(a, b);
        assertFalse(a.equals(b));
        assertFalse(a.equals(children[0]));
        assertFalse(a.equals(children[1]));
        assertFalse(b.equals(children[0]));
        assertFalse(b.equals(children[1]));
        assertFalse(children[0].equals(children[1]));
        ga = new GeneticAlgorithm("resources/mona_lisa.jpg", 100, 100, 3, 10, 0.8, 0.8, 0);
        ga.init();
        children = ga.crossover(a, a);
        assertEquals(a, children[0]);
        assertEquals(a, children[1]);
        assertEquals(children[0], children[1]);
    }

    @Test
    public void testNext() {
        ga.evaluate();
        double best = ga.getBest().getDifference();
        ga.next();
        ga.evaluate();
        assertTrue(ga.getBest().getDifference() <= best);
    }

    @Test
    public void colonizeTest() {
        GeneticAlgorithm gb = new GeneticAlgorithm("resources/mona_lisa.jpg", 100, 100, 3, 10, 0.8, 0.8, 0.1);
        gb.read();
        gb.init();
        assertTrue(!ga.equals(gb));
        gb = ga.colonize(0, 100);
        assertTrue(ga.equals(gb));
        assertEquals(ga.hashCode(), gb.hashCode());
        assertEquals(ga, gb);
    }

}
