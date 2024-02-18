package com.example.designersconnect.Models;

public class Post {
    private String postId, userId, postPicture, description;
    private long uploadTime;

    public Post(String postId, String userId, String postPicture, String description) {
        this.postId = postId;
        this.userId = userId;
        this.postPicture = postPicture;
        this.description = description;
        this.uploadTime = System.currentTimeMillis();
    }

    public Post() {
    }

    public String getPostId() {
        return postId;
    }

    public String getUserId() {
        return userId;
    }

    public String getPostPicture() {
        return postPicture;
    }

    public String getDescription() {
        return description;
    }

    public long getUploadTime() {
        return uploadTime;
    }
}
