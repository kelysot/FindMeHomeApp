package com.example.findmehomeapp.ui.Login;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.findmehomeapp.R;

public class LoginFragment extends Fragment {
    EditText emailEt;
    EditText passwordEt;
    Button loginBtn;
    TextView registerTv;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        emailEt = view.findViewById(R.id.login_et_email);
        passwordEt = view.findViewById(R.id.login_et_password);

        loginBtn = view.findViewById(R.id.login_btn_login);
        registerTv = view.findViewById(R.id.login_register_page);

        registerTv.setOnClickListener((v)-> {
            Navigation.findNavController(v).navigate(LoginFragmentDirections.actionNavLoginToNavRegister());
        });
        loginBtn.setOnClickListener((v)-> {
            //TODO: valid user email and password
            Navigation.findNavController(v).navigate(LoginFragmentDirections.actionGlobalNavProfile(emailEt.getText().toString()));
        });

        return view;
    }
}