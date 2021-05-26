package com.codecool.battleshipoop;

import java.awt.geom.Point2D;


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


    private final int SHIP_COUNT = 5;


    public Game(MainWindow window)
    {
        this.window = window;
    }


    public void Start() {
        // TODO: RESET
        gameState = GameState.PLACEMENT;
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

        if (fieldPanel.mouseInBoard() && playerHighlight != null) {

            // Hajó lerakás
            if (fieldPanel.mouseEvents.mouseLeftClick) {

                // Kezdőpont
                if (shipPlacementPoint == null)
                    shipPlacementPoint = playerHighlight;

                // Irány
                else {
                    // TODO: rendes ship generálás
                    ShipPiece[] shipPieces = new ShipPiece[] {
                        new ShipPiece((int)shipPlacementPoint.getX(), (int)shipPlacementPoint.getY(), false, ShipPart.SMALL)
                    };

                    Ship ship = new Ship(shipPieces, 0);
                    playerShips[player] = Util.addShip(playerShips[player], ship);

                    shipPlacementPoint = null;
                }
            }

            // Utolsó hajó törlése
            else if (fieldPanel.mouseEvents.mouseRightClick)
                playerShips[player] = Util.removeShip(playerShips[player]);
        }
    }


    private void UpdateAttack() {

    }
}
