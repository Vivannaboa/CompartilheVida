package br.com.compartilhevida.compartilhevida;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.compartilhevida.compartilhevida.models.Hemocentro;
import br.com.compartilhevida.compartilhevida.models.Post;
import br.com.compartilhevida.compartilhevida.models.Usuario;
import br.com.compartilhevida.compartilhevida.util.CircleTransform;
import br.com.compartilhevida.compartilhevida.util.Validador;

public class PedidoDoacaoActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "PedidoDoacaoActivity";

    private Toolbar mToolbar;
    private AppBarLayout appbarLayout;
    private MenuItem btnSalvar;
    private FloatingActionButton mFloatingActionButton;
    private ImageView mPhotoUser;
    private TextView mNomeUser;
//    private EditText mEdtFavorecido;
    private AutoCompleteTextView mAutHemocentro;
    private Spinner mSpnSanguineo;
    private EditText mBodyField;

    //adapter
    private ArrayAdapter<String> autoComplete;
    private List<Hemocentro> hemocentroList = new ArrayList<>();
    //base de dados
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_doacao);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        recuperarComponentes();
        listnerAutoComplet();
        setSupportActionBar(mToolbar);

        if (getDisplayNome()!=null){
            mNomeUser.setText(getDisplayNome());
        }
        if (getUrlPhoto()!=null){
            Glide.with(getBaseContext()).load(getUrlPhoto()).transform(new CircleTransform(this)).into(mPhotoUser);
        }
        getSupportActionBar().setTitle("Fazer um pedido de doação");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void recuperarComponentes() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_pedido);
        mPhotoUser = (ImageView) findViewById(R.id.toolbar_logo);
        mNomeUser = (TextView) findViewById(R.id.post_author);
//        mEdtFavorecido =(EditText) findViewById(R.id.edt_favorecido);
        mAutHemocentro= (AutoCompleteTextView)findViewById(R.id.auct_hemocentro);
        mSpnSanguineo = (Spinner) findViewById(R.id.spinnerTipoSanguineo);
        mBodyField = (EditText)findViewById(R.id.edtBodyField);
        appbarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(this);
    }

    private void listnerAutoComplet() {
        autoComplete = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        mDatabase.child("hemocentros").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()) {
                    String strHemocentro = suggestionSnapshot.child("nome").getValue(String.class);
                    //converte o json em objeto
                    Hemocentro hemocentro = suggestionSnapshot.getValue(Hemocentro.class);
                    hemocentro.setUid(suggestionSnapshot.getKey());
                    hemocentroList.add(hemocentro);
                    autoComplete.add(strHemocentro);
                }
            }

            @Override
            public void onCancelled(DatabaseError mDatabaseError) {
                FirebaseCrash.report(mDatabaseError.toException());
            }
        });
        mAutHemocentro.setAdapter(autoComplete);
    }

    private void setEditingEnabled(boolean enabled) {
        mAutHemocentro.setEnabled(enabled);
        mSpnSanguineo.setEnabled(enabled);
//        mEdtFavorecido.setEnabled(enabled);
        mFloatingActionButton.setEnabled(enabled);
        btnSalvar.setEnabled(enabled);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tolbar_footer_new_post, menu);
        btnSalvar = (MenuItem) menu.findItem(R.id.enviar_post);
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                submitPost();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.enviar_post:
                submitPost();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    private boolean validarDados(){
        if (!Validador.validateNotNull(mBodyField,"Você deve informar uma mensagem!")){
            return false;
        }
        if (!Validador.validateNotNull(mAutHemocentro, "Informe em qual hemocentro o doador deve doar!")){
            return false;
        }
        if (!verificarCadastroHemocentro(mAutHemocentro.getText().toString())){
            mAutHemocentro.setError("É necessário selecionar um hemocentro na lista.");
            mAutHemocentro.setFocusable(true);
            mAutHemocentro.requestFocus();
           return false;
        }


        return true;
    }

    private boolean verificarCadastroHemocentro(String hemocentro){
        for (Hemocentro item: hemocentroList) {
            if (item.getNome().toString().equalsIgnoreCase(hemocentro.toString())){
                return true;
            }
        }
        return false;
    }

    private void submitPost() {
        setEditingEnabled(false);
        if (!validarDados()){
            setEditingEnabled(true);
            return;
        }

        final String mensagem = mBodyField.getText().toString();
        final String userId = getUid();
        final String tipoSanguineo = mSpnSanguineo.getSelectedItem().toString();
        final String hemocentro = mAutHemocentro.getText().toString();
        final String titulo = "Pedido de doação para o hemocentro " + hemocentro;
        String urlPhoto = "";
        if (getUrlPhoto()!=null){
            urlPhoto = getUrlPhoto().toString();
        }
        final String finalUrlPhoto = urlPhoto;
        Toast.makeText(this, "Publicando...", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getApplicationContext(),
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            writeNewPost(userId, user.getFirst_name(), titulo ,mensagem,tipoSanguineo,hemocentro, finalUrlPhoto);
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
    }

    // Aqui a mágica acontece
    private void writeNewPost(String userId, String username, String titulo, String body,String tipo, String hemocentro,String urlPhoto) {

        // Cria um novo post em /user-posts/$userid/$postid
        // e ao mesmo tempo  adiciona em /posts/$postid
        String key = mDatabase.child("posts").push().getKey();
        Post post = new Post(userId, username,titulo, body,urlPhoto, tipo, hemocentro);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }
}
