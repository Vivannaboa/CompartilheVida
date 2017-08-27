package br.com.compartilhevida.compartilhevida;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {
    public static final String EXTRA_TITULO= "titulo";
    public static final String EXTRA_TEXTO= "texto";
    public static final String EXTRA_IMAGEM = "imagem";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);


        Resources resources = getResources();
        collapsingToolbar.setTitle(getIntent().getStringExtra(EXTRA_TITULO));

        TextView description = (TextView) findViewById(R.id.description);
        description.setText(getIntent().getStringExtra(EXTRA_TITULO));

        TextView placeDetail = (TextView) findViewById(R.id.place_detail);
        placeDetail.setText(getIntent().getStringExtra(EXTRA_TEXTO));

        TypedArray placePictures = resources.obtainTypedArray(R.array.places_picture);
        ImageView placePicutre = (ImageView) findViewById(R.id.image);
        placePicutre.setImageDrawable(placePictures.getDrawable(getIntent().getIntExtra(EXTRA_IMAGEM,0) % placePictures.length()));

        placePictures.recycle();
    }
}
