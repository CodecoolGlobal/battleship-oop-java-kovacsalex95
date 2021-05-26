package com.codecool.battleshipoop;

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

    public ShipPiece(int x, int y, boolean hit, ShipPart part) {
        this.part = part;
        this.position = new Point2D.Double(x, y);
        this.hit = hit;
    }

    public ShipPart part;
}

public class Ship {

    private ShipPiece[] shipPieces;

    private float angle;


    public ShipPiece[] getShipPieces() {
        return shipPieces;
    }
    public float getAngle() {
        return angle;
    }


    public Ship(ShipPiece[] shipPieces, float angle) {
        this.shipPieces = shipPieces;
        this.angle = angle;
    }


    public boolean isDestroyed() {
        for (ShipPiece position : shipPieces) {
            if (!position.hit) return false;
        }
        return true;
    }

}
