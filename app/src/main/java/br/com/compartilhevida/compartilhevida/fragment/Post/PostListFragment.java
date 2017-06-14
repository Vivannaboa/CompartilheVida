package br.com.compartilhevida.compartilhevida.fragment.Post;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;

import br.com.compartilhevida.compartilhevida.PostDetailActivity;
import br.com.compartilhevida.compartilhevida.R;
import br.com.compartilhevida.compartilhevida.adapter.CommentAdapter;
import br.com.compartilhevida.compartilhevida.models.Post;
import br.com.compartilhevida.compartilhevida.viewholder.PostViewHolder;


public abstract class PostListFragment extends Fragment {

    private static final String TAG = "PostListFragment";

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<Post, PostViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public PostListFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_posts, container, false);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecycler = (RecyclerView) rootView.findViewById(R.id.messages_list);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(Post.class, R.layout.item_post,
                PostViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final PostViewHolder viewHolder, final Post model, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                final DatabaseReference mCommentsReference =  FirebaseDatabase.getInstance().getReference()
                        .child("post-comments").child(postKey);
                CommentAdapter commentAdapter = new CommentAdapter(getActivity(), mCommentsReference);
                viewHolder.mCommentsRecycler.setAdapter(commentAdapter);

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity

                    }
                });

                // Determine if the current user has liked this post and set UI accordingly
                if (model.getCoracao().containsKey(getUid())) {
                    viewHolder.starView.setImageResource(R.drawable.ic_favorite_red_24dp);
                } else {
                    viewHolder.starView.setImageResource(R.drawable.ic_favorite_border_red_24dp);
                }

                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        // Need to write to both places the post is stored
                        DatabaseReference globalPostRef = mDatabase.child("posts").child(postRef.getKey());
                        DatabaseReference userPostRef = mDatabase.child("user-posts").child(model.getUid()).child(postRef.getKey());

                        // Run two transactions
                        onStarClicked(globalPostRef);
                        onStarClicked(userPostRef);
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        share(getActivity(), viewHolder.bodyView.getText().toString(), viewHolder.titleView.getText().toString(), "Compartilhe Vida");
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                        intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, postKey);
                        startActivity(intent);
                    }
                });
            }
        };
        mRecycler.setAdapter(mAdapter);

    }

    public void share(Context context, String text, String subject, String title) {
        //facebook que é uma porcaria tem que ser assim
//        ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
//                .setContentTitle(title)
//                .setContentDescription(text)
//                .setContentUrl(Uri.parse("https://compartilhevida-6fce1.firebaseapp.com/"))
//                .setImageUrl(Uri.parse("https://firebasestorage.googleapis.com/v0/b/compartilhevida-6fce1.appspot.com/o/ic_launcher.png?alt=media&token=e23f9cf2-b0b2-44d0-af53-5d63751e3b53"))
//                .build();
//
//        ShareDialog.show(this,shareLinkContent);
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        if (!subject.isEmpty()) {
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        }
        if (!text.isEmpty()) {
            intent.putExtra(android.content.Intent.EXTRA_TEXT, text);
        }
        if (!title.isEmpty()) {
            intent.putExtra(Intent.EXTRA_TITLE, title);
        }
        intent.putExtra(Intent.EXTRA_STREAM, "https://firebasestorage.googleapis.com/v0/b/compartilhevida-6fce1.appspot.com/o/ic_launcher.png?alt=media&token=e23f9cf2-b0b2-44d0-af53-5d63751e3b53");
        context.startActivity(Intent.createChooser(intent, "Compartilhar via"));
    }


    // [START post_stars_transaction]
    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.getCoracao().containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    p.setCoracaoCount(p.getCoracaoCount() -1);
                    p.getCoracao().remove(getUid());
                } else {
                    // Star the post and add self to stars
                    p.setCoracaoCount(p.getCoracaoCount() +1);
                    p.getCoracao().put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }
    // [END post_stars_transaction]

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    public String getUid() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            return null;
        }else {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
    }

    public abstract Query getQuery(DatabaseReference databaseReference);


}
