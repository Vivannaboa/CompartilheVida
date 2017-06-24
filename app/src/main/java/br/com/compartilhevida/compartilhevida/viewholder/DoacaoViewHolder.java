package br.com.compartilhevida.compartilhevida.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import br.com.compartilhevida.compartilhevida.BaseActivity;
import br.com.compartilhevida.compartilhevida.R;
import br.com.compartilhevida.compartilhevida.models.Doacao;

public class DoacaoViewHolder extends RecyclerView.ViewHolder implements Toolbar.OnMenuItemClickListener {
    public TextView txtProximaDoacao;
    public TextView txtDataDoacao;
    public TextView txtTipoDoacao;
    public Toolbar toolbarCardDoacao;
    private Context context;


    public DoacaoViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        txtProximaDoacao = (TextView) itemView.findViewById(R.id.txt_proxima_doacao);
        txtDataDoacao = (TextView) itemView.findViewById(R.id.txt_data_doacao);
        txtTipoDoacao = (TextView) itemView.findViewById(R.id.txt_tipo_doacao);
        toolbarCardDoacao =(Toolbar)itemView.findViewById(R.id.toolbar_card_doacao);
        toolbarCardDoacao.inflateMenu(R.menu.action_shared);


    }

    public void bindToPost(Doacao doacao) {
        txtDataDoacao.setText("Data da doação : " + BaseActivity.soDateToString(new Date(doacao.getDataDoacao())));
        toolbarCardDoacao.setSubtitle(doacao.getHemocentro());
        toolbarCardDoacao.setOnMenuItemClickListener(this);

        if (doacao.isVoluntaria()){
            txtTipoDoacao.setText("Essa doação foi voluntária");
        }else{
            txtTipoDoacao.setText("Essa doação foi para " +doacao.getFavorecido());
        }
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
        txtProximaDoacao.setText("Próxima doação pode ser realizada a partir do dia:" + BaseActivity.soDateToString(cal.getTime()));
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getItemId() == R.id.compartilhar){
            Toast.makeText(context, "Implementar o compartilhar", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
