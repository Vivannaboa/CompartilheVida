package br.com.compartilhevida.compartilhevida.viewholder;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.com.compartilhevida.compartilhevida.DetailsActivity;
import br.com.compartilhevida.compartilhevida.R;
import br.com.compartilhevida.compartilhevida.models.Cartilha;

public class CartilhaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtTitulo;
    public TextView txtTexto;
    private Context context;
    public ImageView picture;
    private final Drawable[] mPlacePictures;
    String texto;


    public CartilhaViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        Resources resources = context.getResources();
//        txtTitulo = (TextView) itemView.findViewById(R.id.card_title);
//        txtTexto = (TextView) itemView.findViewById(R.id.card_text);
//        picture = (ImageView) itemView.findViewById(R.id.card_image);
        TypedArray a = resources.obtainTypedArray(R.array.places_picture);
        picture = (ImageView) itemView.findViewById(R.id.tile_picture);
        txtTitulo = (TextView) itemView.findViewById(R.id.tile_title);
        mPlacePictures = new Drawable[a.length()];
        for (int i = 0; i < mPlacePictures.length; i++) {
            mPlacePictures[i] = a.getDrawable(i);
        }
        itemView.setOnClickListener(this);
    }



    public void bindToPost(Cartilha cartilha, int position) {
        txtTitulo.setText(cartilha.getTitulo());
        texto = cartilha.getTexto().trim().replace("\\n", "\n");
        picture.setImageDrawable(mPlacePictures[position % mPlacePictures.length]);


    }

    @Override
    public void onClick(View v) {
        Context context = v.getContext();
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra(DetailsActivity.EXTRA_TITULO, txtTitulo.getText());
        intent.putExtra(DetailsActivity.EXTRA_TEXTO, texto);
        intent.putExtra(DetailsActivity.EXTRA_IMAGEM, getAdapterPosition());
        context.startActivity(intent);
    }

}
