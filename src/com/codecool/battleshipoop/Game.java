package com.codecool.battleshipoop;

import java.awt.geom.Point2D;

public class Game {
    private MainWindow window;

    private int boardSize = 10;

    private int round = 0;
    private int player = 0;

    private Ship[] player1Ships;
    private Ship[] player2Ships;

    private Point2D[] player1Hits;
    private Point2D[] player2Hits;

    public Game(MainWindow window)
    {
        this.window = window;

        // player1Hits[0] = new Point2D.Double(0, 0);
    }
}
