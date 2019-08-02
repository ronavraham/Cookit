package com.grd.cookit.recycler.adapters;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grd.cookit.R;
import com.mikepenz.iconics.view.IconicsButton;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import com.grd.cookit.model.ui.UIRecipe;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

    public List<UIRecipe> Posts;
    public OnItemClickListener deletePostListener;

    public PostAdapter(List<UIRecipe> posts) {
        if (posts == null) {
            Posts = new LinkedList<>();
        } else {
            Posts = posts;
        }
        deletePostListener = null;
    }

    public PostAdapter(List<UIRecipe> posts, OnItemClickListener deletePostListener) {
        if (posts == null) {
            Posts = new LinkedList<>();
        } else {
            Posts = posts;
        }
        deletePostListener = deletePostListener;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post, parent, false);

        PostHolder ph = new PostHolder(v);


        return ph;
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        UIRecipe currentPost = this.Posts.get(position);
        holder.postUid = currentPost.uid;
        holder.Background.setBackgroundDrawable(currentPost.RecipeImage);
        holder.PostText.setText(currentPost.name);
        holder.ProfileImage.setImageDrawable(currentPost.ProfileImage);
        holder.Timestamp.setText(new PrettyTime().format(currentPost.timestamp));
        holder.UserName.setText(currentPost.userName);
        if (this.deletePostListener != null) {
            holder.Bind(holder, this.deletePostListener);
        }
    }

    @Override
    public int getItemCount() {
        return Posts.size();
    }

    public interface OnItemClickListener {
        void onItemClick(PostHolder item);
    }

    public static class PostHolder extends RecyclerView.ViewHolder {

        public CircleImageView ProfileImage;
        public TextView UserName;
        public TextView Timestamp;
        public AppCompatImageView Background;
        public TextView PostText;
        public IconicsButton RemoveButton;
        public String postUid;

        public PostHolder(View itemView) {
            super(itemView);
            ProfileImage = itemView.findViewById(R.id.post_profile_image);
            UserName = itemView.findViewById(R.id.post_user_name);
            Timestamp = itemView.findViewById(R.id.post_time_ago);
            Background = itemView.findViewById(R.id.post_backround);
            PostText = itemView.findViewById(R.id.post_text);
            RemoveButton = itemView.findViewById(R.id.remove_post_button);
            RemoveButton.setVisibility(View.INVISIBLE);
        }

        public void Bind(PostHolder holder, OnItemClickListener listener) {
            holder.RemoveButton.setVisibility(View.VISIBLE);
            holder.RemoveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(holder);
                }
            });
        }
    }


}

