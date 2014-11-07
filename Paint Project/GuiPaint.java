//Author: Stephen Mezzacapo
//October 14, 2014

package paint;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;

/**
 * Creates a top-level window which contains a Graphical User Interface used to
 * provide the ability to open and edit an image file. The layout for GuiPaint
 * is BorderLayout. At the top of the frame is a JMenuBar which contains a drop
 * down menu for file operations and a drop down menu for drawing operations.
 * The remainder of the frame contains a DrawingPanel object.
 *
 * @see DrawingPanel
 */
public class GuiPaint extends JFrame implements ActionListener {

    static DrawingPanel paintPanel; //White Rectangle for creating/editing images
    private JMenu fileOptions; //Drop down menu with file operations. 
    private JMenu drawingOptions; //Drop down menu with drawing features
    private JMenu colorOptions; //Drop down menu with color selections        
    static String currentColor = "000000"; //Sets the current color to be drawn with. Black by default. 
    static int currentColorValue = 0; //Corresponds to currentColor, necessary for paintComponent() method
    static String currentToolEnabled = null; //current drawing tool enabled
    ArrayList<MouseClass> currentMouseClass = new ArrayList<>(); //Stores reference to current MouseClass object of the drawing tool using the mouse listener
    int currentMouseClassCount = 0; //Keeps track of which MouseClass object is currently being used
    static int paintComponentOption = 0; //in paintComponent() method: 0 is default, 1 is if saveAs() method has been called
    static ArrayList<Integer> allDrawing = new ArrayList<>(); //Contains coordinates of all drawn lines
    static BufferedImage image = null; //The originally inputted image
    static JLabel imageSet; //Used to add the inputted image to the paintPanel
    static boolean hasBeenEdited = false; //Used to determine whether or not changes have been made to the inputted image
    static boolean currentSelect = false; //Used to determine whether or not the select tool is active

    /**
     * Constructs a new GuiPaint object that initially has a menu bar and a
     * blank DrawingPanel object.
     */
    public GuiPaint() {

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());  //Layout of the entire JFrame 

        JMenuBar menuBar = new JMenuBar(); //Located at the top of the border, contains all drop down menus. 
        this.add(menuBar, BorderLayout.PAGE_START); //Add menuBar to the top of the JFrame via Page_Start borderLayout feature

        fileOptions = new JMenu("File"); //New JMenu (drop down menu) with selections for various file operations
        fileOptions.setMnemonic(KeyEvent.VK_F); //Shortcut to open File Menu - 'Alt + F'
        menuBar.add(fileOptions); //add fileOptions to the JMenuBar

        setFileDrop(); //Method for initializing file menu options

        drawingOptions = new JMenu("Draw"); //Drop down Menu for Drawing Operations
        drawingOptions.setMnemonic(KeyEvent.VK_D); //Keyboard Shorcut: 'Alt + D'
        menuBar.add(drawingOptions);

        setDrawDrop(); //Method for initializing drawing menu options

        colorOptions = new JMenu("Color"); //Color Selection Menu
        colorOptions.setMnemonic(KeyEvent.VK_C); //Keyboard shorcut: 'Alt + C'
        menuBar.add(colorOptions);

        setColorDrop(); //Method for initializing color menu options

        paintPanel = new DrawingPanel(); //creates the panel on which images will be added/edited/etc
        paintPanel.setBackground(Color.white);

        this.add(paintPanel, BorderLayout.WEST); //Add paintPanel to the left side of the JFrame

        this.pack();
        this.setVisible(true);
    }//GuiPaint Constructor

    /**
     * Creates the left-most drop down menu on the menu bar. It contains items
     * for all file operations including Open, Save, Save As, New Drawing, and
     * Close. These items all also have keyboard shortcuts and an ActionListener
     * added.
     */
    public void setFileDrop() {
        JMenu openImage = new JMenu("Open"); //Submenu containing options to open a file from the user's directory or the internet.
        openImage.setMnemonic(KeyEvent.VK_O); //Shortcut to select Open - 'Alt + O'
        fileOptions.add(openImage);

        JMenuItem openImageDirectory = new JMenuItem("Open From Directory"); //Open image from user's directory
        openImageDirectory.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_D, ActionEvent.ALT_MASK)); //Add a keyboard shortcut 'Alt + D'
        openImageDirectory.addActionListener(this);
        openImage.add(openImageDirectory);

        JMenuItem openImageInternet = new JMenuItem("Open From Internet"); //Open image from internet URL
        openImageInternet.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_I, ActionEvent.ALT_MASK)); //Add a keyboard shortcut 'Alt + I'
        openImageInternet.addActionListener(this);
        openImage.add(openImageInternet);

        JMenuItem saveImage = new JMenuItem("Save"); //Option to save an image to a default location with default name
        saveImage.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Z, ActionEvent.ALT_MASK)); //Keyboard Shorcut: 'Alt + Z' //83
        saveImage.addActionListener(this);
        fileOptions.add(saveImage);

        JMenuItem saveImageAs = new JMenuItem("Save As"); //Save Image
        saveImageAs.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.ALT_MASK)); //Keyboard Shorcut: 'Alt + S' //83
        saveImageAs.addActionListener(this);
        fileOptions.add(saveImageAs);

        JMenuItem newDrawing = new JMenuItem("New Drawing"); //Option to create a new drawing
        newDrawing.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, ActionEvent.ALT_MASK)); //Keyboard Shortcut: 'Alt + N' //78
        newDrawing.addActionListener(this);
        fileOptions.add(newDrawing);

        JMenuItem close = new JMenuItem("Close"); //Close Window
        close.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_C, ActionEvent.ALT_MASK)); //Keyboard Shortcut: 'Alt + C'67
        close.addActionListener(this);
        fileOptions.add(close);

    }

    /**
     * Creates the second item on the menu bar. It contains items for all
     * drawing tools such as those for the Straight Line, Pencil, Select
     * Feature, and Drag Feature. All items have keyboard shortcuts and an
     * ActionListener added.
     */
    public void setDrawDrop() {
        JMenuItem straightLine = new JMenuItem("Straight Line"); //Draw a straight line
        straightLine.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_L, ActionEvent.ALT_MASK)); //Keyboard Shortcut: 'Alt + L'
        straightLine.addActionListener(this);
        drawingOptions.add(straightLine);

        JMenuItem pencil = new JMenuItem("Pencil"); //Free-draw with a pencil
        pencil.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_P, ActionEvent.ALT_MASK)); //Keyboard Shortcut: 'Alt + P'
        pencil.addActionListener(this);
        drawingOptions.add(pencil);

        JMenuItem select = new JMenuItem("Select"); //Select an image from the paintPanel
        select.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_X, ActionEvent.ALT_MASK)); //Keyboard Shortcut: 'Alt + X'
        select.addActionListener(this);
        drawingOptions.add(select);

        JMenuItem drag = new JMenuItem("Drag"); //Select an image from the paintPanel
        drag.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Z, ActionEvent.ALT_MASK)); //Keyboard Shortcut: 'Alt + Z'
        drag.addActionListener(this);
        drawingOptions.add(drag);
    }

    /**
     * Creates the drop down menu used to select the color of the chosen drawing
     * tool. 
     */
    public void setColorDrop() { //Constructs the color JMenu and all the JMenuItems within
        JMenuItem black = new JMenuItem("Black");
        black.addActionListener(this);
        black.setOpaque(true);
        black.setBackground(Color.black);
        colorOptions.add(black);

        //The following 5 blocks of code create menu items for selecting specific colors
        JMenuItem red = new JMenuItem("Red");
        red.addActionListener(this);
        red.setOpaque(true);
        red.setBackground(Color.red);
        colorOptions.add(red);

        JMenuItem blue = new JMenuItem("Blue");
        blue.addActionListener(this);
        blue.setOpaque(true);
        blue.setBackground(Color.blue);
        colorOptions.add(blue);

        JMenuItem green = new JMenuItem("Green");
        green.addActionListener(this);
        green.setOpaque(true);
        green.setBackground(Color.green);
        colorOptions.add(green);

        JMenuItem yellow = new JMenuItem("Yellow");
        yellow.addActionListener(this);
        yellow.setOpaque(true);
        yellow.setBackground(Color.yellow);
        colorOptions.add(yellow);
    }

    /**
     * Invoked when a JMenuItem from the JMenuBar is clicked.
     *
     * @param e Event which indicates which component-defined action occurred.
     */
    public void actionPerformed(ActionEvent e) {

        JMenuItem event = (JMenuItem) e.getSource(); //Detects the mouse click

        if (event.getActionCommand().equals("Open From Directory")) { //If you click open file            
            try {
                FileOpClass.openFile(); //Method to open files from user's directory
            } catch (IOException badImage2) { //If there is an IO error with the openFile() method
                System.out.println("Issue with openFile() method");
            }
        } else if (event.getActionCommand().equals("Open From Internet")) {
            try {
                FileOpClass.openImageInternet(); //Method to open files from internet
            } catch (IOException badImage3) {
                System.out.println("Issue with openImageInternet Method");
            }
        } else if (event.getActionCommand().equals("New Drawing")) { //If you click new drawing option
            FileOpClass.newDrawing(); //Method that clears the paint screen and resets it
        } else if (event.getActionCommand().equals("Save As")) { //If you click the save option
            paintComponentOption = 1;
            try {
                FileOpClass.saveImageAs(); //Method that opens the user's directory and allows them to choose where to save

                paintComponentOption = 0;
            } catch (IOException badImage) { //If there is an IO error with the saveImageAs method
                System.out.println("Issue with Save method");
            }
        } else if (event.getActionCommand().equals("Save")) { //Method that saves image to a default file            
            try {
                FileOpClass.saveImage(); //
            } catch (IOException badImage) { //If there is an IO error with the saveImageAs method
                System.out.println("Issue with Save method");
            }
        } else if (event.getActionCommand().equals("Straight Line")) {
            currentToolEnabled = "Straight Line";
            draw(currentToolEnabled); //Method used to draw with MouseClass depending on which tool is selected          
        } else if (event.getActionCommand().equals("Brush")) {
            //currently not implemented            
        } else if (event.getActionCommand().equals("Pencil")) {
            currentToolEnabled = "Pencil"; //Method used to draw with MouseClass depending on which tool is selected 
            draw(currentToolEnabled);
        } else if (event.getActionCommand().equals("Select")) {
            currentToolEnabled = "Select";

            draw(currentToolEnabled);
        } else if (event.getActionCommand().equals("Drag")) {
            currentToolEnabled = "Drag";
            currentSelect = true;
            draw(currentToolEnabled);
        } else if (event.getActionCommand().equals("Black")) { //The Following 5 'else if' statements set currentColor
            //to a string corresponding to the hex value of the chosen color         
            currentColor = "000000";
            currentColorValue = 0;
        } else if (event.getActionCommand().equals("Red")) {
            currentColor = "#FF0000";
            currentColorValue = 1;
        } else if (event.getActionCommand().equals("Blue")) {
            currentColor = "#0000FF";
            currentColorValue = 2;
        } else if (event.getActionCommand().equals("Green")) {
            currentColor = "#008000";
            currentColorValue = 3;
        } else if (event.getActionCommand().equals("Yellow")) {
            currentColor = "#FFFF00";
            currentColorValue = 4;
        } else {  //If you click close          
            //this.dispose(); //closes the window
            FileOpClass.closePaint(); //Close method
        }
    }//ActionPerformed()

     /**
     * Creates a new MouseClass object that has a MouseListener and a
     * MouseMotionListener added in order to allow for the user to control the
     * chosen drawing utensil (defined by the utensil parameter) via the mouse.
     * Previous instances of the MouseClass object will have the MouseListener
     * and MouseMotionListener removed in order to ensure that only one
     * MouseClass object is detecting mouse actions at a time.
     *
     * @param utensil a String corresponding to which drawing tool the user
     * selects
     * @see MouseClass
     * @see MouseListener
     * @see MouseMotionListener
     */
    public void draw(String utensil) { //This method creates an object of the MouseClass class which detects specific 
        //MouseEvents in order to draw a straight line of a chosen color.       
        hasBeenEdited = true;
        MouseClass currentMouseUse = new MouseClass(utensil); //Create a new mouse object for the chosen utensil   
        currentMouseClass.add(currentMouseUse); //add the new object to the currentMouseClass array
        currentMouseClassCount++;
        addMouseListener(currentMouseUse);  //Adds a MouseListener to the mouseUse object in order to detect clicks
        addMouseMotionListener(currentMouseUse); //Adds a MouseMotionListener to the mouseUse object in order to detect mouse dragging
        if (currentMouseClassCount > 1) { //The following code removes the mouseListener/MotionListener from the previous object
            MouseClass previousMouseUse = currentMouseClass.get(currentMouseClassCount - 2);
            removeMouseListener(previousMouseUse);
            removeMouseMotionListener(previousMouseUse);
        }
    }//DrawLine()

}//class GuiPaint

/**
 * Creates an initially blank panel that can have an image added on top of it.
 * This image can then be edited and have these alterations saved to a file. A
 * DrawingPanel object overrides the paintComponent(Graphics g) method of JPanel
 * in order to ensure that edits made to the image are not repainted over
 * whenever the overall GuiPaint window is resized.
 *
 * @see JPanel
 */
class DrawingPanel extends JPanel {

    ArrayList<BufferedImage> subList = new ArrayList<>(); //Contains all subimages of the original image (those selected with select tool)
    ArrayList<Integer> dragCoords = new ArrayList<>(); //Coordinates of the selected subimages

    /**
     * Paints graphics objects to the DrawingPanel using dimensions defined by
     * coordinates obtained by the MouseListener and MouseMotionListener added
     * to each instance of the MouseClass object.
     *
     * @param g Graphics object used to draw on the DrawingPanel. 
     * @see MouseClass
     * @see Graphics
     */
    @Override
    public void paintComponent(Graphics g) {
        if (GuiPaint.paintComponentOption == 0) { //If the saveAs method has not yet been called
            g = GuiPaint.paintPanel.getGraphics();
            if (MouseClass.straightLine.length > 3) { //If coordinates for a straight line or for the Pencil tool have been set by the mouse
                drawAndAddLines(g);
                if (!GuiPaint.allDrawing.isEmpty() && GuiPaint.currentToolEnabled.equals("Straight Line")) { //If a new straight line is being drawn 
                    //and previous straight lines have already been drawn
                    reDrawLines(g);
                }
            }
            if (MouseClass.currentDrag[2] != 0) {
                //If the saveAs method has been called and the select/drag methods have already been used. 
                //This allows for the changes made by select/drag to be reflected in the saved file. 
                drawAndAddDraggedImage(g); 
            }
        } else { //For save as method
            if (!GuiPaint.allDrawing.isEmpty()) {//If coordinates are present in allDrawing then redraw all lines after calling the saveAs method.
                reDrawLines(g);
            }
        }//If you are saving
        if (MouseClass.currentSelect[4] != 0) {//If coordinates for the select rectangle have been established
            g.drawRect(MouseClass.currentSelect[0], MouseClass.currentSelect[1], MouseClass.currentSelect[2], MouseClass.currentSelect[3]);
            MouseClass.currentSelect[4] = 0;
        }
        if (dragCoords.size() > 4) { //Draw the subimage in its new location. This is necessary to allow for multiple
            //Subimages to be visible at once. 
            reDrawImage(g);
        }
    }//paintComponent
    
    /**
     * Draw to the DrawingPanel the current line being drawn by the user. Then add
     * the relevant coordinates for this line to an array so that it can be redrawn
     * in the future. 
     * @param g Graphics object used to draw on the DrawingPanel. 
     * @see Graphics
     */
    public void drawAndAddLines(Graphics g) {
        g.setColor(Color.decode(GuiPaint.currentColor));
        g.drawLine(MouseClass.straightLine[0], MouseClass.straightLine[1], MouseClass.straightLine[2], MouseClass.straightLine[3]);
        if (MouseClass.straightLine.length > 4 && MouseClass.straightLine[4] == 1) {
            for (int i = 0; i < 4; i++) { //Add 4 coordinate points of a line to allDrawing
                GuiPaint.allDrawing.add(MouseClass.straightLine[i]);
            }
            GuiPaint.allDrawing.add(GuiPaint.currentColorValue); //add the color value to allDrawing after the coordinates
        }
    }//drawAndAddLines()

    /**
     * Redraw all lines that have previously been drawn by the user on the DrawingPanel. 
     * @param g Graphics object used to draw on the DrawingPanel. 
     * @see Graphics
     */
    public void reDrawLines(Graphics g) {

        for (int i = 0; i < GuiPaint.allDrawing.size(); i = i + 5) {
            int colorValue = GuiPaint.allDrawing.get(i + 4);
            GuiPaint.currentColor = getCurrentColor(colorValue);
            g.setColor(Color.decode(GuiPaint.currentColor));
            g.drawLine(GuiPaint.allDrawing.get(i), GuiPaint.allDrawing.get(i + 1), GuiPaint.allDrawing.get(i + 2), GuiPaint.allDrawing.get(i + 3));
        }
    }//reDrawLines()
    
    /**
     * While dragging the selected sub-image, draw the sub-image at the current
     * location of the mouse while also drawing a solid white rectangle at the
     * original location of the sub-image. Once the sub-image has been dragged to its
     * intended location, add both the sub-image and the coordinates of its location
     * to ArrayLists so that they can be stored and then later used in order to
     * redraw the sub-image in its appropriate location. 
     * @param g Graphics object used to draw on the DrawingPanel. 
     * @see Graphics
     */
    public void drawAndAddDraggedImage(Graphics g) {

        g.drawImage(MouseClass.sub, MouseClass.currentDrag[0] - 5, MouseClass.currentDrag[1] - 50, this);
        g.setColor(Color.WHITE);
        g.fillRect(MouseClass.currentSelect[0], MouseClass.currentSelect[1], MouseClass.currentSelect[2], MouseClass.currentSelect[3]);
        if (GuiPaint.currentSelect == true && MouseClass.finalYDrag != 0) {
            //If select has been used and a subimage is being dragged. This block allows for multiple uses of
            //select/drag to be used without repainting over previous uses. 
            subList.add(MouseClass.sub); //Add the current subimage to subList
            dragCoords.add(MouseClass.finalXDrag); //Add the final x coordinate of the dragged subimage
            dragCoords.add(MouseClass.finalYDrag); //Add the final y coordinate of the dragged subimage
            for (int i = 0; i < 4; i++) {
                dragCoords.add(MouseClass.currentSelect[i]);
            }
            reDrawImage(g);
        }
    }
    
    /**
     * Redraw all sub-images in order to ensure that all changes made by the 
     * select and drag tools are reflected in the DrawingPanel. 
     * @param g Graphics object used to draw on the DrawingPanel. 
     * @see Graphics
     */
    public void reDrawImage(Graphics g) {
        for (int i = 0; i < dragCoords.size(); i = i + 6) {
            g.drawImage(subList.get(i / 6), dragCoords.get(i), dragCoords.get(i + 1), this);
            g.setColor(Color.WHITE);
            g.fillRect(dragCoords.get(i + 2), dragCoords.get(i + 3), dragCoords.get(i + 4), dragCoords.get(i + 5));
        }
    }
    
    /**
     * Converts from a stored integer value representing the selected color at the
     * time the value was stored to a string value that can be used by the 
     * Color.decode() method in order to set the color of the graphics object. 
     * @param value an integer value representing the color of the line stored in an array. 
     * @return A String value corresponding to the color the line being drawn by the 
     * Graphics object was originally drawn in. 
     * @see Color
     */
    public String getCurrentColor(int value) { //getCurrentColor takes an int value and converts it to the appropriate
        //String value corresponding to the desired color. 
        switch (value) {
            case 0:
                return "000000";
            case 1:
                return "#FF0000";
            case 2:
                return "#0000FF";
            case 3:
                return "#008000";
            case 4:
                return "#FFFF00";
        }
        return "000000"; //Black by default
    }
}//class DrawingPanel
