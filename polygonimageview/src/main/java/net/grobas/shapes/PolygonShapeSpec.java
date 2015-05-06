package net.grobas.shapes;

import android.graphics.Color;

/**
 * Basic specification for a shape
 */
public class PolygonShapeSpec {

    //static vars
    private static final float DEFAULT_SHADOW_RADIUS = 7.5f;
    private static final int DEFAULT_SHADOW_COLOR = Color.BLACK;
    private static final float DEFAULT_X_OFFSET = 0f;
    private static final float DEFAULT_Y_OFFSET = 0f;

    //vars
    private float rotation;
    private int numVertex;
    private boolean hasBorder;
    private float cornerRadius;
    private int borderColor;
    private int borderWidth;
    private boolean hasShadow;
    private float shadowRadius;
    private float shadowXOffset, shadowYOffset;
    private int shadowColor;

    //size and position
    private float diameter;
    private float centerX, centerY;

    public PolygonShapeSpec() {
        defaultShadow();
    }

    public PolygonShapeSpec(float centerX, float centerY, float diameter, int numVertex, float rotation) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.diameter = diameter;
        this.numVertex = numVertex;
        this.rotation = rotation;

        defaultShadow();
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }

    public float getCenterX() {
        return centerX;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    public float getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    public float getDiameter() {
        return diameter;
    }

    public void setDiameter(float diameter) {
        this.diameter = diameter;
    }

    public boolean hasShadow() {
        return hasShadow;
    }

    public void setHasShadow(boolean hasShadow) {
        this.hasShadow = hasShadow;
        if(!this.hasShadow)
            defaultShadow();
    }

    public boolean hasBorder() {
        return hasBorder;
    }

    public void setHasBorder(boolean hasBorder) {
        this.hasBorder = hasBorder;
    }

    public float getShadowXOffset() {
        return shadowXOffset;
    }

    public void setShadowXOffset(float shadowXOffset) {
        this.shadowXOffset = shadowXOffset;
    }

    public float getShadowYOffset() {
        return shadowYOffset;
    }

    public void setShadowYOffset(float shadowYOffset) {
        this.shadowYOffset = shadowYOffset;
    }

    public float getShadowRadius() {
        return shadowRadius;
    }

    public void setShadowRadius(float shadowRadius) {
        this.shadowRadius = shadowRadius;
    }

    public int getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(int shadowColor) {
        this.shadowColor = shadowColor;
    }

    public int getNumVertex() {
        return numVertex;
    }

    public void setNumVertex(int numVertex) {
        this.numVertex = numVertex;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public void updatePosition(float centerX, float centerY, float diameter) {
        setCenterX(centerX);
        setCenterY(centerY);
        setDiameter(diameter);
    }

    private void defaultShadow() {
        shadowRadius = DEFAULT_SHADOW_RADIUS;
        shadowXOffset = DEFAULT_X_OFFSET;
        shadowYOffset = DEFAULT_Y_OFFSET;
        shadowColor = DEFAULT_SHADOW_COLOR;
    }
}
