package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.example.parstagram.databinding.ActivityMainBinding;
import com.example.parstagram.fragments.ComposeFragment;
import com.example.parstagram.fragments.ProfileFragment;
import com.example.parstagram.fragments.TimelineFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final FragmentManager fragmentManager = getSupportFragmentManager();
    public static final String USER_ID = "userId";
    private BottomNavigationView bottomNavigationView;
    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        bottomNavigationView = binding.bottomNavigation;


        bottomNavigationView.setOnItemSelectedListener( item -> {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.userProfile:
                    fragment = new ProfileFragment();
                    // pass current user ID to profile fragment
                    Bundle bundle = new Bundle();
                    bundle.putString(USER_ID, ParseUser.getCurrentUser().getObjectId());
                    fragment.setArguments(bundle);
                    break;
                case R.id.addPost:
                    // todo: udpate fragment
                    fragment = new ComposeFragment();
                    break;
                case R.id.home:
                default:
                    fragment = new TimelineFragment();
                    break;
            }
            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
            return true;
        });

        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void navigateToUserProfile(ParseUser user) {
        // navigate to the ProfileFragment
        Fragment fragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID, user.getObjectId());
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
    }
}