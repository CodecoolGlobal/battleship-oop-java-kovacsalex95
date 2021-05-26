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

    public GameState getGameState() {
        return gameState;
    }

    private GameState gameState = GameState.NOT_STARTED;


    private int boardSize = 10;
    private int round = 0;
    private int player = 0;

    public Ship[][] playerShips = null;

    public Point2D[][] playerHits = null;

    private Point2D shipPlacementPoint = null;

    public int getBoardSize() {
        return boardSize;
    }
    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    public Game(MainWindow window)
    {
        this.window = window;
    }

    public void Start() {
        // RESET
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
        if (playerShips[player].length == 5) {
            if (player == 0) player = 1;
            else {
                player = 0;
                gameState = GameState.ATTACK;
            }
        }

        Point2D playerHighlight = window.fieldDrawer.getBoardHighlight(player);

        if (window.fieldDrawer.mouseInBoard() && playerHighlight != null) {
            if (window.fieldDrawer.mouseEvents.mouseLeftClick)
            {
                if (shipPlacementPoint == null)
                {
                    shipPlacementPoint = playerHighlight;
                }
                else
                {
                    ShipPiece[] shipPieces = new ShipPiece[] {
                        new ShipPiece((int)shipPlacementPoint.getX(), (int)shipPlacementPoint.getY(), false, ShipPart.Small)
                    };
                    Ship ship = new Ship(shipPieces, 0);
                    playerShips[player] = Util.addShip(playerShips[player], ship);
                    shipPlacementPoint = null;
                }
            }
            else if (window.fieldDrawer.mouseEvents.mouseRightClick)
            {
                playerShips[player] = Util.removeShip(playerShips[player]);
            }
        }
    }

    private void UpdateAttack() {
    }
}
