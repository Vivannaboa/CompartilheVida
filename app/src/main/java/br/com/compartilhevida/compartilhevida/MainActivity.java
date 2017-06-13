package br.com.compartilhevida.compartilhevida;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import br.com.compartilhevida.compartilhevida.fragment.Post.MyPostsFragment;
import br.com.compartilhevida.compartilhevida.fragment.Post.RecentPostsFragment;
import br.com.compartilhevida.compartilhevida.models.Usuario;
import br.com.compartilhevida.compartilhevida.util.CircleTransform;

import static android.view.animation.AnimationUtils.loadAnimation;

public class MainActivity extends BaseActivity
        implements
        NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 10;
    private static final String TAG = "MainActivity";

    // Firebase instance variables
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth mAuth;
    private Usuario mUsuario;
    private FirebaseUser userFirebase;
    private DatabaseReference mUserDatabase;
    private ValueEventListener mUserEventListener;
    private ImageView imageView;
    private TextView usuario;
    private TextView email;

    //floating botton
    public FloatingActionButton fab;
    public LinearLayout fab1;
    public LinearLayout fab2;
    public FrameLayout frameFlatButtom;

    //Animations
    private Animation show_fab_1;
    private Animation hide_fab_1;
    private Animation show_fab_2;
    private Animation hide_fab_2;

    //flag que controla se os botões fabs estão abertos
    private boolean FAB_Status = false;

    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.you.name", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
        mUsuario = Usuario.getInstance();

        //get firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        //get current user
        userFirebase = FirebaseAuth.getInstance().getCurrentUser();
        //adiciona listner que verifica se tem um usuário logado
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser userFirebase = firebaseAuth.getCurrentUser();
                if (userFirebase == null) {
                    //se não tem nenhum usuário logado abre a tela de loguin
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        //agora que esta tudo certo com o usuário vamos carregar o layout
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //o usuário do firebase só tem os dados básicos então vamos no banco pegar o restante
        if (userFirebase != null) {
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userFirebase.getUid());

           // Cria um adapter para inflar as tabs
            mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
                private final android.support.v4.app.Fragment[] mFragments = new android.support.v4.app.Fragment[] {
                        new RecentPostsFragment(),
                        new MyPostsFragment()
                };
                private final String[] mFragmentNames = new String[] {
                        getString(R.string.heading_recent),
                        getString(R.string.heading_my_posts)
                };
                @Override
                public android.support.v4.app.Fragment getItem(int position) {
                    return mFragments[position];
                }
                @Override
                public int getCount() {
                    return mFragments.length;
                }
                @Override
                public CharSequence getPageTitle(int position) {
                    return mFragmentNames[position];
                }
            };
        }
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        //Floating Action Buttons
        frameFlatButtom = (FrameLayout) findViewById(R.id.frameFlatButtom);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (LinearLayout) findViewById(R.id.fab_1);
        fab2 = (LinearLayout) findViewById(R.id.fab_2);

        //Animations
        show_fab_1 = loadAnimation(getApplication(), R.anim.fab1_show);
        hide_fab_1 = loadAnimation(getApplication(), R.anim.fab1_hide);
        show_fab_2 = loadAnimation(getApplication(), R.anim.fab2_show);
        hide_fab_2 = loadAnimation(getApplication(), R.anim.fab2_hide);
        fab.setOnClickListener(this);


        //carrega o layout do menu lateral
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
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_doacao) {
            permissionReadContat();
        } else if (id == R.id.nav_hemocentros) {
            startActivity(new Intent(MainActivity.this, HemocentrosActivity.class));
            //startActivity(new Intent(MainActivity.this, MapsActivity.class));
        } else if (id == R.id.nav_cartilha_doador) {

        } else if (id == R.id.nav_config) {

//            getFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.containerView, new ConfigFragment(),"Config")
//                    .commit();
        } else if (id == R.id.nav_conta) {
//            getFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.containerView, ContaFragment.newInstance(mUserDatabase),"Conta")
//                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void permissionReadContat() {
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }else{
           //Aqui deu certo
        }

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (!FAB_Status) {
                    //Display FAB menu
                    expandFAB();
                    FAB_Status = true;
                } else {
                    //Close FAB menu
                    hideFAB();
                    FAB_Status = false;
                }
                break;

            case R.id.fab_1:
                startActivity(new Intent(MainActivity.this, DoacaoActivity.class));
                hideFAB();
                FAB_Status = false;
                break;

            case R.id.fab_2:
                startActivity(new Intent(MainActivity.this, NewPostActivity.class));
                //Close FAB menu
                hideFAB();
                FAB_Status = false;
                break;
            case R.id.frameFlatButtom:
                hideFAB();
                FAB_Status = false;
                break;

            default:
                break;

        }
    }

    private void expandFAB() {
        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        //layoutParams.rightMargin += (int) fab1.getWidth();
        layoutParams.bottomMargin += (int) (fab1.getHeight() * 0.8);
        fab1.setLayoutParams(layoutParams);
        fab1.startAnimation(show_fab_1);
        fab1.setClickable(true);
        fab1.setOnClickListener(this);

        //Floating Action Button 2
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        //layoutParams2.rightMargin += (int) fab2.getWidth();
        layoutParams2.bottomMargin += (int) (fab2.getHeight() * 1.6);
        fab2.setLayoutParams(layoutParams2);
        fab2.startAnimation(show_fab_2);
        fab2.setClickable(true);
        fab2.setOnClickListener(this);

        frameFlatButtom.setBackgroundColor(Color.parseColor("#d0ffffff"));
        frameFlatButtom.setOnClickListener(this);
    }

    private void hideFAB() {
        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        //layoutParams.rightMargin -= (int) fab1.getWidth();
        layoutParams.bottomMargin -= (int) (fab1.getHeight() * 0.8);
        fab1.setLayoutParams(layoutParams);
        fab1.startAnimation(hide_fab_1);
        fab1.setClickable(false);

        //Floating Action Button 2
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        //layoutParams2.rightMargin -= (int) fab2.getWidth();
        layoutParams2.bottomMargin -= (int) (fab2.getHeight() * 1.6);
        fab2.setLayoutParams(layoutParams2);
        fab2.startAnimation(hide_fab_2);
        fab2.setClickable(false);

        frameFlatButtom.setBackgroundColor(Color.TRANSPARENT);
        frameFlatButtom.setClickable(false);
    }

}
