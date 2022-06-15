package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.parstagram.databinding.ActivityProfileBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;
    Button logoutButton;
    BottomNavigationView bottomNavigationView;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        logoutButton = binding.logoutButton;
        bottomNavigationView = binding.bottomNavigation;

        logoutButton.setOnClickListener(v -> {
            ParseUser.logOutInBackground(new LogOutCallback() {
                 @Override
                 public void done(ParseException e) {
                        if (e != null) {
                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                            Toast.makeText(ProfileActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                        }
                 }
            });
        });

        bottomNavigationView.setOnItemSelectedListener( item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    // set current item as selected
                    startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                    // set is checked to true for the selected icon
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.addPost:
                    startActivity(new Intent(ProfileActivity.this, CreatePostActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
            }

            return true;
        });

        bottomNavigationView.setSelectedItemId(R.id.userProfile);
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}