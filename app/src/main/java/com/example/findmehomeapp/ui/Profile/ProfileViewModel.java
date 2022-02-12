package com.example.findmehomeapp.ui.Profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.findmehomeapp.Model.Model;
import com.example.findmehomeapp.Model.Post;

import java.util.List;

public class ProfileViewModel extends ViewModel {
    LiveData<List<Post>> data;

    public ProfileViewModel(){
        data = Model.instance.getAllUserPosts();
    }

    public LiveData<List<Post>> getData() {
        return data;
    }
}
