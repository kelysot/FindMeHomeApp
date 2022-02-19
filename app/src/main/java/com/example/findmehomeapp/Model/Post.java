package com.example.findmehomeapp.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

@Entity
public class Post {
    final public static String COLLECTION_NAME = "posts";

    @PrimaryKey
    @NonNull
    String id = "";
    String name ="";
    String userId = "";
    String text = "";
    String image = "";
    String type = "";
    String age = "";
    String size = "";
    String gender = "";
    String location = "";
    Long updateDate = new Long(0);

    public Post(){}

    @Ignore
    public Post(String userId, String text, String image, String type, String age, String size, String gender, String location){  //}), List<String> likesUserId) {
        this.userId = userId;
        this.text = text;
        this.image = image;
        this.type = type;
        this.age = age;
        this.size = size;
        this.gender = gender;
        this.location = location;
    }

    public static Post create(String postId, Map<String, Object> json) {
        String id = postId;
        String userId = (String) json.get("userId");
        String text = (String) json.get("text");
        String image = (String) json.get("image");
        String type = (String) json.get("type");
        String age = (String) json.get("age");
        String size = (String) json.get("size");
        String gender = (String) json.get("gender");
        String location = (String) json.get("location");

        Timestamp ts = (Timestamp)json.get("updateDate");
        Long updateDate = ts.getSeconds();

        Post post = new Post(userId,text,image,type,age,size, gender,location);
        post.setUpdateDate(updateDate);

        post.setId(postId);
        return post;
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
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Long updateDate) {
        this.updateDate = updateDate;
    }

    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("id",id);
        json.put("userId",userId);
        json.put("text",text);
        json.put("image",image);
        json.put("type",type);
        json.put("age",age);
        json.put("size",size);
        json.put("gender",gender);
        json.put("location",location);
        json.put("updateDate", FieldValue.serverTimestamp());

        return json;
    }
}
