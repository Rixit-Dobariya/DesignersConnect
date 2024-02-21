package com.example.designersconnect.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.ContactsContract;
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
    List<UserData> users, searchedUsersList;
    List<String> usersList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMessagesBinding.inflate(inflater, container, false);

        users = new ArrayList<>();
        usersList = new ArrayList<>();
        searchedUsersList = new ArrayList<>();

        UserMessageAdapter adapter = new UserMessageAdapter(users,getContext());
        binding.messageItems.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.messageItems.setAdapter(adapter);
        String userId = FirebaseAuth.getInstance().getUid();

        Query query = FirebaseDatabase.getInstance().getReference("messages");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for(DataSnapshot userSnapshot : snapshot.getChildren())
                {
                    Message msg = userSnapshot.getValue(Message.class);
                    if(msg.getReceiver().equals(userId)){
                        usersList.add(msg.getSender());
                    }
                    if(msg.getSender().equals(userId)){
                        usersList.add(msg.getReceiver());
                    }
                }

                if(usersList.isEmpty())
                {
                    binding.tvSuggestions.setVisibility(View.VISIBLE);
                    FirebaseDatabase.getInstance().getReference("Follow").child(userId).child("following").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists())
                            {
                                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                                {
                                    usersList.add(dataSnapshot.getKey());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if(usersList.isEmpty())
                {
                    binding.tvSuggestions.setVisibility(View.GONE);
                }
                setUsers(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(binding.searchBar.getQuery().toString().isEmpty())
                {
                    binding.rvSearchMessage.setVisibility(View.GONE);
                    binding.messageItems.setVisibility(View.VISIBLE);
                }
                else
                {
                    binding.rvSearchMessage.setVisibility(View.VISIBLE);
                    binding.messageItems.setVisibility(View.GONE);
                    searchedUsersList.clear();
                    for(UserData user: users)
                    {
                        if(user.getUsername().toLowerCase().contains(query.toLowerCase()))
                        {
                            searchedUsersList.add(user);
                        }
                    }
                    UserMessageAdapter userMessageAdapter = new UserMessageAdapter(searchedUsersList,getContext());
                    binding.rvSearchMessage.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.rvSearchMessage.setAdapter(userMessageAdapter);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(binding.searchBar.getQuery().toString().isEmpty())
                {
                    binding.rvSearchMessage.setVisibility(View.GONE);
                    binding.messageItems.setVisibility(View.VISIBLE);
                }
                else
                {
                    binding.rvSearchMessage.setVisibility(View.VISIBLE);
                    binding.messageItems.setVisibility(View.GONE);
                    searchedUsersList.clear();
                    for(UserData user: users)
                    {
                        if(user.getUsername().toLowerCase().contains(newText.toLowerCase()))
                        {
                            searchedUsersList.add(user);
                        }
                    }
                    UserMessageAdapter userMessageAdapter = new UserMessageAdapter(searchedUsersList,getContext());
                    binding.rvSearchMessage.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.rvSearchMessage.setAdapter(userMessageAdapter);
                }
                return true;
            }
        });
        return binding.getRoot();
    }

    //add users using user ids list
    void setUsers(UserMessageAdapter adapter)
    {
        users.clear();
        for (String id : usersList) {
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
                            }
                        });
            }
    }


}