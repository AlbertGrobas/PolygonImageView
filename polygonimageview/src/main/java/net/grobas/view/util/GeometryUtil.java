package net.grobas.view.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Geometry Util
 */
public class GeometryUtil {

    /**
     * Intersection between line, determined for two points, and a circle with radius x
     * @param pointA line point a
     * @param pointB line point b
     * @param center point center circle
     * @param radius circle radius
     * @return intersection points
     */
    public static List<Point> getCircleLineIntersectionPoint(Point pointA, Point pointB, Point center, double radius) {
        double baX = pointB.x - pointA.x;
        double baY = pointB.y - pointA.y;
        double caX = center.x - pointA.x;
        double caY = center.y - pointA.y;

        double a = baX * baX + baY * baY;
        double bBy2 = baX * caX + baY * caY;
        double c = caX * caX + caY * caY - radius * radius;

        double pBy2 = bBy2 / a;
        double q = c / a;

        double disc = pBy2 * pBy2 - q;
        if (disc < 0) {
            return Collections.emptyList();
        }

        double tmpSqrt = Math.sqrt(disc);
        double abScalingFactor1 = -pBy2 + tmpSqrt;
        double abScalingFactor2 = -pBy2 - tmpSqrt;

        Point p1 = new Point(pointA.x - baX * abScalingFactor1, pointA.y
                - baY * abScalingFactor1);
        if (disc == 0) {
            return Collections.singletonList(p1);
        }
        Point p2 = new Point(pointA.x - baX * abScalingFactor2, pointA.y
                - baY * abScalingFactor2);
        return Arrays.asList(p1, p2);
    }

    public static class Point {
        public double x, y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }


    }
}
