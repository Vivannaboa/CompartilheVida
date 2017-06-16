package br.com.compartilhevida.compartilhevida.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import br.com.compartilhevida.compartilhevida.R;
import br.com.compartilhevida.compartilhevida.models.Cartilha;
import br.com.compartilhevida.compartilhevida.models.Doacao;
import br.com.compartilhevida.compartilhevida.viewholder.CartilhaViewHolder;
import br.com.compartilhevida.compartilhevida.viewholder.DoacaoViewHolder;


public class CartilhaFragment extends Fragment {

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<Cartilha, CartilhaViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    private int mExpandedPosition =-1;
    public CartilhaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_cartilha, container, false);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecycler = (RecyclerView) rootView.findViewById(R.id.recycler_cartilha);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query query = mDatabase.child("cartilha");
        mAdapter = new FirebaseRecyclerAdapter<Cartilha, CartilhaViewHolder>(Cartilha.class, R.layout.item_cartilha,
                CartilhaViewHolder.class, query) {
            @Override
            protected void populateViewHolder(final CartilhaViewHolder viewHolder, final Cartilha model, final int position) {
                viewHolder.bindToPost(model);
            }
        };
        mRecycler.setAdapter(mAdapter);
    }


}
