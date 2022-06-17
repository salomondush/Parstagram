package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.example.parstagram.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private BottomNavigationView bottomNavigationView;
    private RecyclerView rvPosts;
    private PostsAdapter adapter;
    private List<Post> posts;

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
        rvPosts = binding.rvPosts;

        // initialize the array that will hold posts and create a PostsAdapter
        posts = new ArrayList<>();
        adapter = new PostsAdapter(posts, this);

        rvPosts.setAdapter(adapter);
        rvPosts.setLayoutManager(new LinearLayoutManager(this));


        bottomNavigationView.setOnItemSelectedListener( item -> {
            switch (item.getItemId()) {
                case R.id.userProfile:
                    // set current item as selected
                    startActivity(new Intent(this, ProfileActivity.class));
                    // set is checked to true for the selected icon
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.addPost:
                    startActivity(new Intent(this, CreatePostActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
            }

            return true;
        });

        bottomNavigationView.setSelectedItemId(R.id.home);

        queryPosts();
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void queryPosts() {
        // Construct query to execute
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER); // include referenced user
        // only get 20 most recent posts
        query.setLimit(20);
        // Execute query to fetch all posts from Parse asynchronously,
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> incomingPosts, ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }

                // arrange posts in chronological order
                for (int i = incomingPosts.size() - 1; i >= 0; --i) {
                    posts.add(incomingPosts.get(i));
                }

                adapter.notifyDataSetChanged();
            }
        });
    }

    }