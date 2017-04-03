package br.com.compartilhevida.compartilhevida;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Fragment;
import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.compartilhevida.compartilhevida.Entidades.User;
import br.com.compartilhevida.compartilhevida.Fragmentos.ConfigFragment;
import br.com.compartilhevida.compartilhevida.Fragmentos.ContaFragment;
import br.com.compartilhevida.compartilhevida.Fragmentos.PostsFragment;
import br.com.compartilhevida.compartilhevida.Utilitarios.CircleTransform;

public class MainActivity extends BaseActivity
        implements
        NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener,
        ContaFragment.OnFragmentInteractionListener,
        PostsFragment.OnFragmentInteractionListener
{

    // Firebase instance variables
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth mAuth;
    private User user;
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
        user = User.getInstance(getBaseContext());

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
        if (user.getBirthday() == null) {

            ret = false;
        }
        if (user.getGender() == null) {

            ret = false;
        }
        if (user.getTipo_sanguineo() == null) {

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
                    User usuario = snapshot.getValue(User.class);

                    if (usuario != null) {
                        user.setGender(usuario.getGender());
                        user.setFirst_name(usuario.getFirst_name());
                        user.setLast_name(usuario.getLast_name());
                        user.setUid(usuario.getUid());
                        user.setEmail(usuario.getEmail());
                        user.setBirthday(usuario.getBirthday());
                        user.setCidade(usuario.getCidade());
                        user.setTipo_sanguineo(usuario.getTipo_sanguineo());
                        user.setProvider(usuario.getProvider());
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
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            //startActivity(new Intent(MainActivity.this, MapsActivity.class));
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_config) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content, new ConfigFragment(),"Config")
                    .commit();
        } else if (id == R.id.nav_conta) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content, new ContaFragment(),"Conta")
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private boolean isNameOk(User user, FirebaseUser firebaseUser) {
        return (
                user.getFirst_name() != null
                        || firebaseUser.getDisplayName() != null
        );
    }

    private void carregaUsuario(User user) {
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
}
