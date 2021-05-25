package com.codecool.battleshipoop;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class FieldPanel extends JPanel {

    public void paintComponent(Graphics originalGraphics) {

        float panelWidth = this.getWidth();
        float panelHeight = this.getHeight();


        super.paintComponent(originalGraphics);

        Graphics2D graphics = (Graphics2D)originalGraphics;


        Rectangle2D backRectangle = new Rectangle2D.Double(0, 0, panelWidth, panelHeight);

        graphics.setPaint(Color.getHSBColor(0, 0, 0.2f));
        graphics.fill(backRectangle);


        Rectangle2D rectangle = new Rectangle2D.Double((panelWidth - 200) / 2, (panelHeight - 200) / 2, 200, 200);

        graphics.setPaint(Color.RED);
        graphics.fill(rectangle);
    }

}
