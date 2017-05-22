package br.com.compartilhevida.compartilhevida;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import br.com.compartilhevida.compartilhevida.models.Usuario;
import br.com.compartilhevida.compartilhevida.util.Validador;

public class SignupActivity extends BaseActivity  {
    private EditText inputEmail, inputPassword, inputPasswordConfirm, edtNome, edtSobrenome;
    private RadioGroup radioGroupSexo;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private Spinner spinnerTipoSanguineo;
    static EditText dataDeNacimento;
    private AutoCompleteTextView autoCompleteTextViewCidade;
    private ArrayAdapter<String> autoComplete;
    private boolean editando;
    ArrayAdapter<CharSequence> adapter;
    Usuario mUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }else{
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        }
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        recuperacomponentes();
        mUsuario = Usuario.getInstance();
        if (mUsuario.getEmail()!=null){
            recuperarDadosDoUsuarioParaComponentes();
            editando =true;
        }
        //Nothing special, create database reference.
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        autoComplete = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        //Child the root before all the push() keys are found and add a ValueEventListener()
        database.child("cidades").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()){
                    String cidade = suggestionSnapshot.child("cidade").getValue(String.class);
                    String uf = suggestionSnapshot.child("uf").getValue(String.class);
                    autoComplete.add(cidade + " - " + uf);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        autoCompleteTextViewCidade.setAdapter(autoComplete);
    }

    private void recuperarDadosDoUsuarioParaComponentes() {
        dataDeNacimento.setText(mUsuario.getBirthday());
        findViewById(R.id.layout_email).setVisibility(View.GONE);
        findViewById(R.id.layout_senha).setVisibility(View.GONE);
        findViewById(R.id.layout_confirmar_senha).setVisibility(View.GONE);
        findViewById(R.id.btn_reset_password).setVisibility(View.GONE);
        findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        if (mUsuario.getGender()!=null) {
            if (mUsuario.getGender().toString().equalsIgnoreCase("Masculino")) {
                RadioButton rb1 = (RadioButton) findViewById(R.id.radioButtonMasculino);
                rb1.setChecked(true);
            } else if (mUsuario.getGender().toString().equalsIgnoreCase("Femenino")) {
                RadioButton rb1 = (RadioButton) findViewById(R.id.radioButtonFeminino);
                rb1.setChecked(true);
            }
        }
        autoCompleteTextViewCidade.setText(mUsuario.getCidade());
        edtNome.setText(mUsuario.getFirst_name());
        edtSobrenome.setText(mUsuario.getLast_name());
        if (spinnerTipoSanguineo!=null) {
            spinnerTipoSanguineo.setSelection(adapter.getPosition(mUsuario.getTipo_sanguineo()));
        }

    }

    private void recuperacomponentes() {
        dataDeNacimento = (EditText) findViewById(R.id.editTextDataNacimento);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputPasswordConfirm = (EditText) findViewById(R.id.confirm_password);
        radioGroupSexo = (RadioGroup) findViewById(R.id.radioGroupSexo) ;
        autoCompleteTextViewCidade= (AutoCompleteTextView)findViewById(R.id.autoCompleteTextViewCidade);
        edtNome = (EditText) findViewById(R.id.edtNome);
        edtSobrenome =(EditText)findViewById(R.id.edtSobrenome);
        spinnerTipoSanguineo = (Spinner) findViewById(R.id.spinnerTipoSanguineo);
        adapter = ArrayAdapter.createFromResource(this, R.array.array_tipo_sangineo, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoSanguineo.setAdapter(adapter);
//        btnSignIn = (Button) findViewById(R.id.sign_in_button);
//        btnSignUp = (Button) findViewById(R.id.sign_up_button);
//        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

    }

    public void clicRsetPassword(View v) {
            startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
    }
    public void clickFinish(View v) {
        finish();
    }

    public void selectDate(View view) {
        DialogFragment newFragment = new SelectDateFragment();
        newFragment.show(getFragmentManager(), "DatePicker");
    }

    public static void populateSetDate(int year, int month, int day) {
        dataDeNacimento.setText( day + "/" +month  + "/" + year);
    }

    public void clickEntrar(View v){
        showProgressDialog();

        if (!validaForm()){
            hideProgressDialog();
            return;
        }

        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        mUsuario.setFirst_name(edtNome.getText().toString());
        mUsuario.setLast_name(edtSobrenome.getText().toString());
        if (mUsuario.getEmail()==null) {
            mUsuario.setEmail(email);
        }
        if (mUsuario.getProvider()==null){
            mUsuario.setProvider("email");
        }
        mUsuario.setBirthday(dataDeNacimento.getText().toString());
        mUsuario.setCidade(autoCompleteTextViewCidade.getText().toString());
        mUsuario.setTipo_sanguineo(spinnerTipoSanguineo.getSelectedItem().toString());
        int radioButtonID = radioGroupSexo.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) radioGroupSexo.findViewById(radioButtonID);
        String selectedtext = (String) radioButton.getText();

        if (selectedtext == null){
            mUsuario.setGender("indefinido");
        }else{
            mUsuario.setGender(selectedtext);
        }


        if (!editando && FirebaseAuth.getInstance().getCurrentUser() == null) {
            //create user
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            showProgressDialog();
                            if (!task.isSuccessful()) {
                                hideProgressDialog();
                                Toast.makeText(SignupActivity.this, "Já existe um usuário cadastrado com esse e-mail" + task.getException(),
                                        Toast.LENGTH_SHORT).show();

                            } else {
                                mUsuario.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                adicionarUsuario();
                                startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                finish();
                            }
                        }
                    });
        }else{
            atualizarUsuario();
            finish();
        }
    }

    private boolean validaForm() {
        boolean ret = true;
        if (!editando && !Validador.validateNotNull(inputEmail, getString(R.string.val_email_empty))){
            ret = false;
        }else if (!editando && !Validador.validateNotNull(inputPassword, getString(R.string.val_senha_empty))){
            ret =  false;
        }else if (!Validador.validateNotNull(edtNome, getString(R.string.informe_o_nome))){
            ret =  false;
        }else if (!Validador.validateNotNull(edtSobrenome, getString(R.string.informe_o_sobrenome))){
            ret =  false;
        }else if (inputPassword.getText().length() < 6 && !editando ) {
            inputPassword.setError("A senha deve ter no minimo 6 Caracteres");
            ret = false;
        }else if (autoCompleteTextViewCidade == null) {
            autoCompleteTextViewCidade.setError("Ops! Esqueceu de informar o bairro do cliente");
            autoCompleteTextViewCidade.setFocusable(true);
            autoCompleteTextViewCidade.requestFocus();
            ret = false;
        }else if (!editando && !inputPassword.getText().toString().equals(inputPasswordConfirm.getText().toString())){
            inputPasswordConfirm.setError("As senhas não correspondem!");
            inputPasswordConfirm.requestFocus();
            ret =false;
        }else if (spinnerTipoSanguineo.getSelectedItem() == null){
            Toast.makeText(this, "Informe seu tipo sanguineo!", Toast.LENGTH_SHORT).show();
            spinnerTipoSanguineo.requestFocus();
            ret =false;
        }
        return ret;
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideProgressDialog();

    }

    @Override
    protected void onStart() {
        super.onStart();
        showProgressDialog();
    }

    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, -18);
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Light_Dialog_MinWidth, this, yy, mm, dd);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            return dialog;
        }

        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            populateSetDate(yy, mm + 1, dd);
        }
    }

    private void adicionarUsuario() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDatabase.child(mUsuario.getUid()).setValue(mUsuario.toMap());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void atualizarUsuario(){
        mDatabase.updateChildren(mUsuario.toMap());
    }


}
