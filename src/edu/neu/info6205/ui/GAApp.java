package edu.neu.info6205.ui;

import edu.neu.info6205.ga.GeneticAlgorithm;
import edu.neu.info6205.ga.ParallelGA;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author BumbleBee
 */
public class GAApp extends App {

    private static final Logger logger = LogManager.getLogger(GAApp.class.getName());
    private String path;
    private JPanel mainPanel;
    private JPanel northPanel;
    private JButton fileBtn;
    private JButton startBtn;
    private JButton continueBtn;
    private JButton pauseBtn;
    private JButton stopBtn;
    private GAPanel gaPanel;
    private GAPanel pgaPanel;
    private CardLayout card;
    private JPanel center;
    private ImagePanel imagePanel;

    private JTextField scaleTF;
    private JTextField cutoffTF;
    private JTextField numTF;
    private JTextField pointNumTF;
    private JTextField maxGenTF;
    private JTextField gapTF;
    private JTextField psTF;
    private JTextField pcTF;
    private JTextField pmTF;

    private JComboBox comboBox;
    private int index = 0;

    private Thread t;

    public GAApp() {
        frame.setSize(800, 600);
        frame.setTitle("Genetic Algorithm App");
        menuMgr.createDefaultActions();

        showUI();
    }

    @Override
    public JPanel getMainPanel() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        path = "resources/mona_lisa.jpg";

        imagePanel = new ImagePanel();
        gaPanel = new GeneticAlgorithm(path, 200, 100, 3, 4000, 0.8, 0.8, 0.1);
        pgaPanel = new ParallelGA(path, 400, 100, 100, 3, 40, 10, 0.8, 0.8, 0.1);
        card = new CardLayout();
        center = new JPanel(card);
        center.add("Serial", gaPanel);
        center.add("Parallel", pgaPanel);
        mainPanel.add(BorderLayout.CENTER, center);
        mainPanel.add(BorderLayout.WEST, imagePanel);
        mainPanel.add(BorderLayout.NORTH, getNorthPanel());
        imagePanel.setPreferredSize(new Dimension(370, 370));

        return mainPanel;
    }

    public JPanel getNorthPanel() {
        northPanel = new JPanel();
        northPanel.setLayout(new GridLayout(4, 6));

        String label[] = {"Serial", "Parallel"};
        comboBox = new JComboBox(label);

        scaleTF = new JTextField(3);
        scaleTF.setText("200");
        cutoffTF = new JTextField(3);
        cutoffTF.setText("50");
        numTF = new JTextField(3);
        numTF.setText("100");
        pointNumTF = new JTextField(3);
        pointNumTF.setText("3");
        maxGenTF = new JTextField(3);
        maxGenTF.setText("2000");
        gapTF = new JTextField(3);
        gapTF.setText("100");
        psTF = new JTextField(3);
        psTF.setText("0.8");
        pcTF = new JTextField(3);
        pcTF.setText("0.8");
        pmTF = new JTextField(3);
        pmTF.setText("0.1");

        northPanel.add(new JLabel("Polygon Number"));
        northPanel.add(numTF);
        northPanel.add(new JLabel("Scale"));
        northPanel.add(scaleTF);
        northPanel.add(new JLabel("Max Generation"));
        northPanel.add(maxGenTF);
        northPanel.add(new JLabel("Point Number"));
        northPanel.add(pointNumTF);
        northPanel.add(new JLabel("Cutoff"));
        northPanel.add(cutoffTF);
        northPanel.add(new JLabel("Generation Gap"));
        northPanel.add(gapTF);
        northPanel.add(new JLabel("Survival Rate"));
        northPanel.add(psTF);
        northPanel.add(new JLabel("Crossover Rate"));
        northPanel.add(pcTF);
        northPanel.add(new JLabel("Mutate Rate"));
        northPanel.add(pmTF);

        fileBtn = new JButton("Choose Image");
        fileBtn.addActionListener(this);
        northPanel.add(fileBtn);

        northPanel.add(comboBox);
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                index = comboBox.getSelectedIndex();
                card.show(center, comboBox.getSelectedItem().toString());
                center.repaint();
            }
        });

        startBtn = new JButton("Start");
        startBtn.addActionListener(this);
        northPanel.add(startBtn);

        continueBtn = new JButton("Continue");
        continueBtn.addActionListener(this);
        northPanel.add(continueBtn);

        pauseBtn = new JButton("Pause");
        pauseBtn.addActionListener(this);
        northPanel.add(pauseBtn);

        stopBtn = new JButton("Stop");
        stopBtn.addActionListener(this);
        northPanel.add(stopBtn);

        return northPanel;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        logger.info("ActionEvent Received");

        if (ae.getActionCommand().equalsIgnoreCase("Start")) {
            gaPanel.stop();

            int scale = Integer.parseInt(scaleTF.getText());
            int cutoff = Integer.parseInt(cutoffTF.getText());
            int num = Integer.parseInt(numTF.getText());
            int pointNum = Integer.parseInt(pointNumTF.getText());
            int maxGen = Integer.parseInt(maxGenTF.getText());
            int gap = Integer.parseInt(gapTF.getText());
            double ps = Double.parseDouble(psTF.getText());
            double pc = Double.parseDouble(pcTF.getText());
            double pm = Double.parseDouble(pmTF.getText());

            if (index == 0) {
                gaPanel.newstart();
                gaPanel.setGA(path, scale, num, pointNum, maxGen, ps, pc, pm);
                System.out.println("Set GeneticAlgorithm (Path: " + path
                        + ",Scale: " + scale
                        + ",Polygon Number: " + num
                        + ",Point Number: " + pointNum
                        + ",Max Generation: " + maxGen
                        + ",Survival Rate: " + ps
                        + ",Crossover Rate: " + pc
                        + ",Mutate Rate: " + pm + ")");
                t = new Thread(gaPanel);
            } else if (index == 1) {
                pgaPanel.newstart();
                pgaPanel.setPGA(path, scale, cutoff, num, pointNum, maxGen, gap, ps, pc, pm);
                System.out.println("Set ParallelGA (Path: " + path
                        + ",Scale: " + scale
                        + ",Cutoff: " + cutoff
                        + ",Polygon Number: " + num
                        + ",Point Number: " + pointNum
                        + ",Max Generation: " + maxGen
                        + ",Generation Gap: " + gap
                        + ",Survival Rate: " + ps
                        + ",Crossover Rate: " + pc
                        + ",Mutate Rate: " + pm + ")");
                t = new Thread(pgaPanel);
            }
            t.start();

        } else if (ae.getActionCommand().equalsIgnoreCase("Continue")) {
            gaPanel.resume();
            pgaPanel.resume();
        } else if (ae.getActionCommand().equalsIgnoreCase("Pause")) {
            gaPanel.suspend();
            pgaPanel.suspend();
        } else if (ae.getActionCommand().equalsIgnoreCase("Stop")) {
            gaPanel.stop();
            pgaPanel.stop();
        } else if (ae.getActionCommand().equalsIgnoreCase("Choose Image")) {
            JFileChooser jfc = new JFileChooser("resources");
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            FileFilter imageFilter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
            jfc.setFileFilter(imageFilter);
            jfc.showDialog(new JLabel(), "Choose Image");
            File file = jfc.getSelectedFile();
            path = file.getAbsolutePath();
            imagePanel.path = path;
            imagePanel.repaint();
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
        logger.info("Window opened");
    }

    @Override
    public void windowClosing(WindowEvent e) {
        logger.info("Window closing");
    }

    @Override
    public void windowClosed(WindowEvent e) {
        logger.info("Window closed");
    }

    @Override
    public void windowIconified(WindowEvent e) {
        logger.info("Window iconified");
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        logger.info("Window deiconified");
    }

    @Override
    public void windowActivated(WindowEvent e) {
        logger.info("Window activated");
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        logger.info("Window deactivated");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        GAApp gaa = new GAApp();
        logger.info("App started");
    }
}
