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
    private FieldPanel fieldPanel;


    private GameState gameState = GameState.NOT_STARTED;

    private int boardSize = 10;
    private int round = 0;
    private int player = 0;

    public Ship[][] playerShips = null;
    public Point2D[][] playerHits = null;

    public Point2D shipPlacementPoint = null;


    public GameState getGameState() {
        return gameState;
    }
    public int getBoardSize() {
        return boardSize;
    }


    public Game(MainWindow window)
    {
        this.window = window;
        this.fieldPanel = window.fieldPanel;
    }


    public void Start() {
        // TODO: RESET
        gameState = GameState.PLACEMENT;
    }


    public void Update() {

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
        if (playerShips[player].length == 5) {
            if (player == 0) player = 1;
            else {
                player = 0;
                gameState = GameState.ATTACK;
            }
        }

        Point2D playerHighlight = fieldPanel.getBoardHighlight(player);

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
                        new ShipPiece((int)shipPlacementPoint.getX(), (int)shipPlacementPoint.getY(), false, ShipPart.Small)
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
