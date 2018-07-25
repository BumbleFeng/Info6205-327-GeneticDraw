package edu.neu.info6205.ui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;

public abstract class App implements ActionListener, WindowListener {

    protected JFrame frame = null;
    protected MenuManager menuMgr = null;

    /**
     * The Biological growth constructor
     */
    public App() {
        initGUI();
    }

    /**
     * Initialize the Graphical User Interface
     */
    public void initGUI() {
        frame = new JFrame();
        frame.setTitle("GAApp");

        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //JFrame.DISPOSE_ON_CLOSE)

        // Permit the app to hear about the window opening
        frame.addWindowListener(this);

        menuMgr = new MenuManager(this);

        frame.setJMenuBar(menuMgr.getMenuBar()); // Add a menu bar to this application

        frame.setLayout(new BorderLayout());
        frame.add(getMainPanel(), BorderLayout.CENTER);
    }

    /**
     * Override this method to provide the main content panel.
     *
     * @return a JPanel, which contains the main content of of your application
     */
    public abstract JPanel getMainPanel();

    /**
     * A convenience method that uses the Swing dispatch threat to show the UI.
     * This prevents concurrency problems during component initialization.
     */
    public void showUI() {

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                frame.setVisible(true); // The UI is built, so display it;
            }
        });

    }

    /**
     * Shut down the application
     */
    public void exit() {
        frame.dispose();
        System.exit(0);
    }

    /**
     * Override this method to show a About Dialog
     */
    public void showHelp() {
    }

}
