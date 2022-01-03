package com.example.findmehomeapp.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.LinkedList;
import java.util.List;

@Entity
public class Post {
    @PrimaryKey
    @NonNull
    String id = "";
    String userId = "";
    String text = "";
    String image = "";
    String type = "";
    String age = "";
    String size = "";
    String gender = "";
    String location = "";
    List<String> likesUserId; //that everyone can do like only once
//    List<Comment> comments = new LinkedList<>();


    public Post(){} //for room

    public Post(String id, String userId, String text, String image, String type, String age, String size, String gender, String location, List<String> likesUserId) {
        this.id = id;
        this.userId = userId;
        this.text = text;
        this.image = image;
        this.type = type;
        this.age = age;
        this.size = size;
        this.gender = gender;
        this.location = location;
        this.likesUserId = likesUserId;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", text='" + text + '\'' +
                ", image='" + image + '\'' +
                ", type='" + type + '\'' +
                ", age='" + age + '\'' +
                ", size='" + size + '\'' +
                ", gender='" + gender + '\'' +
                ", location='" + location + '\'' +
                ", likesUserId=" + likesUserId +
                '}';
    }

    public List<String> getLikesUserId() {
        return likesUserId;
    }

    public void setLikesUserId(List<String> likesUserId) {
        this.likesUserId = likesUserId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

}
