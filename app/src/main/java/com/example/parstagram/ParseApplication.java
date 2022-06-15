package com.example.parstagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);

        // Initialize the Parse SDK.
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("nwKj4KMRnckXoW2hYW1Fdwhdu7G6pkfeVgbgO6ng")
                .clientKey("OyNwp3wJNVM9yFiroZPTncwbSSHROE0AB0d1GrgO")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
