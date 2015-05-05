package net.grobas.view;

import android.graphics.Path;

/**
 * Created by Albert on 05/05/2015.
 */
public interface PolygonShape {

    Path getPolygonPath(PolygonShapeSpec polygonShapeSpec);
}
