package net.grobas.polygonimageview.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import net.grobas.shapes.PaperPolygonShape;
import net.grobas.shapes.StarPolygonShape;
import net.grobas.view.PolygonImageView;

public class SampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        PolygonImageView kitty = (PolygonImageView) findViewById(R.id.kitty01);
        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);

        PolygonImageView view = new PolygonImageView(this);
        view.setImageResource(R.drawable.cat07);

        view.addShadow(15f, 0f, 7.5f, Color.YELLOW);
        view.addBorder(15, Color.WHITE);
        view.setCornerRadius(15);
        view.setVertices(16);

        view.setPolygonShape(new StarPolygonShape(0.8f, false));
        layout.addView(view, kitty.getLayoutParams());

        PolygonImageView view2 = new PolygonImageView(this);
        view2.setImageResource(R.drawable.cat01);
        view2.addShadow(7f, 0f, 0f, Color.BLACK);
        view2.addBorder(10, Color.WHITE);
        view2.setCornerRadius(10);
        view2.setVertices(23);

        view2.setPolygonShape(new StarPolygonShape(0.8f, true));
        layout.addView(view2, kitty.getLayoutParams());

        PolygonImageView view3 = new PolygonImageView(this);
        view3.setImageResource(R.drawable.cat04);
        view3.addShadowResource(10f, 0f, 7.5f, R.color.shadow);
        view3.addBorderResource(5, R.color.border);
        view3.setCornerRadius(2);
        view3.setVertices(5);

        view3.setPolygonShape(new PaperPolygonShape(-15, 25));
        layout.addView(view3, kitty.getLayoutParams());



    }

}
