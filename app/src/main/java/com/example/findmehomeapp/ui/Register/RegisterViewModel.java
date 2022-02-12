package com.example.findmehomeapp.ui.Register;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.findmehomeapp.Model.Model;
import com.example.findmehomeapp.Model.User;

public class RegisterViewModel extends ViewModel {

    MutableLiveData<User> data;

    public RegisterViewModel(){
        data = new MutableLiveData<>();
    }

    public void Register(User user, Model.AddUserListener listener){
        Model.instance.addUser(user, listener);
    }

    public void setData(User user){
        data.setValue(user);
    }
}
