package br.com.compartilhevida.compartilhevida;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import br.com.compartilhevida.compartilhevida.models.Topico;
import br.com.compartilhevida.compartilhevida.models.Usuario;


public class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(getString(R.string.carregando));
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public Uri getUrlPhoto() {
        return FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
    }
    public String getDisplayNome(){
        return FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    }

    @Nullable
    public static Drawable loadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date getDateTime() {
        return new Date();
    }

    public static String dateToString(Date data) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        if (data == null) {
            data = getDateTime();
        }
        return dateFormat.format(data);
    }

    public static String soDateToString(Date data) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd/MM/yyyy", Locale.getDefault());
        dateFormat.setLenient(false);
        if (data == null) {
            data = getDateTime();
        }
        return dateFormat.format(data);
    }

    public static Date stringToDate(String string) throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        if (string == null) {
            return null;
        }
        return dateFormat.parse(string);
    }


    public static Calendar timestampToCalendar(Timestamp timestamp) {
        //"2015-05-28 12:45:59";
//        Timestamp timestamp = Timestamp.valueOf(text);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.getTime());
        return calendar;
    }

    public static Timestamp calendarToTimestamp(Calendar calendar){
        return new Timestamp(calendar.getTimeInMillis());
    }





    public boolean isOnline() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo() != null && manager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public void enviarNotificacao(String titulo,String mensagem, String topico){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications");
        Map notification = new HashMap<>();
        notification.put("titulo", titulo);
        notification.put("topico",topico);
        notification.put("mensagem", mensagem);

        reference.push().setValue(notification);
    }
    public String getStrTopicosUF(){
        Usuario mUser = Usuario.getInstance();
        return "'"+ mUser.getCidade().substring(mUser.getCidade().indexOf("-")+2) + "' in topics ";
    }
    public String getStrTipoSanguineoTopico(String tipo){
        return "'"+tipo.replace("+","_plus") +  "' in topics";
    }
    private String strTopicosUsuario(){
        Usuario mUser = Usuario.getInstance();
        String ret = null;
        boolean i = true;
        for (Topico item: mUser.getTopicos()) {
            if (i){
                ret = "'" + item.getTopico() + "' in topics";
                i=false;
            }else {
                ret = ret +" && '" + item.getTopico() + "' in topics";
            }
        }
        return ret.toString();
    }
}
