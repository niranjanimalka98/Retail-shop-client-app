package com.jkdesigns.stsproject;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class PharmWare extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
