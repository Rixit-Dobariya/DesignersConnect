package com.example.designersconnect.Models;

public class Comment {
    String publisherId, comment, commentId;
    long commentTime;

    public Comment(String commentId, String publisherId, String comment) {
        this.commentId = commentId;
        this.publisherId = publisherId;
        this.comment = comment;
        commentTime = System.currentTimeMillis();
    }

    public String getCommentId() {
        return commentId;
    }

    public Comment() {
    }


    public String getPublisherId() {
        return publisherId;
    }

    public String getComment() {
        return comment;
    }

    public long getCommentTime() {
        return commentTime;
    }
}
