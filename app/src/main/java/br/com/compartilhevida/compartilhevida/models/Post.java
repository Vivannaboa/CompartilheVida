package br.com.compartilhevida.compartilhevida.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vivan on 20/03/2017.
 */

@IgnoreExtraProperties
public class Post {
    private String uid;
    private String autor;
    private String urlFoto;
    private String mensagem;
    private String tipo;
    private int coracaoCount = 0;
    private Map<String, Boolean> coracao = new HashMap<>();

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String uid, String autor, String title, String mensagem, String urlFoto, String tipo) {
        this.uid = uid;
        this.autor = autor;
        this.mensagem = mensagem;
        this.urlFoto = urlFoto;
        this.tipo = tipo;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("autor", autor);
        result.put("mensagem", mensagem);
        result.put("coracaoCount", coracaoCount);
        result.put("coracao", coracao);
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

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }


    public String getBody() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public int getCoracaoCount() {
        return coracaoCount;
    }

    public void setCoracaoCount(int coracaoCount) {
        this.coracaoCount = coracaoCount;
    }

    public Map<String, Boolean> getCoracao() {
        return coracao;
    }

    public void setCoracao(Map<String, Boolean> coracao) {
        this.coracao = coracao;
    }
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}