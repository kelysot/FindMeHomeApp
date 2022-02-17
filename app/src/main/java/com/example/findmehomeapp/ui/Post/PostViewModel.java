package com.example.findmehomeapp.ui.Post;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.findmehomeapp.Model.Model;
import com.example.findmehomeapp.Model.Post;

public class PostViewModel extends ViewModel {

    MutableLiveData<Post> data;

    public PostViewModel(){
        data = new MutableLiveData<>();
    }

    public void setData(Post post){
        data.setValue(post);
    }

    public void GetPostById(String postId, Model.GetPostById listener){
        Model.instance.getPostById(postId, listener);
    }

    public Post getData() {
        return data.getValue();
    }
}
