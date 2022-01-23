package com.example.findmehomeapp.ui.Login;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findmehomeapp.Model.Model;
import com.example.findmehomeapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {
    EditText emailEt;
    EditText passwordEt;
    Button loginBtn;
    TextView registerTv;
    FirebaseAuth firebaseAuth;
    NavController navController;

    public LoginFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailEt = view.findViewById(R.id.login_et_email);
        passwordEt = view.findViewById(R.id.login_et_password);

        loginBtn = view.findViewById(R.id.login_btn_login);
        registerTv = view.findViewById(R.id.login_register_page);

        navController = Navigation.findNavController(view);
        firebaseAuth = FirebaseAuth.getInstance();


        registerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               navController.navigate(R.id.action_nav_login_to_nav_register);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEt.getText().toString();
                String password = passwordEt.getText().toString();


                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getContext(), "Credentials Required", Toast.LENGTH_SHORT).show();
                }

                if (password.length() < 6) {

                    passwordEt.setError("Password Length Must Be 6 or more Chars");

                }
                loginTheUser(email, password);
            }
        });
    }

    private void loginTheUser(String email, String password) {
        Model.instance.login(email, password, () -> {
            navController.navigate(R.id.action_global_nav_home);
        });

    }

    //This func is for that if the user is login to the app then he won't need to login but immediately will go to a profile page.
//    @Override
//    public void onStart() {
//        super.onStart();
//
//        if (firebaseAuth.getCurrentUser()!=null) {
//            navController.navigate(R.id.action_global_nav_home);
//        }
//    }
}
