//Author: Stephen Mezzacapo
//October 14, 2014
package paint;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileOpClass {
    
    /**
     * The name of the file from which the image being edited was opened. This field
     * provides a default file name to be used by the 'Save' Button if no new file name
     * is chosen. 
     */
    private static String imageDefaultName; 

    /**
     * Opens a selected image file using a JFileChooser object. The image is set
     * to a JLabel on the DrawingPanel as an ImageIcon
     *
     * @throws IOException if there is an error opening the selected file.
     * @see JFileChooser
     * @see JLabel
     * @see ImageIcon
     */
    public static void openFile() throws IOException {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter( //Choose which file extensions to allow
                "JPG Images", "jpg");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(GuiPaint.paintPanel); //Opens a dialogue window above paintPanel showing user's directory
        if (returnVal == JFileChooser.APPROVE_OPTION) { //If the chosen file is valid
            File imageFile = new File(chooser.getSelectedFile().getPath()); //Select the image file

            imageDefaultName = chooser.getSelectedFile().getName();
            GuiPaint.image = ImageIO.read(imageFile); //Convert the imgae file to a bufferedImage  

            GuiPaint.imageSet = new JLabel();
            GuiPaint.paintPanel.add(GuiPaint.imageSet); //Add a new JLabel to paintPanel
            GuiPaint.imageSet.setIcon(new ImageIcon(GuiPaint.image)); //Add selected image to JLabel label

        }
    }//openFile()

    /**
     * Choose a name and directory location for a file containing the image
     * using a JFileChooser object. A BufferedImage is created using the
     * dimensions of the JLabel containing the image and is painted with a
     * Graphics object. This BufferedImage object is then saved to the selected
     * location with the chosen name.
     *
     * @throws IOException if there is an error saving the image to a file.
     * @see JFileChooser
     * @see BufferedImage
     * @see Graphics
     */
    public static void saveImageAs() throws IOException { //Choose image name and save it to chosen location
        //Currently the user has to include the file extension while saving. This will be fixed in the next version.

        //The Following three lines select the image from the JFrame and convert it to a format which can be saved
        BufferedImage bi = new BufferedImage(GuiPaint.imageSet.getWidth(), GuiPaint.imageSet.getHeight(), BufferedImage.TYPE_INT_ARGB); //Create a buffered image from image within dimensions of JFrame
        Graphics g2 = bi.createGraphics();
        GuiPaint.imageSet.paint(g2);
        GuiPaint.paintPanel.paintComponent(g2);
        JFileChooser c = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter( //Select which file extensions are valid
                "JPG, GIF, PNG Images", "jpg", "gif", "png");
        c.setFileFilter(filter);
        JPanel saveLocation = new JPanel();
        GuiPaint.paintPanel.add(saveLocation, BorderLayout.EAST);
        int rVal = c.showSaveDialog(saveLocation); //Open directory window above paintPanel
        if (rVal == JFileChooser.APPROVE_OPTION) { //If selected file is valid
            File f = c.getSelectedFile();  //Select the file name
            String path = f.getAbsolutePath(); //Determine where the file is located in the directory
            File outputfile = new File(path);   //Create output file with selected path           
            ImageIO.write(bi, "png", outputfile); //Write file with the selected name to the selected location     
        }
    }//saveImageAs()

    /**
     * Saves the image to a default location - the location where the image was
     * originally opened from.
     *
     * @throws IOException
     */
    public static void saveImage() throws IOException {
        if (imageDefaultName != null) {
            BufferedImage bi = new BufferedImage(GuiPaint.imageSet.getWidth(), GuiPaint.imageSet.getHeight(), BufferedImage.TYPE_INT_ARGB); //Create a buffered image from image within dimensions of JFrame
            Graphics g2 = bi.createGraphics();
            GuiPaint.imageSet.paint(g2);
            GuiPaint.paintPanel.paintComponent(g2);
            try { //Use the location from which the inputted image originated to save. 
                File output = new File(imageDefaultName);
                ImageIO.write(bi, "png", output);
            } catch (Exception e) {
                System.out.println("Error Saving to Default Location");
            }
        }
        else {
            System.out.println("There is no original image to save!");
        }
    }

    /**
     * Clears the current image and resets the DrawingPanel to be blank.
     */
    public static void newDrawing() {
        GuiPaint.paintPanel.removeAll(); //Remove everything on paintPanel
        GuiPaint.paintPanel.revalidate(); //Reset paint panel's original state
        GuiPaint.paintPanel.repaint();    //Reset paint panel's original state
    }//newDrawing()

    /**
     * Closes the window and exits the program. If any edits to the image have
     * been made, the user will first be prompted to save.
     */
    public static void closePaint() { //Closes the window, but first prompts the user to save. 
        if (GuiPaint.hasBeenEdited) { //If edits have been made, a prompt will appear
            GuiPaint.paintComponentOption = 1;
            int x = JOptionPane.showConfirmDialog(
                    GuiPaint.paintPanel,
                    "Changes have been made, would you like to save?",
                    "To Save or not to Save",
                    JOptionPane.YES_NO_CANCEL_OPTION);

            if (x == 0) { //Yes
                try {
                    FileOpClass.saveImageAs(); //Call save as before closing

                } catch (Exception e) {
                    System.out.println("Error in attempting to save after selecting close");
                }
                System.exit(0);
            } else if (x == 1) { //No
                System.exit(0);
            } else { //Cancel
                GuiPaint.paintComponentOption = 0;
                return;
            }
        }//If the file has been edited
        System.exit(0);
    }//closePaint()

    /**
     * A dialogue box is open that prompts the user to input a URL for an image
     * from the Internet. This URL is read and converted into a BufferedImage
     * which is then set to a JLabel on the DrawingPanel as an ImageIcon.
     *
     * @throws IOException if the URL is invalid or there is an error in opening
     * it.
     * @see BufferedImage
     * @see JLabel
     * @see ImageIcon
     */
    public static void openImageInternet() throws IOException {
        String s = (String) JOptionPane.showInputDialog(
                GuiPaint.paintPanel,
                "Please Enter a URL",
                "URL Input",
                JOptionPane.PLAIN_MESSAGE); //Open a dialogue box with a text field to input a URL and 'OK' + 'Cancel' Buttons
        try {
            BufferedImage img = ImageIO.read(new URL(s)); //Create a bufferedImage from the URL
            JLabel label = new JLabel();
            GuiPaint.paintPanel.add(label); //Add a new JLabel to paintPanel

            label.setIcon(new ImageIcon(img)); //Add selected image to JLabel label
        } catch (Exception badURL) {
            System.out.println("Your URL should end in a .jpg file extension");
            return; //Exit the method if an invalid URL is inputted. The original paint window remains open. 
        }
    }//openImageInternet() method

}
