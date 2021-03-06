package com.example.findmehomeapp.ui.EditProfile;

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
import android.widget.Toast;

import com.example.findmehomeapp.Model.Model;
import com.example.findmehomeapp.Model.User;
import com.example.findmehomeapp.MyApplication;
import com.example.findmehomeapp.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;

import de.hdodenhof.circleimageview.CircleImageView;


public class EditProfileFragment extends Fragment {

    EditProfileViewModel viewModel;
    EditText nameEt;
    EditText phoneEt;
    Spinner genderSpinner;
    Button saveBtn;
    String genderS;
    CircleImageView picture;
    ImageView addPicture;
    int requestCode;
    Bitmap imageBitmap;
    int flag = 0;
    int flagPic = 0;

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    ProgressBar progressBar;


    public EditProfileFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        viewModel.GetUserById(user -> {
            viewModel.setData(user);
            nameEt.setText(user.getName());
            phoneEt.setText(user.getPhone());
            if (user.getAvatarUrl() != null) {
                Picasso.get().load(user.getAvatarUrl()).into(picture);
            }
        });

        picture = view.findViewById(R.id.edit_profile_user_imageView);
        addPicture = view.findViewById(R.id.edit_profile_add_pic_imv);
        nameEt = view.findViewById(R.id.edit_profile_et_name);
        phoneEt = view.findViewById(R.id.edit_profile_phone_number);
        progressBar = view.findViewById(R.id.edit_profile_progressBar);
        progressBar.setVisibility(View.GONE);
        progressBar.getIndeterminateDrawable().setColorFilter(rgb(191, 215, 255), PorterDuff.Mode.MULTIPLY);

        genderSpinner = view.findViewById(R.id.edit_profile_gender_spinner);
        ArrayAdapter<CharSequence> adapterGender = ArrayAdapter.createFromResource(this.getContext(),
                R.array.gender, android.R.layout.simple_spinner_item);
        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapterGender);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genderS = parent.getItemAtPosition(position).toString();
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

        saveBtn = view.findViewById(R.id.edit_profile_btn_register);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        return view;
    }

    private void showImagePickDialog() {

        viewModel.GetUserById(user -> {
            if (user.getAvatarUrl() != null || flagPic == 1) {
                String[] items = {"Camera", "Gallery", "Delete Photo"};
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
                        if (i == 2) {
                            deleteImage();
                        }
                    }
                });

                builder.create().show();
            } else {
                flagPic = 1;
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
        });

    }

    private void deleteImage() {
        imageBitmap = null;
        picture.setImageBitmap(null);
        picture.setBackgroundResource(R.drawable.user);
        flag = 1;
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


    //To show user's gender information from the database.
    @Override
    public void onResume() {
        super.onResume();
        AtomicInteger genderPos = new AtomicInteger();

        viewModel.GetUserById(user -> {
            viewModel.setGender(user.getGender());
            switch (viewModel.data.getValue().getGender()) {
                case "Female":
                    genderPos.set(0);
                    break;
                case "Male":
                    genderPos.set(1);
                    break;
            }
            genderSpinner.setSelection(genderPos.get());
        });
    }

    public void save() {
        progressBar.setVisibility(View.VISIBLE);
        saveBtn.setEnabled(false);
        addPicture.setEnabled(false);
        genderSpinner.setEnabled(false);
        nameEt.setEnabled(false);
        phoneEt.setEnabled(false);

        String fullName = nameEt.getText().toString();
        String phone = phoneEt.getText().toString();

        if (fullName.isEmpty()) {
            nameEt.setError("Please enter your full name.");
            progressBar.setVisibility(View.GONE);
            saveBtn.setEnabled(true);
            addPicture.setEnabled(true);
            genderSpinner.setEnabled(true);
            nameEt.setEnabled(true);
            phoneEt.setEnabled(true);
        } else if (phone.isEmpty()) {
            phoneEt.setError("Please enter your phone number.");
            progressBar.setVisibility(View.GONE);
            saveBtn.setEnabled(true);
            addPicture.setEnabled(true);
            genderSpinner.setEnabled(true);
            nameEt.setEnabled(true);
            phoneEt.setEnabled(true);
        } else if (!phone.startsWith("0")) {
            phoneEt.setError("Your phone number should start with the number 0.");
            progressBar.setVisibility(View.GONE);
            saveBtn.setEnabled(true);
            addPicture.setEnabled(true);
            genderSpinner.setEnabled(true);
            nameEt.setEnabled(true);
            phoneEt.setEnabled(true);
        } else if (phone.length() < 9 || phone.length() > 10) {
            phoneEt.setError("Your phone number length should be 10 or 9 numbers, examples: 05X-XXXXXXX or 0A-XXXXXXX.");
            progressBar.setVisibility(View.GONE);
            saveBtn.setEnabled(true);
            addPicture.setEnabled(true);
            genderSpinner.setEnabled(true);
            nameEt.setEnabled(true);
            phoneEt.setEnabled(true);
        } else {
            // saveBtn.setEnabled(true);
            viewModel.setGender(genderS);
            User user = new User(viewModel.getConnectedUserId() , fullName, phone, viewModel.data.getValue().getEmail(),
                    viewModel.data.getValue().getGender(), "true");
            user.setAvatarUrl(viewModel.data.getValue().getAvatarUrl());

            if (imageBitmap == null) {
                if (flag == 1 && flagPic == 0) {
                    user.setAvatarUrl(null);
                    Model.instance.deleteImage(viewModel.data.getValue().getEmail() + ".jpg", () -> {
                        viewModel.EditUser(user, new Model.EditUserListener() {
                            @Override
                            public void onComplete() {
                                NavHostFragment.findNavController(EditProfileFragment.this).navigate(R.id.action_global_nav_profile);
                            }

                            @Override
                            public void onFailure() {
                                progressBar.setVisibility(View.GONE);
                                saveBtn.setEnabled(true);
                                addPicture.setEnabled(true);
                                genderSpinner.setEnabled(true);
                                nameEt.setEnabled(true);
                                phoneEt.setEnabled(true);
                            }
                        });
                    });
                } else {
                    viewModel.EditUser(user, new Model.EditUserListener() {
                        @Override
                        public void onComplete() {
                            NavHostFragment.findNavController(EditProfileFragment.this).navigate(R.id.action_global_nav_profile);
                        }

                        @Override
                        public void onFailure() {
                            progressBar.setVisibility(View.GONE);
                            saveBtn.setEnabled(true);
                            addPicture.setEnabled(true);
                            genderSpinner.setEnabled(true);
                            nameEt.setEnabled(true);
                            phoneEt.setEnabled(true);
                        }
                    });
                }
            } else {
                Model.instance.saveImage(imageBitmap, viewModel.data.getValue().getEmail() + ".jpg", url -> {
                    user.setAvatarUrl(url);
                    if (requestCode == REQUEST_CAMERA) {
                        saveImageToFile(imageBitmap, getLocalImageFileName(url));
                    }
                    viewModel.EditUser(user, new Model.EditUserListener() {
                        @Override
                        public void onComplete() {
                            NavHostFragment.findNavController(EditProfileFragment.this).navigate(R.id.action_global_nav_profile);
                        }

                        @Override
                        public void onFailure() {
                            progressBar.setVisibility(View.GONE);
                            saveBtn.setEnabled(true);
                            addPicture.setEnabled(true);
                            genderSpinner.setEnabled(true);
                            nameEt.setEnabled(true);
                            phoneEt.setEnabled(true);
                        }
                    });
                });
            }
        }
    }
}