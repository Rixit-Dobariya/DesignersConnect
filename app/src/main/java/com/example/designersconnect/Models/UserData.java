package com.example.designersconnect.Models;

public class UserData {
    String userId, username, displayName, jobTitle, profilePicture;
    UserData(){}
    public UserData(String userId, String username, String displayName, String jobTitle, String profilePicture)
    {
        this.userId=userId;
        this.username=username;
        this.displayName=displayName;
        this.jobTitle=jobTitle;
        this.profilePicture=profilePicture;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getJobTitle() {
        return jobTitle;
    }
}
