package com.example.designersconnect.Activities;

import static com.example.designersconnect.Helpers.MessageOperations.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.designersconnect.Adapters.MessageAdapter;
import com.example.designersconnect.Models.Message;
import com.example.designersconnect.Models.UserData;
import com.example.designersconnect.R;
import com.example.designersconnect.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    DatabaseReference databaseReference;
    List<Message> messageList;
    MessageAdapter messageAdapter;
    String receiver;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        messageList = new ArrayList<>();
        Intent i = getIntent();
        receiver = i.getStringExtra("userId");
        userId = FirebaseAuth.getInstance().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        ImageView imgOptions = findViewById(R.id.imgOptions);
        imgOptions.setOnClickListener(v->{
            PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu_delete, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    clearChat(receiver, userId);
                    return true;
                }

            });

            popupMenu.show();
        });


        databaseReference.child("users").child(receiver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    UserData user = snapshot.getValue(UserData.class);
                    if(user!=null)
                    {
                        TextView tvUsername = findViewById(R.id.tvUsername);
                        tvUsername.setText(user.getUsername());

//                        TextView tvStatus = findViewById(R.id.tvStatus);
//                        tvStatus.setText(user.getStatus());

                        ShapeableImageView imageView = findViewById(R.id.imgProfilePhoto);
                        Glide.with(getApplicationContext())
                                .load(user.getProfilePicture())
                                .apply(new RequestOptions().circleCrop())
                                .into(imageView);

                        ImageView backButton = findViewById(R.id.imgBackButton);
                        backButton.setOnClickListener(v->{
                            onBackPressed();
                        });

                        View view = findViewById(R.id.include);
                        view.setOnClickListener(v -> {
                            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                            intent.putExtra("userId",user.getUserId());
                            startActivity(intent);
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.btnSendMessage.setOnClickListener(v->{
            String messageText = binding.etMessage.getText().toString();
            binding.etMessage.setText("");
            if(messageText.isEmpty())
            {
                Toast.makeText(this, "Message can't be empty!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                sendMessage(messageText, receiver);
            }
        });
        databaseReference.child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for(DataSnapshot message:snapshot.getChildren())
                {
                    Message messageObject = message.getValue(Message.class);
                    if(messageObject.getSender().equals(userId) && messageObject.getReceiver().equals(receiver) || messageObject.getSender().equals(receiver) && messageObject.getReceiver().equals(userId))
                    {
                        messageList.add(messageObject);
                    }
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.rvMessages.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageAdapter(messageList,this);
        binding.rvMessages.setAdapter(messageAdapter);
    }
    void sendMessage(String messageText,String receiver){

        String userId = FirebaseAuth.getInstance().getUid();
        String messageId = databaseReference.child("messages").push().getKey();
        Message message = new Message(messageText, userId, receiver, messageId);
        databaseReference.child("messages").child(messageId).setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ChatActivity.this, "Message sent!", Toast.LENGTH_SHORT).show();
            }
        });
    }


}