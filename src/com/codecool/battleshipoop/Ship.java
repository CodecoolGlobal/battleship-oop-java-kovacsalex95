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
    public int[] particleSystems = null;

    public ShipPiece(int x, int y, boolean hit, ShipPart part) {
        this.part = part;
        this.position = new Point2D.Double(x, y);
        this.hit = hit;
        this.explosionAngleOffset = Util.random(0, 360);
        this.explosionAngleSpeed = Util.random(-2, 2);
    }
    public ShipPiece(Point2D position, boolean hit, ShipPart part) {
        this.part = part;
        this.position = position;
        this.hit = hit;
        this.explosionAngleOffset = Util.random(0, 360);
        this.explosionAngleSpeed = Util.random(-2, 2);
    }

    public ShipPart part;
    public float explosionAngleOffset;
    public float explosionAngleSpeed;
}

public class Ship {

    public ShipPiece[] shipPieces;
    public float angle;

    public float xDegree;
    public float yDegree;


    public Ship(ShipPiece[] shipPieces, float angle) {
        this.shipPieces = shipPieces;
        this.angle = angle;

        xDegree = Util.random(0, 360);
        yDegree = Util.random(0, 360);
    }

    public Point2D offset(float speed)
    {
        xDegree += speed;
        yDegree += speed * 1.2f;

        if (xDegree > 360)
            xDegree -= 360;

        if (yDegree > 360)
            yDegree -= 360;

        float xOffset = (float)Math.sin(Math.toRadians(xDegree));
        float yOffset = (float)Math.sin(Math.toRadians(yDegree));

        return new Point2D.Double(xOffset, yOffset);
    }


    public boolean isDestroyed() {
        for (ShipPiece position : shipPieces) {
            if (!position.hit) return false;
        }
        return true;
    }

}
