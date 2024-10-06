package com.example.socialmediatdcproject.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.CommentPostActivity;
import com.example.socialmediatdcproject.database.UserDatabase;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.User;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_TEXT = 0;  // Kiểu view cho post không có ảnh
    private static final int VIEW_TYPE_IMAGE = 1; // Kiểu view cho post có ảnh

    private ArrayList<Post> postList;

    // Constructor
    public PostAdapter(ArrayList<Post> postList) {
        this.postList = postList;
    }

    @Override
    public int getItemViewType(int position) {
        Post post = postList.get(position);
        if (post.getPostImage() != null) {
            // Nếu có ảnh thì trả về kiểu view có ảnh
            return VIEW_TYPE_IMAGE;
        } else {
            // Nếu không có ảnh thì trả về kiểu view không có ảnh
            return VIEW_TYPE_TEXT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_IMAGE) {
            // Nếu là post có ảnh, nạp layout item_post_image
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_post_image, parent, false);
            return new PostImageViewHolder(view);
        } else {
            // Nếu là post không có ảnh, nạp layout item_post
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_post, parent, false);
            return new PostTextViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Post post = postList.get(position);
        UserDatabase userDatabase = new UserDatabase();

        if (holder.getItemViewType() == VIEW_TYPE_IMAGE) {
            PostImageViewHolder imageViewHolder = (PostImageViewHolder) holder;
            imageViewHolder.postcontent.setText(post.getContent());
            for (User u : userDatabase.dataUser()) {
                if (u.getUserId() == post.getUserId()) {
                    imageViewHolder.postAdminUserId.setText(u.getFullName());
                }
            }
            imageViewHolder.buttonComment.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), CommentPostActivity.class);
                intent.putExtra("postId", post.getPostId());  // Truyền postId qua Activity mới
                v.getContext().startActivity(intent);
            });
        } else {
            PostTextViewHolder textViewHolder = (PostTextViewHolder) holder;
            textViewHolder.postcontent.setText(post.getContent());
            for (User u : userDatabase.dataUser()) {
                if (u.getUserId() == post.getUserId()) {
                    textViewHolder.postAdminUserId.setText(u.getFullName());
                }
            }
            textViewHolder.buttonComment.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), CommentPostActivity.class);
                intent.putExtra("postId", post.getPostId());  // Truyền postId qua Activity mới
                v.getContext().startActivity(intent);
            });
        }
    }


    @Override
    public int getItemCount() {
        return postList.size();
    }

    // ViewHolder cho post không có ảnh
    public static class PostTextViewHolder extends RecyclerView.ViewHolder {
        TextView postcontent;
        TextView postAdminUserId;
        LinearLayout buttonComment;

        public PostTextViewHolder(View itemView) {
            super(itemView);
            postcontent = itemView.findViewById(R.id.post_content);
            postAdminUserId = itemView.findViewById(R.id.post_name);
            buttonComment = itemView.findViewById(R.id.button_comment);
        }
    }

    // ViewHolder cho post có ảnh
    public static class PostImageViewHolder extends RecyclerView.ViewHolder {
        // ImageView postImageView;  // Giả sử bạn có ImageView cho ảnh bài post
        TextView postcontent;
        TextView postAdminUserId;
        LinearLayout buttonComment;

        public PostImageViewHolder(View itemView) {
            super(itemView);
            postcontent = itemView.findViewById(R.id.post_content);
            postAdminUserId = itemView.findViewById(R.id.post_name);
            buttonComment = itemView.findViewById(R.id.button_comment);
            // postImageView = itemView.findViewById(R.id.post_image);  // Gán ImageView cho ảnh bài post
        }
    }
}
