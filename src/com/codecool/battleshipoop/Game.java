package com.codecool.battleshipoop;

import java.awt.geom.Point2D;
import java.sql.Timestamp;


enum GameState {
    NOT_STARTED,
    PLACEMENT,
    PREPARE_TO_ATTACK,
    ATTACK,
    END
}

public class Game {
    private final MainWindow window;
    private FieldPanel fieldPanel = null;


    public GameState gameState = GameState.NOT_STARTED;

    public int boardSize = 10;
    public int round = 0;
    public int player = 0;

    public Ship[][] playerShips = null;
    public Point2D[][] playerHits = null;

    public Point2D shipPlacementPoint = null;

    public int frozeState = 0;
    public Timestamp frozeStartTime;


    public final int SHIP_COUNT = 5;


    public Game(MainWindow window) {
        this.window = window;
    }


    public void start() {
        gameState = GameState.PLACEMENT;
        frozeState = 0;

        round = 0;
        player = 0;

        playerShips = new Ship[2][0];
        playerHits = new Point2D[2][0];

        shipPlacementPoint = null;

        if (fieldPanel != null) {
            fieldPanel.creditsMode = false;
            fieldPanel.madness = false;
        }
    }


    public void update() {

        if (fieldPanel == null)
            fieldPanel = window.fieldPanel;


        switch (gameState) {

            case PLACEMENT:
                updatePlacement();
                break;

            case PREPARE_TO_ATTACK:
                if (fieldPanel.mouseEvents.mouseLeftClick)
                    gameState = GameState.ATTACK;
                break;

            case ATTACK:
                updateAttack();
                break;
        }
    }


    private void updatePlacement() {

        if (playerShips == null)
            playerShips = new Ship[2][0];

        // Player váltás / Placement státusz vége
        if (playerShips[player].length == SHIP_COUNT) {
            if (player == 0) player = 1;
            else {
                player = 0;
                gameState = GameState.PREPARE_TO_ATTACK;

                orderPlayerShips();
            }
        }

        if (fieldPanel.boardHighlight == null)
            return;

        Point2D playerHighlight = fieldPanel.boardHighlight[player];

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
                if (shipSize == 1) {
                    if (!validatePlacementPoint(shipPlacementPoint, playerShips[player])) {
                        shipPlacementPoint = null;
                        return;
                    }
                    Ship ship = new Ship(new ShipPiece[]{new ShipPiece(shipPlacementPoint, false, ShipPart.SMALL)}, 0);
                    playerShips[player] = addShip(playerShips[player], ship);
                    shipPlacementPoint = null;
                    return;
                }

                // Large ship with angle
                if (fieldPanel.boardHighlight == null || fieldPanel.boardHighlight[player] == null)
                    return;

                Point2D[] placementPoint = getPlacementPoints(shipPlacementPoint, fieldPanel.boardHighlight[player], shipSize, boardSize, playerShips[player]);

                if (placementPoint == null) {
                    shipPlacementPoint = null;
                    return;
                }

                float placementAngle = getPlacementAngle(shipPlacementPoint, fieldPanel.boardHighlight[player]);

                ShipPiece[] shipPieces = new ShipPiece[placementPoint.length];
                for (int i = 0; i < shipPieces.length; i++) {
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


    private void updateAttack() {

        if (playerShipDestroyed(player)) {
            gameState = GameState.END;
            frozeState = 0;
            return;
        }

        if (frozeState > 0) {
            if (frozeState == 1) {
                if (Util.elapsedMilliseconds(new Timestamp(System.currentTimeMillis()), frozeStartTime) > 5000 || fieldPanel.mouseEvents.mouseLeftClick)
                    frozeState = 2;
            } else {
                if (fieldPanel.mouseEvents.mouseLeftClick) {
                    player = player == 0 ? 1 : 0;
                    frozeState = 0;
                }
            }

            return;
        }

        if (fieldPanel.boardHighlight == null || fieldPanel.boardHighlight[player == 0 ? 1 : 0] == null)
            return;

        Point2D enemyHighlight = fieldPanel.boardHighlight[player == 0 ? 1 : 0];

        if (fieldPanel.mouseInBoard() && enemyHighlight != null && fieldPanel.mouseEvents.mouseLeftClick) {
            if (playerHits == null || playerHits[player] == null)
                playerHits = new Point2D[2][0];

            // ATTACK
            for (int i = 0; i < playerHits[player].length; i++) {
                if (playerHits[player][i].equals(enemyHighlight))
                    return;
            }

            fieldPanel.vibrate();

            playerHits[player] = addHit(playerHits[player], enemyHighlight);
            orderPlayerHits();

            for (int i = 0; i < playerShips[player == 0 ? 1 : 0].length; i++) {
                for (int j = 0; j < playerShips[player == 0 ? 1 : 0][i].shipPieces.length; j++) {
                    if (playerShips[player == 0 ? 1 : 0][i].shipPieces[j].position.equals(enemyHighlight)) {
                        playerShips[player == 0 ? 1 : 0][i].shipPieces[j].hit = true;

                        playerShips[player == 0 ? 1 : 0][i].shipPieces[j].particleSystems = new int[] {
                            fieldPanel.addFireParticle(enemyHighlight, player == 0 ? 1 : 0),
                            fieldPanel.addShockwaveParticle(enemyHighlight, player == 0 ? 1 : 0)
                        };
                    }
                }
            }

            frozeGame();
        }
    }


    private void frozeGame() {
        frozeState = 1;
        frozeStartTime = new Timestamp(System.currentTimeMillis());
    }


    // MISC
    private Ship[] addShip(Ship[] collection, Ship ship) {
        Ship[] result = new Ship[collection.length + 1];
        for (int i = 0; i < collection.length; i++)
            result[i] = collection[i];
        result[collection.length] = ship;
        return result;
    }

    private Ship[] removeShip(Ship[] collection) {
        if (collection.length == 0) return collection;

        Ship[] result = new Ship[collection.length - 1];
        for (int i = 0; i < result.length; i++)
            result[i] = collection[i];
        return result;
    }

    private void orderPlayerShips()
    {
        for (int i=0; i<playerShips.length;i++) {
            for (int j = 0; j<playerShips[i].length; j++)
            {
                int iteration = 0;
                int shipPartCount = playerShips[i][j].shipPieces.length;

                while (iteration < shipPartCount - 1)
                {
                    int subIteration = 0;

                    while (subIteration < shipPartCount - iteration - 1) {
                        if (playerShips[i][j].shipPieces[subIteration].position.getY() > playerShips[i][j].shipPieces[subIteration + 1].position.getY()) {
                            ShipPiece temp = playerShips[i][j].shipPieces[subIteration];
                            playerShips[i][j].shipPieces[subIteration] = playerShips[i][j].shipPieces[subIteration + 1];
                            playerShips[i][j].shipPieces[subIteration + 1] = temp;
                        }

                        subIteration++;
                    }

                    iteration++;
                }
            }


            int iteration = 0;
            int shipCount = playerShips[i].length;

            while (iteration < shipCount - 1) {
                int subIteration = 0;

                while (subIteration < shipCount - iteration - 1) {
                    if (playerShips[i][subIteration].shipPieces[playerShips[i][subIteration].shipPieces.length - 1].position.getY() > playerShips[i][subIteration + 1].shipPieces[playerShips[i][subIteration + 1].shipPieces.length - 1].position.getY()) {
                        Ship temp = playerShips[i][subIteration];
                        playerShips[i][subIteration] = playerShips[i][subIteration + 1];
                        playerShips[i][subIteration + 1] = temp;
                    }

                    subIteration++;
                }

                iteration++;
            }
        }
    }

    private Point2D[] addHit(Point2D[] collection, Point2D point) {
        Point2D[] result = new Point2D[collection.length + 1];
        for (int i = 0; i < collection.length; i++)
            result[i] = collection[i];
        result[collection.length] = point;
        return result;
    }

    private void orderPlayerHits()
    {
        for (int i=0; i<playerHits.length; i++) {
            int iteration = 0;
            int hitCount = playerHits[i].length;

            while (iteration < hitCount - 1)
            {
                int subIteration = 0;

                while (subIteration < hitCount - iteration - 1)
                {
                    if (playerHits[i][subIteration].getY() > playerHits[i][subIteration + 1].getY())
                    {
                        Point2D temp = playerHits[i][subIteration];
                        playerHits[i][subIteration] = playerHits[i][subIteration + 1];
                        playerHits[i][subIteration + 1] = temp;
                    }

                    subIteration++;
                }

                iteration++;
            }
        }
    }

    private boolean playerShipDestroyed(int player) {
        if (playerShips == null || playerShips[player] == null)
            return false;

        for (int i = 0; i < playerShips[player].length; i++) {
            if (!playerShips[player][i].isDestroyed())
                return false;
        }

        return true;
    }

    public static Point2D[] getPlacementPoints(Point2D startPoint, Point2D endPoint, int pointCount, int boardSize, Ship[] ships) {

        Point2D[] points = new Point2D[pointCount];
        Point2D direction = Util.pointDirection(startPoint, endPoint);
        if (direction == null)
            return null;

        for (int i = 0; i < pointCount; i++) {
            points[i] = new Point2D.Double(startPoint.getX() + i * direction.getX(), startPoint.getY() + i * direction.getY());

            if (points[i].getX() < 0 || points[i].getY() < 0 || points[i].getX() >= boardSize || points[i].getY() >= boardSize)
                return null;

            if (!validatePlacementPoint(points[i], ships)) return null;
        }

        return points;
    }

    public static boolean validatePlacementPoint(Point2D point, Ship[] ships) {
        for (int j = 0; j < ships.length; j++) {
            for (int k = 0; k < ships[j].shipPieces.length; k++) {
                Point2D distanceToPoint = Util.pointDistance(point, ships[j].shipPieces[k].position);
                int distanceX = (int) Math.abs(distanceToPoint.getX());
                int distanceY = (int) Math.abs(distanceToPoint.getY());

                if (distanceX <= 1 && distanceY <= 1) return false;
            }
        }

        return true;
    }

    public static float getPlacementAngle(Point2D startPoint, Point2D endPoint) {
        Point2D direction = Util.pointDirection(startPoint, endPoint);
        if (direction == null)
            return 0;

        if (direction.equals(new Point2D.Double(-1, 0)))
            return 0;
        if (direction.equals(new Point2D.Double(1, 0)))
            return 180;
        if (direction.equals(new Point2D.Double(0, -1)))
            return 90;

        return 270;
    }

}
