package com.example.findmehomeapp.ui.Register;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.findmehomeapp.Model.Model;
import com.example.findmehomeapp.Model.ModelFirebase;
import com.example.findmehomeapp.Model.User;
import com.example.findmehomeapp.R;
import com.example.findmehomeapp.ui.Login.LoginFragmentDirections;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RegisterFragment extends Fragment {
    // TODO: add image

    EditText nameEt;
    EditText phoneEt;
    EditText emailEt;
    EditText passwordEt;
    EditText repasswordEt;
    Spinner genderSpinner;
    Spinner ageSpinner;
    Button registerBtn;

    Executor executor = Executors.newFixedThreadPool(1);

    ModelFirebase modelFirebase = new ModelFirebase();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_register, container, false);

        nameEt = view.findViewById(R.id.register_et_name);
        phoneEt = view.findViewById(R.id.register_phone_number);
        emailEt = view.findViewById(R.id.register_et_email);
        passwordEt = view.findViewById(R.id.register_et_password);
        repasswordEt = view.findViewById(R.id.register_et_repassword);
        genderSpinner = view.findViewById(R.id.register_gender_spinner);
        ageSpinner = view.findViewById(R.id.register_age_spinner);
        registerBtn = view.findViewById(R.id.register_btn_register);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndSave();
            }
        });

        return view;
    }

    private void validateAndSave(){
        registerBtn.setEnabled(false);
        String name = nameEt.getText().toString();
        String phone = phoneEt.getText().toString();
        String email = emailEt.getText().toString();
        String password = "";//passwordEt.getText().toString();
        String repassword = "";//repasswordEt.getText().toString();
        String gender = "";//genderSpinner.getSelectedItem().toString();
        String age = "";//ageSpinner.getSelectedItem().toString();

        User user = new User(name,phone,email,password,gender,age);

        modelFirebase.getAllUsers(new ModelFirebase.GetAllUsersListener() {
            @Override
            public void onComplete(List<User> list) {
            }
        });
        //TODO: check if user email is in the list of users
//        if (email != "") {
            // save user in firebase
            Model.instance.addUser(user,()->{
                Navigation.findNavController(nameEt).navigate(RegisterFragmentDirections.actionNavRegisterToNavProfile(user.getEmail()));
            });

            Log.d("TAG", "user has registered");
//        } else {
//            //TODO: alert to user that the email is already in use
//        }
    }
}