package br.com.compartilhevida.compartilhevida.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vivan on 18/07/2017.
 */

public class Topico {
    private String topico;

    public Topico() {
    }

    public Topico(String topico) {
        this.topico = topico;
    }

    public String getTopico() {
        return topico;
    }

    public void setTopico(String topico) {
        this.topico = topico;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("topico", topico);
        return result;
    }
}
