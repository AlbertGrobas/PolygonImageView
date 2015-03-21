/*
 * Copyright (C) 2014 Albert Grobas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.grobas.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Construct a custom ImageView with a regular polygonal form.
 * The number of vertices determines the polygon form.
 * Special cases for vertex number are:
 * 0 -> Circle
 * 1 -> Regular ImageView
 * 2 -> Square
 * Use square images
 *
 * @author Albert Grobas
 */
public class PolygonImageView extends ImageView {

    //static vars
    private static final float DEFAULT_SHADOW_RADIUS = 7.5f;
    private static final int DEFAULT_SHADOW_COLOR = Color.BLACK;
    private static final float DEFAULT_X_OFFSET = 0f;
    private static final float DEFAULT_Y_OFFSET = 0f;

    //vars
    private float rotationAngle;
    private int numVertices;
    private boolean isBordered;
    private float cornerRadius;
    private int borderColor;
    private int borderWidth;
    private boolean hasShadow;
    private float shadowRadius;
    private float shadowXOffset, shadowYOffset;

    //size and position
    private float mDiameter;
    private int canvasHeight, canvasWidth;
    private float centerX, centerY;

    //draws
    private Paint mPaint;
    private Paint mBorderPaint;
    private Path mPath;

    public PolygonImageView(Context context) {
        this(context, null);
    }

    public PolygonImageView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.polygonImageViewStyle);
    }

    public PolygonImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.PolygonImageView, defStyle, 0);

        try {
            rotationAngle = attributes.getFloat(R.styleable.PolygonImageView_poly_rotation_angle, 0f);
            numVertices = attributes.getInteger(R.styleable.PolygonImageView_poly_vertices, 5);
            cornerRadius = attributes.getFloat(R.styleable.PolygonImageView_poly_corner_radius, 0f);
            hasShadow = attributes.getBoolean(R.styleable.PolygonImageView_poly_shadow, false);
            isBordered = attributes.getBoolean(R.styleable.PolygonImageView_poly_border, false);
            borderColor = attributes.getColor(R.styleable.PolygonImageView_poly_border_color, Color.WHITE);
            borderWidth = attributes.getDimensionPixelOffset(R.styleable.PolygonImageView_poly_border_width,
                    (int) (getResources().getDisplayMetrics().density) * 4);

        } finally {
            attributes.recycle();
        }

        init();
    }

    /**
     * Init paints and effects
     */
    @SuppressLint("NewApi")
    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setPathEffect(new CornerPathEffect(cornerRadius));
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setPathEffect(new CornerPathEffect(cornerRadius));
        mBorderPaint.setColor(borderColor);
        if (isBordered)
            mBorderPaint.setStrokeWidth(borderWidth);

        //Avoid known shadow problems
        if (Build.VERSION.SDK_INT > 13)
            setLayerType(LAYER_TYPE_SOFTWARE, mBorderPaint);

        if (hasShadow) {
            //Shadow on border even if isBordered is false. Better effect and performance that
            //using shadow on main paint
            mBorderPaint.setShadowLayer(DEFAULT_SHADOW_RADIUS, DEFAULT_X_OFFSET, DEFAULT_Y_OFFSET, DEFAULT_SHADOW_COLOR);
            shadowRadius = DEFAULT_SHADOW_RADIUS;
            shadowXOffset = DEFAULT_X_OFFSET;
            shadowYOffset = DEFAULT_Y_OFFSET;
        }
    }

    /**
     * Gets incoming new canvas size and updates polygon form and image if needed.
     *
     * @param w
     * @param h
     * @param oldW
     * @param oldH
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        canvasWidth = w;
        canvasHeight = h;
        updatePolygonSize();

        if (Math.min(canvasWidth, canvasHeight) != Math.min(oldW, oldH))
            refreshImage();
    }

    /**
     * Draw the polygon form.
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null || getDrawable().getIntrinsicWidth() == 0 ||
                getDrawable().getIntrinsicHeight() == 0)
            return;

        switch (numVertices) {
            case 0: //CIRCLE
                if (hasShadow || isBordered)
                    canvas.drawCircle(centerX, centerY, mDiameter / 2, mBorderPaint);
                canvas.drawCircle(centerX, centerY, mDiameter / 2, mPaint);
                break;
            case 1: //REGULAR IMAGEVIEW
                super.onDraw(canvas);
                break;
            case 2: //SQUARE
                if (hasShadow || isBordered)
                    canvas.drawRect(centerX - mDiameter / 2, centerY - mDiameter / 2, centerX + mDiameter / 2,
                            centerY + mDiameter / 2, mBorderPaint);
                canvas.drawRect(centerX - mDiameter / 2, centerY - mDiameter / 2, centerX + mDiameter / 2,
                        centerY + mDiameter / 2, mPaint);
                break;
            default: //POLYGON
                if (hasShadow || isBordered)
                    canvas.drawPath(mPath, mBorderPaint);
                canvas.drawPath(mPath, mPaint);
        }
    }

    /**
     * Take cares about padding changes.
     *
     * @param start
     * @param top
     * @param end
     * @param bottom
     */
    @TargetApi(17)
    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        super.setPaddingRelative(start, top, end, bottom);
        updatePolygonSize();
        invalidate();
    }

    /**
     * Take cares about padding changes.
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        updatePolygonSize(left, top, right, bottom);
        invalidate(left, top, right, bottom);
    }

    /**
     * Update image.
     *
     * @param bm new image.
     */
    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        refreshImage();
        invalidate();
    }

    /**
     * Update image.
     *
     * @param drawable new image.
     */
    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        refreshImage();
        invalidate();
    }

    /**
     * Update image.
     *
     * @param resId new image.
     */
    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        refreshImage();
        invalidate();
    }

    /**
     * Update image.
     *
     * @param uri new image.
     */
    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        refreshImage();
        invalidate();
    }

    /**
     * Refresh image with new canvas size or new image.
     */
    private void refreshImage() {
        Bitmap image = drawableToBitmap(getDrawable());
        int canvasSize = Math.min(canvasWidth, canvasHeight);
        if (canvasSize > 0 && image != null) {
            //Preserve image ratio if it is not square
            BitmapShader shader = new BitmapShader(ThumbnailUtils.extractThumbnail(image, canvasSize, canvasSize),
                    Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mPaint.setShader(shader);
        }
    }

    /**
     * Rebuild polygon after changes, take cares about padding, border and shadow radius.
     * Rotate vertices with the variable angle.
     */
    private void rebuildPolygon() {
        if (mPath == null)
            mPath = new Path();
        else
            mPath.reset();
        //recalculate new center
        int borderNeeded = isBordered ? borderWidth : 0;
        centerX = mDiameter / 2 + (float) (getPaddingLeft() + getPaddingRight()) / 2 + borderNeeded +
                shadowRadius + Math.abs(shadowXOffset);
        centerY = mDiameter / 2 + (float) (getPaddingTop() + getPaddingBottom()) / 2 + borderNeeded +
                shadowRadius + Math.abs(shadowYOffset);

        if (numVertices < 3)
            return;

        float pointX, pointY, rotatedPointX, rotatedPointY;
        double angleRadians = Math.toRadians(rotationAngle);

        int i = 0;
        do {
            //next vertex point
            pointX = centerX + mDiameter / 2f * (float) Math.cos(2 * Math.PI * i / numVertices);
            pointY = centerY + mDiameter / 2f * (float) Math.sin(2 * Math.PI * i / numVertices);
            //rotate vertex
            rotatedPointX = (float) (Math.cos(angleRadians) * (pointX - centerX) -
                    Math.sin(angleRadians) * (pointY - centerY) + centerX);
            rotatedPointY = (float) (Math.sin(angleRadians) * (pointX - centerX) +
                    Math.cos(angleRadians) * (pointY - centerY) + centerY);

            if (i == 0) { //move to first vertex
                mPath.moveTo(rotatedPointX, rotatedPointY);
            } else {
                mPath.lineTo(rotatedPointX, rotatedPointY);
            }
            i++;
        } while (i < numVertices);
        mPath.close();
    }

    /**
     * Update polygon size with unspecified padding.
     */
    private void updatePolygonSize() {
        updatePolygonSize(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
    }

    /**
     * Update polygon with new padding.
     *
     * @param l Left padding.
     * @param t Top padding.
     * @param r Right padding.
     * @param b Bottom padding.
     */
    private void updatePolygonSize(int l, int t, int r, int b) {
        int borderPadding = isBordered ? borderWidth : 0;
        float xPadding = (float) (l + r + borderPadding * 2 + shadowRadius * 2 + Math.abs(shadowXOffset));
        float yPadding = (float) (t + b + borderPadding * 2 + shadowRadius * 2 + Math.abs(shadowYOffset));
        float diameter = Math.min((float) canvasWidth - xPadding, (float) canvasHeight - yPadding);
        //if the size is changed we need to rebuild the polygon
        if (diameter != mDiameter) {
            mDiameter = diameter;
            rebuildPolygon();
        }
    }

    /**
     * Returns the rotate angle.
     *
     * @return angle in degrees.
     */
    public float getAngle() {
        return rotationAngle;
    }

    /**
     * Set new rotate angle and updates polygon form.
     *
     * @param mAngle angle in degrees.
     */
    public void setRotationAngle(float mAngle) {
        this.rotationAngle = mAngle;
        rebuildPolygon();
        invalidate();
    }

    /**
     * Returns the vertex number.
     *
     * @return
     */
    public int getVertices() {
        return numVertices;
    }

    /**
     * Sets the new vertex number and updates polygon form.
     *
     * @param numVertices
     */
    public void setVertices(int numVertices) {
        this.numVertices = numVertices;
        rebuildPolygon();
        invalidate();
    }

    /**
     * Indicates if it's bordered.
     *
     * @return
     */
    public boolean isBordered() {
        return isBordered;
    }

    /**
     * Enables or disables the border option.
     *
     * @param bordered
     */
    public void setBorder(boolean bordered) {
        isBordered = bordered;
        updateBorderSpecs();
    }

    /**
     * Sets new border width.
     *
     * @param borderWidthPixels new width in pixels.
     */
    public void setBorderWidth(int borderWidthPixels) {
        if (isBordered) {
            borderWidth = borderWidthPixels;
            updateBorderSpecs();
        }
    }

    /**
     * Sets new border width and update polygon size.
     */
    private void updateBorderSpecs() {
        mBorderPaint.setStrokeWidth(borderWidth);
        updatePolygonSize();
        invalidate();
    }

    /**
     * Sets new radius for corners and updates view.
     *
     * @param cornerRadius
     */
    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
        mBorderPaint.setPathEffect(new CornerPathEffect(cornerRadius));
        mPaint.setPathEffect(new CornerPathEffect(cornerRadius));
        invalidate();
    }

    /**
     * Sets new border color.
     *
     * @param borderColor Color class var.
     */
    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        mBorderPaint.setColor(borderColor);
        invalidate();
    }

    /**
     * Sets new border color
     *
     * @param resourceBorderColor Resource xml color.
     */
    public void setBorderColorResource(int resourceBorderColor) {
        setBorderColor(getResources().getColor(resourceBorderColor));
    }

    /**
     * Adds a default shadow
     */
    public void addShadow() {
        addShadow(DEFAULT_SHADOW_RADIUS, DEFAULT_X_OFFSET, DEFAULT_Y_OFFSET, DEFAULT_SHADOW_COLOR);
    }

    /**
     * Adds a specific shadow.
     *
     * @param radius  shadow blur size.
     * @param offsetX negative value moves shadow to left and positive to right.
     * @param offsetY negative value moves shadow down and positive towards up.
     * @param color   shadow color
     */
    public void addShadow(float radius, float offsetX, float offsetY, int color) {
        shadowRadius = radius;
        shadowXOffset = offsetX;
        shadowYOffset = offsetY;
        hasShadow = true;

        mBorderPaint.setShadowLayer(shadowRadius, shadowXOffset, shadowYOffset, color);
        updatePolygonSize();
        invalidate();
    }

    /**
     * Removes shadow
     */
    public void clearShadow() {
        if (!hasShadow)
            return;

        shadowRadius = 0f;
        shadowXOffset = 0f;
        shadowYOffset = 0f;
        hasShadow = false;
        mBorderPaint.clearShadowLayer();
        updatePolygonSize();
        invalidate();
    }

    /**
     * Transforms a drawable into a bitmap.
     *
     * @param drawable
     * @return new bitmap
     */
    private static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        } else if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        //Avoid Color Drawable special case
        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap;
        try {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError e) {
            return null;
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
