package com.example.findmehomeapp.ui.Profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.findmehomeapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class ProfileFragment extends Fragment {

    TextView nameTv;
    TextView locationTv;
    TextView phoneTv;
    //ImageView avatarImv;
    Button addPostBtn;
    Button editProfileBtn;
    RecyclerView postList;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    NavController navController;
    String userid;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameTv = view.findViewById(R.id.profile_user_name);
        phoneTv = view.findViewById(R.id.profile_user_phone);
        locationTv = view.findViewById(R.id.profile_user_location);

        addPostBtn = view.findViewById(R.id.profile_btn_add_post);
        editProfileBtn = view.findViewById(R.id.profile_btn_edit_profile);

        navController = Navigation.findNavController(view);



        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        addPostBtn.setOnClickListener((v)->{
            navController.navigate(R.id.action_nav_profile_to_nav_create_post);
        });

        editProfileBtn.setOnClickListener((v)->{
            navController.navigate(R.id.action_nav_profile_to_nav_edit_profile);
        });


//        String userId = firebaseAuth.getCurrentUser().getUid();
//
//        Log.d("TAG1", "user Id:" + userId );
//
//        Model.instance.getUserById(userId, new Model.GetUserById() {
//            @Override
//            public void onComplete(User user) {
//                Log.d("TAG2", "user Id:" + userId );
//                nameTv.setText(user.getName());
//                emailTv.setText(user.getEmail());
//                phoneTv.setText(user.getPhone());
////                if (user.getAvatarUrl() != null) {
////                    Picasso.get().load(user.getAvatarUrl()).into(avatarImv);
////                }
//            }
//        });




    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String theuserid = user.getUid();


        firestore.collection("Users").document(theuserid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    DocumentSnapshot ssnahot = task.getResult();

                    String userName = ssnahot.getString("username");
                    String userId = ssnahot.getString("userid");
                    String userPhone = ssnahot.getString("phone");
                    String userLocation = ssnahot.getString("location");
                    //imageUrl = ssnahot.getString("imageUrl");

                    nameTv.setText(userName);
                    phoneTv.setText(userPhone);
                    locationTv.setText(userLocation);
                   // emailTv.setText();

                    //Glide.with(getContext()).load(imageUrl).centerCrop().into(circleImageView);



                }

            }
        });
    }
}