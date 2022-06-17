package com.example.parstagram;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.parstagram.databinding.ActivityProfileBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private File photoFile;
    private String photoFileName = "photo.jpg";
    private static final String TAG = "ProfileActivity";



    private final int SPAN_COUNT = 3;
    private ActivityProfileBinding binding;
    private Button logoutButton;
    private ImageView profileImage;
    private BottomNavigationView bottomNavigationView;
    private RecyclerView rvProfilePosts;
    private List<Post> posts;
    private ProfilePostsAdapter adapter;

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
        rvProfilePosts = binding.rvProfilePosts;
        profileImage = binding.ivProfileImage;

        posts = new ArrayList<>();
        adapter = new ProfilePostsAdapter(posts, this);

        rvProfilePosts.setAdapter(adapter);
        rvProfilePosts.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));

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


        Glide.with(this)
                .load(ParseUser.getCurrentUser().getParseFile("profilePhoto").getUrl())
                .transform(new RoundedCorners(PostsAdapter.ViewHolder.PROFILE_RADIUS))
                .into(profileImage);

        queryUserPosts();
    }

    private void queryUserPosts() {
        // Query for all posts by current user
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        query.orderByDescending(Post.KEY_CREATED_AT);
        query.findInBackground((userPosts, e) -> {
            if (e != null) {
                e.printStackTrace();
                Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } else {

                for (int i = 0; i < userPosts.size(); i++) {
                    Log.i("ProfileActivity", "Post: " + userPosts.get(i).getDescription());
                }


                posts.addAll(userPosts);
                adapter.notifyDataSetChanged();
            }
        });
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void saveProfilePhoto(File photoFile) {
//        showProgressBar();
        ParseUser user = ParseUser.getCurrentUser();
        user.put("profilePhoto", new ParseFile(photoFile));
        user.saveInBackground(e -> {
            if (e != null) {
                e.printStackTrace();
                Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProfileActivity.this, "Profile photo saved", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void launchCamera(View view) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(ProfileActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                profileImage.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }
}