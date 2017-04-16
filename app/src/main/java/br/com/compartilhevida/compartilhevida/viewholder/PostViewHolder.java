package br.com.compartilhevida.compartilhevida.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import br.com.compartilhevida.compartilhevida.models.Post;
import br.com.compartilhevida.compartilhevida.R;
import br.com.compartilhevida.compartilhevida.util.CircleTransform;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView authorView;
    public ImageView starView;
    public TextView numStarsView;
    public TextView bodyView;
    public ImageView imageView;
    private Context context;

    public PostViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        titleView = (TextView) itemView.findViewById(R.id.post_title);
        authorView = (TextView) itemView.findViewById(R.id.post_author);
        starView = (ImageView) itemView.findViewById(R.id.star);
        numStarsView = (TextView) itemView.findViewById(R.id.post_num_stars);
        bodyView = (TextView) itemView.findViewById(R.id.post_body);
        imageView = (ImageView)itemView.findViewById(R.id.post_author_photo);
    }

    public void bindToPost(Post post, View.OnClickListener starClickListener) {
        titleView.setText(post.getTitle());
        authorView.setText(post.getAuthor());
        numStarsView.setText(String.valueOf(post.getStarCount()));
        bodyView.setText(post.getBody());
        if (post.getUrlPhoto()!=null) {
            Glide.with(context).load(post.getUrlPhoto()).transform(new CircleTransform(context)).into(imageView);
        }
        starView.setOnClickListener(starClickListener);
    }
}
