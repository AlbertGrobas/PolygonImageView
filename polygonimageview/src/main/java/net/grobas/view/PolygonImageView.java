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
import android.graphics.ColorFilter;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import net.grobas.shapes.PolygonShape;
import net.grobas.shapes.PolygonShapeSpec;
import net.grobas.shapes.RegularPolygonShape;

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

    //draws
    private Paint mPaint;
    private Paint mBorderPaint;
    private Path mPath;

    private PolygonShape mPolygonShape;
    private PolygonShapeSpec mPolygonShapeSpec;

    private int canvasWidth, canvasHeight;

    public PolygonImageView(Context context) {
        this(context, null);
    }

    public PolygonImageView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.polygonImageViewStyle);
    }

    public PolygonImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mPolygonShapeSpec = new PolygonShapeSpec();

        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.PolygonImageView, defStyle, 0);

        try {
            mPolygonShapeSpec.setRotation(attributes.getFloat(R.styleable.PolygonImageView_poly_rotation_angle, 0f));
            mPolygonShapeSpec.setNumVertex(attributes.getInteger(R.styleable.PolygonImageView_poly_vertices, 6));
            mPolygonShapeSpec.setCornerRadius(attributes.getFloat(R.styleable.PolygonImageView_poly_corner_radius, 0f));
            mPolygonShapeSpec.setHasShadow(attributes.getBoolean(R.styleable.PolygonImageView_poly_shadow, false));
            mPolygonShapeSpec.setShadowColor(attributes.getColor(R.styleable.PolygonImageView_poly_shadow_color, Color.BLACK));
            mPolygonShapeSpec.setHasBorder(attributes.getBoolean(R.styleable.PolygonImageView_poly_border, false));
            mPolygonShapeSpec.setBorderColor(attributes.getColor(R.styleable.PolygonImageView_poly_border_color, Color.WHITE));
            mPolygonShapeSpec.setBorderWidth(attributes.getDimension(R.styleable.PolygonImageView_poly_border_width, 4));

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
        mPaint.setPathEffect(new CornerPathEffect(mPolygonShapeSpec.getCornerRadius()));
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setPathEffect(new CornerPathEffect(mPolygonShapeSpec.getCornerRadius()));

        if(mPolygonShapeSpec.hasBorder()) {
            mBorderPaint.setColor(mPolygonShapeSpec.getBorderColor());
            mBorderPaint.setStrokeWidth(mPolygonShapeSpec.getBorderWidth());
        }

        if (mPolygonShapeSpec.hasShadow()) {
            //Shadow on border even if isBordered is false. Better effect and performance that
            //using shadow on main paint
            mBorderPaint.setShadowLayer(mPolygonShapeSpec.getShadowRadius(), mPolygonShapeSpec.getShadowXOffset(),
                    mPolygonShapeSpec.getShadowYOffset(), mPolygonShapeSpec.getShadowColor());
        }

        //Avoid known shadow problems
        if (Build.VERSION.SDK_INT > 13)
            setLayerType(LAYER_TYPE_SOFTWARE, null);

        mPolygonShape = new RegularPolygonShape();
    }

    /**
     * Gets incoming new canvas size and updates polygon form and image if needed.
     *
     * @param w    new Width
     * @param h    new Height
     * @param oldW old Width
     * @param oldH old Height
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        canvasWidth = w;
        canvasHeight = h;
        updatePolygonSize();

        if (Math.min(canvasWidth, canvasHeight) != Math.min(oldW, oldH)) {
            refreshImage();
        }
    }

    /**
     * Force Override to solve bug on Lollipop
     *
     * @param widthMeasureSpec  Width Spec Measure
     * @param heightMeasureSpec Height Spec Measure
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureWidth(int measureSpecWidth) {
        return measure(measureSpecWidth);
    }

    private int measureHeight(int measureSpecHeight) {
        //Force do not square measure to solve bug (use base 2 better performance)
        return (measure(measureSpecHeight) + 2);
    }

    private int measure(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY || specMode == MeasureSpec.AT_MOST) {
            result = specSize;
        } else {
            result = Math.min(canvasWidth, canvasHeight);
        }

        return result;
    }

    /**
     * Draw the polygon form.
     *
     * @param canvas main canvas
     */
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if (getDrawable() == null || getDrawable().getIntrinsicWidth() == 0 ||
                getDrawable().getIntrinsicHeight() == 0)
            return;

        switch (mPolygonShapeSpec.getNumVertex()) {
            case 0: //CIRCLE
                if (mPolygonShapeSpec.hasShadow() || mPolygonShapeSpec.hasBorder()) {
                    canvas.drawCircle(mPolygonShapeSpec.getCenterX(), mPolygonShapeSpec.getCenterY(),
                            mPolygonShapeSpec.getDiameter() / 2, mBorderPaint);
                }
                canvas.drawCircle(mPolygonShapeSpec.getCenterX(), mPolygonShapeSpec.getCenterY(),
                        mPolygonShapeSpec.getDiameter() / 2, mPaint);
                break;
            case 1: //REGULAR IMAGE VIEW
                super.onDraw(canvas);
                break;
            case 2: //SQUARE
                if (mPolygonShapeSpec.hasShadow() || mPolygonShapeSpec.hasBorder()) {
                    canvas.drawRect(mPolygonShapeSpec.getCenterX() - mPolygonShapeSpec.getDiameter() / 2,
                        mPolygonShapeSpec.getCenterY() - mPolygonShapeSpec.getDiameter() / 2,
                        mPolygonShapeSpec.getCenterX() + mPolygonShapeSpec.getDiameter() / 2,
                        mPolygonShapeSpec.getCenterY() + mPolygonShapeSpec.getDiameter() / 2,
                        mBorderPaint);
                }
                canvas.drawRect(mPolygonShapeSpec.getCenterX() - mPolygonShapeSpec.getDiameter() / 2,
                    mPolygonShapeSpec.getCenterY() - mPolygonShapeSpec.getDiameter() / 2,
                    mPolygonShapeSpec.getCenterX() + mPolygonShapeSpec.getDiameter() / 2,
                    mPolygonShapeSpec.getCenterY() + mPolygonShapeSpec.getDiameter() / 2,
                    mPaint);
                break;
            default: //POLYGON
                if (mPolygonShapeSpec.hasShadow() || mPolygonShapeSpec.hasBorder())
                    canvas.drawPath(mPath, mBorderPaint);
                canvas.drawPath(mPath, mPaint);
        }
    }

    /**
     * Take cares about padding changes.
     *
     * @param start  start
     * @param top    top
     * @param end    end
     * @param bottom bottom
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
     * @param left   left
     * @param top    top
     * @param right  right
     * @param bottom bottom
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
    public void setImageResource(@DrawableRes int resId) {
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

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
        invalidate();
    }

    public void setColorFilterWithBorder(ColorFilter cf) {
        mPaint.setColorFilter(cf);
        mBorderPaint.setColorFilter(cf);
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
        //recalculate new center
        float borderNeeded = mPolygonShapeSpec.hasBorder() ? mPolygonShapeSpec.getBorderWidth() : 0;
        float shadowNeeded = mPolygonShapeSpec.hasShadow() ? mPolygonShapeSpec.getShadowRadius() : 0;
        mPolygonShapeSpec.setCenterX(mPolygonShapeSpec.getDiameter() / 2 + (float) (getPaddingLeft() +
                getPaddingRight()) / 2 + borderNeeded + shadowNeeded);
        mPolygonShapeSpec.setCenterY(mPolygonShapeSpec.getDiameter() / 2 + (float) (getPaddingTop() +
                getPaddingBottom()) / 2 + borderNeeded + shadowNeeded);

        if (mPolygonShapeSpec.getNumVertex() < 3)
            return;

        mPath = mPolygonShape.getPolygonPath(mPolygonShapeSpec);
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
        if(mPolygonShapeSpec == null)
            return;

        float borderPadding = mPolygonShapeSpec.hasBorder() ? mPolygonShapeSpec.getBorderWidth() : 0f;
        float shadowPadding = mPolygonShapeSpec.hasShadow() ? mPolygonShapeSpec.getShadowRadius() : 0f;
        float xPadding = (l + r + (borderPadding * 2) + (shadowPadding * 2));
        float yPadding = (t + b + (borderPadding * 2) + (shadowPadding * 2));
        float diameter = Math.min((float) canvasWidth - xPadding, (float) canvasHeight - yPadding);
        //if the size is changed we need to rebuild the polygon
        if (diameter != mPolygonShapeSpec.getDiameter()) {
            mPolygonShapeSpec.setDiameter(diameter);
            rebuildPolygon();
        }
    }

    /**
     *
     * @param polygonShape set new shape
     */
    public void setPolygonShape(PolygonShape polygonShape) {
        mPolygonShape = polygonShape;
        rebuildPolygon();
        invalidate();
    }

    /**
     *
     * @return get current shape
     */
    public PolygonShape getPolygonShape() {
        return mPolygonShape;
    }

    /**
     *
     * @return get current shape spec
     */
    public PolygonShapeSpec getPolygonShapeSpec() {
        return mPolygonShapeSpec;
    }

    /**
     *
     * @param spec set shape spec
     */
    public void setPolygonShapeSpec(PolygonShapeSpec spec) {
        this.mPolygonShapeSpec = spec;
    }

    /**
     * Returns the rotate angle.
     *
     * @return angle in degrees.
     */
    public float getRotationAngle() {
        return mPolygonShapeSpec.getRotation();
    }

    /**
     * Set new rotate angle and updates polygon form.
     *
     * @param mAngle angle in degrees.
     */
    public void setRotationAngle(float mAngle) {
        mPolygonShapeSpec.setRotation(mAngle);
        rebuildPolygon();
        invalidate();
    }

    /**
     * Returns the vertex number.
     *
     * @return vertex number
     */
    public int getVertices() {
        return mPolygonShapeSpec.getNumVertex();
    }

    /**
     * Sets the new vertex number and updates polygon form.
     *
     * @param numVertices new number of vertices
     */
    public void setVertices(int numVertices) {
        mPolygonShapeSpec.setNumVertex(numVertices);
        rebuildPolygon();
        invalidate();
    }

    /**
     * Indicates if it's bordered.
     *
     * @return boolean
     */
    public boolean isBordered() {
        return mPolygonShapeSpec.hasBorder();
    }

    /**
     * Enables or disables the border option.
     *
     * @param bordered if it's bordered
     */
    public void setBorder(boolean bordered) {
        mPolygonShapeSpec.setHasBorder(bordered);
        updateBorderSpecs();
    }

    /**
     * Sets new border width.
     *
     * @param borderWidth new width.
     */
    public void setBorderWidth(float borderWidth) {
        mPolygonShapeSpec.setBorderWidth(borderWidth * (getResources().getDisplayMetrics().density));
        updateBorderSpecs();
    }

    /**
     * Sets new border width and update polygon size.
     */
    private void updateBorderSpecs() {
        if(mPolygonShapeSpec.hasBorder()) {
            mBorderPaint.setStrokeWidth(mPolygonShapeSpec.getBorderWidth());
            mBorderPaint.setColor(mPolygonShapeSpec.getBorderColor());
        } else {
            mBorderPaint.setStrokeWidth(0);
            mBorderPaint.setColor(0);
        }
        updatePolygonSize();
        invalidate();
    }

    /**
     * Sets new border color.
     *
     * @param borderColor Color class var.
     */
    public void setBorderColor(int borderColor) {
        mPolygonShapeSpec.setBorderColor(borderColor);
        mBorderPaint.setColor(borderColor);
        invalidate();
    }

    /**
     * Sets new border color
     *
     * @param resourceBorderColor Resource xml color.
     */
    public void setBorderColorResource(@ColorRes int resourceBorderColor) {
        setBorderColor(getResources().getColor(resourceBorderColor));
    }

    public void addBorder(float borderWidth, int borderColor) {
        mPolygonShapeSpec.setHasBorder(true);
        mPolygonShapeSpec.setBorderWidth(borderWidth * (getResources().getDisplayMetrics().density));
        mPolygonShapeSpec.setBorderColor(borderColor);
        updateBorderSpecs();
    }

    public void addBorderResource(float borderWidth, @ColorRes int resourceBorderColor) {
        addBorder(borderWidth, getResources().getColor(resourceBorderColor));
    }

    /**
     * Sets new radius for corners and updates view.
     *
     * @param cornerRadius new corner radius
     */
    public void setCornerRadius(float cornerRadius) {
        mPolygonShapeSpec.setCornerRadius(cornerRadius);
        mBorderPaint.setPathEffect(new CornerPathEffect(cornerRadius));
        mPaint.setPathEffect(new CornerPathEffect(cornerRadius));
        invalidate();
    }

    /**
     * Adds a default shadow
     */
    public void addShadow() {
        startShadow();
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
        mPolygonShapeSpec.setShadowRadius(radius);
        mPolygonShapeSpec.setShadowXOffset(offsetX);
        mPolygonShapeSpec.setShadowYOffset(offsetY);
        mPolygonShapeSpec.setShadowColor(color);
        startShadow();
    }

    public void addShadowResource(float radius, float offsetX, float offsetY, @ColorRes int color) {
        addShadow(radius, offsetX, offsetY, getResources().getColor(color));
    }

    private void startShadow() {
        mPolygonShapeSpec.setHasShadow(true);
        mBorderPaint.setShadowLayer(mPolygonShapeSpec.getShadowRadius(), mPolygonShapeSpec.getShadowXOffset(),
                mPolygonShapeSpec.getShadowYOffset(), mPolygonShapeSpec.getShadowColor());
        updatePolygonSize();
        invalidate();
    }

    /**
     * Removes shadow
     */
    public void clearShadow() {
        if (!mPolygonShapeSpec.hasShadow())
            return;

        mPolygonShapeSpec.setHasShadow(false);
        mBorderPaint.clearShadowLayer();
        updatePolygonSize();
        invalidate();
    }

    /**
     * Transforms a drawable into a bitmap.
     *
     * @param drawable incoming drawable
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
            Log.e("PolygonImageView", "OutOfMemory during bitmap creation");
            return null;
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
