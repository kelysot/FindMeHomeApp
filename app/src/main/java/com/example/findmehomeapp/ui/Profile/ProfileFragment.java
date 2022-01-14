package com.example.findmehomeapp.ui.Profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.findmehomeapp.Model.Model;
import com.example.findmehomeapp.Model.User;
import com.example.findmehomeapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;


public class ProfileFragment extends Fragment {

    TextView nameTv;
    TextView locationTv;
    TextView phoneTv;
    ImageView avatarImv;
    Button addPostBtn;
    Button editProfileBtn;
    RecyclerView postList;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    String userId;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();


//        String stId = ProfileFragmentArgs.fromBundle(getArguments()).getUserId();
        userId = firebaseAuth.getCurrentUser().getUid();
        Log.d("TAG1", "user Id:" + userId );


        Model.instance.getUserById(userId, new Model.GetUserById() {
            @Override
            public void onComplete(User user) {
                Log.d("TAG111", "user Id:" + user.getId() );
                nameTv.setText(user.getName());
                phoneTv.setText(user.getPhone());
                locationTv.setText(user.getLocation());
                if (user.getAvatarUrl() != null) {
                    Picasso.get().load(user.getAvatarUrl()).into(avatarImv);
                }
            }
        });

        nameTv = view.findViewById(R.id.profile_user_name);
        phoneTv = view.findViewById(R.id.profile_user_phone);
        locationTv = view.findViewById(R.id.profile_user_location);
        avatarImv = view.findViewById(R.id.profile_image);

        addPostBtn = view.findViewById(R.id.profile_btn_add_post);
        editProfileBtn = view.findViewById(R.id.profile_btn_edit_profile);


        addPostBtn.setOnClickListener((v)->{
            Navigation.findNavController(v).navigate(R.id.action_nav_profile_to_nav_create_post);
        });

        editProfileBtn.setOnClickListener((v)->{
            Navigation.findNavController(v).navigate(R.id.action_nav_profile_to_nav_edit_profile);
        });
        return view;
    }

}