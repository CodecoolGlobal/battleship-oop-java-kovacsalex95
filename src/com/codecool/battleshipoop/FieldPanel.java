package com.codecool.battleshipoop;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.Timer;
import java.util.TimerTask;

public class FieldPanel extends JPanel {

    private Game game = null;

    public FieldMouseEvents mouseEvents;

    private float cellSize = 0;

    private Rectangle2D[] boardRectangle;
    public Point2D[] boardHighlight = null;

    private boolean firstFrame = true;

    public boolean creditsMode = false;

    private ImageIcon shipFront;
    private ImageIcon shipMiddle;
    private ImageIcon shipRear;
    private ImageIcon shipSmall;

    private ImageIcon explosionIcon;

    private ImageIcon oceanImage;

    private ImageIcon logoImage;

    public boolean madness = false;
    public float madnessAmount = 0;

    private float explosionRotation = 0;

    private final float padding = 30;

    public final Color[] playerColor = new Color[]{
            Util.rgbColor(252, 150, 150),
            new Color(0.96f, 0.96f, 0.48f)
    };

    public final Color oceanColor = Util.rgbColor(147, 202, 248);

    public ImageIcon shipPartIcon(ShipPart part) {
        if (part == ShipPart.FRONT)
            return shipFront;
        if (part == ShipPart.MIDDLE)
            return shipMiddle;
        if (part == ShipPart.REAR)
            return shipRear;
        if (part == ShipPart.SMALL)
            return shipSmall;

        return null;
    }

    public boolean mouseInBoard() {
        return mouseEvents.mousePosition != null;
    }


    // MISC
    public void init(Game game) {

        this.game = game;
        boardRectangle = new Rectangle2D[2];
        boardHighlight = new Point2D[] {null, null};

        // Órajel (framek)
        Timer clock = new Timer();
        clock.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                clockTick();
            }
        }, 20, 20);

        // Betűméret
        setFont(getFont().deriveFont(18f));

        // Egér esemélyek
        mouseEvents = new FieldMouseEvents();
        addMouseListener(mouseEvents);
        addMouseMotionListener(mouseEvents);

        // Képek betöltése
        LoadImages();
    }

    private void LoadImages() {
        ClassLoader classLoader = this.getClass().getClassLoader();

        java.net.URL frontUrl = classLoader.getResource("images/ship_front.png");
        java.net.URL middleUrl = classLoader.getResource("images/ship_middle.png");
        java.net.URL rearUrl = classLoader.getResource("images/ship_rear.png");
        java.net.URL smallUrl = classLoader.getResource("images/ship_small.png");

        java.net.URL explosionUrl = classLoader.getResource("images/explosion.png");

        java.net.URL oceanUrl = classLoader.getResource("images/ocean.jpg");

        java.net.URL logoUrl = classLoader.getResource("images/logo.png");

        shipFront = new ImageIcon(frontUrl);
        shipMiddle = new ImageIcon(middleUrl);
        shipRear = new ImageIcon(rearUrl);
        shipSmall = new ImageIcon(smallUrl);

        explosionIcon = new ImageIcon(explosionUrl);

        oceanImage = new ImageIcon(oceanUrl);

        logoImage = new ImageIcon(logoUrl);
    }

    public void clockTick() {
        this.repaint();
    }


    // FRAMEK
    public void paintComponent(Graphics originalGraphics) {

        super.paintComponent(originalGraphics);

        Frame((Graphics2D) originalGraphics);
    }


    private void Frame(Graphics2D g) {

        // Madness = Rotate boards
        if (madness) {
            madnessAmount += 5;
            if (madnessAmount > 360)
                madnessAmount -= 360;
        }

        explosionRotation++;

        // game not started yet
        if (game.gameState == GameState.NOT_STARTED) {
            //g.drawString("Press NEW GAME to START", this.getWidth() / 2 - 110, this.getHeight() / 2);

            float logoSize = Math.min(this.getWidth(), this.getHeight());
            g.drawImage(logoImage.getImage(), (int)(this.getWidth() - logoSize) / 2, (int)(this.getHeight() - logoSize) / 2, (int)logoSize, (int)logoSize, null);
            return;
        }

        // Egér kezelése
        if (!firstFrame)
            handleMouseEvents();

        // Rajzolás
        drawElements(g, new Dimension(this.getWidth(), this.getHeight()));

        // Játéklogika frissítése
        game.Update();

        // Reset
        firstFrame = false;
        mouseEvents.reset();
    }


    private void handleMouseEvents() {

        if (!mouseInBoard())
        {
            boardHighlight = new Point2D[] {null, null};
            return;
        }

        // egér a board 1-en
        for (int boardIndex = 0; boardIndex < boardRectangle.length; boardIndex++)
        {
            if (boardRectangle[boardIndex].contains((mouseEvents.mousePosition)))
            {
                Point2D mouseInBoard = new Point2D.Double(mouseEvents.mousePosition.getX() - boardRectangle[boardIndex].getX(), mouseEvents.mousePosition.getY() - boardRectangle[boardIndex].getY());
                boardHighlight[boardIndex] = new Point2D.Double(Math.floor(mouseInBoard.getX() / cellSize), Math.floor(mouseInBoard.getY() / cellSize));
            }
            else
                boardHighlight[boardIndex] = null;
        }
    }


    // RAJZOLÁS
    private void drawElements(Graphics2D g, Dimension panelSize) {

        // Rotate boards
        AffineTransform backup = g.getTransform();
        if (madness) Util.rotateGraphics(g, madnessAmount, (float)panelSize.width / 2, (float)panelSize.height / 2);


        // Boards
        Rectangle2D workingArea = new Rectangle2D.Double(0, padding, panelSize.width, panelSize.height - padding);


        if (creditsMode) {
            drawCredits(g, workingArea);
            return;
        }

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

        if (game.gameState == GameState.END) {
            int winner = (game.player == 0 ? 1 : 0) + 1;
            String winnerText = "The winner is Player " + ((Integer) winner) + "!";
            int winnerTextWidth = g.getFontMetrics().stringWidth(winnerText);

            g.setPaint(Util.rgbAColor(255, 255, 255, 0.5f));
            g.fill(boardsRectangle);
            g.setPaint(Color.black);
            g.drawString(winnerText, (int)boardsRectangle.getX() + (int)(boardsRectangle.getWidth() / 2) - winnerTextWidth / 2, (int)boardsRectangle.getY() + (int)(boardsRectangle.getHeight() / 2));
        }
        else if (game.gameState == GameState.PREPARE_TO_ATTACK) {
            String prepareText = "Player " + ((Integer) (game.player + 1)) + " prepare to attack";
            int prepareTextWidth = g.getFontMetrics().stringWidth(prepareText);

            g.setPaint(Util.rgbAColor(255, 255, 255, 0.5f));
            g.fill(boardsRectangle);
            g.setPaint(Color.black);
            g.drawString(prepareText, (int)boardsRectangle.getX() + (int)(boardsRectangle.getWidth() / 2) - prepareTextWidth / 2, (int)boardsRectangle.getY() + (int)(boardsRectangle.getHeight() / 2));
        }

        g.setTransform(backup);
    }


    private void drawBoards(Graphics2D g, Rectangle2D boardsRectangle) {

        boardRectangle[0] = new Rectangle2D.Double(boardsRectangle.getX(), boardsRectangle.getY(), boardsRectangle.getHeight(), boardsRectangle.getHeight());
        boardRectangle[1] = new Rectangle2D.Double(boardsRectangle.getX() + (boardsRectangle.getWidth() - boardsRectangle.getHeight()), boardsRectangle.getY(), boardsRectangle.getHeight(), boardsRectangle.getHeight());

        cellSize = (float)boardRectangle[0].getWidth() / game.boardSize;

        drawBoard(g, 0);
        drawBoard(g, 1);
    }


    private void drawBoard(Graphics2D g, int boardIndex) {

        // Player név szöveg
        String playerText = "Player " + ((Integer)(boardIndex + 1));
        int playerTextWidth = g.getFontMetrics().stringWidth(playerText);

        g.setPaint(playerColor[boardIndex].darker());
        g.drawString(playerText, (int)boardRectangle[boardIndex].getX() + (int)(boardRectangle[boardIndex].getWidth() / 2) - (int)((float)playerTextWidth / 2f), (int)boardRectangle[boardIndex].getY() - padding / 2);

        // Háttér
        g.drawImage(oceanImage.getImage(), (int)Math.floor(boardRectangle[boardIndex].getX()), (int)Math.floor(boardRectangle[boardIndex].getY()), (int)Math.ceil(boardRectangle[boardIndex].getWidth()), (int)Math.ceil(boardRectangle[boardIndex].getHeight()), null);


        if (game.frozeState == 2) {
            if (boardIndex != game.player) {
                g.setPaint(Util.rgbAColor(255, 255, 255, 0.6f));
                g.fill(boardRectangle[boardIndex]);

                String prepareText = "Player " + ((Integer) (boardIndex + 1)) + ": prepare to attack!";
                int prepareTextWidth = g.getFontMetrics().stringWidth(prepareText);

                g.setPaint(Color.black);
                g.drawString(prepareText, (int)boardRectangle[boardIndex].getX() + (int)(boardRectangle[boardIndex].getWidth() / 2) - prepareTextWidth / 2, (int)boardRectangle[boardIndex].getY() + (int)(boardRectangle[boardIndex].getHeight() / 2));
            }

            return;
        }

        if (game.gameState == GameState.PREPARE_TO_ATTACK) {
            return;
        }

        boolean placementPreviewActive = game.gameState == GameState.PLACEMENT && boardIndex == game.player && game.shipPlacementPoint != null;

        // Rács
        if ((game.gameState == GameState.PLACEMENT && boardIndex == game.player) || (game.gameState == GameState.ATTACK && boardIndex != game.player)) {
            if (!placementPreviewActive && game.frozeState == 0)
                drawBoardHighlight(g, boardIndex);
            drawGrid(g, boardIndex);
        }

        if (placementPreviewActive) {
            drawPlacementPreview(g, boardIndex);
        }

        // Hajók
        if (!madness && (boardIndex == game.player || game.gameState == GameState.END)) {
            if (game.playerShips == null || game.playerShips[boardIndex] == null)
                return;

            for (int i = 0; i < game.playerShips[boardIndex].length; i++) {
                drawShip(g, boardIndex, game.playerShips[boardIndex][i]);
            }
        }

        // Találatok
        if (game.gameState == GameState.ATTACK && boardIndex != game.player)
            drawAttackPoints(g, boardIndex);
    }


    private void drawAttackPoints(Graphics2D g, int boardIndex) {
        Point2D[] hits = game.playerHits[boardIndex == 0 ? 1 : 0];
        Ship[] ships = game.playerShips[boardIndex];

        for (int i=0; i<hits.length; i++)
        {
            boolean shipHit = false;

            for (int j = 0; j<ships.length; j++)
            {
                for (int k = 0; k<ships[j].shipPieces.length; k++)
                {
                    if (ships[j].shipPieces[k].position.equals(hits[i])) {
                        shipHit = true;
                        break;
                    }
                }
                if (shipHit)
                    break;
            }

            Rectangle2D partRectangle = new Rectangle2D.Double(boardRectangle[boardIndex].getX() + cellSize * hits[i].getX(), boardRectangle[boardIndex].getY() + cellSize * hits[i].getY(), cellSize, cellSize);

            if (shipHit)
                g.drawImage(explosionIcon.getImage(), (int)Math.floor(partRectangle.getX()), (int)Math.floor(partRectangle.getY()), (int)Math.ceil(partRectangle.getWidth()), (int)Math.ceil(partRectangle.getHeight()), null);
            else {
                g.setPaint(Util.rgbAColor(0, 0, 0, 0.6f));
                g.fill(partRectangle);
            }
        }
    }


    private void drawPlacementPreview(Graphics2D g, int boardIndex) {

        // Hajó mérete
        int shipSize = game.SHIP_COUNT;
        if (game.playerShips != null && game.playerShips[boardIndex] != null)
            shipSize = game.SHIP_COUNT - game.playerShips[boardIndex].length;


        if (boardHighlight == null || boardHighlight[boardIndex] == null) return;


        Point2D[] placementPoints = Game.getPlacementPoints(game.shipPlacementPoint, boardHighlight[boardIndex], shipSize, game.boardSize, game.playerShips[boardIndex]);
        if (placementPoints == null)
            return;

        for (int i=0; i<placementPoints.length; i++)
        {
            drawCellHighlight(g, placementPoints[i], Util.fade(playerColor[boardIndex], 0.4f), boardRectangle[boardIndex], cellSize);
        }
    }


    private void drawBoardHighlight(Graphics2D g, int boardIndex) {

        if (boardHighlight == null || boardHighlight[boardIndex] == null)
            return;

        drawCellHighlight(g, boardHighlight[boardIndex], Util.fade(playerColor[boardIndex], 0.65f), boardRectangle[boardIndex], cellSize);
    }


    private void drawGrid(Graphics2D g, int boardIndex) {

        g.setPaint(Util.fade(Color.BLACK, 0.35f));

        for (int i = 1; i < game.boardSize; i++) {
            Line2D horizontal = new Line2D.Double(boardRectangle[boardIndex].getX(), boardRectangle[boardIndex].getY() + i * cellSize, boardRectangle[boardIndex].getX() + boardRectangle[boardIndex].getWidth(), boardRectangle[boardIndex].getY() + i * cellSize);
            g.draw(horizontal);

            Line2D vertical = new Line2D.Double(boardRectangle[boardIndex].getX() + i * cellSize, boardRectangle[boardIndex].getY(), boardRectangle[boardIndex].getX() + i * cellSize, boardRectangle[boardIndex].getY() + boardRectangle[boardIndex].getHeight());
            g.draw(vertical);
        }
    }


    private void drawShip(Graphics2D g, int boardIndex, Ship ship) {

        for (int i = 0; i < ship.shipPieces.length; i++) {

            ShipPiece shipPiece = ship.shipPieces[i];
            drawShipPart(g, boardIndex, ship, shipPiece);//shipPiece.position, shipPiece.part, ship.angle, shipPiece.hit, shipPiece.explosionAngleOffset);
        }
    }


    private void drawShipPart(Graphics2D g, int boardIndex, Ship ship, ShipPiece shipPiece) {

        // Rectangle számolás
        Rectangle2D partRectangle = new Rectangle2D.Double(boardRectangle[boardIndex].getX() + cellSize * shipPiece.position.getX(), boardRectangle[boardIndex].getY() + cellSize * shipPiece.position.getY(), cellSize, cellSize);

        // Ikon bekérés
        Image partIcon = shipPartIcon(shipPiece.part).getImage();

        // Grafika forgatása
        AffineTransform backup = Util.rotateGraphics(g, ship.angle, (float) partRectangle.getX() + cellSize / 2f, (float) partRectangle.getY() + cellSize / 2);

        // Kép rajzolás
        g.drawImage(partIcon, (int)Math.floor(partRectangle.getX()), (int)Math.floor(partRectangle.getY()), (int)Math.ceil(partRectangle.getWidth()), (int)Math.ceil(partRectangle.getHeight()), null);

        // Grafika forgatás reset
        g.setTransform(backup);


        if (shipPiece.hit) {
            AffineTransform backup2 = Util.rotateGraphics(g, shipPiece.explosionAngleOffset + explosionRotation * shipPiece.explosionAngleSpeed, (float) partRectangle.getX() + cellSize / 2f, (float) partRectangle.getY() + cellSize / 2);
            g.drawImage(explosionIcon.getImage(), (int) Math.floor(partRectangle.getX()), (int) Math.floor(partRectangle.getY()), (int) Math.ceil(partRectangle.getWidth()), (int) Math.ceil(partRectangle.getHeight()), null);
            g.setTransform(backup2);
        }
    }


    private void drawCellHighlight(Graphics2D g, Point2D cell, Color color, Rectangle2D rectangle, float cellSize) {
        Rectangle2D highlightRectangle = new Rectangle2D.Double(rectangle.getX() + cell.getX() * cellSize, rectangle.getY() + cell.getY() * cellSize, cellSize, cellSize);

        g.setPaint(color);
        g.fill(highlightRectangle);
    }


    private void drawCredits(Graphics2D g, Rectangle2D rectangle) {

    }
}


