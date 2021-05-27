package com.codecool.battleshipoop;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.Timer;

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

    private ImageIcon shipShadowFront;
    private ImageIcon shipShadowMiddle;
    private ImageIcon shipShadowRear;
    private ImageIcon shipShadowSmall;

    private ImageIcon[] explosionImages;

    private ImageIcon shockWaveImage;

    private ImageIcon oceanImage;

    private ImageIcon logoImage;

    public boolean madness = false;
    public float madnessAmount = 0;

    private final float padding = 30;

    public final Color[] playerColor = new Color[]{
            Util.rgbColor(252, 150, 150),
            new Color(0.96f, 0.96f, 0.48f)
    };

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

    public ImageIcon shipPartShadowIcon(ShipPart part) {
        if (part == ShipPart.FRONT)
            return shipShadowFront;
        if (part == ShipPart.MIDDLE)
            return shipShadowMiddle;
        if (part == ShipPart.REAR)
            return shipShadowRear;
        if (part == ShipPart.SMALL)
            return shipShadowSmall;

        return null;
    }

    public boolean mouseInBoard() {
        return mouseEvents.mousePosition != null;
    }

    //ParticleSystem fireParticles;
    public ParticleSystemCollection particleSystems;


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
        loadImages();

        // Particle rendszerek
        particleSystems = new ParticleSystemCollection();
    }

    private void loadImages() {
        ClassLoader classLoader = this.getClass().getClassLoader();

        java.net.URL frontUrl = classLoader.getResource("images/ship_front.png");
        java.net.URL middleUrl = classLoader.getResource("images/ship_middle.png");
        java.net.URL rearUrl = classLoader.getResource("images/ship_rear.png");
        java.net.URL smallUrl = classLoader.getResource("images/ship_small.png");

        java.net.URL frontShadowUrl = classLoader.getResource("images/ship_shadow_front.png");
        java.net.URL middleShadowUrl = classLoader.getResource("images/ship_shadow_middle.png");
        java.net.URL rearShadowUrl = classLoader.getResource("images/ship_shadow_rear.png");
        java.net.URL smallShadowUrl = classLoader.getResource("images/ship_shadow_small.png");

        java.net.URL shockWaveImageUrl = classLoader.getResource("images/shockwave.png");
        java.net.URL oceanUrl = classLoader.getResource("images/ocean.gif");

        java.net.URL logoUrl = classLoader.getResource("images/logo.png");

        shipFront = new ImageIcon(frontUrl);
        shipMiddle = new ImageIcon(middleUrl);
        shipRear = new ImageIcon(rearUrl);
        shipSmall = new ImageIcon(smallUrl);


        shipShadowFront = new ImageIcon(frontShadowUrl);
        shipShadowMiddle = new ImageIcon(middleShadowUrl);
        shipShadowRear = new ImageIcon(rearShadowUrl);
        shipShadowSmall = new ImageIcon(smallShadowUrl);

        shockWaveImage = new ImageIcon(shockWaveImageUrl);

        oceanImage = new ImageIcon(oceanUrl);

        logoImage = new ImageIcon(logoUrl);

        int expImageCount = 20;
        explosionImages = new ImageIcon[expImageCount];

        for (int i=0; i<expImageCount; i++)
        {
            java.net.URL expUrl = classLoader.getResource("images/explosion/explosion-" + ((Integer)(i+1)) + ".png");
            explosionImages[i] = new ImageIcon(expUrl);
        }
    }

    public void clockTick() {
        this.repaint();
    }


    // FRAMEK
    public void paintComponent(Graphics originalGraphics) {

        super.paintComponent(originalGraphics);

        frame((Graphics2D) originalGraphics);
    }



    //ParticleSystem shockwaveTest = null;

    private void frame(Graphics2D g) {

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Madness = Rotate boards
        if (madness) {
            madnessAmount += 5;
            if (madnessAmount > 360)
                madnessAmount -= 360;
        }

        // Rotate boards
        AffineTransform backup = g.getTransform();
        if (madness) Util.rotateGraphics(g, madnessAmount, (float)this.getWidth() / 2, (float)this.getHeight() / 2);

        if (creditsMode) {
            drawCredits(g, new Rectangle2D.Double(0, -padding * 2, this.getWidth(), this.getHeight() + padding * 4));
            return;
        }

        // game not started yet
        if (game.gameState == GameState.NOT_STARTED) {

            float logoSize = Math.min(this.getWidth(), this.getHeight());
            g.drawImage(logoImage.getImage(), (int) (this.getWidth() - logoSize) / 2, (int) (this.getHeight() - logoSize) / 2, (int) logoSize, (int) logoSize, null);

            return;
        }

        // Egér kezelése
        if (!firstFrame)
            handleMouseEvents();

        // Rajzolás
        drawElements(g, new Dimension(this.getWidth(), this.getHeight()));

        // Játéklogika frissítése
        game.update();

        // Reset
        firstFrame = false;
        mouseEvents.reset();

        g.setTransform(backup);
    }


    private void handleMouseEvents() {

        if (!mouseInBoard())
        {
            boardHighlight = new Point2D[] {null, null};
            return;
        }

        if (boardRectangle == null)
            return;

        // egér a board 1-en
        for (int boardIndex = 0; boardIndex < boardRectangle.length; boardIndex++)
        {
            if (boardRectangle[boardIndex] == null)
                continue;

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

        if (game.gameState == GameState.END) {
            int winner = (game.player == 0 ? 1 : 0) + 1;
            String winnerText = "The winner is Player " + ((Integer) winner) + "!";

            drawCenteredLabel(g, winnerText, boardsRectangle, Color.black, Util.rgbAColor(255, 255, 255, 0.2f));
        }
        else if (game.gameState == GameState.PREPARE_TO_ATTACK) {
            String prepareText = "Player " + ((Integer) (game.player + 1)) + " prepare to attack";

            drawCenteredLabel(g, prepareText, boardsRectangle, Color.black, Util.rgbAColor(255, 255, 255, 0.5f));
        }
    }


    private void drawBoards(Graphics2D g, Rectangle2D boardsRectangle) {

        particleSystems.clipRectangle = boardsRectangle;

        boardRectangle[0] = new Rectangle2D.Double(boardsRectangle.getX(), boardsRectangle.getY(), boardsRectangle.getHeight(), boardsRectangle.getHeight());
        boardRectangle[1] = new Rectangle2D.Double(boardsRectangle.getX() + (boardsRectangle.getWidth() - boardsRectangle.getHeight()), boardsRectangle.getY(), boardsRectangle.getHeight(), boardsRectangle.getHeight());

        cellSize = (float)boardRectangle[0].getWidth() / game.boardSize;

        drawBoard(g, 0);
        drawBoard(g, 1);
    }


    private void drawBoard(Graphics2D g, int boardIndex) {

        // Player név szöveg
        String playerText = "Player " + ((Integer)(boardIndex + 1));

        drawCenteredLabel(g, playerText, new Rectangle2D.Double(boardRectangle[boardIndex].getX(), boardRectangle[boardIndex].getY() - padding, boardRectangle[boardIndex].getWidth(), padding), playerColor[boardIndex].darker());

        // Háttér
        g.drawImage(oceanImage.getImage(), (int)Math.floor(boardRectangle[boardIndex].getX()), (int)Math.floor(boardRectangle[boardIndex].getY()), (int)Math.ceil(boardRectangle[boardIndex].getWidth()), (int)Math.ceil(boardRectangle[boardIndex].getHeight()), null);


        if (game.frozeState == 2) {
            if (boardIndex != game.player) {
                String prepareText = "Player " + ((Integer) (boardIndex + 1)) + ": prepare to attack!";

                drawCenteredLabel(g, prepareText, boardRectangle[boardIndex], Color.black, Util.rgbAColor(255, 255, 255, 0.5f));
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

            Rectangle2D oldClip = g.getClipBounds();
            g.setClip(boardRectangle[boardIndex]);

            for (int i = 0; i < game.playerShips[boardIndex].length; i++) {
                drawShip(g, boardIndex, game.playerShips[boardIndex][i]);
            }

            g.setClip(oldClip);
        }

        // Találatok
        if (game.gameState == GameState.ATTACK && boardIndex != game.player) {

            Rectangle2D oldClip = g.getClipBounds();
            g.setClip(boardRectangle[boardIndex]);

            drawAttackPoints(g, boardIndex);

            g.setClip(oldClip);
        }

        particleSystems.drawParticlesInOrder(g);
    }


    private void drawAttackPoints(Graphics2D g, int boardIndex) {
        Point2D[] hits = game.playerHits[boardIndex == 0 ? 1 : 0];
        Ship[] ships = game.playerShips[boardIndex];

        for (int i=0; i<hits.length; i++)
        {
            int hitPartParticleSystemIndex = -1;
            int hitPartShockwaveParticleSystemIndex = -1;

            for (int j = 0; j<ships.length; j++)
            {
                for (int k = 0; k<ships[j].shipPieces.length; k++)
                {
                    if (ships[j].shipPieces[k].position.equals(hits[i])) {
                        hitPartParticleSystemIndex = ships[j].shipPieces[k].particleSystemIndex;
                        hitPartShockwaveParticleSystemIndex = ships[j].shipPieces[k].shockwaveParticleSystemIndex;
                        break;
                    }
                }
                if (hitPartParticleSystemIndex != -1 && hitPartShockwaveParticleSystemIndex != -1)
                    break;
            }

            Rectangle2D partRectangle = new Rectangle2D.Double(boardRectangle[boardIndex].getX() + cellSize * hits[i].getX(), boardRectangle[boardIndex].getY() + cellSize * hits[i].getY(), cellSize, cellSize);

            if (hitPartParticleSystemIndex != -1 || hitPartShockwaveParticleSystemIndex != -1) {

                if (hitPartShockwaveParticleSystemIndex != -1) {
                    particleSystems.getSystem(hitPartShockwaveParticleSystemIndex).scale = particleScale(boardIndex);
                    particleSystems.queueParticleDraw(hitPartShockwaveParticleSystemIndex);
                }
                if (hitPartParticleSystemIndex != -1) {
                    particleSystems.getSystem(hitPartParticleSystemIndex).scale = particleScale(boardIndex);
                    particleSystems.queueParticleDraw(hitPartParticleSystemIndex);
                }
            }
            else {
                g.setPaint(Util.rgbAColor(0, 0, 0, 0.6f));
                g.fill(partRectangle);
            }
        }
    }


    public Point2D cellToPixel(Point2D point, int boardIndex)
    {
        return new Point2D.Double(boardRectangle[boardIndex].getX() + cellSize * point.getX() + cellSize / 2f, boardRectangle[boardIndex].getY() + cellSize * point.getY() + cellSize / 2f);
    }


    private float particleScale(int boardIndex)
    {
        return (float)boardRectangle[boardIndex].getWidth() / 800;
    }


    public int addShockwaveParticle(Point2D point)
    {
        int particleIndex = particleSystems.add(point, new ImageIcon[] { shockWaveImage }, 1, 1, false, false);

        ParticleSystem shockwaveTest = particleSystems.getSystem(particleIndex);

        shockwaveTest.addOpacityKeyFrame(0f, 0f);
        shockwaveTest.addOpacityKeyFrame(0.1f, 0.7f);
        shockwaveTest.addOpacityKeyFrame(1f, 0f);

        shockwaveTest.addSizeKeyFrame(0f, 0, 0);
        shockwaveTest.addSizeKeyFrame(0.2f, 140, 140);
        shockwaveTest.addSizeKeyFrame(1f, 180, 180);

        return particleIndex;
    }


    public int addFireParticle(Point2D point)
    {
        int particleIndex = particleSystems.add(point, explosionImages, 10, 5, true, false);
        ParticleSystem system = particleSystems.getSystem(particleIndex);

        system.autoIndexKeyFrames();

        system.zOrder = ParticleZOrder.LAST_ON_TOP;

        system.defaultOpacity = 1f;
        system.defaultRotation = 0;
        system.defaultSize = new Dimension(100, 100);
        system.defaultPosition = new Point2D.Double(0, 0);
        system.defaultImageIndex = 0;

        system.addPositionKeyFrame(0f, 0, 0);
        system.addPositionKeyFrame(0.2f, -5, -10);
        system.addPositionKeyFrame(0.4f, 10, -35);
        system.addPositionKeyFrame(0.6f, -15, -50);
        system.addPositionKeyFrame(0.8f, 0, -60);
        system.addPositionKeyFrame(1f, 15, -70);

        system.addOpacityKeyFrame(0f, 0f);
        system.addOpacityKeyFrame(0.2f, 1f);
        system.addOpacityKeyFrame(0.8f, 0.2f);
        system.addOpacityKeyFrame(1f, 0f);

        system.addRotationKeyFrame(0f, 0);
        system.addRotationKeyFrame(0.5f, 180f);
        system.addRotationKeyFrame(1f, 360f);

        system.addSizeKeyFrame(0, 70, 70);
        system.addSizeKeyFrame(0.2f, 130, 130);
        system.addSizeKeyFrame(0.6f, 160, 160);
        system.addSizeKeyFrame(1f, 120, 120);

        return particleIndex;
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

        Point2D shipOffset = ship.offset(Util.lerp(0.4f, 0.1f, (ship.shipPieces.length - 1) / (game.SHIP_COUNT - 1)));

        float maxOffset = (float)boardRectangle[boardIndex].getHeight() * 0.01f;

        // Rectangle számolás
        Rectangle2D partRectangle = new Rectangle2D.Double(boardRectangle[boardIndex].getX() + cellSize * shipPiece.position.getX() + shipOffset.getX() * maxOffset, boardRectangle[boardIndex].getY() + cellSize * shipPiece.position.getY() + shipOffset.getY() * maxOffset, cellSize, cellSize);
        Rectangle2D partShadowPostRectangle = new Rectangle2D.Double(boardRectangle[boardIndex].getX() + cellSize * shipPiece.position.getX() + shipOffset.getX() * maxOffset, boardRectangle[boardIndex].getY() + cellSize * shipPiece.position.getY() + maxOffset, cellSize, cellSize);

        float shadowRatio = Math.abs((float)partShadowPostRectangle.getY() - (float)partRectangle.getY()) / maxOffset;

        float partShadowSizePlus = Util.lerp(cellSize / 3, cellSize / 12, 1 - shadowRatio);

        partShadowPostRectangle = new Rectangle2D.Double(boardRectangle[boardIndex].getX() + cellSize * shipPiece.position.getX() + shipOffset.getX() * maxOffset, boardRectangle[boardIndex].getY() + cellSize * shipPiece.position.getY() - maxOffset * 1.5f, cellSize, cellSize);

        Rectangle2D partShadowRectangle = new Rectangle2D.Double(partShadowPostRectangle.getX() - partShadowSizePlus / 2, partShadowPostRectangle.getY() - partShadowSizePlus / 2, partShadowPostRectangle.getWidth() + partShadowSizePlus, partShadowPostRectangle.getHeight() + partShadowSizePlus);
        if (ship.shipPieces.length > 1) {
            if (shipPiece.part == ShipPart.FRONT)
                partShadowRectangle = new Rectangle2D.Double(partShadowPostRectangle.getX() - partShadowSizePlus / 2, partShadowPostRectangle.getY() - partShadowSizePlus / 2, partShadowPostRectangle.getWidth() + partShadowSizePlus / 2, partShadowPostRectangle.getHeight() + partShadowSizePlus);
            else if (shipPiece.part == ShipPart.MIDDLE)
                partShadowRectangle = new Rectangle2D.Double(partShadowPostRectangle.getX(), partShadowPostRectangle.getY() - partShadowSizePlus / 2, partShadowPostRectangle.getWidth(), partShadowPostRectangle.getHeight() + partShadowSizePlus);
            else if (shipPiece.part == ShipPart.REAR)
                partShadowRectangle = new Rectangle2D.Double(partShadowPostRectangle.getX(), partShadowPostRectangle.getY() - partShadowSizePlus / 2, partShadowPostRectangle.getWidth() + partShadowSizePlus / 2, partShadowPostRectangle.getHeight() + partShadowSizePlus);
        }

        // Ikon bekérés
        Image partIcon = shipPartIcon(shipPiece.part).getImage();
        Image partShadowIcon = shipPartShadowIcon(shipPiece.part).getImage();

        // Grafika forgatása
        AffineTransform backup = Util.rotateGraphics(g, ship.angle, (float) partRectangle.getX() + cellSize / 2f, (float) partRectangle.getY() + cellSize / 2);

        // Kép rajzolás
        g.drawImage(partShadowIcon, (int)Math.ceil(partShadowRectangle.getX()), (int)Math.ceil(partShadowRectangle.getY()), (int)Math.floor(partShadowRectangle.getWidth()), (int)Math.floor(partShadowRectangle.getHeight()), null);
        g.drawImage(partIcon, (int)Math.floor(partRectangle.getX()), (int)Math.floor(partRectangle.getY()), (int)Math.ceil(partRectangle.getWidth()), (int)Math.ceil(partRectangle.getHeight()), null);

        // Grafika forgatás reset
        g.setTransform(backup);


        if (shipPiece.hit) {
            /*fireParticles.scale = particleScale(boardIndex);
            fireParticles.draw(g, partRectangle.getX() + partRectangle.getWidth() / 2, partRectangle.getY() + partRectangle.getHeight() / 2);*/

            if (shipPiece.particleSystemIndex != -1) {
                particleSystems.getSystem(shipPiece.particleSystemIndex).scale = particleScale(boardIndex);
                particleSystems.queueParticleDraw(shipPiece.particleSystemIndex);
            }
        }
    }


    private void drawCellHighlight(Graphics2D g, Point2D cell, Color color, Rectangle2D rectangle, float cellSize) {
        Rectangle2D highlightRectangle = new Rectangle2D.Double(rectangle.getX() + cell.getX() * cellSize, rectangle.getY() + cell.getY() * cellSize, cellSize, cellSize);

        g.setPaint(color);
        g.fill(highlightRectangle);
    }


    private void drawCenteredLabel(Graphics2D g, String text, Rectangle2D rectangle, Color foreColor) {
        drawCenteredLabel(g, text, rectangle, foreColor, null);
    }

    private void drawCenteredLabel(Graphics2D g, String text, Rectangle2D rectangle, Color foreColor, Color backColor) {
        if (backColor != null)
        {
            g.setPaint(Util.rgbAColor(255, 255, 255, 0.6f));
            g.fill(rectangle);
        }

        int textWidth = g.getFontMetrics().stringWidth(text);

        g.setPaint(foreColor);
        g.drawString(text, (int)rectangle.getX() + (int)(rectangle.getWidth() / 2) - textWidth / 2, (int)rectangle.getY() + (int)(rectangle.getHeight() / 2));
    }


    public CreditsRow[] creditsRows = null;

    private void drawCredits(Graphics2D g, Rectangle2D rectangle) {

        int namesCount = 5;

        if (creditsRows == null) {
            creditsRows = new CreditsRow[namesCount];

            float distance = 1f / namesCount;

            for (int i=0; i<creditsRows.length; i++)
            {
                creditsRows[i] = new CreditsRow();
                creditsRows[i].progress = i * distance;
            }
        }

        for (int i=0; i<creditsRows.length; i++)
        {
            if (creditsRows[i].progress >= 1)
                creditsRows[i] = new CreditsRow();

            creditsRows[i].progress += 0.005f;

            Rectangle2D creditsRectangle1 = new Rectangle2D.Double(rectangle.getX(), rectangle.getY() + rectangle.getHeight() * (1f - creditsRows[i].progress) - padding, rectangle.getWidth(), padding);
            Rectangle2D creditsRectangle2 = new Rectangle2D.Double(rectangle.getX(), rectangle.getY() + rectangle.getHeight() * (1f - creditsRows[i].progress), rectangle.getWidth(), padding);

            drawCenteredLabel(g, creditsRows[i].bullsht, creditsRectangle1, Color.black);
            drawCenteredLabel(g, creditsRows[i].name, creditsRectangle2, Color.black);
        }
    }

    public static String[] creditsBullsht = new String[] {
        "Talent director",
        "Script director",
        "Script editor",
        "Recording engineer",
        "Playtester",
        "Special thank to",
        "Producer",
        "Executive assistant",
        "Senior producer",
        "Director, studio operations",
        "Sound design",
        "Art supervisor",
        "Senior game director",
        "Narrative director",
        "Art director",
        "Environment art director",
        "Level design",
        "Gameplay mechanics",
        "QA department",
        "Lead audio designer",
        "Sound FX",
        "Visual FX supervisor",
        "Production assistant",
        "Production coordinator",
        "Special guest",
        "Utility programmer",
        "Presentation programmer",
        "Music arrangements"
    };
}

class ParticleSystemInstance
{
    public ParticleSystem system;
    public Point2D position;

    public ParticleSystemInstance(ParticleSystem system, Point2D position)
    {
        this.system = system;
        this.position = position;
    }
    public ParticleSystemInstance(ParticleSystem system, float x, float y)
    {
        this.system = system;
        this.position = new Point2D.Double(x, y);
    }
}

class ParticleSystemCollection extends ArrayList<ParticleSystemInstance> {

    Queue<Integer> drawOrder;

    public Rectangle2D clipRectangle = null;

    public ParticleSystemCollection() {
        super();

        drawOrder = new LinkedList();
    }

    public void queueParticleDraw(int particleIndex)
    {
        if (getSystem(particleIndex) != null)
            drawOrder.add((Integer)particleIndex);
    }

    public void drawParticlesInOrder(Graphics2D graphics)
    {
        Rectangle2D oldClip = graphics.getClipBounds();
        if (clipRectangle != null)
            graphics.setClip(clipRectangle);

        while (drawOrder.size() > 0)
        {
            Integer particleIndex = drawOrder.poll();

            if (particleIndex == null)
                continue;

            if (particleIndex < 0 || particleIndex >= super.size())
                continue;

            ParticleSystemInstance instance = get(particleIndex);
            instance.system.frame();
            instance.system.draw(graphics, instance.position);
        }

        if (clipRectangle != null)
            graphics.setClip(oldClip);
    }

    public int add(float x, float y, ImageIcon image, int particleCount, float duration, boolean repeating, boolean preHeating)
    {
        super.add(new ParticleSystemInstance(new ParticleSystem(new ImageIcon[] { image }, particleCount, duration, repeating, preHeating), x, y));
        return super.size() - 1;
    }
    public int add(Point2D position, ImageIcon image, int particleCount, float duration, boolean repeating, boolean preHeating)
    {
        super.add(new ParticleSystemInstance(new ParticleSystem(new ImageIcon[] { image }, particleCount, duration, repeating, preHeating), position));
        return super.size() - 1;
    }
    public int add(float x, float y, ImageIcon[] images, int particleCount, float duration, boolean repeating, boolean preHeating)
    {
        super.add(new ParticleSystemInstance(new ParticleSystem(images, particleCount, duration, repeating, preHeating), x, y));
        return super.size() - 1;
    }
    public int add(Point2D position, ImageIcon[] images, int particleCount, float duration, boolean repeating, boolean preHeating)
    {
        super.add(new ParticleSystemInstance(new ParticleSystem(images, particleCount, duration, repeating, preHeating), position));
        return super.size() - 1;
    }

    public ParticleSystem getSystem(int index)
    {
        if (index < 0 || index >= super.size())
            return null;

        return super.get(index).system;
    }

    public Point2D getPosition(int index)
    {
        if (index < 0 || index >= super.size())
            return null;

        return super.get(index).position;
    }

    public float getX(int index)
    {
        if (index < 0 || index >= super.size())
            return 0;

        return (float)super.get(index).position.getX();
    }
    public float getY(int index)
    {
        if (index < 0 || index >= super.size())
            return 0;

        return (float)super.get(index).position.getY();
    }

}

