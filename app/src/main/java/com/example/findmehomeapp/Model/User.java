package com.example.findmehomeapp.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;
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
    Long updateDate = new Long(0);
    String avatarUrl = "";

    public User(){} //for room

    public User(String id, String name, String phone, String email, String password, String location) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.location = location;
        this.avatarUrl = null;
//        this.posts = posts;
    }
//    //TODO: add location
//    @Ignore
//    public User(String id, String name, String phone, String email, String password, String location, String avatarUrl) {
//        this.id = id;
//        this.name = name;
//        this.phone = phone;
//        this.email = email;
//        this.password = password;
//        this.location = location;
//        this.avatarUrl = avatarUrl;
////        this.posts = posts;
//    }




    public void setUpdateDate(Long updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", location='" + location + '\'' +
                ", updateDate=" + updateDate +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }

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

    public Long getUpdateDate() {
        return updateDate;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public static User create(Map<String, Object> json) {
        String id = (String) json.get("id");
        String name = (String) json.get("name");
        String phone = (String) json.get("phone");
        String email = (String) json.get("email");
        String password = (String) json.get("password");
        String location = (String) json.get("location");
        String avatarUrl = (String) json.get("avatarUrl");

//        Timestamp ts = (Timestamp)json.get("updateDate");
//        Long updateDate = ts.getSeconds();
        User user = new User(id, name,phone,email,password,location);
        user.setAvatarUrl(avatarUrl);

//        user.setUpdateDate(updateDate);
        return user;
    }

    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("id",id);
        json.put("name",name);
        json.put("phone",phone);
        json.put("email",email);
        json.put("password",password);
        json.put("location",location);
        json.put("avatarUrl",avatarUrl);
        //json.put("updateDate", FieldValue.serverTimestamp());

        return json;
    }
}
