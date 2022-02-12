package com.example.findmehomeapp.ui.Login;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.findmehomeapp.Model.Model;
import com.example.findmehomeapp.Model.ModelFirebase;
import com.example.findmehomeapp.Model.User;

public class LoginViewModel extends ViewModel {
    MutableLiveData<User> data;

    public LoginViewModel(){
        data = new MutableLiveData<>();
    }
    public void Login(String email, String password, Model.LoginListener listener){
        Model.instance.login(email, password, listener);
    }

    public void setData(String email){
        Model.instance.getUserByEmail(email, new Model.GetUserByEmail() {
            @Override
            public void onComplete(User user) {
                data.setValue(user);
            }
        });
    }
}
