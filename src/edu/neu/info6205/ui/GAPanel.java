package edu.neu.info6205.ui;

import javax.swing.JPanel;

/**
 *
 * @author BumbleBee
 */
public class GAPanel extends JPanel implements Runnable {

    protected boolean suspended = false;
    protected boolean stopped = false;

    public void newstart() {
        suspended = false;
        stopped = false;
    }

    synchronized void stop() {
        stopped = true;
        suspended = false;
        notify();
    }

    synchronized void suspend() {
        suspended = true;
    }

    synchronized void resume() {
        suspended = false;
        notify();
    }

    @Override
    public void run() {
    }

    public void setGA(String path, int scale, int num, int pointNum, int maxGen, double ps, double pc, double pm) {
    }

    public void setPGA(String path, int scale, int cutoff, int num, int pointNum, int maxGen, int gap, double ps, double pc, double pm) {
    }
}
