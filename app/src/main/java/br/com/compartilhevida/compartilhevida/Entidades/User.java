package br.com.compartilhevida.compartilhevida.Entidades;

import android.content.Context;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {
    private static User instance;
    private Context context;

    private String uid;
    private String first_name;
    private String last_name;
    private String email;
    private String provider;
    private String urlPhoto;
    private String birthday;
    private String gender;
    private String tipoSanguineo;
    private String cidade;

    public static User getInstance(Context context) {
        if (instance == null)
            instance = new User(context);
        return instance;
    }

    private User(Context context) {
        this.context = context;

    }
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("first_name", first_name);
        result.put("last_name", last_name);
        result.put("email", email);
        result.put("urlPhoto",urlPhoto);
        result.put("gender", gender);
        result.put("provider",provider);
        result.put("birthday",birthday);
        result.put("tipoSanguineo", tipoSanguineo);
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
        return urlPhoto;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
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

    public String getTipoSanguineo() {
        return tipoSanguineo;
    }

    public void setTipoSanguineo(String tipoSanguineo) {
        this.tipoSanguineo = tipoSanguineo;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }
}