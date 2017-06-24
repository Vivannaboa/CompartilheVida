package br.com.compartilhevida.compartilhevida.viewholder;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;

import br.com.compartilhevida.compartilhevida.R;
import br.com.compartilhevida.compartilhevida.adapter.CommentAdapter;
import br.com.compartilhevida.compartilhevida.models.Post;
import br.com.compartilhevida.compartilhevida.util.CircleTransform;

public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView titleView;
    public TextView authorView;
    public ImageView starView;
    public TextView numStarsView;
    public TextView bodyView;
    public TextView postNumCmpartilhar;
    public ImageView imageView;
    public ImageView compartilharView;
    public ImageView comentarView;
    private Context context;
    private LinearLayout mLinearLayout;
    public RecyclerView mCommentsRecycler;
    public CommentAdapter commentAdapter;
    View layout_post, layout_pedido;
    TextView txtHemocentro,txtDataLimite,txtFavorecido,txtTipoSanguineo,txtMensagem;


    public PostViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        titleView = (TextView) itemView.findViewById(R.id.post_title);
        authorView = (TextView) itemView.findViewById(R.id.post_author);
        starView = (ImageView) itemView.findViewById(R.id.star);
        compartilharView = (ImageView) itemView.findViewById(R.id.compartilhar);
        numStarsView = (TextView) itemView.findViewById(R.id.post_num_stars);
        bodyView = (TextView) itemView.findViewById(R.id.post_body);
        imageView = (ImageView)itemView.findViewById(R.id.post_author_photo);
        comentarView = (ImageView)itemView.findViewById(R.id.comentar);
        postNumCmpartilhar = (TextView) itemView.findViewById(R.id.post_num_compartilhar);
        mCommentsRecycler =(RecyclerView) itemView.findViewById(R.id.recycler_comments_card);
        mCommentsRecycler.setLayoutManager(new LinearLayoutManager(context));
        itemView.findViewById(R.id.imageViewExpand).setOnClickListener(this);
        mLinearLayout = (LinearLayout) itemView.findViewById(R.id.comentarios);
        layout_pedido = (View) itemView.findViewById(R.id.layout_pedido);
        layout_post   = (View) itemView.findViewById(R.id.layout_post);
        txtHemocentro =(TextView) itemView.findViewById(R.id.txt_hemocentro);
        txtDataLimite = (TextView) itemView.findViewById(R.id.txt_data_limite);
        txtFavorecido = (TextView) itemView.findViewById(R.id.txt_favorecido);
        txtTipoSanguineo=(TextView) itemView.findViewById(R.id.txt_tipo_sanguineo);
        txtMensagem=(TextView) itemView.findViewById(R.id.txt_mensagem);
        mLinearLayout.setVisibility(View.GONE);

    }

    public void bindToPost(DatabaseReference mCommentsReference, Post post, View.OnClickListener starClickListener, View.OnClickListener sharedClickListner, View.OnClickListener comentarClickListner ) {
        authorView.setText(post.getAutor());
        numStarsView.setText(String.valueOf(post.getCoracaoCount()));
        postNumCmpartilhar.setText(String.valueOf(post.getComentariosCont()));
        if (!post.getUrlFoto().toString().isEmpty()) {
            Glide.with(context).load(post.getUrlFoto()).transform(new CircleTransform(context)).into(imageView);
        }else{
            Glide.with(context).load(R.drawable.ic_action_account_circle_40).transform(new CircleTransform(context)).into(imageView);
        }
        starView.setOnClickListener(starClickListener);
        commentAdapter = new CommentAdapter(context, mCommentsReference);
        mCommentsRecycler.setAdapter(commentAdapter);
        compartilharView.setOnClickListener(sharedClickListner);
        comentarView.setOnClickListener(comentarClickListner);
        if (post.getTipo() != null && post.getTipo().equalsIgnoreCase("pedido")){
            layout_pedido.setVisibility(View.VISIBLE);
            layout_post.setVisibility(View.GONE);
            txtHemocentro.setText(post.getHemocentro());
            txtDataLimite.setText(post.getData_limite_doacao());
            txtFavorecido.setText(post.getFavorecido());
            txtTipoSanguineo.setText(post.getTipo_sanguineo());
            txtMensagem.setText(post.getMensagem());
        }else{
            layout_post.setVisibility(View.VISIBLE);
            layout_pedido.setVisibility(View.GONE);
            titleView.setText(post.getTitulo());
            bodyView.setText(post.getMensagem());
        }
    }

    @Override
    public void onClick(View v) {
        if (mLinearLayout.getVisibility() == View.GONE) {
            mLinearLayout.setVisibility(View.VISIBLE);
        }else {
            mLinearLayout.setVisibility(View.GONE);
        }
    }


}
