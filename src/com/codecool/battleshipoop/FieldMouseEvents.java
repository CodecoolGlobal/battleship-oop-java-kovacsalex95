package com.codecool.battleshipoop;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;

class FieldMouseEvents implements MouseListener, MouseMotionListener {

    public Point2D mousePosition = null;
    public boolean mouseLeftDown = false;
    public boolean mouseRightDown = false;
    public boolean mouseLeftClick = false;
    public boolean mouseRightClick = false;

    public void reset()
    {
        mouseLeftClick = false;
        mouseRightClick = false;
    }

    // MOUSE EVENTS
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == 1)
            mouseLeftClick = true;
        if (e.getButton() == 2)
            mouseRightClick = true;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // EGÉR GOMB LE

        if (e.getButton() == 1)
            mouseLeftDown = true;
        if (e.getButton() == 2)
            mouseRightDown = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // EGÉR GOMB FEL

        if (e.getButton() == 0)
            mouseLeftDown = false;
        if (e.getButton() == 1)
            mouseRightDown = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // EGÉR BELÉPETT
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // EGÉR KILÉPETT
        mousePosition = null;
        mouseLeftDown = false;
        mouseRightDown = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // EGÉR HÚZOTT
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // EGÉR MOZGOTT
        mousePosition = e.getPoint();
    }
}