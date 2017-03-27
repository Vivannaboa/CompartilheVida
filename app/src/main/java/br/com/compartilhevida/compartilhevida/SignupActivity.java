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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

import br.com.compartilhevida.compartilhevida.Utilitarios.Validador;

public class SignupActivity extends BaseActivity implements View.OnTouchListener {
    private EditText inputEmail, inputPassword, inputPasswordConfirm;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private RadioGroup radioGroupSexo;
    private FirebaseAuth auth;
    static EditText dataDeNacimento;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        recuperacomponentes();
    }

    private void recuperacomponentes() {
        dataDeNacimento = (EditText) findViewById(R.id.editTextDataNacimento);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputPasswordConfirm = (EditText) findViewById(R.id.confirm_password);
        radioGroupSexo = (RadioGroup) findViewById(R.id.radioGroupSexo) ;

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

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
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (!Validador.validateNotNull(inputEmail, getString(R.string.val_email_empty))){
            hideProgressDialog();
            return;
        }
        if (!Validador.validateNotNull(inputPassword, getString(R.string.val_senha_empty))){
            hideProgressDialog();
            return;
        }


        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        //create user
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
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

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.editTextDataNacimento:
//                DialogFragment newFragment = new DatePickerFragment();
//                newFragment.show(getFragmentManager(), "datePicker");

                break;

            default:
                break;
        }
        return true;
    }

    //    public static class DatePickerFragment extends DialogFragment
//            implements DatePickerDialog.OnDateSetListener {
//
//        @RequiresApi(api = Build.VERSION_CODES.N)
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            final Calendar calendar = Calendar.getInstance();
//            int yy = calendar.get(Calendar.YEAR);
//            int mm = calendar.get(Calendar.MONTH);
//            int dd = calendar.get(Calendar.DAY_OF_MONTH);
//            return new DatePickerDialog(getActivity(), this, yy, mm, dd);
//        }
//
//        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
//            populateSetDate(yy, mm + 1, dd);
//        }
//    }
//    public void populateSetDate(int year, int month, int day) {
//        mEdit = (EditText)findViewById(R.id.editText1);
//        mEdit.setText(month+"/"+day+"/"+year);
//    }

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
