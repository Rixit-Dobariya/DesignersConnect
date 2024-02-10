package com.example.designersconnect.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.designersconnect.Activities.PostsActivity;
import com.example.designersconnect.Models.Photo;
import com.example.designersconnect.Activities.MyPostsActivity;
import com.example.designersconnect.R;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private List<Photo> photos;
    private Context context;
    private String userId;
    public enum page {
        PROFILEACTIVITY,
        MYPROFILEFRAGMENT
    }
    page pageName;

    public PhotoAdapter(List<Photo> photos,Context context, page pageName, String userId) {
        this.photos = photos;
        this.context = context;
        this.pageName = pageName;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Photo photo = photos.get(position);
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(context)
                .load(photo.getPicture())
                .apply(requestOptions)
                .into(holder.imageView);
        holder.imageView.setOnClickListener(v->{
            if(pageName == page.MYPROFILEFRAGMENT)
            {
                Intent i = new Intent(context, MyPostsActivity.class);
                i.putExtra("position",position);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
            else if(pageName == page.PROFILEACTIVITY)
            {
                Intent i = new Intent(context, PostsActivity.class);
                i.putExtra("position",position);
                i.putExtra("userId",userId);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivPost);
        }
    }
}
