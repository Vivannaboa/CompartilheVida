package br.com.compartilhevida.compartilhevida.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vivan on 15/06/2017.
 */

public class Cartilha {
    private String uid;
    private String titulo;
    private String texto;

    public Cartilha(String uid, String titulo, String texto) {
        this.uid = uid;
        this.titulo = titulo;
        this.texto = texto;
    }

    public Cartilha() {
    }


    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("autor", titulo);
        result.put("dataDoacao", texto);
        return result;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
