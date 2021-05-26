package com.codecool.battleshipoop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.*;
import java.awt.image.ImageObserver;
import java.util.Timer;
import java.util.TimerTask;

public class FieldPanel extends JPanel {

    private Game game = null;
    private Timer clock;

    private FieldMouseEvents mouseEvents;

    private Rectangle2D boardRectangle1;
    private Rectangle2D boardRectangle2;

    private float cellSize;

    public Point2D board1Highlight = null;
    public Point2D board2Highlight = null;

    private boolean firstFrame = true;

    private float padding = 30;

    public static Color[] playerColor = new Color[] {
            Util.rgbColor(252, 150, 150),
            new Color(0.96f, 0.96f, 0.48f)
    };

    public static Color oceanColor = Util.rgbColor(147, 202, 248);

    private ImageIcon shipFront;
    private ImageIcon shipMiddle;
    private ImageIcon shipRear;
    private ImageIcon shipSmall;



    public ImageIcon shipPartIcon(ShipPart part)
    {
        if (part == ShipPart.Front)
            return shipFront;
        if (part == ShipPart.Middle)
            return shipMiddle;
        if (part == ShipPart.Rear)
            return shipRear;
        if (part == ShipPart.Small)
            return shipSmall;

        return null;
    }

    public boolean mouseInBoard() {
        return mouseEvents.mousePosition != null;
    }

    public void init(Game game) {
        this.game = game;
        this.clock = new Timer();
        this.clock.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                clockTick();
            }
        }, 50, 50);

        setFont(getFont().deriveFont(18f));

        mouseEvents = new FieldMouseEvents();
        addMouseListener(mouseEvents);
        addMouseMotionListener(mouseEvents);

        LoadImages();
    }

    private void LoadImages()
    {
        ClassLoader classLoader = this.getClass().getClassLoader();

        java.net.URL frontUrl = classLoader.getResource("images/ship_front.png");
        java.net.URL middleUrl = classLoader.getResource("images/ship_middle.png");
        java.net.URL rearUrl = classLoader.getResource("images/ship_rear.png");
        java.net.URL smallUrl = classLoader.getResource("images/ship_small.png");

        shipFront = new ImageIcon(frontUrl);
        shipMiddle = new ImageIcon(middleUrl);
        shipRear = new ImageIcon(rearUrl);
        shipSmall = new ImageIcon(smallUrl);
    }

    public void clockTick() {
        this.repaint();
    }

    public void paintComponent(Graphics originalGraphics) {

        super.paintComponent(originalGraphics);

        if (!firstFrame)
            handleMouseEvents();

        drawAll((Graphics2D) originalGraphics, new Dimension(this.getWidth(), this.getHeight()));

        firstFrame = false;

        mouseEvents.reset();
    }


    private void handleMouseEvents()
    {
        if (!mouseInBoard())
        {
            board1Highlight = board2Highlight = null;
            return;
        }

        // egér a board 1-en
        if (boardRectangle1.contains(mouseEvents.mousePosition))
        {
            Point2D mouseInBoard = new Point2D.Double(mouseEvents.mousePosition.getX() - boardRectangle1.getX(), mouseEvents.mousePosition.getY() - boardRectangle1.getY());
            board1Highlight = new Point2D.Double(Math.floor(mouseInBoard.getX() / cellSize), Math.floor(mouseInBoard.getY() / cellSize));
        }
        else
            board1Highlight = null;

        // egér a board 2-n
        if (boardRectangle2.contains(mouseEvents.mousePosition))
        {
            Point2D mouseInBoard = new Point2D.Double(mouseEvents.mousePosition.getX() - boardRectangle2.getX(), mouseEvents.mousePosition.getY() - boardRectangle2.getY());
            board2Highlight = new Point2D.Double(Math.floor(mouseInBoard.getX() / cellSize), Math.floor(mouseInBoard.getY() / cellSize));
        }
        else
            board2Highlight = null;
    }


    private void drawAll(Graphics2D g, Dimension panelSize) {
        // Background
        Rectangle2D backRectangle = new Rectangle2D.Double(0, 0, panelSize.width, panelSize.height);
        g.setPaint(Color.getHSBColor(0, 0, 0.2f));
        g.fill(backRectangle);


        // Boards
        Rectangle2D workingArea = new Rectangle2D.Double(0, padding, panelSize.width, panelSize.height - padding);


        float fullWidth = (int)workingArea.getWidth() - padding * 2;
        float fullHeight = (int)workingArea.getHeight() - padding * 2;

        float boardsWidth = fullHeight * 2 + padding;
        float boardsHeight = fullHeight;

        if (boardsWidth > fullWidth) {
            boardsWidth = fullWidth;
            boardsHeight = (boardsWidth - padding) / 2;
        }

        Rectangle2D boardsRectangle = new Rectangle2D.Double(workingArea.getX() + (workingArea.getWidth() - boardsWidth) / 2, workingArea.getY() + (workingArea.getHeight() - boardsHeight) / 2, boardsWidth, boardsHeight);
        drawBoards(g, boardsRectangle);
    }


    private void drawBoards(Graphics2D g, Rectangle2D boardsRectangle) {
        boardRectangle1 = new Rectangle2D.Double(boardsRectangle.getX(), boardsRectangle.getY(), boardsRectangle.getHeight(), boardsRectangle.getHeight());
        boardRectangle2 = new Rectangle2D.Double(boardsRectangle.getX() + (boardsRectangle.getWidth() - boardsRectangle.getHeight()), boardsRectangle.getY(), boardsRectangle.getHeight(), boardsRectangle.getHeight());

        drawBoard(g, 0, boardRectangle1);
        drawBoard(g, 1, boardRectangle2);

        drawBoardHighlight(g, boardRectangle1, board1Highlight, playerColor[0]);
        drawBoardHighlight(g, boardRectangle2, board2Highlight, playerColor[1]);
    }


    private void drawBoard(Graphics2D g, int boardIndex, Rectangle2D boardRectangle) {
        String playerText = "Player " + ((Integer)(boardIndex + 1));
        int playerTextWidth = g.getFontMetrics().stringWidth(playerText);

        g.setPaint(playerColor[boardIndex]);
        g.drawString(playerText, (int)boardRectangle.getX() + (int)(boardRectangle.getWidth() / 2) - (int)((float)playerTextWidth / 2f), (int)boardRectangle.getY() - padding / 2);

        // OCEAN
        g.setPaint(oceanColor);
        g.fill(boardRectangle);

        drawGrid(g, boardRectangle);

        // DEBUG SHIPS (removable)
        drawShipPart(g, boardRectangle, new Point2D.Double(0, 0), ShipPart.Front);
        drawShipPart(g, boardRectangle, new Point2D.Double(1, 0), ShipPart.Middle);
        drawShipPart(g, boardRectangle, new Point2D.Double(2, 0), ShipPart.Rear);
        drawShipPart(g, boardRectangle, new Point2D.Double(3, 3), ShipPart.Small);
    }

    private void drawBoardHighlight(Graphics2D g, Rectangle2D boardRectangle, Point2D highlightPosition, Color color)
    {
        if (highlightPosition == null)
            return;

        Rectangle2D highlightRectangle = new Rectangle2D.Double(boardRectangle.getX() + highlightPosition.getX() * cellSize, boardRectangle.getY() + highlightPosition.getY() * cellSize, cellSize, cellSize);



        g.setPaint(Util.fade(color, 0.65f));
        g.fill(highlightRectangle);
    }

    private void drawGrid(Graphics2D g, Rectangle2D boardRectangle) {
        g.setPaint(Color.BLACK);
        int boardSize = game.getBoardSize();
        cellSize = (float) boardRectangle.getWidth() / boardSize;

        for (int i = 1; i < boardSize; i++) {
            Line2D horizontal = new Line2D.Double(boardRectangle.getX(), boardRectangle.getY() + i * cellSize, boardRectangle.getX() + boardRectangle.getWidth(), boardRectangle.getY() + i * cellSize);
            g.draw(horizontal);

            Line2D vertical = new Line2D.Double(boardRectangle.getX() + i * cellSize, boardRectangle.getY(), boardRectangle.getX() + i * cellSize, boardRectangle.getY() + boardRectangle.getHeight());
            g.draw(vertical);
        }
    }

    private void drawShipPart(Graphics2D g, Rectangle2D boardRectangle, Point2D cell, ShipPart part)
    {
        Rectangle2D partRectangle = new Rectangle2D.Double(boardRectangle.getX() + cellSize * cell.getX(), boardRectangle.getY() + cellSize * cell.getY(), cellSize, cellSize);
        Image partIcon = shipPartIcon(part).getImage();


        AffineTransform backup = Util.rotateGraphics(g, 0, (float) partRectangle.getX() + cellSize / 2f, (float) partRectangle.getY() + cellSize / 2);
        g.drawImage(partIcon, (int)partRectangle.getX(), (int)partRectangle.getY(), (int)partRectangle.getWidth(), (int)partRectangle.getHeight(), null);
        g.setTransform(backup);
    }

}

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
        // KATTINTÁS

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
