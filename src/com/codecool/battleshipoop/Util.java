package com.codecool.battleshipoop;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.sql.Timestamp;
import java.util.Random;

public class Util {
    public static Color fade(Color original, float alpha)
    {
        return new Color((float)original.getRed() / 255f, (float)original.getGreen() / 255f, (float)original.getBlue() / 255f, alpha);
    }

    public static Color rgbAColor(int r, int g, int b, float alpha)
    {
        return new Color((float)r / 255f, (float)g / 255f, (float)b / 255f, alpha);
    }

    public static Color rgbColor(int r, int g, int b)
    {
        return rgbAColor(r, g, b, 1);
    }

    public static AffineTransform rotateGraphics(Graphics2D g, float degrees, float x, float y)
    {
        AffineTransform backup = g.getTransform();
        AffineTransform a = AffineTransform.getRotateInstance(Math.toRadians (degrees), x, y);
        g.setTransform(a);
        return backup;
    }

    public static Point2D pointDistance(Point2D pointA, Point2D pointB) {
        return new Point2D.Double(pointB.getX() - pointA.getX(), pointB.getY() - pointA.getY());
    }

    public static float normalize(float original) {
        if (original == 0) return 0;
        if (original < 0) return -1;
        return 1;
    }

    public static Point2D pointDirection(Point2D pointA, Point2D pointB) {
        Point2D distance = pointDistance(pointA, pointB);
        if (pointA.equals(pointB) || normalize((float)Math.abs(distance.getX())) == normalize((float)Math.abs(distance.getY())))
            return null;

        float directionX = normalize((float)distance.getX());
        float directionY = normalize((float)distance.getY());

        return new Point2D.Double(directionX, directionY);
    }

    public static int random(int from, int to)
    {
        Random r = new Random();
        int low = Math.min(from, to);
        int high = Math.max(from, to);
        return r.nextInt(high-low) + low;
    }

    public static int elapsedMilliseconds(Timestamp currentTime, Timestamp oldTime)
    {
        long milliseconds1 = oldTime.getTime();
        long milliseconds2 = currentTime.getTime();

        return Math.round(milliseconds2 - milliseconds1);
    }

    public static float lerp(float a, float b, float f)
    {
        return a + f * (b - a);
    }

}
