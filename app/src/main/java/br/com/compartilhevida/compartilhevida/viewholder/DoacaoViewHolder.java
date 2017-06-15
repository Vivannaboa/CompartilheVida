package br.com.compartilhevida.compartilhevida.viewholder;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.crash.FirebaseCrash;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import br.com.compartilhevida.compartilhevida.BaseActivity;
import br.com.compartilhevida.compartilhevida.R;
import br.com.compartilhevida.compartilhevida.models.Doacao;
import br.com.compartilhevida.compartilhevida.models.Post;
import br.com.compartilhevida.compartilhevida.util.CircleTransform;

public class DoacaoViewHolder extends RecyclerView.ViewHolder {
    public TextView txtDoacaoHemocentro;
    public TextView txtProximaDoacao;
    public TextView txtDataDoacao;
    public TextView txtTipoDoacao;
    private Context context;

    public DoacaoViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        txtDoacaoHemocentro = (TextView) itemView.findViewById(R.id.txt_doacao_hemocentro);
        txtProximaDoacao = (TextView) itemView.findViewById(R.id.txt_proxima_doacao);
        txtDataDoacao = (TextView) itemView.findViewById(R.id.txt_data_doacao);
        txtTipoDoacao = (TextView) itemView.findViewById(R.id.txt_tipo_doacao);
    }

    public void bindToPost(Doacao doacao) {
        txtDataDoacao.setText("Data da doação : " + doacao.getDataDoacao());
        txtDoacaoHemocentro.setText(doacao.getHemocentro());
        if (doacao.isVoluntaria()){
            txtTipoDoacao.setText("Essa doação foi voluntária");
        }else{
            txtTipoDoacao.setText("Essa doação foi para " +doacao.getFavorecido());
        }
        // homens a 03 meses e as mulheres a cada 04
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        try {
            date = BaseActivity.stringToDate( doacao.getDataDoacao());
        } catch (ParseException e) {
            FirebaseCrash.report(e);
        }
        if (date != null) {
            cal.setTime(date);
            if (doacao.getSexoDoador().equalsIgnoreCase("Masculino")) {
                cal.add(cal.DAY_OF_MONTH, + 90);
            } else {
                cal.add(cal.DAY_OF_MONTH, + 120);
            }
        }
        txtProximaDoacao.setText("Próxima doação pode ser realizada a partir do dia:" + date.toString());
    }


}
