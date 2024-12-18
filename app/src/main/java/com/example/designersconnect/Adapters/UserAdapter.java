package com.example.designersconnect.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.designersconnect.Activities.ChatActivity;
import com.example.designersconnect.Activities.MainActivity;
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
    boolean main;

    public UserAdapter(Context context, List<UserData> users, boolean main) {
        this.context = context;
        this.users = users;
        this.main = main;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(main)
        {
            view = LayoutInflater.from(context).inflate(R.layout.single_search_result_main,parent,false);
        }
        else
        {
            view = LayoutInflater.from(context).inflate(R.layout.single_search_result,parent,false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String userId = FirebaseAuth.getInstance().getUid();
        UserData user = users.get(position);
        holder.tvsrUsername.setText(user.getUsername());
        holder.tvsrDisplayName.setText(user.getDisplayName());

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(context)
                .load(user.getProfilePicture())
                .apply(requestOptions.centerCrop())
                .into(holder.tvsrProfilePhoto);
        if(main)
        {
            if(user.getUserId().equals(userId)){
                holder.btnFollow.setVisibility(View.GONE);
            }
            FollowOperations.followText(holder.btnFollow, user.getUserId());
            holder.btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FollowOperations.follow(holder.btnFollow,user.getUserId());
                }
            });
        }
        else
        {
            holder.imgDeleteHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase.getInstance().getReference("Search-history").child(userId).child(user.getUserId()).removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            Toast.makeText(context, "Search history removed", Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                        }
                    });
                }
            });
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user.getUserId().equals(userId)){
                    Intent i = new Intent(context, MainActivity.class);
                    i.putExtra("fragment",5);
                    context.startActivity(i);
                }
                else{
                    updateSearchHistory(user.getUserId());
                    Intent i = new Intent(context, ProfileActivity.class);
                    i.putExtra("userId",user.getUserId());
                    context.startActivity(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void updateSearchHistory(String userId)
    {
        String selfUserId = FirebaseAuth.getInstance().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Search-history");
        databaseReference.child(selfUserId).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        childSnapshot.getRef().removeValue();
                    }
                    databaseReference.child(selfUserId).child(userId).setValue(System.currentTimeMillis()+"");
                }
                else
                {
                    databaseReference.child(selfUserId).child(userId).setValue(System.currentTimeMillis()+"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    class ViewHolder extends RecyclerView.ViewHolder{
        public ShapeableImageView tvsrProfilePhoto;
        public TextView tvsrDisplayName;
        public TextView tvsrUsername;
        ImageView imgDeleteHistory;
        public AppCompatButton btnFollow, btnMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvsrProfilePhoto = itemView.findViewById(R.id.tvsrProfilePhoto);
            tvsrDisplayName = itemView.findViewById(R.id.tvsrDisplayName);
            tvsrUsername = itemView.findViewById(R.id.tvsrUsername);
            btnFollow = itemView.findViewById(R.id.btnFollow);
            imgDeleteHistory = itemView.findViewById(R.id.imgDeleteHistory);
        }
    }
}
