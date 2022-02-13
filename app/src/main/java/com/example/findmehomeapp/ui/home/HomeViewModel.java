package com.example.findmehomeapp.ui.home;

import androidx.annotation.MainThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.findmehomeapp.Model.Model;
import com.example.findmehomeapp.Model.Post;

import java.net.HttpCookie;
import java.util.List;

public class HomeViewModel extends ViewModel {

    LiveData<List<Post>> data;

    public HomeViewModel(){
        data = Model.instance.getAllPosts();
    }
    public LiveData<List<Post>> getData() {
        return data;
    }
}