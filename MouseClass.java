//Author: Stephen Mezzacapo
//October 14, 2014

package paint;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 * Detects mouse events and uses this information in order to define the dimensions
 * for Graphics objects that are to be drawn onto the DrawingPanel by the drawing
 * tools defined in GuiPaint. 
 * @see GuiPaint
 * @see Graphics
 * @see DrawingPanel
 */
public class MouseClass extends MouseAdapter
        implements MouseMotionListener {  

    /**
     * Constructs a MouseClass object which detects mouse events and obtains the
     * values of the dimensions for the appropriate Graphics objects to be drawn 
     * which is based upon the utensil parameter. 
     * @param utensil defines which drawing tool is currently enabled by the user. 
     */
    public MouseClass(String utensil) { 
        this.value = utensil;
    }
    private String value; //The current drawing utensil selected    
    private Point startPoint = null; //start point of the line to be drawn
    private Point endPoint = null;   //end point of the line to be drawn
    private Point currentPoint = null; //Current Point the mouse is hovering over while being dragged
    private int startX; //Starting x value of the select Rectangle
    private int startY; //Starting y value of the Select Rectangle
    private int height; //Height of the select rectangle
    private int width; //Width of the select rectangle
    static Graphics g = GuiPaint.paintPanel.getGraphics(); //new graphics object - within the paintPanel JPanel    
    static int[] straightLine = new int[5]; //Array containing the coordinates of the current line being drawn
    static int[] currentSelect = new int[5]; //Array containing the coordinates of the current rectangle being drawn
    static int[] currentDrag = new int [3]; //Array containing coordinates of where the subimage is currently being dragged
    private ImageIcon imageToMove = null; 
    static BufferedImage sub = null; //The subimage, created from the original inputted image
    static int finalXDrag = 0; //The x coordinate where the subimage being dragged is released
    static int finalYDrag = 0; //The y coordinate where the subimage being dragged is released
    
    /**
     * Detects when and where the mouse is first pressed and uses this information
     * to determine the starting point for certain drawing tools such as 'Straight Line',
     * 'Pencil', and 'Select'. 
     * @param e location of mouse
     */
    @Override
    public void mousePressed(MouseEvent e) { 
        g.setColor(Color.decode(GuiPaint.currentColor)); //Determines color based on the hex value of currentColor
        switch (value) { //Switch statement based on the current drawing utensil selected
            case "Straight Line":
                startPoint = e.getPoint();
                straightLine[0] = startPoint.x-9;
                straightLine[1] = startPoint.y-50;
                break;
            case "Pencil":
                startPoint = e.getPoint();
                currentPoint = e.getPoint();
                straightLine[0] = currentPoint.x-9;
                straightLine[1] = currentPoint.y-50;
                break;
            case "Select":
                startX = e.getX()-9;
                startY = e.getY()-50;               
                currentSelect[0] = startX;
                currentSelect[1] = startY;
                break;
            case "Drag":
                break;
        }
    }//mousePressed

    /**
     * Determines the coordinates of the mouse as it is dragged along the 
     * DrawingPanel in order to use these coordinates as dimensions to draw
     * Graphics objects in real time while the mouse is being moved by the user.
     * @param e location of mouse
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        switch (value) {
            case "Straight Line":
                endPoint = e.getPoint();
                straightLine[2] = endPoint.x-9;
                straightLine[3] = endPoint.y-50;
                GuiPaint.paintPanel.repaint(); //Repaint is necessary to prevent any coordinates not on the straight line from being drawn to
                GuiPaint.paintPanel.paintComponent(g); //The line is drawn via the paintComponent method. 
                GuiPaint.paintComponentOption = 1; //Access to the first if block of paintComponent, prevents access to the second
                GuiPaint.paintPanel.paintComponent(g);
                GuiPaint.paintComponentOption = 0; //Access to the second if block of paintComponent, prevents access to the first       
                break;
            case "Pencil":
                endPoint = e.getPoint(); //Current location of the mouse
                straightLine[2] = endPoint.x-9; 
                straightLine[3] = endPoint.y-50; 
                straightLine[4] = 1;
                GuiPaint.paintPanel.paintComponent(g);                      
                currentPoint = e.getPoint();
                straightLine[0] = currentPoint.x-9; //Overwrite starting x value
                straightLine[1] = currentPoint.y-50; //Overwrite starting y value
                break;
            case "Select": //Draw a rectangle that encompasses the boundaries of the subimage
                int currentX = e.getX()-9;
                int currentY = e.getY()-50;
                height = currentY - startY;
                width = currentX - startX;
                currentSelect[2] = width;
                currentSelect[3] = height;  
                currentSelect[4] = 1;
                GuiPaint.paintPanel.repaint(); //Repaint so that multiple rectangles are not made visible while dragging
                GuiPaint.paintPanel.paintComponent(g);               
                break;
            case "Drag": //Drag the subimage
                GuiPaint.paintPanel.repaint();
                currentDrag[0] = e.getX();
                currentDrag[1] = e.getY();
                currentDrag[2] = 1;
                GuiPaint.paintPanel.paintComponent(g);            
                break;
        }
    }//mouseDragged

    /**
     * Determines the coordinates of the location where the mouse is released.
     * These coordinates are used to set the end points of the drawing tools. 
     * @param e location of mouse
     */
    @Override
    public void mouseReleased(MouseEvent e) { 
        switch (value) {
            case "Straight Line":
                endPoint = e.getPoint();
                straightLine[2] = endPoint.x-9;
                straightLine[3] = endPoint.y-50;
                straightLine[4] = 1;
                GuiPaint.paintPanel.paintComponent(g);
                straightLine[4] = 0; //Prevents access to the if block of paintComponent that draws the straight lines
                break;
            case "Pencil":
                straightLine[4] = 0; //Prevents access to the if block of paintComponent that draws free form lines
                break;
            case "Select":
                int currentX = e.getX()-9;
                int currentY = e.getY()-50;
                height = currentY - startY;
                width = currentX - startX;
                currentSelect[2] = width;
                currentSelect[3] = height;  
                currentSelect[4] = 1;
                GuiPaint.paintPanel.paintComponent(g);   
                sub = GuiPaint.image.getSubimage(currentSelect[0], currentSelect[1], currentSelect[2], currentSelect[3]); 
                //create the subimage using the coordinates of the drawn rectangle
                break;
            case "Drag":
                currentDrag[0] = e.getX();
                currentDrag[1] = e.getY();
                finalXDrag = currentDrag[0]-5;
                finalYDrag = currentDrag[1]-50;
                GuiPaint.paintPanel.paintComponent(g);
                MouseClass.currentDrag[2]=0;
                finalXDrag = 0;
                finalYDrag = 0;
        }
    }//mouseReleased

    @Override
    public void mouseMoved(MouseEvent e) {
        //Not implemented
    }
}//class MouseClass