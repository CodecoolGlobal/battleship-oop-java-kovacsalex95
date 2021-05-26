package com.codecool.battleshipoop;

import java.awt.geom.Point2D;

enum ShipPart
{
    FRONT,
    MIDDLE,
    REAR,
    SMALL
}

class ShipPiece {
    public Point2D position;
    public boolean hit;

    public ShipPiece(int x, int y, boolean hit, ShipPart part) {
        this.part = part;
        this.position = new Point2D.Double(x, y);
        this.hit = hit;
    }
    public ShipPiece(Point2D position, boolean hit, ShipPart part) {
        this.part = part;
        this.position = position;
        this.hit = hit;
    }

    public ShipPart part;
}

public class Ship {

    public ShipPiece[] shipPieces;
    public float angle;


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
