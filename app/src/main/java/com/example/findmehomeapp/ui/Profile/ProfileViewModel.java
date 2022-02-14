package com.example.findmehomeapp.ui.Profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.findmehomeapp.Model.Model;
import com.example.findmehomeapp.Model.Post;
import com.example.findmehomeapp.Model.User;

import java.util.List;

public class ProfileViewModel extends ViewModel {
    LiveData<List<Post>> data;
    MutableLiveData<User> userData;

    public ProfileViewModel(){
        data = Model.instance.getAllUserPosts();
        userData = new MutableLiveData<>();
    }

    public void setUserData(User user){
        userData.setValue(user);
    }

    public void GetUserById(String userId, Model.GetUserById listener){
        Model.instance.getUserById(userId, listener);
    }

    public LiveData<List<Post>> getData() {
        return data;
    }
}
