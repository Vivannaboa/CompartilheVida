package br.com.compartilhevida.compartilhevida.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import br.com.compartilhevida.compartilhevida.R;
import br.com.compartilhevida.compartilhevida.models.Doacao;
import br.com.compartilhevida.compartilhevida.viewholder.DoacaoViewHolder;


public class DoacaoFragment extends Fragment {
    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<Doacao, DoacaoViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public DoacaoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_doacao, container, false);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecycler = (RecyclerView) rootView.findViewById(R.id.doaco_list);
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
        Query postsQuery = mDatabase.child("user-doacoes").child(getUid()).orderByChild("dataDoacao");;
        mAdapter = new FirebaseRecyclerAdapter<Doacao, DoacaoViewHolder>(Doacao.class, R.layout.item_doacao,
                DoacaoViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final DoacaoViewHolder viewHolder, final Doacao model, final int position) {
                viewHolder.bindToPost(model);
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    public String getUid() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            return null;
        }else {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
    }

}
