package com.codecool.battleshipoop;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

enum ParticleZOrder {
    FIRST_ON_TOP,
    LAST_ON_TOP,
}

public class ParticleSystem {
    public ParticleZOrder zOrder = ParticleZOrder.LAST_ON_TOP;

    private ImageIcon[] images;
    private Particle[] particles;

    private OpacityKeyFrame[] opacityKeyFrames = new OpacityKeyFrame[0];
    private SizeKeyFrame[] sizeKeyFrames = new SizeKeyFrame[0];
    private RotationKeyFrame[] rotationKeyFrames = new RotationKeyFrame[0];
    private PositionKeyFrame[] positionKeyFrames = new PositionKeyFrame[0];
    private IndexKeyFrame[] indexKeyFrames = new IndexKeyFrame[0];

    public float defaultOpacity = 1;
    public Dimension defaultSize = new Dimension(10, 10);
    public float defaultRotation = 0;
    public Point2D defaultPosition = new Point2D.Double(0, 0);
    public int defaultImageIndex = 0;

    private boolean repeating = false;
    private float duration = 5; // másodperc

    public float scale = 1;

    public ParticleSystem(ImageIcon[] images, int particleCount, float duration, boolean repeating, boolean preHeating)
    {
        this.duration = duration;
        this.repeating = repeating;
        this.images = images;

        particles = new Particle[particleCount];

        for (int i=0; i<particleCount; i++)
        {
            particles[i] = new Particle((float)i / (float)particleCount, defaultOpacity, defaultRotation, defaultSize, defaultPosition, defaultImageIndex, i == 0 || preHeating);
        }

        reorderParticles();
    }

    public void draw(Graphics2D graphics, double x, double y)
    {
        draw(graphics, Math.round(x), Math.round(y));
    }
    public void draw(Graphics2D graphics, float x, float y)
    {
        draw(graphics, Math.round(x), Math.round(y));
    }
    public void draw(Graphics2D graphics, int x, int y)
    {
        Image[] images = new Image[this.images.length];
        for (int i=0; i<images.length; i++)
        {
            images[i] = this.images[i].getImage();
        }
        //this.image.getImage();

        for (int i=0; i<particles.length; i++)
        {
            if (!particles[i].enabled)
                continue;


            // calculate keyframes
            if (opacityKeyFrames.length > 0) {
                int opacityKeyFrameIndex = getKeyframeIndexFromPosition(opacityKeyFrames, particles[i].progress);
                int opacityNextKeyframeIndex = opacityKeyFrameIndex < opacityKeyFrames.length - 1 ? opacityKeyFrameIndex + 1 : 0;

                float ratioDelta = opacityKeyFrames[opacityNextKeyframeIndex].point - opacityKeyFrames[opacityKeyFrameIndex].point;
                float ratioMin = particles[i].progress - opacityKeyFrames[opacityKeyFrameIndex].point;
                float opacityRatio = ratioMin / ratioDelta;

                particles[i].opacity = lerp(opacityKeyFrames[opacityKeyFrameIndex].opacity, opacityKeyFrames[opacityNextKeyframeIndex].opacity, opacityRatio);
            }
            else
                particles[i].opacity = defaultOpacity;

            if (sizeKeyFrames.length > 0) {
                int sizeKeyFrameIndex = getKeyframeIndexFromPosition(sizeKeyFrames, particles[i].progress);
                int sizeNextKeyframeIndex = sizeKeyFrameIndex < sizeKeyFrames.length - 1 ? sizeKeyFrameIndex + 1 : 0;

                float ratioDelta = sizeKeyFrames[sizeNextKeyframeIndex].point - sizeKeyFrames[sizeKeyFrameIndex].point;
                float ratioMin = particles[i].progress - sizeKeyFrames[sizeKeyFrameIndex].point;
                float sizeRatio = ratioMin / ratioDelta;

                float sizeX = lerp((float)sizeKeyFrames[sizeKeyFrameIndex].size.width, (float)sizeKeyFrames[sizeNextKeyframeIndex].size.width, sizeRatio);
                float sizeY = lerp((float)sizeKeyFrames[sizeKeyFrameIndex].size.height, (float)sizeKeyFrames[sizeNextKeyframeIndex].size.height, sizeRatio);

                particles[i].size = new Dimension(Math.round(sizeX), Math.round(sizeY));
            }
            else
                particles[i].size = defaultSize;

            if (rotationKeyFrames.length > 0) {
                int rotationKeyFrameIndex = getKeyframeIndexFromPosition(rotationKeyFrames, particles[i].progress);
                int rotationNextKeyframeIndex = rotationKeyFrameIndex < rotationKeyFrames.length - 1 ? rotationKeyFrameIndex + 1 : 0;

                float ratioDelta = rotationKeyFrames[rotationNextKeyframeIndex].point - rotationKeyFrames[rotationKeyFrameIndex].point;
                float ratioMin = particles[i].progress - rotationKeyFrames[rotationKeyFrameIndex].point;
                float rotationRatio = ratioMin / ratioDelta;

                particles[i].rotation = lerpRotation(rotationKeyFrames[rotationKeyFrameIndex].rotation, rotationKeyFrames[rotationNextKeyframeIndex].rotation, rotationRatio);
            }
            else
                particles[i].rotation = defaultRotation;

            if (positionKeyFrames.length > 0) {
                int positionKeyFrameIndex = getKeyframeIndexFromPosition(positionKeyFrames, particles[i].progress);
                int positionNextKeyframeIndex = positionKeyFrameIndex < positionKeyFrames.length - 1 ? positionKeyFrameIndex + 1 : 0;

                float ratioDelta = positionKeyFrames[positionNextKeyframeIndex].point - positionKeyFrames[positionKeyFrameIndex].point;
                float ratioMin = particles[i].progress - positionKeyFrames[positionKeyFrameIndex].point;
                float positionRatio = ratioMin / ratioDelta;

                float positionX = lerp((float)positionKeyFrames[positionKeyFrameIndex].position.getX(), (float)positionKeyFrames[positionNextKeyframeIndex].position.getX(), positionRatio);
                float positionY = lerp((float)positionKeyFrames[positionKeyFrameIndex].position.getY(), (float)positionKeyFrames[positionNextKeyframeIndex].position.getY(), positionRatio);

                particles[i].position = new Point2D.Double(positionX, positionY);
            }
            else
                particles[i].position = defaultPosition;

            Image image1;
            Image image2;
            float imageLerp = 0;

            if (indexKeyFrames.length > 0) {
                int indexKeyFrameIndex = getKeyframeIndexFromPosition(indexKeyFrames, particles[i].progress);
                int indexNextKeyFrameIndex = indexKeyFrameIndex < indexKeyFrames.length - 1 ? indexKeyFrameIndex + 1 : 0;

                float ratioDelta = indexKeyFrames[indexNextKeyFrameIndex].point - indexKeyFrames[indexKeyFrameIndex].point;
                float ratioMin = particles[i].progress - indexKeyFrames[indexKeyFrameIndex].point;
                imageLerp = ratioMin / ratioDelta;

                particles[i].imageIndex = indexKeyFrames[indexKeyFrameIndex].index;

                image1 = images[indexKeyFrames[indexKeyFrameIndex].index];
                image2 = images[indexKeyFrames[indexNextKeyFrameIndex].index];
            }
            else {
                particles[i].imageIndex = defaultImageIndex;
                image1 = image2 = images[particles[i].imageIndex];
                imageLerp = 0;
            }

            particles[i].imageIndex = Math.max(0, Math.min(images.length - 1, particles[i].imageIndex));


            // draw particle
            float fullOpacity = particles[i].opacity;
            float image1Transp = (1 - imageLerp) * fullOpacity;
            float image2Transp = imageLerp * fullOpacity;

            if (image1Transp > 0)
                image1Transp = (float)Math.sqrt(image1Transp);

            if (image2Transp > 0)
                image2Transp = (float)Math.sqrt(image2Transp);

            image1Transp = Math.max(0f, Math.min(1f, image1Transp));
            image2Transp = Math.max(0f, Math.min(1f, image2Transp));

            AffineTransform transformBackup = graphics.getTransform();
            AffineTransform rotatedTransform = AffineTransform.getRotateInstance(Math.toRadians (particles[i].rotation), x + (particles[i].position.getX() * scale), y + (particles[i].position.getY() * scale));
            graphics.setTransform(rotatedTransform);

            AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, image1Transp);

            graphics.setComposite(composite);

            graphics.drawImage(image1, x + (int)Math.floor((float)(particles[i].position.getX() * scale) - (float)(particles[i].size.width * scale) / 2f), y + (int)Math.round((float)(particles[i].position.getY() * scale) - (float)(particles[i].size.height * scale) / 2f), (int)Math.ceil(particles[i].size.width * scale), (int)Math.ceil(particles[i].size.height * scale), null);

            composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, image2Transp);

            graphics.setComposite(composite);

            graphics.drawImage(image2, x + (int)Math.floor((float)(particles[i].position.getX() * scale) - (float)(particles[i].size.width * scale) / 2f), y + (int)Math.round((float)(particles[i].position.getY() * scale) - (float)(particles[i].size.height * scale) / 2f), (int)Math.ceil(particles[i].size.width * scale), (int)Math.ceil(particles[i].size.height * scale), null);

            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));

            graphics.setTransform(transformBackup);
        }
    }


    public void frame()
    {
        boolean reorderZ = false;

        for (int i=0; i<particles.length; i++) {
            if (particles[i].progress >= 1) {
                particles[i].progress -= 1;
                particles[i].enabled = true;
                reorderZ = true;
            }

            // advance progress
            particles[i].progress += frameStep();
        }

        if (reorderZ)
            reorderParticles();
    }


    private void reorderParticles()
    {
        int iteration = 0;
        int particleCount = particles.length;

        while (iteration < particleCount - 1)
        {
            int subIteration = 0;

            while (subIteration < particleCount - iteration - 1)
            {
                if ((zOrder == ParticleZOrder.LAST_ON_TOP && particles[subIteration].progress > particles[subIteration + 1].progress) ||
                        (zOrder == ParticleZOrder.FIRST_ON_TOP && particles[subIteration].progress < particles[subIteration + 1].progress))
                {
                    Particle temp = particles[subIteration + 1];
                    particles[subIteration + 1] = particles[subIteration];
                    particles[subIteration] = temp;
                }

                subIteration++;
            }

            iteration++;
        }
    }

    private float frameStep()
    {
        float frameTime = 1f / 50f;
        return frameTime * (1f / duration);
    }

    private float lerp(float a, float b, float t)
    {
        return a + t * (b - a);
    }

    public static float lerpRotation(float a, float b, float t)
    {
        float difference = Math.abs(b - a);
        if (difference > 180) {
            if (b > a)
                a += 360;
            else
                b += 360;
        }

        float value = (a + ((b - a) * t));

        float rangeZero = 360;

        if (value >= 0 && value <= 360)
            return value;

        return (value % rangeZero);
    }

    private OpacityKeyFrame[] addToArray(OpacityKeyFrame[] array, OpacityKeyFrame element)
    {
        OpacityKeyFrame[] result = new OpacityKeyFrame[array.length + 1];
        for (int i=0; i< array.length; i++)
            result[i] = array[i];
        result[array.length] = element;
        return result;
    }
    private RotationKeyFrame[] addToArray(RotationKeyFrame[] array, RotationKeyFrame element)
    {
        RotationKeyFrame[] result = new RotationKeyFrame[array.length + 1];
        for (int i=0; i< array.length; i++)
            result[i] = array[i];
        result[array.length] = element;
        return result;
    }
    private SizeKeyFrame[] addToArray(SizeKeyFrame[] array, SizeKeyFrame element)
    {
        SizeKeyFrame[] result = new SizeKeyFrame[array.length + 1];
        for (int i=0; i< array.length; i++)
            result[i] = array[i];
        result[array.length] = element;
        return result;
    }
    private PositionKeyFrame[] addToArray(PositionKeyFrame[] array, PositionKeyFrame element)
    {
        PositionKeyFrame[] result = new PositionKeyFrame[array.length + 1];
        for (int i=0; i< array.length; i++)
            result[i] = array[i];
        result[array.length] = element;
        return result;
    }
    private IndexKeyFrame[] addToArray(IndexKeyFrame[] array, IndexKeyFrame element)
    {
        IndexKeyFrame[] result = new IndexKeyFrame[array.length + 1];
        for (int i=0; i< array.length; i++)
            result[i] = array[i];
        result[array.length] = element;
        return result;
    }


    private boolean keyframeExists(float point, KeyFrame[] array)
    {
        if (array.length == 0)
            return false;

        for (int i=0; i< array.length; i++) {
            if (array[i].point == point)
                return true;
        }
        return false;
    }

    private int getKeyframeIndexFromPosition(KeyFrame[] keyFrames, float point)
    {
        if (keyFrames.length <= 1)
            return 0;

        int result = 0;

        for (int i = 0; i<keyFrames.length; i++) {
            if (point >= keyFrames[i].point)
                result = i;
        }

        return result;
    }


    public void addOpacityKeyFrame(float point, float opacity)
    {
        // check false input
        if (point < 0 || point > 1 || opacity < 0 || opacity > 1)
            return;

        if (keyframeExists(point, opacityKeyFrames))
            return;

        opacityKeyFrames = addToArray(opacityKeyFrames, new OpacityKeyFrame(point, opacity));
    }

    public void addSizeKeyFrame(float point, float width, float height)
    {
        addSizeKeyFrame(point, new Dimension(Math.round(width), Math.round(height)));
    }
    public void addSizeKeyFrame(float point, int width, int height)
    {
        addSizeKeyFrame(point, new Dimension(width, height));
    }
    public void addSizeKeyFrame(float point, Dimension size)
    {
        // check false input
        if (point < 0 || point > 1)
            return;

        if (keyframeExists(point, sizeKeyFrames))
            return;

        sizeKeyFrames = addToArray(sizeKeyFrames, new SizeKeyFrame(point, size));
    }

    public void addRotationKeyFrame(float point, float rotation)
    {
        // check false input
        if (point < 0 || point > 1)
            return;

        if (keyframeExists(point, rotationKeyFrames))
            return;

        rotationKeyFrames = addToArray(rotationKeyFrames, new RotationKeyFrame(point, rotation));
    }

    public void addPositionKeyFrame(float point, float x, float y)
    {
        addPositionKeyFrame(point, new Point2D.Double(x, y));
    }
    public void addPositionKeyFrame(float point, Point2D position)
    {
        // check false input
        if (point < 0 || point > 1)
            return;

        if (keyframeExists(point, positionKeyFrames))
            return;

        positionKeyFrames = addToArray(positionKeyFrames, new PositionKeyFrame(point, position));
    }

    public void addIndexKeyFrame(float point, int index)
    {
        if (point < 0 || point > 1 || index < 0 || index >= images.length)
            return;

        if (keyframeExists(point, indexKeyFrames))
            return;

        indexKeyFrames = addToArray(indexKeyFrames, new IndexKeyFrame(point, index));
    }

    public void autoIndexKeyFrames()
    {
        float distance = 1f / images.length;

        for (int i=0; i<images.length; i++)
        {
            addIndexKeyFrame(distance * i, i);
        }
    }
}

class Particle
{
    public boolean enabled;

    public float progress;

    public float opacity;
    public float rotation;
    public Dimension size;
    public Point2D position;

    public int imageIndex;

    public Particle(float progress, float opacity, float rotation, Dimension size, Point2D position, int imageIndex, boolean enabled)
    {
        this.progress = progress;
        this.opacity = opacity;
        this.rotation = rotation;
        this.size = size;
        this.position = position;
        this.enabled = enabled;
        this.imageIndex = imageIndex;
    }
}

class KeyFrame
{
    public float point;
}

class OpacityKeyFrame extends KeyFrame
{
    public float opacity;

    public OpacityKeyFrame(float point, float opacity)
    {
        this.point = point;
        this.opacity = opacity;
    }
}

class SizeKeyFrame extends KeyFrame
{
    public Dimension size;

    public SizeKeyFrame(float point, Dimension size)
    {
        this.point = point;
        this.size = size;
    }
}

class RotationKeyFrame extends KeyFrame
{
    public float rotation;

    public RotationKeyFrame(float point, float rotation)
    {
        this.point = point;
        this.rotation = rotation;
    }

    public float radians()
    {
        return (float)Math.toRadians(rotation);
    }
}

class PositionKeyFrame extends KeyFrame
{
    public Point2D position;

    public PositionKeyFrame(float point, Point2D position)
    {
        this.point = point;
        this.position = position;
    }
}

class IndexKeyFrame extends KeyFrame
{
    public int index;

    public IndexKeyFrame(float point, int index)
    {
        this.point = point;
        this.index = index;
    }
}
