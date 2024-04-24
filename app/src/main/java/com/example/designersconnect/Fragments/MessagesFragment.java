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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessagesFragment extends Fragment {

    FragmentMessagesBinding binding;
    List<UserData> users, searchedUsersList, suggestionsList, usersList;
    List<String> userDataList;
    DatabaseReference databaseReference;
    String userId;
    UserMessageAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMessagesBinding.inflate(inflater, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        users = new ArrayList<>();
        userDataList = new ArrayList<>();
        searchedUsersList = new ArrayList<>();
        suggestionsList = new ArrayList<>();
        usersList = new ArrayList<>();

        adapter = new UserMessageAdapter(users,getContext());
        binding.messageItems.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.messageItems.setAdapter(adapter);
        userId = FirebaseAuth.getInstance().getUid();

        UserMessageAdapter userMessageAdapter = new UserMessageAdapter(searchedUsersList,getContext());
        binding.rvSearchMessage.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSearchMessage.setAdapter(userMessageAdapter);

        binding.tvSuggestions.setVisibility(View.VISIBLE);

        setData();

        binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                if(binding.searchBar.getQuery().toString().isEmpty())
//                {
//                    binding.messageItems.setVisibility(View.GONE);
//                }
//                else
//                {
//                    binding.rvSearchMessage.setVisibility(View.VISIBLE);
//                    searchedUsersList.clear();
//                    for(UserData user: users)
//                    {
//                        if(user.getUsername().toLowerCase().contains(query.toLowerCase()))
//                        {
//                            searchedUsersList.add(user);
//                        }
//                    }
//                    userMessageAdapter.notifyDataSetChanged();
//                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(binding.searchBar.getQuery().toString().isEmpty())
                {
                    binding.rvSearchMessage.setVisibility(View.GONE);
                    binding.messageItems.setVisibility(View.VISIBLE);
                    binding.tvSuggestions.setVisibility(View.VISIBLE);
                    binding.rvSuggestions.setVisibility(View.VISIBLE);
                }
                else
                {
                    binding.rvSearchMessage.setVisibility(View.VISIBLE);
                    binding.messageItems.setVisibility(View.GONE);
                    binding.tvSuggestions.setVisibility(View.GONE);
                    binding.rvSuggestions.setVisibility(View.GONE);
                    searchedUsersList.clear();
                    for(UserData user: users)
                    {
                        if(user.getUsername().toLowerCase().contains(newText.toLowerCase()))
                        {
                            searchedUsersList.add(user);
                        }
                    }
                    for(UserData user: usersList )
                    {
                        if(user.getUsername().toLowerCase().contains(newText.toLowerCase()))
                        {
                            searchedUsersList.add(user);
                        }
                    }
                    userMessageAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });
        return binding.getRoot();
    }

    void setData(){
        Query query = FirebaseDatabase.getInstance().getReference("messages");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userDataList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Message msg = userSnapshot.getValue(Message.class);
                    if (msg.getReceiver().equals(userId)) {
                        userDataList.add(msg.getSender());
                    }
                    if (msg.getSender().equals(userId)) {
                        userDataList.add(msg.getReceiver());
                    }
                }
                setUsers(adapter);
                setSuggestions();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //add users using user ids list
    void setUsers(UserMessageAdapter adapter)
    {
        users.clear();
        for (String id : userDataList) {
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


    void setSuggestions(){
        List<String> userIdList = new ArrayList<>();

        UserMessageAdapter userMessageAdapter = new UserMessageAdapter(usersList, getContext());
        binding.rvSuggestions.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSuggestions.setAdapter(userMessageAdapter);
        FirebaseDatabase.getInstance().getReference("Follow").child(userId).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        String userId = dataSnapshot.getKey();
                        if(!userDataList.contains(userId)){
                            userIdList.add(userId);
                        }
                    }
                }
                if(userIdList.isEmpty())
                {
                    binding.tvSuggestions.setVisibility(View.GONE);
                }
                usersList.clear();
                for (String id : userIdList) {
                    FirebaseDatabase.getInstance().getReference("users").child(id)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        UserData user = dataSnapshot.getValue(UserData.class);
                                        boolean exists=false;
                                        for(UserData userData: usersList)
                                        {
                                            if(userData.getUserId().equals(user.getUserId()))
                                            {
                                                exists=true;
                                                break;
                                            }
                                        }
                                        if(!exists)
                                        {
                                            usersList.add(user);
                                        }
                                    }
                                    userMessageAdapter.notifyDataSetChanged();
                                }


                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setData();
    }
}