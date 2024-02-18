package com.example.designersconnect.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.designersconnect.Adapters.MessageAdapter;
import com.example.designersconnect.Models.Message;
import com.example.designersconnect.Models.UserData;
import com.example.designersconnect.R;
import com.example.designersconnect.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        databaseReference.child("users").child(receiver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    UserData user = snapshot.getValue(UserData.class);
                    if(user!=null)
                    {
                        TextView tvUsername = findViewById(R.id.tvUsername);
                        TextView tvStatus = findViewById(R.id.tvStatus);
                        tvUsername.setText(user.getUsername());
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
        Message message = new Message(messageText, userId, receiver);
        databaseReference.child("messages").push().setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ChatActivity.this, "Message sent!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}