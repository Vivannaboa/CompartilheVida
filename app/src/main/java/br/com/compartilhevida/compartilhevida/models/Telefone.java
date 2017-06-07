package br.com.compartilhevida.compartilhevida.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vivan on 06/06/2017.
 */

class Telefone {
    private String fone;

    public Telefone() {
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("fone", fone);
        return result;
    }

    public String getFone ()
    {
        return fone;
    }

    public void setFone (String fone)
    {
        this.fone = fone;
    }
}
