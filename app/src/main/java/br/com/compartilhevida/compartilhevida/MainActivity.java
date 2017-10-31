package br.com.compartilhevida.compartilhevida;

import android.app.Activity;
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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
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
import android.widget.Toast;

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

import br.com.compartilhevida.compartilhevida.adapter.CommentAdapter;
import br.com.compartilhevida.compartilhevida.fragment.CartilhaFragment;
import br.com.compartilhevida.compartilhevida.fragment.ConfigFragment;
import br.com.compartilhevida.compartilhevida.fragment.ContaFragment;
import br.com.compartilhevida.compartilhevida.fragment.DoacaoFragment;
import br.com.compartilhevida.compartilhevida.fragment.TabFragment;
import br.com.compartilhevida.compartilhevida.models.Usuario;
import br.com.compartilhevida.compartilhevida.util.CircleTransform;

import static android.view.animation.AnimationUtils.loadAnimation;

public class MainActivity extends BaseActivity
        implements
        NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, ContaFragment.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";
    private static final int RESULT_DOACAO = 1;

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
    private long lastBackPressTime = 0;
    private Toast toast;

    //floating botton
    public FloatingActionButton fab;
    public LinearLayout fab1;
    public LinearLayout fab2;
    public LinearLayout fab3;
    public FrameLayout frameFlatButtom;

    //Animations
    private Animation show_fab_1;
    private Animation hide_fab_1;
    private Animation show_fab_2;
    private Animation hide_fab_2;
    private Animation show_fab_3;
    private Animation hide_fab_3;

    //flag que controla se os botões fabs estão abertos
    private boolean FAB_Status = false;

    private FragmentPagerAdapter mPagerAdapter;
//    private ViewPager mViewPager;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FragmentManager FM;
    private FragmentTransaction FT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "br.com.compartilhevida.compartilhevida",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }


        //pega instancia de usuário
        mUsuario = Usuario.getInstance();

        //pega intancia do firebase auth
        mAuth = FirebaseAuth.getInstance();

        //pega o usuário corrente do firebase
        userFirebase = FirebaseAuth.getInstance().getCurrentUser();

        //cria um listner que verifica se tem um usuário logado
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

        //agora vamos carregar o layout
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Floating Action Buttons
        frameFlatButtom = (FrameLayout) findViewById(R.id.frameFlatButtom);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (LinearLayout) findViewById(R.id.fab_1);
        fab2 = (LinearLayout) findViewById(R.id.fab_2);
        fab3 = (LinearLayout) findViewById(R.id.fab_3);

        //Animations
        show_fab_1 = loadAnimation(getApplication(), R.anim.fab1_show);
        hide_fab_1 = loadAnimation(getApplication(), R.anim.fab1_hide);
        show_fab_2 = loadAnimation(getApplication(), R.anim.fab2_show);
        hide_fab_2 = loadAnimation(getApplication(), R.anim.fab2_hide);
        show_fab_3 = loadAnimation(getApplication(), R.anim.fab3_show);
        hide_fab_3 = loadAnimation(getApplication(), R.anim.fab3_hide);
        fab.setOnClickListener(this);


        //carrega o layout do menu lateral
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        FM= getSupportFragmentManager();
        FT = FM.beginTransaction();

        //o usuário do firebase só tem os dados básicos então vamos no banco pegar o restante
        if (userFirebase != null) {
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userFirebase.getUid());
            FT.replace(R.id.containerView, new TabFragment()).commit();
        }

        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        View hView = navigationView.getHeaderView(0);
        imageView = (ImageView) hView.findViewById(R.id.imageViewUsuario);
        usuario = (TextView) hView.findViewById(R.id.textViewUsuario);
        email = (TextView) hView.findViewById(R.id.textViewEmail);

    }

    private boolean verificaDadosPendendtes() {
        boolean ret = true;
        if (CompartilheVida.jaVerificou){
            return true;
        }
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
                            CompartilheVida.jaVerificou = true;
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("Olá " + usuario.getFirst_name() + ", gostariamos de lhe conhecer melhor! " + "\n" + " Deseja concluir seu cadastro agora?")
                                    .setTitle("Completar cadastro");
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
                        }else{
                            if (mUserEventListener!= null){
                                mUserDatabase.removeEventListener(mUserEventListener);
                            }
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
        }
        if (FAB_Status) {
            hideFAB();
            FAB_Status = false;
            return;
        }
        int count = FM.getBackStackEntryCount();
        if (count == 0) {
            if (this.lastBackPressTime < System.currentTimeMillis() - 2000) {
                toast = Toast.makeText(this, "Pressione o Botão Voltar novamente para fechar o Aplicativo.", Toast.LENGTH_SHORT);
                toast.show();
                this.lastBackPressTime = System.currentTimeMillis();
            } else {
                if (toast != null) {
                    toast.cancel();
                }
                super.onBackPressed();
            }
        } else {
            FM.popBackStackImmediate();
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
        drawerLayout.closeDrawer(GravityCompat.START);
        FragmentTransaction fragmentTransaction = FM.beginTransaction();
        switch (item.getItemId()){
            case R.id.nav_posts:
                fragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
                break;
            case R.id.nav_doacao:
                fragmentTransaction.replace(R.id.containerView, new DoacaoFragment()).addToBackStack("Fragment").commit();
                break;
            case R.id.nav_hemocentros:
                startActivity(new Intent(MainActivity.this, HemocentrosActivity.class));
                break;
            case R.id.nav_cartilha_doador:
                fragmentTransaction.replace(R.id.containerView,new CartilhaFragment()).addToBackStack("Fragment").commit();
                break;
            case R.id.nav_config:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
//                fragmentTransaction.replace(R.id.containerView,new ConfigFragment()).addToBackStack("Fragment").commit();
                break;
            case R.id.nav_conta:
                fragmentTransaction.replace(R.id.containerView,new ContaFragment()).addToBackStack("Fragment").commit();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Sem conexão com a Internet!", Toast.LENGTH_SHORT).show();
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
                startActivityForResult(new Intent(MainActivity.this, DoacaoActivity.class),RESULT_DOACAO);
                hideFAB();
                FAB_Status = false;
                break;

            case R.id.fab_2:
                startActivity(new Intent(MainActivity.this, NewPostActivity.class));
                //Close FAB menu
                hideFAB();
                FAB_Status = false;
                break;
            case R.id.fab_3:
                startActivity(new Intent(MainActivity.this, PedidoDoacaoActivity.class));
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

        //Floating Action Button 3
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        //layoutParams3.rightMargin += (int) fab3.getWidth();;
        layoutParams3.bottomMargin += (int) (fab3.getHeight() * 2.4);
        fab3.setLayoutParams(layoutParams3);
        fab3.startAnimation(show_fab_3);
        fab3.setClickable(true);
        fab3.setOnClickListener(this);

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

        //Floating Action Button 3
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        //layoutParams3.rightMargin -= (int) fab3.getWidth();
        layoutParams3.bottomMargin -= (int) (fab3.getHeight() * 2.4);
        fab3.setLayoutParams(layoutParams3);
        fab3.startAnimation(hide_fab_3);
        fab3.setClickable(false);

        frameFlatButtom.setBackgroundColor(Color.TRANSPARENT);
        frameFlatButtom.setClickable(false);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_DOACAO) {
            if (resultCode == Activity.RESULT_OK) {
                FragmentTransaction fragmentTransaction = FM.beginTransaction();
                fragmentTransaction.replace(R.id.containerView, new DoacaoFragment()).commit();
            }
        }

    }

}
