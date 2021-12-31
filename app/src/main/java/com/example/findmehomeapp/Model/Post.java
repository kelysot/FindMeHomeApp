package com.example.findmehomeapp.Model;

import java.util.LinkedList;
import java.util.List;

public class Post {

    String userId = "";
    String text = "";
    String image = "";
    String type = "";
    String age = "";
    String size = "";
    String gender = "";
    String location = "";
    String numOfLikes = "";
    List<Comment> comments = new LinkedList<>();

    public Post(String userId, String text, String image, String type, String age, String size, String gender, String location, String numOfLikes, List<Comment> comments) {
        this.userId = userId;
        this.text = text;
        this.image = image;
        this.type = type;
        this.age = age;
        this.size = size;
        this.gender = gender;
        this.location = location;
        this.numOfLikes = numOfLikes;
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Post{" +
                "userId='" + userId + '\'' +
                ", text='" + text + '\'' +
                ", image='" + image + '\'' +
                ", type='" + type + '\'' +
                ", age='" + age + '\'' +
                ", size='" + size + '\'' +
                ", gender='" + gender + '\'' +
                ", location='" + location + '\'' +
                ", numOfLikes='" + numOfLikes + '\'' +
                ", comments=" + comments +
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNumOfLikes() {
        return numOfLikes;
    }

    public void setNumOfLikes(String numOfLikes) {
        this.numOfLikes = numOfLikes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
