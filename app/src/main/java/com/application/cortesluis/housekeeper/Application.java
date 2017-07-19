package com.application.cortesluis.housekeeper;

import net.danlew.android.joda.JodaTimeAndroid;

import io.paperdb.Paper;

/**
 * Created by luis_cortes on 7/16/17.
 */

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // For paper library
        Paper.init(this);

        JodaTimeAndroid.init(this);
    }
}
