package br.com.compartilhevida.compartilhevida;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.compartilhevida.compartilhevida.models.Usuario;
import br.com.compartilhevida.compartilhevida.fragment.ConfigFragment;
import br.com.compartilhevida.compartilhevida.fragment.ContaFragment;
import br.com.compartilhevida.compartilhevida.fragment.PostFragment;
import br.com.compartilhevida.compartilhevida.fragment.dummy.DummyContent;
import br.com.compartilhevida.compartilhevida.util.CircleTransform;

public class MainActivity extends BaseActivity
        implements
        NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener,
        ContaFragment.OnFragmentInteractionListener,
        PostFragment.OnListFragmentInteractionListener
{

    // Firebase instance variables
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth mAuth;
    private Usuario mUsuario;
    FirebaseUser userFirebase;
    private DatabaseReference mUserDatabase;
    ValueEventListener mUserEventListener;
    ImageView imageView;
    TextView usuario;
    TextView email;
    private Fragment currentFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUsuario = Usuario.getInstance(getBaseContext());

        //get firebase auth instance
        mAuth = FirebaseAuth.getInstance();
        //get current user
        userFirebase = FirebaseAuth.getInstance().getCurrentUser();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser userFirebase = firebaseAuth.getCurrentUser();
                if (userFirebase == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        //pega o usuário do banco
        if (userFirebase != null) {
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userFirebase.getUid());
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        currentFragment = getFragmentManager().findFragmentById(R.id.content);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NewPostActivity.class));
//                PostDialogFragment dialog = PostDialogFragment.newInstance();
//                dialog.show(getSupportFragmentManager(), "LicensesDialog");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);
        imageView = (ImageView) hView.findViewById(R.id.imageViewUsuario);
        usuario = (TextView) hView.findViewById(R.id.textViewUsuario);
        email = (TextView) hView.findViewById(R.id.textViewEmail);

    }

    private boolean verificaDadosPendendtes() {
        boolean ret = true;
        if (mUsuario.getBirthday() == null) {

            ret = false;
        }
        if (mUsuario.getGender() == null) {

            ret = false;
        }
        if (mUsuario.getTipo_sanguineo() == null) {

            ret = false;
        }
        return ret;
    }



    @Override
    protected void onResume() {
        super.onResume();
       hideProgressDialog();

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mUserDatabase!=null) {
            mUserEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Usuario usuario = snapshot.getValue(Usuario.class);

                    if (usuario != null) {
                        mUsuario.setGender(usuario.getGender());
                        mUsuario.setFirst_name(usuario.getFirst_name());
                        mUsuario.setLast_name(usuario.getLast_name());
                        mUsuario.setUid(usuario.getUid());
                        mUsuario.setEmail(usuario.getEmail());
                        mUsuario.setBirthday(usuario.getBirthday());
                        mUsuario.setCidade(usuario.getCidade());
                        mUsuario.setTipo_sanguineo(usuario.getTipo_sanguineo());
                        mUsuario.setProvider(usuario.getProvider());
                        carregaUsuario(usuario);
                        if (!verificaDadosPendendtes()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("Olá " + usuario.getFirst_name() + ", gostariamos de lhe conhecer melhor! " + "\n" + " Deseja concluir seu cadastro agora?")
                                    .setTitle("Competar cadastro");
                            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                                    startActivity(intent);
                                }
                            });
                            builder.setNegativeButton("Mais tarde", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();

                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            };
            mUserDatabase.addValueEventListener(mUserEventListener);
        }
        mAuth.addAuthStateListener(authListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            mAuth.removeAuthStateListener(authListener);
        }
        if (mUserEventListener!= null){
            mUserDatabase.removeEventListener(mUserEventListener);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerView,PostFragment.newInstance(1),"Post")
                    .commit();

        } else if (id == R.id.nav_gallery) {
            //startActivity(new Intent(MainActivity.this, MapsActivity.class));
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_config) {

            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerView, new ConfigFragment(),"Config")
                    .commit();
        } else if (id == R.id.nav_conta) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerView, ContaFragment.newInstance(mUserDatabase),"Conta")
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private boolean isNameOk(Usuario mUsuario, FirebaseUser firebaseUser) {
        return (
                mUsuario.getFirst_name() != null
                        || firebaseUser.getDisplayName() != null
        );
    }

    private void carregaUsuario(Usuario user) {
        if (userFirebase != null) {
            if (userFirebase.getPhotoUrl() != null) {
                final Uri uri = userFirebase.getPhotoUrl();
                Glide.with(getBaseContext()).load(uri).transform(new CircleTransform(this)).into(imageView);
            }
            usuario.setText(user.getFirst_name());
            email.setText(user.getEmail());
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }
}
