package br.com.compartilhevida.compartilhevida.models;

import android.content.Context;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Usuario {
    private static Usuario instance;

    private String uid;
    private String first_name;
    private String last_name;
    private String email;
    private String provider;
    private String url_photo;
    private String birthday;
    private String gender;
    private String tipo_sanguineo;
    private String cidade;
    private boolean recebeNotificcacao;

    public static Usuario getInstance() {
        if (instance == null)
            instance = new Usuario();
        return instance;
    }

    public Usuario() {
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("first_name", first_name);
        result.put("last_name", last_name);
        result.put("email", email);
        result.put("url_photo",url_photo);
        result.put("gender", gender);
        result.put("provider",provider);
        result.put("birthday",birthday);
        result.put("tipo_sanguineo", tipo_sanguineo);
        result.put("cidade", cidade);
        return result;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getUrlPhoto() {
        return url_photo;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.url_photo = urlPhoto;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public void setTipo_sanguineo(String tipo_sanguineo) {
        this.tipo_sanguineo = tipo_sanguineo;
    }
    public String getTipo_sanguineo() {
        return tipo_sanguineo;
    }
    public boolean isRecebeNotificcacao() {
        return recebeNotificcacao;
    }

    public void setRecebeNotificcacao(boolean recebeNotificcacao) {
        this.recebeNotificcacao = recebeNotificcacao;
    }
}