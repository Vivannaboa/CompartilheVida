package br.com.compartilhevida.compartilhevida;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import br.com.compartilhevida.compartilhevida.models.Doacao;
import br.com.compartilhevida.compartilhevida.models.Hemocentro;
import br.com.compartilhevida.compartilhevida.models.Usuario;
import br.com.compartilhevida.compartilhevida.util.Validador;

public class DoacaoActivity extends BaseActivity implements
        CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private static final String TAG = "DoacaoActivity";
    private Switch mSwitch;
    private static EditText edtDataDoacao;
    private AutoCompleteTextView autcHemocentro;
    private EditText edtFavorecido;
    private AppBarLayout appbarLayout;
    private FloatingActionButton floatingActionButton;
    private MenuItem btnSalvar;
    static Calendar calendar = Calendar.getInstance(Locale.getDefault());
    //adapter
    private ArrayAdapter<String> autoComplete;
    private List<Hemocentro> hemocentroList = new ArrayList<>();
    //base de dados
    DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doacao);
        Toolbar tlb = (Toolbar) findViewById(R.id.toolbarDoacao);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setSupportActionBar(tlb);
        getSupportActionBar().setTitle("Registro de Doação");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        recuperaComponentes();
        listnerAutoComplet();
    }

    private void listnerAutoComplet() {
        autoComplete = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        mDatabase.child("hemocentros").addListenerForSingleValueEvent(new ValueEventListener() {
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
        autcHemocentro.setAdapter(autoComplete);
    }

    private void recuperaComponentes() {
        edtDataDoacao = (EditText) findViewById(R.id.edt_data_doacao);
        edtDataDoacao.setText(soDateToString(null));
        calendar.setTime(new Date());
        autcHemocentro = (AutoCompleteTextView) findViewById(R.id.auct_hemocentro);
        edtFavorecido = (EditText) findViewById(R.id.edt_favorecido);
        mSwitch = (Switch) findViewById(R.id.mSwitch);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floating_button);
        appbarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        floatingActionButton.setOnClickListener(this);
        mSwitch.setOnCheckedChangeListener(this);
        edtDataDoacao.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.action_edit, menu);
        btnSalvar = (MenuItem) menu.findItem(R.id.salvar);
        //esconder botão do menu
        appbarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float percentage = ((float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange());
                if (percentage >= 0.7404844) {
                    btnSalvar.setVisible(true);
                } else {
                    btnSalvar.setVisible(false);
                }
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            findViewById(R.id.layout_favorecido).setVisibility(View.GONE);
        } else {
            findViewById(R.id.layout_favorecido).setVisibility(View.VISIBLE);
        }
    }

    private static void populateSetDate(int year, int month, int day) {
        calendar.set(year,month,day);
        edtDataDoacao.setText(soDateToString(calendar.getTime()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floating_button:
                Log.i(TAG, "Clicou no floating");
                //floating booton
                if (validarDados()) {
                    writeNewDoacao();
                    setResult(Activity.RESULT_OK);
                    finish();
                }
                break;
            case R.id.edt_data_doacao:
                Log.i(TAG, "Clicou na data da doação");
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
                break;
            default:
                break;
        }
    }

    private void writeNewDoacao() {
        try {
            String key = mDatabase.child("doacoes").push().getKey();
            calendar.getTime().getTime();
            Doacao doacao = new Doacao();
            doacao.setUid(key);
            doacao.setAutor(Usuario.getInstance().getFirst_name());
            doacao.setDataDoacao(new Timestamp(calendar.getTimeInMillis()).getTime());
            doacao.setHemocentro(autcHemocentro.getText().toString());
            doacao.setVoluntaria(mSwitch.isChecked());
            doacao.setFavorecido(edtFavorecido.getText().toString());
            doacao.setSexoDoador(Usuario.getInstance().getGender());

            // homens a 03 meses e as mulheres a cada 04
            Calendar cal = Calendar.getInstance();
            Date date =  new Date(doacao.getDataDoacao());
            if (date != null) {
                cal.setTime(date);
                if (doacao.getSexoDoador() !=null && doacao.getSexoDoador().equalsIgnoreCase("Masculino")) {
                    cal.add(cal.DAY_OF_MONTH, + 90);
                } else {
                    cal.add(cal.DAY_OF_MONTH, + 120);
                }
            }
            doacao.setProximaDoacao(cal.getTime().getTime());

            Map<String, Object> objectMap = doacao.toMap();

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/doacoes/" + key, objectMap);
            childUpdates.put("/user-doacoes/" + getUid() + "/" + key, objectMap);
            mDatabase.updateChildren(childUpdates);
            VerificaCadastroHemocentro(doacao.getHemocentro());
            Toast.makeText(this, "Doação registrada com sucesso!", Toast.LENGTH_SHORT).show();

            FirebaseMessaging.getInstance().subscribeToTopic(doacao.getHemocentro());
        } catch (Exception e) {
            FirebaseCrash.report(e);
        }
    }

    private boolean validarDados() {
        boolean ret = true;
        if (!Validador.validateNotNull(autcHemocentro, "Informe o hemocentro que você doou sangue!")) {
            ret = false;
        }
        if (!mSwitch.isChecked()) {
            if (!Validador.validateNotNull(edtFavorecido, "Informe para quem você foi doar sangue!")) {
                ret = false;
            }
        }
        return ret;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.salvar:
                Log.i(TAG, "Clicou em salvar");
                if (validarDados()) {
                    writeNewDoacao();
                    setResult(Activity.RESULT_OK);
                    finish();
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void onDateSet(DatePicker view, int year, int month, int day) {
            populateSetDate(year, month, day);
        }
    }

    private void VerificaCadastroHemocentro(String hemocentro){
        boolean cadastrarHemocentro = true;
        for (Hemocentro item: hemocentroList) {
            if (item.getNome().toString().equalsIgnoreCase(hemocentro.toString())){
                cadastrarHemocentro = false;
                return;
            }
        }
        if (cadastrarHemocentro){
            String keyHemocentro = mDatabase.child("hemocentros").push().getKey();
            Hemocentro hm = new Hemocentro();
            hm.setNome(hemocentro);
            Map<String, Object> hemocentroMap = hm.toMap();
            //mDatabase.child("hemocentros").setValue(hemocentroMap);
            Map<String, Object> childUpdatesHemocentros = new HashMap<>();
            childUpdatesHemocentros.put("/hemocentros/" + keyHemocentro, hemocentroMap);
            mDatabase.updateChildren(childUpdatesHemocentros);
        }
    }
}