package com.example.findmehomeapp.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.HashMap;
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
    String gender = "";
    Long updateDate = new Long(0);
    String avatarUrl = "";
    String connected = "";

    public User(){} //for room

    public User(String id, String name, String phone, String email, String gender, String connected) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.gender = gender;
        this.avatarUrl = null;
        this.connected = connected;
    }

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
                ", gender='" + gender + '\'' +
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getConnected() {
        return connected;
    }

    public void setConnected(String connected) {
        this.connected = connected;
    }

    public static User create(Map<String, Object> json) {
        String id = (String) json.get("id");
        String name = (String) json.get("name");
        String phone = (String) json.get("phone");
        String email = (String) json.get("email");
        String gender = (String) json.get("gender");
        String avatarUrl = (String) json.get("avatarUrl");
        String connected = (String) json.get("connected");

        User user = new User(id, name,phone,email,gender,connected);
        user.setAvatarUrl(avatarUrl);

        return user;
    }

    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("id",id);
        json.put("name",name);
        json.put("phone",phone);
        json.put("email",email);
        json.put("gender",gender);
        json.put("avatarUrl",avatarUrl);
        json.put("connected",connected);

        return json;
    }
}
