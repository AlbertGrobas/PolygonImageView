package net.grobas.view;

import android.graphics.Path;

/**
 * Created by Albert on 05/05/2015.
 */
abstract public class BasePolygonShape implements PolygonShape {

    private Path mPath;

    public BasePolygonShape() {
        this.mPath = new Path();
    }

    @Override
    public Path getPolygonPath(PolygonShapeSpec spec) {
        float pointX, pointY, rotatedPointX, rotatedPointY;
        double angleRadians = Math.toRadians(spec.getRotation());

        mPath.reset();
        int i = 0;
        do {
            //next vertex point
            pointX = spec.getCenterX() + spec.getDiameter() / 2f *
                    (float) Math.cos(2 * Math.PI * i / spec.getNumVertex());
            pointY = spec.getCenterY() + spec.getDiameter() / 2f *
                    (float) Math.sin(2 * Math.PI * i / spec.getNumVertex());
            //rotate vertex
            rotatedPointX = (float) (Math.cos(angleRadians) * (pointX - spec.getCenterX()) -
                    Math.sin(angleRadians) * (pointY - spec.getCenterY()) + spec.getCenterX());
            rotatedPointY = (float) (Math.sin(angleRadians) * (pointX - spec.getCenterX()) +
                    Math.cos(angleRadians) * (pointY - spec.getCenterY()) + spec.getCenterY());

            if (i == 0) { //move to first vertex
                mPath.moveTo(rotatedPointX, rotatedPointY);
            } else {
                addEffect(rotatedPointX, rotatedPointY);
            }
            i++;
        } while (i < spec.getNumVertex());
        mPath.close();

        return mPath;
    }

    public Path getPath() {
        return mPath;
    }

    abstract void addEffect(float pointX, float pointY);

}
