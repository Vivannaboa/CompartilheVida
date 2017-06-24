package br.com.compartilhevida.compartilhevida;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import br.com.compartilhevida.compartilhevida.adapter.CommentAdapter;
import br.com.compartilhevida.compartilhevida.models.Comentario;
import br.com.compartilhevida.compartilhevida.models.Post;
import br.com.compartilhevida.compartilhevida.models.Usuario;
import br.com.compartilhevida.compartilhevida.util.CircleTransform;
import br.com.compartilhevida.compartilhevida.util.Validador;

public class PostDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "PostDetailActivity";

    public static final String EXTRA_POST_KEY = "post_key";

    private DatabaseReference mPostReference;
    private DatabaseReference mCommentsReference;
    private ValueEventListener mPostListener;
    private String mPostKey;
    private CommentAdapter mAdapter;
    private ImageView imageView;
    private TextView mAuthorView;
    private TextView mTitleView;
    private TextView mBodyView;
    private EditText mCommentField;
    private ImageButton mCommentButton;
    private RecyclerView mCommentsRecycler;
    private LinearLayoutManager mManager;

    Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // Get post key from intent
        mPostKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (mPostKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // Initialize Database
        mPostReference = FirebaseDatabase.getInstance().getReference()
                .child("posts").child(mPostKey);
        mCommentsReference = FirebaseDatabase.getInstance().getReference()
                .child("post-comments").child(mPostKey);

        // Initialize Views
        mAuthorView = (TextView) findViewById(R.id.post_author);
        mTitleView = (TextView) findViewById(R.id.post_title);
        mBodyView = (TextView) findViewById(R.id.post_body);
        mCommentField = (EditText) findViewById(R.id.field_comment_text);
        mCommentButton = (ImageButton) findViewById(R.id.button_post_comment);
        mCommentsRecycler = (RecyclerView) findViewById(R.id.recycler_comments);
        imageView = (ImageView)findViewById(R.id.post_author_photo);


        mCommentButton.setOnClickListener(this);
        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mCommentsRecycler.setLayoutManager(mManager);

    }

    @Override
    public void onStart() {
        super.onStart();

        // Add listener to post
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Obter o objeto Post e usar os valores para atualizar a UI
                post = dataSnapshot.getValue(Post.class);
                mAuthorView.setText(post.getAutor());
                mTitleView.setText(post.getTitulo());
                mBodyView.setText(post.getMensagem());
                if (!post.getUrlFoto().toString().isEmpty()) {
                    Glide.with(getApplicationContext()).load(post.getUrlFoto()).transform(new CircleTransform(getApplicationContext())).into(imageView);
                }else{
                    Glide.with(getApplicationContext()).load(R.drawable.ic_action_account_circle_40).transform(new CircleTransform(getApplicationContext())).into(imageView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Obtendo Mensagem falha, registra uma mensagem
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(PostDetailActivity.this, "Falha ao carregar dados do Post.", Toast.LENGTH_SHORT).show();
            }
        };
        mPostReference.addValueEventListener(postListener);
        // termina o listner do post

        // Mantenha a cópia do ouvinte do post para que possamos removê-lo quando o aplicativo parar
        mPostListener = postListener;

        // Listen para os comentários
        mAdapter = new CommentAdapter(this, mCommentsReference);
        mCommentsRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mPostListener != null) {
            mPostReference.removeEventListener(mPostListener);
        }
        // Clean up comments listener
        mAdapter.cleanupListener();
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_post_comment) {
            if (!Validador.validateNotNull(mCommentField,"Ops, acho que esqueceu do comentário!")){
                return;
            }
            postComment();
            post.setComentariosCont(post.getComentariosCont() + 1);
            postContComment();
        }
    }

    private void postComment() {
        final String uid = getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // pega o usuário
                        Usuario user = dataSnapshot.getValue(Usuario.class);
                        String authorName = user.getFirst_name();

                        // Cria um objeto de comentario
                        String commentText = mCommentField.getText().toString();
                        Comentario comment = new Comentario(uid, authorName, commentText, getUrlPhoto().toString());

                        // grava o comentário
                        mCommentsReference.push().setValue(comment);

                        // limpa o campo
                        mCommentField.setText(null);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void postContComment(){
        Map<String, Object> postValues = post.toMap();
        mPostReference.updateChildren(postValues);
    }




}
