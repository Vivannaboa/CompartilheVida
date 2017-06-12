package br.com.compartilhevida.compartilhevida;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DoacaoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doacao);
        getSupportActionBar().setTitle("Registro de Doação" );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
