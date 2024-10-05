package com.example.socialmediatdcproject.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.database.UserDatabase;
import com.example.socialmediatdcproject.model.Comment;
import com.example.socialmediatdcproject.model.User;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> commentsList;

    // Constructor
    public CommentAdapter(List<Comment> commentList) {
        this.commentsList = commentList;
    }

    // Tạo ViewHolder
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.CommentViewHolder holder, int position) {
        Comment comment = commentsList.get(position);
        UserDatabase userDatabase = new UserDatabase();
        if (comment != null) {
            // Set dữ liệu cho các view
            for (User u: userDatabase.dataUser()) {
                if (u.getUserId() == comment.getUserId()){
                    holder.commentUserId.setText(u.getFullName());
                    break;
                }
            }
            holder.commentLike.setText(comment.getCommentLike() + "");
            holder.commentContent.setText(comment.getContent());

        } else {
            Log.e("MemberAdapter", "User at position " + position + " is null");
        }
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    // Lớp ViewHolder
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentUserId;
        TextView commentContent;
        TextView commentLike;
        RecyclerView recyclerViewReply;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentUserId = itemView.findViewById(R.id.comment_user_name);
            commentContent = itemView.findViewById(R.id.comment_content);
            commentLike = itemView.findViewById(R.id.comment_like);
        }
    }
}
