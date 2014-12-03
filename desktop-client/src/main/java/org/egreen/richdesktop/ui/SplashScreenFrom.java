package org.egreen.richdesktop.ui;

import org.cef.OS;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

import static java.awt.Toolkit.getDefaultToolkit;

/**
 * Created by dewmal on 11/24/14.
 */
public class SplashScreenFrom extends JFrame {
    private int duration;

    public SplashScreenFrom(int d) {
        duration = d;
    }

    public static void main(String[] args) {

        Thread serverThread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    LocalServer.mainRunServer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });serverThread.start();


//        // Throw a nice little title page up on the screen first
        SplashScreenFrom splash = new SplashScreenFrom(400);
        splash.showSplash(args);
//        // Normally, we'd call splash.showSplash() and get on with the program.
//        // But, since this is only a test...
//        splash.showSplashAndExit(args);
    }

    // A simple little method to show a title screen in the center
    // of the screen for the amount of time given in the constructor
    public void showSplash(String args[]) {
        JPanel content = (JPanel) getContentPane();
        content.setBackground(Color.red);

        // Set the window's bounds, centering the window
        int width = 600;
        int height = 350;
        Dimension screen = getDefaultToolkit().getScreenSize();
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, width, height);

        // Build the splash screen
        ImageIcon imageIcon = createImageIcon("splash.png",
                "a pretty but meaningless splat");

        JLabel label = new JLabel(imageIcon);
        content.add(label, BorderLayout.CENTER);
        this.repaint();


        // Display it
        setVisible(true);

        // Wait a little while, maybe while loading resources
        try {
            Thread.sleep(duration);
            new Thread(new Runnable() {
                @Override
                public void run() {

                    new MainFrame("http://localhost:8090/",OS.isLinux(),false);
                }
            }).start();


        } catch (Exception e) {
        }

        setVisible(false);


        // The simple example application is created as anonymous class and points
        // to Google as the very first loaded page. If this example is used on
        // Linux, it's important to use OSR mode because windowed rendering is not
        // supported yet. On Macintosh and Windows windowed rendering is used as
        // default. If you want to test OSR mode on those platforms, simply replace
        // "OS.isLinux()" with "true" and recompile.
      //


    }

    public void showSplashAndExit(String args[]) {
        showSplash(args);
        System.exit(0);
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     */
    protected ImageIcon createImageIcon(String path,
                                        String description) {
        java.net.URL imgURL = getClass().getResource(path);

        URL resource = SplashScreenFrom.class.getResource("./splash.png");


        System.out.println(imgURL.getPath());
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}