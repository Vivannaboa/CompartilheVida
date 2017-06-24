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
    private String titulo;
    private String mensagem;
    private String tipo;
    private String tipo_sanguineo;
    private String hemocentro;
    private String data_limite_doacao;
    private String favorecido;
    private int coracaoCount = 0;
    private int comentariosCont =0 ;
    private Map<String, Boolean> coracao = new HashMap<>();
    private Usuario mUser;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String uid, String autor, String titulo, String mensagem, String urlFoto, String tipo_sanguineo, String hemocentro,String data_limite_doacao,String favorecido,String tipo) {
        this.uid = uid;
        this.autor = autor;
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.urlFoto = urlFoto;
        this.tipo = tipo;
        this.tipo_sanguineo=tipo_sanguineo;
        this.hemocentro = hemocentro;
        this.data_limite_doacao =data_limite_doacao;
        this.favorecido=favorecido;
    }

    public Post(String uid, String autor,  String titulo, String mensagem,String urlFoto,String tipo) {
        this.uid = uid;
        this.autor = autor;
        this.urlFoto = urlFoto;
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.tipo = tipo;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("autor", autor);
        result.put("titulo", titulo);
        result.put("mensagem", mensagem);
        result.put("coracaoCount", coracaoCount);
        result.put("coracao", coracao);
        result.put("urlFoto", urlFoto);
        result.put("comentariosCont", comentariosCont);
        result.put("hemocentro",hemocentro);
        result.put("favorecido",favorecido);
        result.put("data_limite_doacao",data_limite_doacao);
        result.put("tipo",tipo);
        result.put("tipo_sanguineo",tipo_sanguineo);
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


    public String getMensagem() {
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
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Usuario getmUser() {
        return mUser;
    }

    public void setmUser(Usuario mUser) {
        this.mUser = mUser;
    }

    public int getComentariosCont() {
        return comentariosCont;
    }

    public void setComentariosCont(int comentariosCont) {
        this.comentariosCont = comentariosCont;
    }

    public String getTipo_sanguineo() {
        return tipo_sanguineo;
    }

    public void setTipo_sanguineo(String tipo_sanguineo) {
        this.tipo_sanguineo = tipo_sanguineo;
    }

    public String getHemocentro() {
        return hemocentro;
    }

    public void setHemocentro(String hemocentro) {
        this.hemocentro = hemocentro;
    }

    public String getData_limite_doacao() {
        return data_limite_doacao;
    }

    public void setData_limite_doacao(String data_limite_doacao) {
        this.data_limite_doacao = data_limite_doacao;
    }

    public String getFavorecido() {
        return favorecido;
    }

    public void setFavorecido(String favorecido) {
        this.favorecido = favorecido;
    }
}