package br.com.compartilhevida.compartilhevida.models;

import com.google.firebase.database.IgnoreExtraProperties;

// [START comment_class]
@IgnoreExtraProperties
public class Comentario {

    public String uid;
    public String autor;
    public String text;

    public Comentario() {
        // Default constructor required for calls to DataSnapshot.getValue(Comentario.class)
    }

    public Comentario(String uid, String author, String text) {
        this.uid = uid;
        this.autor = author;
        this.text = text;
    }

}
// [END comment_class]
