package com.codecool.battleshipoop;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class FieldPanel extends JPanel {

    private Game game = null;

    public void Init(Game game)
    {
        this.game = game;
    }

    public void paintComponent(Graphics originalGraphics) {

        super.paintComponent(originalGraphics);

        DrawAll((Graphics2D)originalGraphics, new Dimension(this.getWidth(), this.getHeight()));
    }


    private void DrawAll(Graphics2D g, Dimension panelSize)
    {
        // Background
        Rectangle2D backRectangle = new Rectangle2D.Double(0, 0, panelSize.width, panelSize.height);
        g.setPaint(Color.getHSBColor(0, 0, 0.2f));
        g.fill(backRectangle);

        // Boards
        float padding = 30;

        float fullWidth = panelSize.width - padding * 2;
        float fullHeight = panelSize.height - padding * 2;

        float boardsWidth = fullHeight * 2 + padding;
        float boardsHeight = fullHeight;

        if (boardsWidth > fullWidth)
        {
            boardsWidth = fullWidth;
            boardsHeight = (boardsWidth - padding) / 2;
        }

        Rectangle2D boardsRectangle = new Rectangle2D.Double((panelSize.width - boardsWidth) / 2, (panelSize.height - boardsHeight) / 2, boardsWidth, boardsHeight);
        DrawBoards(g, boardsRectangle);
    }


    private void DrawBoards(Graphics2D g, Rectangle2D boardsRectangle)
    {
        Rectangle2D boardRectangle1 = new Rectangle2D.Double(boardsRectangle.getX(), boardsRectangle.getY(), boardsRectangle.getHeight(), boardsRectangle.getHeight());
        Rectangle2D boardRectangle2 = new Rectangle2D.Double(boardsRectangle.getX() + (boardsRectangle.getWidth() - boardsRectangle.getHeight()), boardsRectangle.getY(), boardsRectangle.getHeight(), boardsRectangle.getHeight());

        DrawBoard(g, 0, boardRectangle1);
        DrawBoard(g, 1, boardRectangle2);
    }


    private void DrawBoard(Graphics2D g, int boardIndex, Rectangle2D boardRectangle)
    {
        g.setPaint(boardIndex == 0 ? Color.RED : Color.blue);
        g.fill(boardRectangle);
    }
}
