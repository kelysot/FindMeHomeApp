package com.example.findmehomeapp.ui.EditProfile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.findmehomeapp.Model.Model;
import com.example.findmehomeapp.Model.User;

public class EditProfileViewModel extends ViewModel {


    MutableLiveData<User> data;

    public EditProfileViewModel() {
        data = new MutableLiveData<>();
    }

    public void EditUser(User user, Model.EditUserListener listener) {
        data.setValue(user);
        Model.instance.editUser(user, listener);
    }

    public void GetUserById(Model.GetUserById listener) {
        Model.instance.getUserById(Model.instance.getConnectedUserId(), listener);
    }

    public void setData(User user) {
        data.setValue(user);
    }

    public void setGender(String gender) {
        data.getValue().setGender(gender);
    }
}









