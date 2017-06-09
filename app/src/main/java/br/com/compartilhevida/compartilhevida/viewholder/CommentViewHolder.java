package br.com.compartilhevida.compartilhevida.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.compartilhevida.compartilhevida.R;

/**
 * Created by vivan on 09/06/2017.
 */

public class CommentViewHolder extends RecyclerView.ViewHolder {

    public TextView authorView;
    public TextView bodyView;
    public ImageView pothoAutor;

    public CommentViewHolder(View itemView) {
        super(itemView);
        pothoAutor = (ImageView) itemView.findViewById(R.id.comment_photo);
        authorView = (TextView) itemView.findViewById(R.id.comment_author);
        bodyView = (TextView) itemView.findViewById(R.id.comment_body);
    }
}