package com.example.designersconnect.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.designersconnect.Adapters.MessageAdapter;
import com.example.designersconnect.Adapters.UserMessageAdapter;
import com.example.designersconnect.Models.Message;
import com.example.designersconnect.Models.UserData;
import com.example.designersconnect.R;
import com.example.designersconnect.databinding.FragmentMessagesBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessagesFragment extends Fragment {

    FragmentMessagesBinding binding;
    List<UserData> users;
    List<String> usersList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMessagesBinding.inflate(inflater, container, false);

        users = new ArrayList<>();
        usersList = new ArrayList<>();

        UserMessageAdapter adapter = new UserMessageAdapter(users,getContext());
        binding.messageItems.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.messageItems.setAdapter(adapter);
        String userId= FirebaseAuth.getInstance().getUid();

        Query query = FirebaseDatabase.getInstance().getReference("messages");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> newUsersList = new ArrayList<>();
                for(DataSnapshot userSnapshot : snapshot.getChildren())
                {
                    Message msg = userSnapshot.getValue(Message.class);
                    if(msg.getReceiver().equals(userId)){
                        newUsersList.add(msg.getSender());
                    }
                    if(msg.getSender().equals(userId)){
                        newUsersList.add(msg.getReceiver());
                    }
                }

                usersList.clear();
                usersList.addAll(newUsersList);
                setUsers(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return binding.getRoot();
    }
    void setUsers(UserMessageAdapter adapter)
    {
        users.clear();

        for (String id : usersList) {
            // Get the user data for each user ID in usersList
            FirebaseDatabase.getInstance().getReference("users").child(id)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                UserData user = dataSnapshot.getValue(UserData.class);
                                boolean exists=false;
                                for(UserData userData: users)
                                {
                                    if(userData.getUserId().equals(user.getUserId()))
                                    {
                                        exists=true;
                                        break;
                                    }
                                }
                                if(!exists)
                                {
                                    users.add(user);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle the error
                        }
                    });
        }
    }
}