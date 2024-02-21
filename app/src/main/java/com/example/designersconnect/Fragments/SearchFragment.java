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
import android.widget.Toast;

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
   List<UserData> users, searchHistoryUsers;
   UserAdapter userAdapter, searchHistoryAdapter;
    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater,container,false);

        binding.searchBar.setOnClickListener(v -> binding.searchBar.setIconified(false));
        setSearchHistoryUsers();
        binding.rvSearchHistory.setVisibility(View.VISIBLE);

        setSearchResults();

        return binding.getRoot();
    }

    private void setSearchResults() {
        users = new ArrayList<>();
        userAdapter = new UserAdapter(getActivity(), users, true);
        binding.rvSearchResults.setAdapter(userAdapter);
        binding.rvSearchResults.setLayoutManager(new LinearLayoutManager(getActivity()));

        binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(binding.searchBar.getQuery().toString().isEmpty())
                {
                    users.clear();
                    userAdapter.notifyDataSetChanged();
                    binding.rvSearchHistory.setVisibility(View.VISIBLE);
                }
                else
                {
                    searchUser(query.toString());
                    binding.rvSearchHistory.setVisibility(View.GONE);
                }
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(binding.searchBar.getQuery().toString().isEmpty())
                {
                    users.clear();
                    userAdapter.notifyDataSetChanged();
                    binding.rvSearchHistory.setVisibility(View.VISIBLE);
                }
                else
                {
                    searchUser(newText.toString());
                    binding.rvSearchHistory.setVisibility(View.GONE);
                }
                return true;
            }
        });
    }

    void setSearchHistoryUsers()
    {
        searchHistoryUsers =  new ArrayList<>();
        searchHistoryAdapter = new UserAdapter(getActivity(),searchHistoryUsers , false);
        binding.rvSearchHistory.setAdapter(searchHistoryAdapter);
        binding.rvSearchHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
        String userId = FirebaseAuth.getInstance().getUid();

        Query query = FirebaseDatabase.getInstance().getReference("Search-history").child(userId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                searchHistoryUsers.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    String id = dataSnapshot.getKey();
                    FirebaseDatabase.getInstance().getReference("users").child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists())
                            {
                                searchHistoryUsers.add(snapshot.getValue(UserData.class));
                                searchHistoryAdapter.notifyDataSetChanged();
                            }
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