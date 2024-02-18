package com.example.designersconnect.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.designersconnect.Adapters.CommentsAdapter;
import com.example.designersconnect.Models.Comment;
import com.example.designersconnect.R;
import com.example.designersconnect.databinding.CommentsLayoutBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CommentsFragment extends BottomSheetDialogFragment {
    String postId;
    List<Comment> commentList;

    static final String ARG_1="postId";
    CommentsLayoutBinding binding;
    DatabaseReference databaseReference;
    CommentsFragment(){}
    public static CommentsFragment newInstance(String postId) {
        Bundle args = new Bundle();
        args.putString(ARG_1,postId);
        CommentsFragment fragment = new CommentsFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CommentsLayoutBinding.inflate(inflater, container, false);
        postId = getArguments().getString(ARG_1);
        String userId = FirebaseAuth.getInstance().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        commentList = new ArrayList<>();
        CommentsAdapter commentsAdapter = new CommentsAdapter(getContext(), commentList, postId);
        binding.rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvComments.setAdapter(commentsAdapter);
        binding.btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.etComment.getText().toString().isEmpty())
                {
                    Toast.makeText(getContext(), "Comment can't be empty!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String val = binding.etComment.getText().toString();
                    String commentId = databaseReference.child("Comments").child(postId).push().getKey();
                    Comment comment = new Comment(commentId, userId, val);
                    databaseReference.child("Comments").child(postId).child(commentId).setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isComplete())
                            {
                                Toast.makeText(getContext(), "Comment added!", Toast.LENGTH_SHORT).show();
                                binding.etComment.setText("");
                            }
                            else
                            {
                                Toast.makeText(getContext(), "Comment add operation failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        databaseReference.child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return binding.getRoot();
    }

}
