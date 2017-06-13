package br.com.compartilhevida.compartilhevida;

import android.os.Bundle;
import android.support.v4.view.ScrollingView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

public class DoacaoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doacao);
        Toolbar tlb = (Toolbar) findViewById(R.id.toolbarDoacao);
        setSupportActionBar(tlb);
        getSupportActionBar().setTitle("Registro de Doação" );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_edit,menu);
        return super.onCreateOptionsMenu(menu);
    }
}
