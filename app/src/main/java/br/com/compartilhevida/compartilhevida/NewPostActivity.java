package br.com.compartilhevida.compartilhevida;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import br.com.compartilhevida.compartilhevida.models.Post;
import br.com.compartilhevida.compartilhevida.models.Usuario;
import br.com.compartilhevida.compartilhevida.util.CircleTransform;
import br.com.compartilhevida.compartilhevida.util.Validador;

public class NewPostActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "NewPostActivity";
    private static final String REQUIRED = "Required";
    private static final String EXTRA_POST_KEY = "post_key" ;

    private DatabaseReference mDatabase;
    private ImageView mPhotoUser;
    private TextView mNomeUser;
    private EditText mTitleField;
    private EditText mBodyField;
    private Usuario mUser;
    private Toolbar mToolbar;
    private AppBarLayout appbarLayout;
    private MenuItem btnSalvar;
    private MenuItem btnExcluir;
    private FloatingActionButton floatingActionButton;

    //para edição do post
    private String mPostKey = "";
    Post post;
    private DatabaseReference mPostReference;
    private DatabaseReference mMYPostReference;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        //referencia do banco
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // instancia do usuário
        mUser  = Usuario.getInstance();

        mPostKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (mPostKey!=null){
            // Initialize Database
            mPostReference = FirebaseDatabase.getInstance().getReference().child("posts").child(mPostKey);
            mMYPostReference = FirebaseDatabase.getInstance().getReference().child("user-posts").child(getUid()).child(mPostKey);
        }

        mPhotoUser = (ImageView) findViewById(R.id.toolbar_logo);
        mNomeUser = (TextView) findViewById(R.id.post_author);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_post);
        mTitleField =(EditText) findViewById(R.id.title_view);
        mBodyField = (EditText)findViewById(R.id.edtBodyField);
        appbarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floating_button);
        floatingActionButton.setOnClickListener(this);
        setSupportActionBar(mToolbar);
        if (mUser!=null){
            mNomeUser.setText(mUser.getFirst_name());
        }
        if (getUrlPhoto()!=null){
            Glide.with(getBaseContext()).load(getUrlPhoto()).transform(new CircleTransform(this)).into(mPhotoUser);
        }
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mPostKey!=null){
            // Add listener to post
            mPostReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    post = dataSnapshot.getValue(Post.class);
                    mTitleField.setText(post.getTitulo());
                    mBodyField.setText(post.getMensagem());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    Toast.makeText(getApplicationContext(), "Falha ao carregar dados do Post.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tolbar_footer_new_post, menu);
        btnSalvar = (MenuItem) menu.findItem(R.id.enviar_post);
        btnExcluir = (MenuItem) menu.findItem(R.id.apagar_post);
        if (mPostKey!=null){
            btnExcluir.setVisible(true);
        }
        appbarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float percentage = ((float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange());
                Log.i(TAG, (String.valueOf(percentage)));
                if (percentage >= 0.51428574) {
                    btnSalvar.setVisible(true);
                } else {
                    btnSalvar.setVisible(false);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.enviar_post:
                if (mPostKey==null){
                    submitPost();
                }else {
                    updatePost();
                }

                break;
            case R.id.apagar_post:
                apagarPost();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    private void apagarPost() {
        setEditingEnabled(false);
        mPostReference.removeValue();
        mMYPostReference.removeValue();
        setEditingEnabled(true);
        finish();
    }

    private void updatePost() {
        setEditingEnabled(false);
        if (!validarDados()){
            setEditingEnabled(true);
            return;
        }
        String body = mBodyField.getText().toString();
        String titulo =  mTitleField.getText().toString();
        post.setTitulo(titulo);
        post.setMensagem(body);
        mPostReference.updateChildren(post.toMap());
        mMYPostReference.updateChildren(post.toMap());
        setEditingEnabled(true);
        finish();
    }

    private boolean validarDados() {
        if (!Validador.validateNotNull(mTitleField,"Informe um título para o seu post")){
            return false;
        }else if (!Validador.validateNotNull(mBodyField,"Você deve informar uma mensagem!")){
            return false;
        }
        return true;
    }


    private void submitPost() {
        setEditingEnabled(false);
        if (!validarDados()){
            setEditingEnabled(true);
            return;
        }

        final String body = mBodyField.getText().toString();
        final String titulo =  mTitleField.getText().toString();

        Toast.makeText(this, "Publicando...", Toast.LENGTH_SHORT).show();

        // [START single_value_read]
        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        Usuario user = dataSnapshot.getValue(Usuario.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(NewPostActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            writeNewPost(userId, user.getFirst_name(), titulo ,body);
                        }
                        setEditingEnabled(true);
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        setEditingEnabled(true);
                    }
                });
        // [END single_value_read]
    }

    private void setEditingEnabled(boolean enabled) {
        mBodyField.setEnabled(enabled);
        mTitleField.setEnabled(enabled);
        floatingActionButton.setEnabled(enabled);
        btnSalvar.setEnabled(enabled);
    }

    // Aqui a mágica acontece
    private void writeNewPost(String userId, String username, String titulo, String body) {
       String urlPhoto = "";
        if (getUrlPhoto()!=null){
            urlPhoto = getUrlPhoto().toString();
        }
        // Cria um novo post em /user-posts/$userid/$postid
        // e ao mesmo tempo  adiciona em /posts/$postid
        String key = mDatabase.child("posts").push().getKey();
        Post post = new Post(userId, username,titulo, body,urlPhoto,"post");
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.floating_button:
                if (mPostKey==null){
                    submitPost();
                }else {
                    updatePost();
                }
                break;
            default:
                break;
        }

    }

}
