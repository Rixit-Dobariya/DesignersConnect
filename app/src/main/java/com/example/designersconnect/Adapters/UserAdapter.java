package com.example.designersconnect.Adapters;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbRequest;
import android.media.audiofx.AutomaticGainControl;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.designersconnect.Models.UserData;
import com.example.designersconnect.ProfileActivity;
import com.example.designersconnect.R;
import com.google.android.material.imageview.ShapeableImageView;

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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ProfileActivity.class);
                i.putExtra("userId",user.getUserId());
                context.startActivity(i);
            }
        });
        holder.tvsrUsername.setText(user.getUsername());
        holder.tvsrDisplayName.setText(user.getDisplayName());
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(context)
                .load(user.getProfilePicture())
                .apply(requestOptions)
                .into(holder.tvsrProfilePhoto);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public ShapeableImageView tvsrProfilePhoto;
        public TextView tvsrDisplayName;
        public TextView tvsrUsername;
        public AppCompatButton btnFollow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvsrProfilePhoto = itemView.findViewById(R.id.tvsrProfilePhoto);
            tvsrDisplayName = itemView.findViewById(R.id.tvsrDisplayName);
            tvsrUsername = itemView.findViewById(R.id.tvsrUsername);
            btnFollow = itemView.findViewById(R.id.btnFollow);
        }
    }
}