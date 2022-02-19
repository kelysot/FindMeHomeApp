package com.example.findmehomeapp.ui.Login;

import static android.graphics.Color.rgb;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findmehomeapp.Model.Model;
import com.example.findmehomeapp.MyApplication;
import com.example.findmehomeapp.R;


import java.util.Locale;

public class LoginFragment extends Fragment {
    EditText emailEt;
    EditText passwordEt;
    Button loginBtn;
    TextView registerTv;
    ProgressBar progressBar;
    LoginViewModel viewModel;

    public LoginFragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                this.setEnabled(false);
                requireActivity().finish();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

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

        progressBar = view.findViewById(R.id.login_progressBar);
        progressBar.setVisibility(View.GONE);
        progressBar.getIndeterminateDrawable().setColorFilter(rgb(191, 215, 255), PorterDuff.Mode.MULTIPLY);

        registerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_nav_login_to_nav_register);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginTheUser();
            }
        });
    }

    private void loginTheUser() {
        progressBar.setVisibility(View.VISIBLE);
        loginBtn.setEnabled(false);
        emailEt.setEnabled(false);
        passwordEt.setEnabled(false);
        registerTv.setEnabled(false);

        String email = emailEt.getText().toString().trim().toLowerCase(Locale.ROOT);
        String password = passwordEt.getText().toString();

        if (email.isEmpty()) {
            emailEt.setError("Please enter your email.");
            progressBar.setVisibility(View.GONE);
            loginBtn.setEnabled(true);
            registerTv.setEnabled(true);
            emailEt.setEnabled(true);
            passwordEt.setEnabled(true);
            registerTv.setEnabled(true);
        } else if (!email.contains("@") || !email.contains(".") || email.endsWith(".") || email.contains("@.") ||
                email.contains(" ") || email.startsWith("@") || email.startsWith(".")) {
            emailEt.setError("Please enter a valid email address.");
            progressBar.setVisibility(View.GONE);
            loginBtn.setEnabled(true);
            registerTv.setEnabled(true);
            emailEt.setEnabled(true);
            passwordEt.setEnabled(true);
            registerTv.setEnabled(true);
        } else if (password.length() < 6) {
            passwordEt.setError("Password length must be 6 or more chars");
            progressBar.setVisibility(View.GONE);
            loginBtn.setEnabled(true);
            registerTv.setEnabled(true);
            emailEt.setEnabled(true);
            passwordEt.setEnabled(true);
            registerTv.setEnabled(true);
        } else {
            viewModel.Login(email, password, new Model.LoginListener() {
                @Override
                public void onComplete() {
                    viewModel.setData(email);
                    NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.action_global_nav_home);
                }

                @Override
                public void onFailure() {
                    Toast.makeText(MyApplication.getContext(), "Sorry, your password or email were incorrect, please try again.", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    loginBtn.setEnabled(true);
                    registerTv.setEnabled(true);
                    emailEt.setEnabled(true);
                    passwordEt.setEnabled(true);
                    registerTv.setEnabled(true);
                }
            });
        }
    }


    //This func is for that if the user is login to the app then he won't need to login but immediately will go to a profile page.
    @Override
    public void onStart() {
        super.onStart();

        if (Model.instance.getConnectedUser() != null) {
            NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.action_global_nav_home);
        }
    }
}
