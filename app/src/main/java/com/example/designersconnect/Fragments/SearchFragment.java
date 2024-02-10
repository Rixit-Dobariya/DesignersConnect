package com.example.designersconnect.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;

import com.example.designersconnect.Adapters.UserAdapter;
import com.example.designersconnect.Models.UserData;
import com.example.designersconnect.databinding.FragmentSearchBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

   FragmentSearchBinding binding;
   List<UserData> users;
    UserAdapter userAdapter;
    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater,container,false);
        users = new ArrayList<>();
        userAdapter = new UserAdapter(getActivity(),users);
        binding.rvSearchResults.setAdapter(userAdapter);
        binding.rvSearchResults.setLayoutManager(new LinearLayoutManager(getActivity()));

        binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(binding.searchBar.getQuery().toString().isEmpty())
                {
                    users.clear();
                    userAdapter.notifyDataSetChanged();
                }
                else
                {
                    searchUser(query.toString());
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(binding.searchBar.getQuery().toString().isEmpty())
                {
                    users.clear();
                    userAdapter.notifyDataSetChanged();
                }
                else
                {
                    searchUser(newText.toString());
                }
                return false;
            }
        });
        return binding.getRoot();
    }

    void searchUser(String s)
    {
        String userId = FirebaseAuth.getInstance().getUid();
        Query query = FirebaseDatabase.getInstance().getReference().child("users")
                .orderByChild("username").startAt(s).endAt(s + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    UserData user = snapshot.getValue(UserData.class);
                    if(!user.getUserId().equals(userId))
                    {
                        users.add(user);
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}