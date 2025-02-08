package xyz.moosh;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class OpenPdis {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OpenPdis::createControlPanel);
    }

    /**
     * Creates a simple control panel with a button to add PNG images.
     */
    private static void createControlPanel() {
        JFrame controlFrame = new JFrame("OpenPdis - Control Panel");
        controlFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        controlFrame.setSize(300, 100);
        controlFrame.setLayout(new FlowLayout());

        JButton addImageButton = new JButton("Add PNG");
        addImageButton.addActionListener(e -> openFileDialog());
        controlFrame.add(addImageButton);

        controlFrame.setLocationRelativeTo(null);
        controlFrame.setVisible(true);
    }

    /**
     * Opens the native file explorer in multi-selection mode.
     * For each selected PNG file, a new image window is created.
     */
    private static void openFileDialog() {
        // Create the native file dialog (AWT FileDialog) for loading files.
        FileDialog fileDialog = new FileDialog((Frame) null, "Select PNG Images", FileDialog.LOAD);
        fileDialog.setFilenameFilter((dir, name) -> name.toLowerCase().endsWith(".png"));
        fileDialog.setMultipleMode(true); // Allow selection of multiple files
        fileDialog.setVisible(true);

        // Retrieve the selected files.
        File[] selectedFiles = fileDialog.getFiles();
        if (selectedFiles == null || selectedFiles.length == 0) {
            System.out.println("No file selected.");
            return;
        }

        // For each selected PNG, open a new image window.
        for (File file : selectedFiles) {
            openImageWindow(file);
        }
    }

    /**
     * Loads the image from the given file and displays it in an undecorated,
     * draggable JFrame.
     */
    private static void openImageWindow(File file) {
        try {
            BufferedImage img = ImageIO.read(file);
            if (img == null) {
                JOptionPane.showMessageDialog(null, "Selected file is not a valid image: " + file.getName());
                return;
            }

            // Create an undecorated frame so only the image is visible.
            JFrame frame = new JFrame();
            frame.setUndecorated(true);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // Display the image in a JLabel.
            JLabel imageLabel = new JLabel(new ImageIcon(img));
            frame.getContentPane().add(imageLabel);
            frame.pack();
            frame.setLocationRelativeTo(null);

            // Create a MouseAdapter to enable dragging the window.
            MouseAdapter dragListener = new MouseAdapter() {
                Point mouseDownCompCoords = null;

                @Override
                public void mousePressed(MouseEvent e) {
                    mouseDownCompCoords = e.getPoint();
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    Point currCoords = e.getLocationOnScreen();
                    frame.setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
                }
            };

            // Attach the drag listener to both the frame and the image label.
            frame.addMouseListener(dragListener);
            frame.addMouseMotionListener(dragListener);
            imageLabel.addMouseListener(dragListener);
            imageLabel.addMouseMotionListener(dragListener);

            // Show the image window.
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading image " + file.getName() + ": " + e.getMessage());
        }
    }
}
