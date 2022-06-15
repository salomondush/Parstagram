package com.example.parstagram;

import com.parse.ParseClassName;
import com.parse.ParseUser;

@ParseClassName("User")

public class User extends ParseUser {
    public static final String KEY_USERNAME = "username";
}
