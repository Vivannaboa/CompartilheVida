package br.com.compartilhevida.compartilhevida;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class NotificacaoActivity extends Activity {

    TextView titulo, mensagem;

    String title;
    String text;
    Button teste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacao);
        titulo = (TextView) findViewById(R.id.txt_titulo);
        mensagem = (TextView) findViewById(R.id.txt_mensagem);

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Dismiss Notification
        notificationmanager.cancel(0);

        // Retrive the data from MainActivity.java
        Intent i = getIntent();

        title = i.getStringExtra("title");
        text = i.getStringExtra("text");

        // Set the data into TextView
        titulo.setText(title);
        mensagem.setText(text);

    }


}
