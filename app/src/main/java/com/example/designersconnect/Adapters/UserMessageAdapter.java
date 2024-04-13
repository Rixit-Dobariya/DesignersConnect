package com.example.designersconnect.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.designersconnect.Activities.ChatActivity;
import com.example.designersconnect.Models.UserData;
import com.example.designersconnect.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class UserMessageAdapter extends RecyclerView.Adapter<UserMessageAdapter.ViewHolder> {

    List<UserData> users;
    Context context;

    public UserMessageAdapter(List<UserData> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_message,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserData user = users.get(position);
        holder.tvUsername.setText(user.getUsername());
        Glide.with(context)
                .load(user.getProfilePicture())
                .apply(new RequestOptions().circleCrop())
                .into(holder.imgProfilePhoto);
        holder.itemView.setOnClickListener(v->{
            Intent i = new Intent(context, ChatActivity.class);
            i.putExtra("userId",user.getUserId());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvUsername, tvTime, tvMessage;
        ShapeableImageView imgProfilePhoto;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvTime=itemView.findViewById(R.id.tvTime);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            imgProfilePhoto = itemView.findViewById(R.id.imgProfilePhoto);
        }
    }
}
