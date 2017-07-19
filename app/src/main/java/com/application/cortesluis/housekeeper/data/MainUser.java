package com.application.cortesluis.housekeeper.data;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luis_cortes on 7/9/17.
 */

public class MainUser {
    private static MainUser mainUser;
    private List<User> userOne;
    private static Context context;

    public static synchronized MainUser get(Context context) {
        if (mainUser == null) {
            return new MainUser(context.getApplicationContext());
        }
        return mainUser;
    }

    private MainUser(Context context) {
        this.context = context.getApplicationContext();
        userOne = new ArrayList<>();
    }

    public void addUser(User user) {
        userOne.add(user);
    }

    public List<User> getUser() {
        return this.userOne;
    }
}
