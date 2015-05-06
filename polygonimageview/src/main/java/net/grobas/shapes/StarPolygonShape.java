package net.grobas.shapes;

import net.grobas.view.util.GeometryUtil;

import java.util.List;

/**
 * Create a star shape
 */
public class StarPolygonShape extends BasePolygonShape {
    //a inner radius scale factor
    private float radiusScale;
    //determine if it's concave
    private boolean isConcave;

    public StarPolygonShape(float valor, boolean isConcave) {
        this.radiusScale = valor;
        this.isConcave = isConcave;
    }

    @Override
    protected void addEffect(float currentX, float currentY, float nextX, float nextY) {
        float cX = getPolygonShapeSpec().getCenterX();
        float cY = getPolygonShapeSpec().getCenterY();
        float radius = (getPolygonShapeSpec().getDiameter()/2f) * radiusScale;

        float pX = (currentX + nextX)/2f;
        float pY = (currentY + nextY)/2f;

        List<GeometryUtil.Point> p = GeometryUtil.getCircleLineIntersectionPoint(new GeometryUtil.Point(pX, pY),
                new GeometryUtil.Point(cX, cY), new GeometryUtil.Point(cX, cY), radius);

        if(isConcave) {
            getPath().quadTo((float) p.get(0).x, (float) p.get(0).y, nextX, nextY);
        } else {
            getPath().lineTo((float) p.get(0).x, (float) p.get(0).y);
            getPath().lineTo(nextX, nextY);
        }
    }

    public boolean isConcave() {
        return isConcave;
    }

    public void setIsConcave(boolean isConcave) {
        this.isConcave = isConcave;
    }

    public float getRadiusScale() {
        return radiusScale;
    }

    public void setRadiusScale(float radiusScale) {
        this.radiusScale = radiusScale;
    }
}
