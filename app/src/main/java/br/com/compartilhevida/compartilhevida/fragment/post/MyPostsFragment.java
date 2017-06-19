package br.com.compartilhevida.compartilhevida.fragment.post;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyPostsFragment extends PostListFragment {

    public MyPostsFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // todos os meus Posts
        return databaseReference.child("user-posts").child(getUid());
    }
}
