package com.application.cortesluis.housekeeper.data;

import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

/**
 * Created by luis_cortes on 7/2/17.
 */

public class User {
    private String name;
    private String email;
    private String uid;
    private UserType type;

    public User() {
//        this.id = UUID.randomUUID();
    }

    public User(String name, String email, String uid) {
        this.name = name;
        this.email = email;
        this.uid = uid;
    }

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

}
