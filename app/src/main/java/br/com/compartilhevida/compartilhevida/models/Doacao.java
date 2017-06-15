package br.com.compartilhevida.compartilhevida.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vivan on 15/04/2017.
 */

public class Doacao {
    private String uid;
    private String autor;
    private String dataDoacao;
    private String hemocentro;
    private boolean voluntaria;
    private String sexoDoador;
    private String favorecido;

    public Doacao() {
    }

    public Doacao(String uid, String autor, String dataDoacao, String hemocentro, boolean voluntaria) {
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
        result.put("dataDoacao", dataDoacao);
        result.put("hemocentro",hemocentro);
        result.put("voluntaria", voluntaria);
        result.put("sexoDoador",sexoDoador);
        result.put("favorecido", favorecido);
        return result;
    }
    public String getDataDoacao() {
        return dataDoacao;
    }

    public void setDataDoacao(String dataDoacao) {
        this.dataDoacao = dataDoacao;
    }

    public String getHemocentro() {
        return hemocentro;
    }

    public void setHemocentro(String hemocentro) {
        this.hemocentro = hemocentro;
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

    public boolean isVoluntaria() {
        return voluntaria;
    }

    public String getSexoDoador() {
        return sexoDoador;
    }

    public void setSexoDoador(String sexoDoador) {
        this.sexoDoador = sexoDoador;
    }

    public String getFavorecido() {
        return favorecido;
    }

    public void setFavorecido(String favorecido) {
        this.favorecido = favorecido;
    }
}
