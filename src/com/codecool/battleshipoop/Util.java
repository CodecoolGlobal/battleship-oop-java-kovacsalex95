package com.codecool.battleshipoop;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Util {
    public static Color fade(Color original, float alpha)
    {
        return new Color((float)original.getRed() / 255f, (float)original.getGreen() / 255f, (float)original.getBlue() / 255f, alpha);
    }

    public static Color rgbColor(int r, int g, int b)
    {
        return new Color((float)r / 255f, (float)g / 255f, (float)b / 255f);
    }

    public static AffineTransform rotateGraphics(Graphics2D g, float degrees, float x, float y)
    {
        AffineTransform backup = g.getTransform();
        AffineTransform a = AffineTransform.getRotateInstance(Math.toRadians (degrees), x, y);
        g.setTransform(a);
        return backup;
    }

    public static Ship[] addShip(Ship[] collection, Ship ship)
    {
        Ship[] result = new Ship[collection.length + 1];
        for (int i=0; i < collection.length; i++)
            result[i] = collection[i];
        result[collection.length] = ship;
        return result;
    }

    public static Ship[] removeShip(Ship[] collection)
    {
        Ship[] result = new Ship[collection.length - 1];
        for (int i=0; i < result.length; i++)
            result[i] = collection[i];
        return result;
    }
}
