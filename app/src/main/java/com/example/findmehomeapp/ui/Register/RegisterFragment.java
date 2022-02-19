package com.example.findmehomeapp.ui.Register;

import static android.graphics.Color.rgb;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
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
import com.example.findmehomeapp.MyApplication;
import com.example.findmehomeapp.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterFragment extends Fragment {

    EditText nameEt;
    EditText phoneEt;
    EditText emailEt;
    EditText passwordEt;
    TextView goLoginTv;
    Spinner genderSpinner;
    Button registerBtn;
    String genderS;
    CircleImageView picture;
    ImageView addPicture;
    int requestCode;
    Bitmap imageBitmap;
    ProgressBar progressBar;
    RegisterViewModel viewModel;
    int flag = 0;

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;


    public RegisterFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        progressBar = view.findViewById(R.id.register_progressBar);
        progressBar.setVisibility(View.GONE);
        progressBar.getIndeterminateDrawable().setColorFilter(rgb(191, 215, 255), PorterDuff.Mode.MULTIPLY);


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

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        goLoginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_global_nav_login);
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

    private void saveImageToFile(Bitmap imageBitmap, String imageFileName){
        try {
            File dir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File imageFile = new File(dir,imageFileName);
            imageFile.createNewFile();
            OutputStream out = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            addPicureToGallery(imageFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addPicureToGallery(File imageFile){
        //add the picture to the gallery so we dont need to manage the cache size
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(imageFile);
        mediaScanIntent.setData(contentUri);
        MyApplication.getContext().sendBroadcast(mediaScanIntent);
    }

    private String getLocalImageFileName(String url) {
        String name = URLUtil.guessFileName(url, null, null);
        return name;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.requestCode = requestCode;
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                picture.setImageBitmap(imageBitmap);
                String email = emailEt.getText().toString().trim().toLowerCase(Locale.ROOT);
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
        genderSpinner.setEnabled(false);
        nameEt.setEnabled(false);
        phoneEt.setEnabled(false);
        emailEt.setEnabled(false);
        passwordEt.setEnabled(false);
        goLoginTv.setEnabled(false);

        String email = emailEt.getText().toString().trim().toLowerCase(Locale.ROOT);
        String password = passwordEt.getText().toString().trim();
        String name = nameEt.getText().toString();
        String phone = phoneEt.getText().toString();

        if (name.isEmpty()) {
            nameEt.setError("Please enter your full name.");
            progressBar.setVisibility(View.GONE);
            registerBtn.setEnabled(true);
            addPicture.setEnabled(true);
            genderSpinner.setEnabled(true);
            nameEt.setEnabled(true);
            phoneEt.setEnabled(true);
            emailEt.setEnabled(true);
            passwordEt.setEnabled(true);
            goLoginTv.setEnabled(true);

        } else if (phone.isEmpty()) {
            phoneEt.setError("Please enter your phone number.");
            progressBar.setVisibility(View.GONE);
            registerBtn.setEnabled(true);
            addPicture.setEnabled(true);
            genderSpinner.setEnabled(true);
            nameEt.setEnabled(true);
            phoneEt.setEnabled(true);
            emailEt.setEnabled(true);
            passwordEt.setEnabled(true);
            goLoginTv.setEnabled(true);
        }else if (!phone.startsWith("0")) {
            phoneEt.setError("Your phone number should start with the number 0.");
            progressBar.setVisibility(View.GONE);
            registerBtn.setEnabled(true);
            addPicture.setEnabled(true);
            genderSpinner.setEnabled(true);
            nameEt.setEnabled(true);
            phoneEt.setEnabled(true);
            emailEt.setEnabled(true);
            passwordEt.setEnabled(true);
            goLoginTv.setEnabled(true);
        }else if (phone.length() <9 || phone.length() > 10 ) {
            phoneEt.setError("Your phone number length should be 10 or 9 numbers, examples: 05X-XXXXXXX or 0A-XXXXXXX.");
            progressBar.setVisibility(View.GONE);
            registerBtn.setEnabled(true);
            addPicture.setEnabled(true);
            genderSpinner.setEnabled(true);
            nameEt.setEnabled(true);
            phoneEt.setEnabled(true);
            emailEt.setEnabled(true);
            passwordEt.setEnabled(true);
            goLoginTv.setEnabled(true);
        } else if (email.isEmpty()) {
            emailEt.setError("Please enter your email address.");
            progressBar.setVisibility(View.GONE);
            registerBtn.setEnabled(true);
            addPicture.setEnabled(true);
            genderSpinner.setEnabled(true);
            nameEt.setEnabled(true);
            phoneEt.setEnabled(true);
            emailEt.setEnabled(true);
            passwordEt.setEnabled(true);
            goLoginTv.setEnabled(true);
        } else if (!email.contains("@") || !email.contains(".") || email.endsWith(".") || email.contains("@.") ||
                email.contains(" ") || email.startsWith("@") ||email.startsWith(".")) {
            emailEt.setError("Please enter a valid email address.");
            progressBar.setVisibility(View.GONE);
            registerBtn.setEnabled(true);
            addPicture.setEnabled(true);
            genderSpinner.setEnabled(true);
            nameEt.setEnabled(true);
            phoneEt.setEnabled(true);
            emailEt.setEnabled(true);
            passwordEt.setEnabled(true);
            goLoginTv.setEnabled(true);
        } else if (password.length() < 6) {
            passwordEt.setError("Password length must be 6 or more chars.");
            progressBar.setVisibility(View.GONE);
            registerBtn.setEnabled(true);
            addPicture.setEnabled(true);
            genderSpinner.setEnabled(true);
            nameEt.setEnabled(true);
            phoneEt.setEnabled(true);
            emailEt.setEnabled(true);
            passwordEt.setEnabled(true);
            goLoginTv.setEnabled(true);
        } else {
            flag = 1;
            User user = new User("0", name, phone, email, genderS, "true");

            if (imageBitmap == null) {
                viewModel.Register(user, password, new Model.AddUserListener() {
                    @Override
                    public void onComplete(User user) {
                        viewModel.setData(user);
                        NavHostFragment.findNavController(RegisterFragment.this).navigate(R.id.action_global_nav_home);
                    }

                    @Override
                    public void onFailure() {
                        progressBar.setVisibility(View.GONE);
                        registerBtn.setEnabled(true);
                        addPicture.setEnabled(true);
                        genderSpinner.setEnabled(true);
                        nameEt.setEnabled(true);
                        phoneEt.setEnabled(true);
                        emailEt.setEnabled(true);
                        passwordEt.setEnabled(true);
                        goLoginTv.setEnabled(true);
                    }
                });
            } else {
                Model.instance.saveImage(imageBitmap, email + ".jpg", url -> {
                    user.setAvatarUrl(url);
                    if (requestCode == REQUEST_CAMERA) {
                        saveImageToFile(imageBitmap, getLocalImageFileName(url));
                    }
                    viewModel.Register(user, password, new Model.AddUserListener() {
                        @Override
                        public void onComplete(User user) {
                            viewModel.setData(user);
                            NavHostFragment.findNavController(RegisterFragment.this).navigate(R.id.action_global_nav_home);
                        }

                        @Override
                        public void onFailure() {
                            progressBar.setVisibility(View.GONE);
                            registerBtn.setEnabled(true);
                            addPicture.setEnabled(true);
                            genderSpinner.setEnabled(true);
                            nameEt.setEnabled(true);
                            phoneEt.setEnabled(true);
                            emailEt.setEnabled(true);
                            passwordEt.setEnabled(true);
                            goLoginTv.setEnabled(true);
                        }
                    });
                });
            }
        }
    }
}