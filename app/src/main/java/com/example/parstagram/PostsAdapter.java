package com.example.parstagram;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.parstagram.databinding.ItemPostBinding;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder>{
    private List<Post> posts;
    private Context context;
    private ItemPostBinding binding;
    private OnItemClickListener userClickListener;
    private OnItemClickListener likeClickListener;
    private OnItemClickListener commentClickListener;




    public PostsAdapter(List<Post> posts, Context context) {
        this.posts = posts;
        this.context = context;
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnUserClickListener(OnItemClickListener listener) {
        this.userClickListener = listener;
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnLikeClickListener(OnItemClickListener listener) {
        this.likeClickListener = listener;
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnCommentClickListener(OnItemClickListener listener) {
        this.commentClickListener = listener;
    }

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    @NonNull
    @Override
    public PostsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemPostBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public static final String TAG = "PostsAdapter";
        public static final int TIME_THRESHOLD = 1;
        public static final int PROFILE_RADIUS = 100;
        private static final int SECOND_MILLIS = 1000;
        private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
        private static final long YEAR_MILLIS = 365L * DAY_MILLIS;

        private TextView tvUsername;
        private TextView captionName;
        private TextView createdAt;
        private ImageView ivImage;
        private ToggleButton likeButton;
        private ImageButton commentButton;

        private ImageView ivProfileImage;
        private TextView tvDescription;
        private TextView postNumLikes;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsername = binding.tvUsername;
            ivImage = binding.ivImage;
            tvDescription = binding.tvDescription;
            captionName = binding.captionUsername;
            ivProfileImage = binding.ivProfileImage;
            createdAt = binding.createdAt;
            ivProfileImage = binding.ivProfileImage;
            likeButton = binding.likeButton;
            commentButton = binding.commentButton;
            postNumLikes = binding.postNumLikes;


            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    likeClickListener.onItemClick(itemView, getAdapterPosition());
                }
            });

            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commentClickListener.onItemClick(itemView, getAdapterPosition());
                }
            });

            captionName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userClickListener.onItemClick(itemView, getAdapterPosition());
                }
            });


            tvUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userClickListener.onItemClick(itemView, getAdapterPosition());
                }
            });

            ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userClickListener.onItemClick(itemView, getAdapterPosition());
                }
            });
        }

        public void bind(Post post) {
            // Bind the post data to the view elements
            tvDescription.setText(post.getDescription());
            tvUsername.setText(post.getUser().getUsername());
            captionName.setText(post.getUser().getUsername());
            createdAt.setText(getRelativeTimeAgo((post.getCreatedAt())));
            ParseFile image = post.getImage();
            ParseFile profileImage = post.getUser().getParseFile("profilePhoto");


            // get the likedUsers array from the post
            List<String> likedUsers = post.getLikedUsers();
            // check if current user has liked this post
            // set the button to checked
            // set the button to unchecked
            likeButton.setChecked(likedUsers.contains(ParseUser.getCurrentUser().getObjectId()));

            postNumLikes.setText(String.valueOf(post.getLikedUsers().size()));

            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivImage);
            } else {
                Glide.with(context).load(R.drawable.post_placeholder).into(ivImage);
            }

            // load placeholder image for now
            if(profileImage != null) {
                Glide.with(context).load(profileImage.getUrl())
                        .circleCrop()
                        .into(ivProfileImage);
            } else {
                Log.i("PostsAdapter", "profileImage is null");
            }
        }

        public  String getRelativeTimeAgo(Date date) {
            long  diff  = (long) Math.floor((new Date()).getTime() - date.getTime());

            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " minutes ago";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " hours ago";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " days ago";
            }
        }
    }
}
