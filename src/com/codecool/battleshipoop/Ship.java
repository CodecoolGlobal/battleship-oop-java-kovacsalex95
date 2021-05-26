package com.codecool.battleshipoop;

import javax.swing.*;
import java.awt.geom.Point2D;

enum ShipPart
{
    Front,
    Middle,
    Rear,
    Small
}

class ShipPiece {
    public Point2D position;
    public boolean hit;

    public ShipPiece(int x, int y, boolean hit) {
        this.position = new Point2D.Double(x, y);
        this.hit = hit;
    }

    public ShipPart part;
}

public class Ship {
    public static int TYPE_CRUISER = 1;
    public static int TYPE_BATTLESHIP = 2;
    public static int TYPE_SUBMARINE = 3;
    public static int TYPE_DESTROYER = 4;
    public static int TYPE_CARRIER = 5;

    private ShipPiece[] positions;

    public float getAngle() {
        return angle;
    }

    private float angle;

    public Ship(ShipPiece[] positions, float angle) {
        this.positions = positions;
        this.angle = angle;
    }

    public boolean isDestroyed() {
        for (ShipPiece position : positions) {
            if (!position.hit) return false;
        }
        return true;
    }

}
