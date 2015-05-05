package net.grobas.polygonimageview.sample;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import net.grobas.view.PaperPolygonShape;
import net.grobas.view.PolygonImageView;
import net.grobas.view.RegularPolygonShape;

public class SampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        PolygonImageView kitty = (PolygonImageView) findViewById(R.id.kitty01);
        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);

        PolygonImageView view = new PolygonImageView(this);
        view.setImageResource(R.drawable.cat07);

        view.addShadow(7.5f, 0f, 7.5f, Color.RED);
        view.setBorder(true);
        view.setBorderWidth(15);
        view.setCornerRadius(5);
        view.setBorderColor(Color.RED);
        view.setVertices(6);


        view.setPolygonShape(new PaperPolygonShape(-20, +15));

        layout.addView(view, kitty.getLayoutParams());
    }

}
