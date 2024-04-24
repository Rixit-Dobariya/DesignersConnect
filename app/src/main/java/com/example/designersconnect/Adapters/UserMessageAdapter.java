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
import com.example.designersconnect.Models.Message;
import com.example.designersconnect.Models.UserData;
import com.example.designersconnect.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
        holder.tvTime.setVisibility(View.GONE);

        Query query = FirebaseDatabase.getInstance().getReference("messages").orderByChild("timestamp");
        String userId = FirebaseAuth.getInstance().getUid();
        final boolean[] msg = {false};
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                        Message message = messageSnapshot.getValue(Message.class);
                        if(message.getSender().equals(userId) && message.getReceiver().equals(user.getUserId())){
                            holder.tvMessage.setText(String.format("You sent: \"%s\"",message.getMessage()));
                            msg[0] = true;
                        }
                        if(message.getSender().equals(user.getUserId()) && message.getReceiver().equals(userId)){
                            holder.tvMessage.setText(String.format("%s sent: \"%s\"",user.getUsername(),message.getMessage()));
                            msg[0] = true;
                        }
                    }
                } else {
                    System.out.println("No messages found.");
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Error retrieving messages: " + databaseError.getMessage());
            }
        });

        if(!msg[0]){
            holder.tvMessage.setText("Start chat");
        }
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
