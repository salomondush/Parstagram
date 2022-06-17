package com.example.parstagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.parstagram.MainActivity;
import com.example.parstagram.Post;
import com.example.parstagram.PostsAdapter;
import com.example.parstagram.R;
import com.example.parstagram.databinding.ActivityMainBinding;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimelineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimelineFragment extends Fragment {

    private RecyclerView rvPosts;
    private PostsAdapter adapter;
    private List<Post> posts;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TimelineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TimelineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TimelineFragment newInstance(String param1, String param2) {
        TimelineFragment fragment = new TimelineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvPosts = view.findViewById(R.id.rvPosts);

        // initialize the array that will hold posts and create a PostsAdapter
        posts = new ArrayList<>();
        adapter = new PostsAdapter(posts, getContext());

        adapter.setOnUserClickListener(new PostsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                if (position != RecyclerView.NO_POSITION) {
                    Post post = posts.get(position);

                    // navigate to the user profile
                    ((MainActivity) requireActivity()).navigateToUserProfile(post.getUser());
                }
            }
        });

        adapter.setOnLikeClickListener(new PostsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                if (position != RecyclerView.NO_POSITION) {
                    // get the post at position and add user to the list of users who have liked the post
                    Post post = posts.get(position);

                    boolean isLiked = false;
                    List<ParseUser> likedUsers = post.getLikedUsers();
                    for (ParseUser user : likedUsers) {
                        if (Objects.equals(user.getObjectId(), ParseUser.getCurrentUser().getObjectId())) {
                            isLiked = true;
                        }
                    }

                    // check if user hasn't liked the post before
                    if (!isLiked) {
                        // add the user to the list of users who have liked the post
                        post.addLiker(ParseUser.getCurrentUser());
                    } else {
                        // remove the user from the list of users who have liked the post
                        post.removeLiker(ParseUser.getCurrentUser());
                    }
                    post.saveInBackground();
                    // update the adapter
                    adapter.notifyItemChanged(position);
                }
            }
        });

        rvPosts.setAdapter(adapter);
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        queryPosts();
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