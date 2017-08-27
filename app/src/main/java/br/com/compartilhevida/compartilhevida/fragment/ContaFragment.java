package br.com.compartilhevida.compartilhevida.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import br.com.compartilhevida.compartilhevida.LoginActivity;
import br.com.compartilhevida.compartilhevida.R;
import br.com.compartilhevida.compartilhevida.models.Usuario;
import br.com.compartilhevida.compartilhevida.util.CircleTransform;


public class ContaFragment extends Fragment implements View.OnClickListener {
    private Button btnSendResetEmail, btnRemoveUser,
            signOut;

    private EditText email;
    private ProgressBar progressBar;
    private FirebaseUser userFirebase;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private ImageView photoPerfil;
    private FloatingActionButton floatingActionButtonCamera;

    private OnFragmentInteractionListener mListener;

    public ContaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewConta = inflater.inflate(R.layout.fragment_conta, container, false);
        //recupera componentes
        userFirebase = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        btnSendResetEmail = (Button) viewConta.findViewById(R.id.sending_pass_reset_button);
        btnRemoveUser = (Button) viewConta.findViewById(R.id.remove_user_button);
        signOut = (Button) viewConta.findViewById(R.id.sign_out);
        email = (EditText) viewConta.findViewById(R.id.email);
        progressBar = (ProgressBar) viewConta.findViewById(R.id.progressBar);
        photoPerfil = (ImageView) viewConta.findViewById(R.id.photo_perfil);
        floatingActionButtonCamera = (FloatingActionButton) viewConta.findViewById(R.id.floatingButtonCamera);

        //instacia do usuário no firebase
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userFirebase.getUid());

        //visibilidade dos componentes
        email.setVisibility(View.GONE);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        if (Usuario.getInstance().getProvider() != null) {
            if (!Usuario.getInstance().getProvider().equalsIgnoreCase("email")) {
                btnSendResetEmail.setVisibility(View.GONE);
            }
        } else {
            btnSendResetEmail.setVisibility(View.GONE);
        }
        //carrega a foto do usuário
        if (userFirebase.getPhotoUrl() != null) {
            final Uri uri = userFirebase.getPhotoUrl();
            Glide.with(getActivity()).load(uri).transform(new CircleTransform(getActivity())).into(photoPerfil);
        }
        //listner for button
        btnSendResetEmail.setOnClickListener(this);
        btnRemoveUser.setOnClickListener(this);
        signOut.setOnClickListener(this);
        floatingActionButtonCamera.setOnClickListener(this);
        return viewConta;
    }

    //sign out method
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.floatingButtonCamera:
                Toast.makeText(getActivity(), "Abrir a camera", Toast.LENGTH_SHORT).show();
                break;
            case R.id.sending_pass_reset_button:
                email.setVisibility(View.VISIBLE);
                break;
            case R.id.sign_out:
                signOut();
                break;
            case R.id.remove_user_button:
                progressBar.setVisibility(View.VISIBLE);
                mUserDatabase.removeValue();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Seu perfil foi excluído!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getActivity(), LoginActivity.class));
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(getActivity(), "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                }
                break;
            default:
                break;
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
