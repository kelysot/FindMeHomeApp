package com.example.findmehomeapp.ui.EditPost;

import android.widget.ProgressBar;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.findmehomeapp.Model.Model;
import com.example.findmehomeapp.Model.Post;

public class EditPostViewModel extends ViewModel {

    MutableLiveData<Post> data;

    public EditPostViewModel(){
        data = new MutableLiveData<>();
    }

    public void EditPost(Model.UpdatePostListener listener){
        Post post = data.getValue();
        Model.instance.savePost(post, listener);
    }

    public void DeletePost(Model.DeletePostListener listener) {
        Post post = data.getValue();
        Model.instance.deletePost(post, listener);
    }

    public void GetPostById(String postId, Model.GetPostById listener){
        Model.instance.getPostById(postId, listener);
    }

    public void setData(Post post){
        data.setValue(post);
    }

    public void setGender(String gender){
        data.getValue().setGender(gender);
    }

    public void setText(String text){
        data.getValue().setText(text);
    }

    public void setAge(String age){
        data.getValue().setAge(age);
    }

    public void setLocation(String location){
        data.getValue().setLocation(location);
    }

    public void setSize(String size){
        data.getValue().setSize(size);
    }

    public void setType(String type){
        data.getValue().setType(type);
    }

    public void setImage(String url){
        data.getValue().setImage(url);
    }
}
