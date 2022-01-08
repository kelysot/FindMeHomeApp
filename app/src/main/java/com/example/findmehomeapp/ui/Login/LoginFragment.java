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
import android.widget.TextView;

import com.example.findmehomeapp.R;

public class LoginFragment extends Fragment {
    Button registerBtn;
    TextView registerTv;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        registerBtn = view.findViewById(R.id.login_btn_login);
        registerTv = view.findViewById(R.id.login_register_page);

        registerTv.setOnClickListener((v)-> {
            Navigation.findNavController(v).navigate(LoginFragmentDirections.actionNavLoginToNavRegister());
        });
        registerBtn.setOnClickListener((v)-> {
//            Navigation.findNavController(v).navigate(LoginFragmentDirections.actionNavLoginToNavRegister());
        });

        return view;
    }
}