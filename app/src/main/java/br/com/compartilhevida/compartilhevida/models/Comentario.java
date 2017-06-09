package br.com.compartilhevida.compartilhevida.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Comentario {
    private String uid;
    private String autor;
    public String text;
    private String urlFoto;


    public Comentario() {
    }

    public Comentario(String uid, String author, String text, String urlFoto) {
        this.uid = uid;
        this.autor = author;
        this.text = text;
        this.urlFoto = urlFoto;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("autor", autor);
        result.put("text", text);
        result.put("urlFoto", urlFoto);
        return result;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }
}

