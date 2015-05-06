package net.grobas.shapes;

/**
 * Paper Effect
 */
public class PaperPolygonShape extends BasePolygonShape {
    //gradient offset
    private int brushOffsetX, brushOffsetY;

    public PaperPolygonShape(int brushOffsetX, int brushOffsetY) {
        super();
        this.brushOffsetX = brushOffsetX;
        this.brushOffsetY = brushOffsetY;
    }

    @Override
    protected void addEffect(float currentX, float currentY, float nextX, float nextY) {
        //could be better...
        getPath().quadTo(nextX + brushOffsetX, nextY + brushOffsetY, nextX, nextY);
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
