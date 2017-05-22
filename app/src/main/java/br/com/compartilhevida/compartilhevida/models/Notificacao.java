package br.com.compartilhevida.compartilhevida.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vivan on 15/04/2017.
 */

public class Notificacao {
    private String uid;
    private String autor;
    private String mensagem;
    private boolean visualizada;
    private Usuario mUser;

    public Notificacao() {
    }

    public Notificacao(String uid, String autor, String mensagem, boolean visualizada) {
        this.uid = uid;
        this.autor = autor;
        this.mensagem = mensagem;
        this.visualizada = visualizada;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("autor", autor);
        result.put("mensagem", mensagem);
        result.put("visualizada", visualizada);
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

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public boolean isVisualizada() {
        return visualizada;
    }

    public void setVisualizada(boolean visualizada) {
        this.visualizada = visualizada;
    }
}
