package com.example.designersconnect.Fragments;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.designersconnect.R;
import com.example.designersconnect.Models.Post;
import com.example.designersconnect.databinding.FragmentAddPostBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddPostFragment extends Fragment {

    FragmentAddPostBinding binding;
    Uri selectedImageUri;
    ActivityResultLauncher<Intent> activityResultLauncher;
    String postPicture;
    DatabaseReference databaseReference;
    String description;
    Dialog customDialog;

    public AddPostFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddPostBinding.inflate(inflater,container,false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            selectedImageUri = data.getData();
                            binding.imgPost.setImageURI(selectedImageUri);
                        }
                    }
                });

        binding.imgPost.setOnClickListener(v->{
            Toast.makeText(getActivity(), "Image clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activityResultLauncher.launch(intent);
        });

        binding.btnPost.setOnClickListener(v -> {
            uploadImage();

        });
        return binding.getRoot();
    }
    public void updateProfilePictureInDatabase()
    {
        String postId=databaseReference.child("posts").push().getKey();
        String userId= FirebaseAuth.getInstance().getUid();

        Post post = new Post(postId,userId,postPicture,description);
        databaseReference.child("posts").child(postId).setValue(post);
        binding.etDescription.setText("");
        binding.imgPost.setImageResource(R.drawable.ic_launcher_background);
        Toast.makeText(getActivity(), "Post added successfully!", Toast.LENGTH_SHORT).show();
        customDialog.dismiss();
    }

    private void uploadImage() {
        description = binding.etDescription.getText().toString();
        if(description.isEmpty())
        {
            Toast.makeText(getActivity(), "Please enter description", Toast.LENGTH_SHORT).show();
        }
        else if (selectedImageUri != null) {
            customDialog = new Dialog(getActivity());
            customDialog.setContentView(R.layout.process);
            customDialog.setCancelable(false);
            customDialog.show();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageReference.child("posts/" + System.currentTimeMillis() + ".jpg");

            imageRef.putFile(selectedImageUri)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            Uri downloadUri = task.getResult();
                                            postPicture=downloadUri.toString();
                                            updateProfilePictureInDatabase();
                                        } else {
                                            Toast.makeText(getActivity(), "Failed to get download URL", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }
}