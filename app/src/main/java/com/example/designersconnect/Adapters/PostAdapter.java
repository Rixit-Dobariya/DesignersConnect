package com.example.designersconnect.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.designersconnect.Fragments.CommentsFragment;
import com.example.designersconnect.Helpers.CommentsOperation;
import com.example.designersconnect.Helpers.LikesOperations;
import com.example.designersconnect.Models.Post;
import com.example.designersconnect.Models.UserData;
import com.example.designersconnect.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private List<Post> postList;
    private Context context;
    private DatabaseReference usersReference;
    enum PAGE_TYPE{
        HOME_FRAGMENT, POSTS_ACTIVITY, MY_POSTS_ACTIVITY
    }
    PAGE_TYPE pageType;

    public PostAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
        this.usersReference = FirebaseDatabase.getInstance().getReference().child("users");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post_to_edit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postList.get(position);

        String userId = post.getUserId();
        usersReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserData user = dataSnapshot.getValue(UserData.class);

                    holder.tvUsername.setText(user.getUsername());
                    Glide.with(context)
                            .load(user.getProfilePicture())
                            .apply(new RequestOptions().circleCrop())
                            .into(holder.imgProfilePhoto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error reading user data: " + databaseError.getMessage());
            }
        });
        holder.tvDescription.setText(post.getDescription());

        Glide.with(context)
                .load(post.getPostPicture())
                .apply(new RequestOptions().centerCrop())
                .into(holder.imgPost);
        holder.imgOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu p = new PopupMenu(context,holder.imgOptions);
                p.getMenuInflater().inflate(R.menu.popup_menu,p.getMenu());
                p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.delete)
                        {
                            FirebaseDatabase.getInstance().getReference("posts").child(post.getPostId()).removeValue();
                            Toast.makeText(context, "Post successfully deleted!", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });
                p.show();
            }
        });

        holder.imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LikesOperations.like(post.getPostId(), holder.imgLike);
            }
        });
        LikesOperations.isLiked(post.getPostId(), holder.imgLike);
        LikesOperations.numberOfLikes(post.getPostId(), holder.tvLikes);
        CommentsOperation.numberOfComments(post.getPostId(), holder.tvComments);
        holder.imgComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialogFragment bottomSheetDialogFragment = CommentsFragment.newInstance(post.getPostId());
                bottomSheetDialogFragment.show(((AppCompatActivity)context).getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvDescription, tvLikes, tvComments;
        ShapeableImageView imgProfilePhoto, imgPost;
        ImageView imgOptions, imgLike, imgComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            imgProfilePhoto = itemView.findViewById(R.id.imgProfilePhoto);
            imgPost = itemView.findViewById(R.id.imgPost);
            imgOptions = itemView.findViewById(R.id.imgOptions);
            imgLike = itemView.findViewById(R.id.imgLike);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            tvComments = itemView.findViewById(R.id.tvComments);
            imgComment = itemView.findViewById(R.id.imgComment);
        }
    }
}
