package net.grobas.view;

/**
 * Created by Albert on 05/05/2015.
 */
public class PaperPolygonShape extends BasePolygonShape {

    private int brushOffsetX, brushOffsetY;

    public PaperPolygonShape(int brushOffsetX, int brushOffsetY) {
        super();
        this.brushOffsetX = brushOffsetX;
        this.brushOffsetY = brushOffsetY;
    }

    @Override
    void addEffect(float pointX, float pointY) {
        getPath().quadTo(pointX + brushOffsetX, pointY + brushOffsetY, pointX, pointY);
    }

    public int getBrushOffsetX() {
        return brushOffsetX;
    }

    public void setBrushOffsetX(int brushOffsetX) {
        this.brushOffsetX = brushOffsetX;
    }

    public int getBrushOffsetY() {
        return brushOffsetY;
    }

    public void setBrushOffsetY(int brushOffsetY) {
        this.brushOffsetY = brushOffsetY;
    }

    public void updateOffsets(int brushOffsetX, int brushOffsetY) {
        setBrushOffsetX(brushOffsetX);
        setBrushOffsetY(brushOffsetY);
    }
}
