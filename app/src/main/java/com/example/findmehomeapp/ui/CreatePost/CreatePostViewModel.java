package com.example.findmehomeapp.ui.CreatePost;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.findmehomeapp.Model.Model;
import com.example.findmehomeapp.Model.Post;

public class CreatePostViewModel extends ViewModel {
    MutableLiveData<Post> data;

    public CreatePostViewModel() {
        data = new MutableLiveData<>();
    }

    public void savePost(Model.AddPostListener listener) {
        Post post = data.getValue();
        Model.instance.addPost(post, listener);
    }

    public void setData(Post post) {
        data.setValue(post);
    }

    public void setGender(String gender) {
        data.getValue().setGender(gender);
    }


    public String getPostId() {
        return data.getValue().getId();
    }

    public void setText(String text) {
        data.getValue().setText(text);
    }

    public void setAge(String age) {
        data.getValue().setAge(age);
    }

    public void setLocation(String location) {
        data.getValue().setLocation(location);
    }

    public void setSize(String size) {
        data.getValue().setSize(size);
    }

    public void setType(String type) {
        data.getValue().setType(type);
    }

    public void setImage(String url) {
        data.getValue().setImage(url);
    }
}
