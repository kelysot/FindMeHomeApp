package com.example.findmehomeapp.Model;

public class Comment {
    String userId = "";
    String text = "";

    public Comment(String userId, String text) {
        this.userId = userId;
        this.text = text;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "userId='" + userId + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
