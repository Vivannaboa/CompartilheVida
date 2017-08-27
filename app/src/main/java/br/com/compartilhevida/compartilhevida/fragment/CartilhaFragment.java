package br.com.compartilhevida.compartilhevida.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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
import br.com.compartilhevida.compartilhevida.viewholder.CartilhaViewHolder;


public class CartilhaFragment extends Fragment {

    // define_database_reference
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Cartilha, CartilhaViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public CartilhaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        View rootView = inflater.inflate(R.layout.fragment_cartilha, container, false);

        //pega a referêsncia do banco
        mDatabase = FirebaseDatabase.getInstance().getReference();
//        mRecycler = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
//        mRecycler.setHasFixedSize(true);
//        return rootView;

        mRecycler = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
//        ContentAdapter adapter = new ContentAdapter(recyclerView.getContext());
//        recyclerView.setAdapter(adapter);
        mRecycler.setHasFixedSize(true);
        int tilePadding = getResources().getDimensionPixelSize(R.dimen.tile_padding);
        mRecycler.setPadding(tilePadding, tilePadding, tilePadding, tilePadding);
        mRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
//        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        return mRecycler;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Configurar Layout Manager, layout reverso
//        mManager = new LinearLayoutManager(getActivity());
//        mManager.setStackFromEnd(true);
//        mRecycler.setLayoutManager(mManager);

        // Configure o FirebaseRecyclerAdapter com a Consulta
        Query query = mDatabase.child("cartilha");
        mAdapter = new FirebaseRecyclerAdapter<Cartilha, CartilhaViewHolder>(Cartilha.class, R.layout.item_tile,
                CartilhaViewHolder.class, query) {
            @Override
            protected void populateViewHolder(final CartilhaViewHolder viewHolder, final Cartilha model, final int position) {
                viewHolder.bindToPost(model,position);
            }
        };
        mRecycler.setAdapter(mAdapter);
    }


}
