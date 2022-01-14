package com.example.findmehomeapp.ui.Profile;

import androidx.lifecycle.LiveData;

import com.example.findmehomeapp.Model.Model;
import com.example.findmehomeapp.Model.Post;

import java.util.List;

public class ProfileViewModel {
    LiveData<List<Post>> data;

    public ProfileViewModel(){
//        data = Model.instance.get();
    }
    public LiveData<List<Post>> getData() {
        return data;
    }
}
