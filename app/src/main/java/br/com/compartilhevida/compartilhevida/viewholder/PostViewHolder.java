package br.com.compartilhevida.compartilhevida.viewholder;

import android.content.Context;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import br.com.compartilhevida.compartilhevida.adapter.CommentAdapter;
import br.com.compartilhevida.compartilhevida.models.Comentario;
import br.com.compartilhevida.compartilhevida.models.Post;
import br.com.compartilhevida.compartilhevida.R;
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
        mLinearLayout.setVisibility(View.GONE);

    }

    public void bindToPost(Post post, View.OnClickListener starClickListener,View.OnClickListener sharedClickListner,View.OnClickListener comentarClickListner ) {
        authorView.setText(post.getAutor());
        numStarsView.setText(String.valueOf(post.getCoracaoCount()));
        titleView.setText(post.getTitulo());
        bodyView.setText(post.getMensagem());
        postNumCmpartilhar.setText(String.valueOf(post.getComentariosCont()));
        if (!post.getUrlFoto().toString().isEmpty()) {
            Glide.with(context).load(post.getUrlFoto()).transform(new CircleTransform(context)).into(imageView);
        }
        starView.setOnClickListener(starClickListener);
        compartilharView.setOnClickListener(sharedClickListner);
        comentarView.setOnClickListener(comentarClickListner);
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
