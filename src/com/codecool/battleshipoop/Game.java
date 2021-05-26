package com.codecool.battleshipoop;

import java.awt.geom.Point2D;
import java.util.Arrays;


enum GameState {
    NOT_STARTED,
    PLACEMENT,
    ATTACK,
    END
}


public class Game {
    private MainWindow window;
    private FieldPanel fieldPanel = null;


    public GameState gameState = GameState.NOT_STARTED;

    public int boardSize = 10;
    public int round = 0;
    public int player = 0;

    public Ship[][] playerShips = null;
    public Point2D[][] playerHits = null;

    public Point2D shipPlacementPoint = null;


    public final int SHIP_COUNT = 5;


    public Game(MainWindow window)
    {
        this.window = window;
    }


    public void Start() {
        gameState = GameState.PLACEMENT;

        round = 0;
        player = 0;

        playerShips = new Ship[2][0];
        playerHits = new Point2D[2][0];

        shipPlacementPoint = null;
    }


    public void Update() {

        if (fieldPanel == null)
            fieldPanel = window.fieldPanel;


        switch (gameState) {

            case PLACEMENT:
                UpdatePlacement();
                break;

            case ATTACK:
                UpdateAttack();
                break;
        }
    }


    private void UpdatePlacement() {

        if (playerShips == null)
            playerShips = new Ship[2][0];

        // Player váltás / Placement státusz vége
        if (playerShips[player].length == SHIP_COUNT) {
            if (player == 0) player = 1;
            else {
                player = 0;
                gameState = GameState.ATTACK;
            }
        }

        if (fieldPanel.boardHighlight == null)
            return;

        Point2D playerHighlight = fieldPanel.boardHighlight[player];

        // TODO: hajók körül helyet kell hagyni
        if (fieldPanel.mouseInBoard() && playerHighlight != null) {

            // Hajó lerakás
            if (fieldPanel.mouseEvents.mouseLeftClick) {

                int shipSize = SHIP_COUNT;
                if (playerShips != null && playerShips[player] != null)
                    shipSize = SHIP_COUNT - playerShips[player].length;

                // Kezdőpont
                if (shipPlacementPoint == null) {
                    shipPlacementPoint = playerHighlight;

                    if (shipSize != 1)
                        return;
                }

                // Small ship
                if (shipSize == 1)
                {
                    Ship ship = new Ship(new ShipPiece[] { new ShipPiece(shipPlacementPoint, false, ShipPart.SMALL) }, 0);
                    playerShips[player] = addShip(playerShips[player], ship);
                    shipPlacementPoint = null;
                    return;
                }

                // Large ship with angle
                if (fieldPanel.boardHighlight == null || fieldPanel.boardHighlight[player] == null)
                    return;

                Point2D[] placementPoint = Util.getPlacementPoints(shipPlacementPoint, fieldPanel.boardHighlight[player], shipSize, boardSize);

                if (placementPoint == null)
                    return;

                float placementAngle = Util.getPlacementAngle(shipPlacementPoint, fieldPanel.boardHighlight[player]);

                ShipPiece[] shipPieces = new ShipPiece[placementPoint.length];
                for (int i=0; i< shipPieces.length; i++)
                {
                    ShipPart part = ShipPart.MIDDLE;

                    if (i == 0)
                        part = ShipPart.REAR;
                    else if (i == shipPieces.length - 1)
                        part = ShipPart.FRONT;

                    shipPieces[i] = new ShipPiece(placementPoint[i], false, part);
                }

                Ship ship = new Ship(shipPieces, placementAngle);
                playerShips[player] = addShip(playerShips[player], ship);

                shipPlacementPoint = null;
            }

            // Utolsó hajó törlése
            else if (fieldPanel.mouseEvents.mouseRightClick)
                playerShips[player] = removeShip(playerShips[player]);
        }
    }


    private void UpdateAttack() {
        if (playerShipDestroyed(player))
        {
            // TODO: nyert XY
            gameState = GameState.END;
            return;
        }

        if (fieldPanel.boardHighlight == null || fieldPanel.boardHighlight[player == 0 ? 1 : 0] == null)
            return;

        Point2D enemyHighlight = fieldPanel.boardHighlight[player == 0 ? 1 : 0];

        if (fieldPanel.mouseInBoard() && enemyHighlight != null && fieldPanel.mouseEvents.mouseLeftClick)
        {
            if (playerHits == null || playerHits[player] == null)
                playerHits = new Point2D[2][0];

            // ATTACK
            for (int i=0; i<playerHits[player].length; i++)
            {
                if (playerHits[player][i].equals(enemyHighlight))
                    return;
            }

            playerHits[player] = addHit(playerHits[player], enemyHighlight);

            for (int i=0; i<playerShips[player == 0 ? 1 : 0].length; i++)
            {
                for (int j = 0; j < playerShips[player == 0 ? 1 : 0][i].shipPieces.length; j++)
                {
                    if (playerShips[player == 0 ? 1 : 0][i].shipPieces[j].position.equals(enemyHighlight))
                        playerShips[player == 0 ? 1 : 0][i].shipPieces[j].hit = true;
                }
            }

            player = player == 0 ? 1 : 0;
        }
    }


    // MISC
    public Ship[] addShip(Ship[] collection, Ship ship)
    {
        Ship[] result = new Ship[collection.length + 1];
        for (int i=0; i < collection.length; i++)
            result[i] = collection[i];
        result[collection.length] = ship;
        return result;
    }

    public Ship[] removeShip(Ship[] collection)
    {
        if (collection.length == 0) return collection;

        Ship[] result = new Ship[collection.length - 1];
        for (int i=0; i < result.length; i++)
            result[i] = collection[i];
        return result;
    }

    public Point2D[] addHit(Point2D[] collection, Point2D point)
    {
        Point2D[] result = new Point2D[collection.length + 1];
        for (int i=0; i < collection.length; i++)
            result[i] = collection[i];
        result[collection.length] = point;
        return result;
    }

    private boolean playerShipDestroyed(int player)
    {
        if (playerShips == null || playerShips[player] == null)
            return false;

        for (int i=0; i<playerShips[player].length; i++)
        {
            if (!playerShips[player][i].isDestroyed())
                return false;
        }

        return true;
    }
}
