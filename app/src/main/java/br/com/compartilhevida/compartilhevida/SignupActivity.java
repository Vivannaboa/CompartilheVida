package br.com.compartilhevida.compartilhevida;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import br.com.compartilhevida.compartilhevida.Entidades.User;
import br.com.compartilhevida.compartilhevida.Utilitarios.Validador;

public class SignupActivity extends BaseActivity  {
    private EditText inputEmail, inputPassword, inputPasswordConfirm, edtNome, edtSobrenome;
    private RadioGroup radioGroupSexo;
    private FirebaseAuth auth;
    private Spinner spinnerTipoSanguineo;
    static EditText dataDeNacimento;
    private AutoCompleteTextView autoCompleteTextViewCidade;
    private ArrayAdapter<String> autoComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        recuperacomponentes();

        //Nothing special, create database reference.
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        //Create a new ArrayAdapter with your context and the simple layout for the dropdown menu provided by Android
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
        dataDeNacimento.setText(month + "/" + day + "/" + year);
    }

    public void clickEntrar(View v){
        showProgressDialog();

        if (!validaForm()){
            return;
        }

        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        User user = new User();
        user.setFirst_name(edtNome.getText().toString());
        user.setLast_name(edtSobrenome.getText().toString());
        user.setEmail(email);
        user.setProvider("email");
        user.setBirthday(dataDeNacimento.getText().toString());
        user.setCidade(autoCompleteTextViewCidade.getText().toString());
        user.setTipoSanguineo(spinnerTipoSanguineo.getSelectedItem().toString());
        if (!radioGroupSexo.isSelected()){
            user.setGender("indefinido");
        }else{
            int radioButtonID = radioGroupSexo.getCheckedRadioButtonId();
            RadioButton radioButton = (RadioButton) radioGroupSexo.findViewById(radioButtonID);
            String selectedtext = (String) radioButton.getText();
            user.setGender(selectedtext);
        }


        //create user
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        showProgressDialog();
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            startActivity(new Intent(SignupActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
    }

    private boolean validaForm() {
        boolean ret = true;
        if (!Validador.validateNotNull(inputEmail, getString(R.string.val_email_empty))){
            hideProgressDialog();
            ret = false;
        }
        if (!Validador.validateNotNull(inputPassword, getString(R.string.val_senha_empty))){
            hideProgressDialog();
            ret =  false;
        }
        if (!Validador.validateNotNull(edtNome, getString(R.string.informe_o_nome))){
            hideProgressDialog();
            ret =  false;
        }
        if (!Validador.validateNotNull(edtSobrenome, getString(R.string.informe_o_sobrenome))){
            hideProgressDialog();
            ret =  false;
        }
        if (inputPassword.getText().length() < 6) {
            inputPassword.setError("A senha deve ter no minimo 6 Caracteres");
            ret = false;
        }
        if (autoCompleteTextViewCidade == null) {
            autoCompleteTextViewCidade.setError("Ops! Esqueceu de informar o bairro do cliente");
            autoCompleteTextViewCidade.setFocusable(true);
            autoCompleteTextViewCidade.requestFocus();
            ret = false;
        }
        if (inputPassword.getText() != inputPasswordConfirm.getText()){
            inputPasswordConfirm.setError("As senhas não correspondem!");
            inputPasswordConfirm.requestFocus();
        }
        if (!spinnerTipoSanguineo.isSelected()){
            Toast.makeText(this, "Informe seu tipo snguineo!", Toast.LENGTH_SHORT).show();
            spinnerTipoSanguineo.requestFocus();
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


}
