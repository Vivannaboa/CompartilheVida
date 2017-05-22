package br.com.compartilhevida.compartilhevida.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vivan on 15/04/2017.
 */

public class Doacao {
    private String uid;
    private String autor;
    private Date dataDoacao;
    private String hemocentro;
    private boolean voluntaria;
    private Usuario mUser;

    public Doacao() {
    }

    public Doacao(String uid, String autor, Date dataDoacao, String hemocentro, boolean voluntaria) {
        this.uid = uid;
        this.autor = autor;
        this.dataDoacao = dataDoacao;
        this.hemocentro = hemocentro;
        this.voluntaria = voluntaria;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("autor", autor);
        result.put("datadoacao", dataDoacao);
        result.put("voluntaria", voluntaria);
        return result;
    }
    public Date getDataDoacao() {
        return dataDoacao;
    }

    public void setDataDoacao(Date dataDoacao) {
        this.dataDoacao = dataDoacao;
    }

    public String getHemocentro() {
        return hemocentro;
    }

    public void setHemocentro(String hemocentro) {
        this.hemocentro = hemocentro;
    }

    public boolean getVoluntaria() {
        return voluntaria;
    }

    public void setVoluntaria(boolean voluntaria) {
        this.voluntaria = voluntaria;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }
}
