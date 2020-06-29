/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QuanLyQuanCafe;

import java.awt.*;
import javax.swing.*;
/**
 *
 * @author namduong
 */
public class RoundedDecoration extends JPanel
{
    private GradientPaint backgroundColor;
    private int cornerRadius = 15;
    
    public RoundedDecoration(LayoutManager layout, int radius) {
        super(layout);
        cornerRadius = radius;
    }
    
    public RoundedDecoration(LayoutManager layout, int radius, GradientPaint bgColor) {
        super(layout);
        cornerRadius = radius;
        backgroundColor = bgColor;
    }

    public RoundedDecoration(int radius) {
        super();
        cornerRadius = radius;
    }

    public RoundedDecoration(int radius, GradientPaint bgColor) {
        super();
        cornerRadius = radius;
        backgroundColor = bgColor;
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension arcs = new Dimension(cornerRadius, cornerRadius);
        int width = getWidth();
        int height = getHeight();
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //Draws the rounded panel with borders. 
        GradientPaint gp = new GradientPaint(150,0,new Color(251,52,90),150,height,new Color(250,97,60));
                
        graphics.setPaint(gp);
        graphics.fillRoundRect(0, 0, width, height, arcs.width, arcs.height); //paint background
        graphics.setColor(getForeground());
    }
}