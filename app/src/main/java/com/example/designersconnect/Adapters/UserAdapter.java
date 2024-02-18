package com.example.designersconnect.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.designersconnect.Activities.ChatActivity;
import com.example.designersconnect.Helpers.FollowOperations;
import com.example.designersconnect.Models.UserData;
import com.example.designersconnect.Activities.ProfileActivity;
import com.example.designersconnect.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    Context context;
    List<UserData> users;

    public UserAdapter(Context context, List<UserData> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_search_result_main,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserData user = users.get(position);
        holder.tvsrUsername.setText(user.getUsername());
        holder.tvsrDisplayName.setText(user.getDisplayName());
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(context)
                .load(user.getProfilePicture())
                .apply(requestOptions)
                .into(holder.tvsrProfilePhoto);
        FollowOperations.followText(holder.btnFollow, user.getUserId());
        holder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FollowOperations.follow(holder.btnFollow,user.getUserId());
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ProfileActivity.class);
                i.putExtra("userId",user.getUserId());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public ShapeableImageView tvsrProfilePhoto;
        public TextView tvsrDisplayName;
        public TextView tvsrUsername;
        public AppCompatButton btnFollow, btnMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvsrProfilePhoto = itemView.findViewById(R.id.tvsrProfilePhoto);
            tvsrDisplayName = itemView.findViewById(R.id.tvsrDisplayName);
            tvsrUsername = itemView.findViewById(R.id.tvsrUsername);
            btnFollow = itemView.findViewById(R.id.btnFollow);
        }
    }
}
