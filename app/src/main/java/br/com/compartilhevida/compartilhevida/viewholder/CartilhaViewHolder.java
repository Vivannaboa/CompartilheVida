package br.com.compartilhevida.compartilhevida.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.com.compartilhevida.compartilhevida.R;
import br.com.compartilhevida.compartilhevida.models.Cartilha;

public class CartilhaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtTitulo;
    public TextView txtTexto;
    private Context context;
    public LinearLayout layoutBody;
    private ImageView imageViewExpand;

    private static final int DURATION = 400;

    public CartilhaViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        txtTitulo = (TextView) itemView.findViewById(R.id.txt_titulo);
        txtTexto = (TextView) itemView.findViewById(R.id.txt_texto);
        layoutBody =(LinearLayout)itemView.findViewById(R.id.layout_body);
        itemView.findViewById(R.id.layout_titulo).setOnClickListener(this);
        imageViewExpand = (ImageView) itemView.findViewById(R.id.imageViewExpand);
    }

    public void bindToPost(Cartilha cartilha) {
        txtTitulo.setText(cartilha.getTitulo());
        txtTexto.setText(cartilha.getTexto().trim().replace("\\n", "\n"));
    }

    @Override
    public void onClick(View v) {
        if (layoutBody.getVisibility() == View.GONE) {
            layoutBody.setVisibility(View.VISIBLE);
            imageViewExpand.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
            rotate(-180.0f);
        }else {
            layoutBody.setVisibility(View.GONE);
            imageViewExpand.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
            rotate(180.0f);
        }

    }

    private void rotate(float angle) {
        Animation animation = new RotateAnimation(0.0f, angle, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setFillAfter(true);
        animation.setDuration(DURATION);
        imageViewExpand.startAnimation(animation);
    }
}
