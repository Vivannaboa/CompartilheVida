package br.com.compartilhevida.compartilhevida;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import br.com.compartilhevida.compartilhevida.Entidades.User;
import br.com.compartilhevida.compartilhevida.Utilitarios.Validador;


public class LoginActivity extends BaseActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private EditText inputEmail, inputPassword;
    private Button btnCadastrar, btnLogin, btnReset;
    private LoginButton loginFacebook;
    private CallbackManager mCallbackManager;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    public static GoogleApiClient mGoogleApiClient;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // set the view now
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnCadastrar = (Button) findViewById(R.id.btn_cadastrar);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
        btnCadastrar.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        findViewById(R.id.sign_in_button_google).setOnClickListener(this);

        //google - pega a id e seta para o componente
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton signInButtonGoogle = (SignInButton) findViewById(R.id.sign_in_button_google);
        signInButtonGoogle.setSize(SignInButton.SIZE_STANDARD);

        //Facebook
        mCallbackManager = CallbackManager.Factory.create();
        loginFacebook = (LoginButton) findViewById(R.id.btn_login_facebook);
        loginFacebook.setReadPermissions(Arrays.asList("email"));
        loginFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessFacebookLoginData(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), R.string.operacao_cancelada, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Ops, não foi possivel logar com o Facebook ", Toast.LENGTH_SHORT).show();
            }
        });
        user = User.getInstance(getBaseContext());
        mAuthListener = getFirebaseAuthResultHandler();

    }

    private FirebaseAuth.AuthStateListener getFirebaseAuthResultHandler() {
        FirebaseAuth.AuthStateListener callback = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser userFirebase = firebaseAuth.getCurrentUser();

                if (userFirebase == null) {
                    hideProgressDialog();
                    return;
                } else {
                    user.setUid(userFirebase.getUid());
                    adicionarUsuario();
                    goMainScreen();
                }


            }
        };
        return (callback);
    }

    private boolean isNameOk(User user, FirebaseUser firebaseUser) {
        return (
                user.getFirst_name() != null
                        || firebaseUser.getDisplayName() != null
        );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //google
            case R.id.sign_in_button_google:
                signIn();
                break;

            case R.id.btn_cadastrar:
                try {
                    startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.btn_reset_password:
                try {
                    startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.btn_login:
                try {
                    showProgressDialog();
                    final String email = inputEmail.getText().toString();
                    final String password = inputPassword.getText().toString();

                    if (!Validador.validateNotNull(inputEmail, getString(R.string.val_email_empty))) {
                        hideProgressDialog();
                        return;
                    }
                    if (!Validador.validateNotNull(inputPassword, getString(R.string.val_senha_empty))) {
                        hideProgressDialog();
                        return;
                    }

                    showProgressDialog();

                    //Autenticar o  usuario
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // Se o login falhar, exiba uma mensagem para o usuário. Se o login for bem-sucedido
                                    // o ouvinte do estado de autenticação será notificado ea lógica para lidar com o
                                    // assinado no usuário pode ser manipulado no ouvinte.
                                   hideProgressDialog();
                                    if (!task.isSuccessful()) {
                                        // there was an error
                                        if (password.length() < 6) {
                                            inputPassword.setError(getString(R.string.minimum_password));
                                        } else {
                                            Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                        }
                                    } else {//Caso feliz
                                        hideProgressDialog();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                user.setEmail(account.getEmail());
                user.setFirst_name(account.getGivenName());
                user.setLast_name(account.getFamilyName());
                user.setUid(account.getId());
                user.setUrlPhoto(account.getPhotoUrl().toString());
                accessGoogleLoginData(account.getIdToken());
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }

        //facebook
        mAuth.addAuthStateListener(mAuthListener);

        //google
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    //google
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Conectado com êxito, mostre interface do usuário autenticada.
            GoogleSignInAccount acct = result.getSignInAccount();

            // updateUI(true);
        } else {
        }
    }

    //google
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    public void goMainScreen() {
        hideProgressDialog();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }


    private void accessLoginData(String provider, String... tokens) {
        if (tokens != null && tokens.length > 0 && tokens[0] != null) {

            AuthCredential credential = FacebookAuthProvider.getCredential(tokens[0]);
            credential = provider.equalsIgnoreCase("google") ? GoogleAuthProvider.getCredential(tokens[0], null) : credential;
            user.setProvider(provider);

            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {
                                hideProgressDialog();
                                Toast.makeText(LoginActivity.this, "Não foi possvel entrar!", Toast.LENGTH_SHORT).show();

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            FirebaseCrash.report(e);
                        }
                    });
        } else {
            mAuth.signOut();
        }
    }

    private void accessFacebookLoginData(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        Bundle bFacebookData = getFacebookData(object);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, location");
        request.setParameters(parameters);
        request.executeAsync();


        accessLoginData(
                "facebook",
                (accessToken != null ? accessToken.getToken() : null)
        );
    }

    private void accessGoogleLoginData(String accessToken) {
        accessLoginData(
                "google",
                accessToken
        );
    }


    private void onAuthSuccess(FirebaseUser user) {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private void adicionarUsuario() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDatabase.child("users").child(user.getUid()).setValue(user.toMap());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private Bundle getFacebookData(JSONObject object) {
        Bundle bundle = null;
        try {
            bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            user.setFirst_name(object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            user.setLast_name(object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            user.setEmail(object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.getString("gender").equalsIgnoreCase("male")) {
                user.setGender("Masculino");
            } else {
                user.setGender("Femenino");
            }
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            user.setBirthday(object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));
            user.setCidade(object.getJSONObject("location").getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bundle;
    }

}

