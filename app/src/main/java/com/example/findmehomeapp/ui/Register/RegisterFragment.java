package com.example.findmehomeapp.ui.Register;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findmehomeapp.Model.Model;
import com.example.findmehomeapp.Model.User;
import com.example.findmehomeapp.R;
import com.example.findmehomeapp.ui.Login.LoginViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;

public class RegisterFragment extends Fragment {

    EditText nameEt;
    EditText phoneEt;
    EditText emailEt;
    EditText passwordEt;
    TextView goLoginTv;
    TextView wrongMessageTv;
    Spinner genderSpinner;
    Button registerBtn;
    NavController navController;
    String userid;
    String genderS;
    ImageView picture;
    ImageView addPicture;
    Bitmap imageBitmap;
    ProgressBar progressBar;
    RegisterViewModel viewModel;



    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;


    public RegisterFragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        picture = view.findViewById(R.id.register_user_imageView);
        addPicture = view.findViewById(R.id.register_add_pic_imv);
        nameEt = view.findViewById(R.id.register_et_name);
        phoneEt = view.findViewById(R.id.register_phone_number);
        emailEt = view.findViewById(R.id.register_et_email);
        passwordEt = view.findViewById(R.id.register_et_password);
        goLoginTv = view.findViewById(R.id.register_tv_gologin);
        wrongMessageTv = view.findViewById(R.id.register_wrong_message);
        wrongMessageTv.setVisibility(View.GONE);

        progressBar = view.findViewById(R.id.register_progressBar);
        progressBar.setVisibility(View.GONE);


        genderSpinner = view.findViewById(R.id.register_gender_spinner);
        ArrayAdapter<CharSequence> adapterGender = ArrayAdapter.createFromResource(this.getContext(),
                R.array.gender, android.R.layout.simple_spinner_item);
        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapterGender);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genderS = parent.getItemAtPosition(position).toString();
                //Toast.makeText(parent.getContext(), gender, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });

        registerBtn = view.findViewById(R.id.register_btn_register);
        navController = Navigation.findNavController(view);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        goLoginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_global_nav_login);
            }
        });
    }

    private void showImagePickDialog() {

        String[] items = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Choose an Option");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                if (i == 0) {
                    openCam();
                }

                if (i == 1) {
                    openGallery();
                }
            }
        });

        builder.create().show();
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLERY);
    }

    private void openCam() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                picture.setImageBitmap(imageBitmap);
                Log.d("TAG33", "imageBitmap name:" + imageBitmap);

            }
        }
        if (requestCode == REQUEST_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {
                        imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                        picture.setImageBitmap(imageBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void registerUser() {
        progressBar.setVisibility(View.VISIBLE);
        registerBtn.setEnabled(false);
        addPicture.setEnabled(false);

        String email = emailEt.getText().toString().trim();
        String password = passwordEt.getText().toString().trim();
        String name = nameEt.getText().toString();
        String phone = phoneEt.getText().toString();

        if (name.isEmpty()) {
            nameEt.setError("Please enter your full name.");
        }
        else if (phone.isEmpty()) {
            phoneEt.setError("Please enter your phone number.");
        }
        else if (email.isEmpty()) {
            emailEt.setError("Please enter your email address.");
        }
        else if(!email.contains("@") || !email.contains(".")){
            emailEt.setError("Please enter a valid email address.");
        }
        else if (password.length() < 6) {
            passwordEt.setError("Password length must be 6 or more chars.");
        }
        else{
            User user = new User("0", name, phone, email, password, genderS, "true");

            if (imageBitmap == null) {
                viewModel.Register(user, newUser -> {
                    viewModel.setData(newUser);
                    navController.navigate(R.id.action_global_nav_home);
                });
            }
            else {
                Model.instance.saveImage(imageBitmap, email + ".jpg", url -> {
                    user.setAvatarUrl(url);
                    viewModel.Register(user, newUser -> {
                        viewModel.setData(newUser);
                        navController.navigate(R.id.action_global_nav_home);
                    });
                });
            }

        }

        progressBar.setVisibility(View.GONE);
        registerBtn.setEnabled(true);
        addPicture.setEnabled(true);

        if(email.contains("@") && email.contains(".") && password.length() > 6){
            wrongMessageTv.setVisibility(View.VISIBLE);
        }

    }
}