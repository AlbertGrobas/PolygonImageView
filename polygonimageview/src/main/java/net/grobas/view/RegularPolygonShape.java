package net.grobas.view;

/**
 * Created by Albert on 05/05/2015.
 */
public class RegularPolygonShape extends BasePolygonShape {

    public RegularPolygonShape() {
        super();
    }

    @Override
    void addEffect(float pointX, float pointY) {
        getPath().lineTo(pointX, pointY);
    }
}
