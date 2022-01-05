package com.example.findmehomeapp.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class User {
    public static final String COLLECTION_NAME = "users";

    @PrimaryKey
    @NonNull
    String id = "";
    String name = "";
    String phone = "";
    String email = "";
    String password = "";
    String location = "";
    String gender = "";
    String age = "";
    Long updateDate = new Long(0);

    public void setUpdateDate(Long updateDate) {
        this.updateDate = updateDate;
    }
//    List<Post> posts;

    public User(){} //for room

    //TODO: add location
    public User(String name, String phone, String email, String password, String gender, String age) {
//        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.location = location;
        this.gender = gender;
        this.age = age;
//        this.posts = posts;
    }

    @Override
    public String toString() {
        return "User{" +
//                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", location='" + location + '\'' +
                ", gender='" + gender + '\'' +
                ", age='" + age + '\'' +
//                ", posts=" + posts +
                '}';
    }

//    public List<Post> getPosts() {
//        return posts;
//    }

//    public void setPosts(List<Post> posts) {
//        this.posts = posts;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public Long getUpdateDate() {
        return updateDate;
    }

    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("id",id);
        json.put("name",name);
        json.put("phone",phone);
        json.put("email",email);
        json.put("password",password);
        json.put("gender",gender);
        json.put("age",age);
        json.put("updateDate", FieldValue.serverTimestamp());

        return json;
    }
}
