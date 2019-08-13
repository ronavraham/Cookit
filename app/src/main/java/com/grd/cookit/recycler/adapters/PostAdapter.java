package com.grd.cookit.recycler.adapters;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.grd.cookit.R;
import com.mikepenz.iconics.view.IconicsButton;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import com.grd.cookit.model.ui.UIRecipe;
import com.squareup.picasso.Callback;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

//    public List<UIRecipe> Posts;

//    public OnItemClickListener deletePostListener;
//    public OnItemClickListener infoRecipeListener;

    private Context mContext;
    public List<UIRecipe> Posts;
    public static OnItemClickListener onItemClickListener;
    private String userId;

    public PostAdapter(List<UIRecipe> posts) {
        if (posts == null) {
            Posts = new LinkedList<>();
        } else {
            Posts = posts;
        }
        onItemClickListener = null;
    }

    public PostAdapter(Context context, List<UIRecipe> posts,String userId ,OnItemClickListener onItemClickListener) {
        this.mContext = context;
        this.Posts = posts;
        this.userId = userId;
        this.onItemClickListener = onItemClickListener;
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
        holder.progressBar.setVisibility(View.VISIBLE);
        currentPost.recipeImageRequestCreator.into(holder.Background, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {

            }
        });
        holder.PostText.setText(currentPost.name);
        currentPost.userProfileRequestCreator.into(holder.ProfileImage);
        holder.Timestamp.setText(new PrettyTime().format(currentPost.timestamp));
        holder.UserName.setText(currentPost.userName);
        if(!currentPost.userGoogleId.equals(this.userId)){
            holder.RemoveButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return Posts.size();
    }

    public interface OnItemClickListener {
        void onDeleteClick(View v, int position);

        void onInfoClick(View v, int position);
    }

    public static class PostHolder extends RecyclerView.ViewHolder {

        public CircleImageView ProfileImage;
        public TextView UserName;
        public TextView Timestamp;
        public AppCompatImageView Background;
        public TextView PostText;
        public IconicsButton RemoveButton;
        public IconicsButton InfoButton;
        public String postUid;
        public ProgressBar progressBar;

        public PostHolder(View itemView) {
            super(itemView);
            ProfileImage = itemView.findViewById(R.id.post_profile_image);
            UserName = itemView.findViewById(R.id.post_user_name);
            Timestamp = itemView.findViewById(R.id.post_time_ago);
            Background = itemView.findViewById(R.id.post_backround);
            PostText = itemView.findViewById(R.id.post_text);
            RemoveButton = itemView.findViewById(R.id.remove_post_button);
            InfoButton = itemView.findViewById(R.id.info_post_button);
            progressBar = itemView.findViewById(R.id.progressBar);
            RemoveButton.setOnClickListener(v -> onItemClickListener.onDeleteClick(v, getAdapterPosition()));

            InfoButton.setOnClickListener(v -> onItemClickListener.onInfoClick(v, getAdapterPosition()));
        }
    }


}

