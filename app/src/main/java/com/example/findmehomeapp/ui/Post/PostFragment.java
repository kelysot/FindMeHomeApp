package com.example.findmehomeapp.ui.Post;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.findmehomeapp.Model.Model;
import com.example.findmehomeapp.Model.Post;
import com.example.findmehomeapp.Model.User;
import com.example.findmehomeapp.R;
import com.squareup.picasso.Picasso;

public class PostFragment extends Fragment {

    ImageView profileImage;
    Button editBtn;
    TextView usernameTv;
    TextView postTimeTv;
    TextView petTextTv;
    ImageView petImage;
    ImageView likeImg;
    TextView likesNumberTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        String postId = PostFragmentArgs.fromBundle(getArguments()).getPostId();

        Model.instance.getPostById(postId, new Model.GetPostById() {
            @Override
            public void onComplete(Post post) {
                postTimeTv.setText(post.getPostTime().toString());
                petTextTv.setText(post.getText());
                if (post.getImage() != null){
                    Picasso.get().load(post.getImage()).into(petImage);
                }
                postTimeTv.setText(post.getPostTime().toString());
                Model.instance.getUserById(post.getUserId(), new Model.GetUserById() {
                    @Override
                    public void onComplete(User user) {
                        Log.d("TAG111", "user Id:" + user.getId() );
                        if (user.getAvatarUrl() != null) {
                            Picasso.get().load(user.getAvatarUrl()).into(profileImage);
                        }
                        if (user.getName() != null) {
                            usernameTv.setText(user.getName());
                        }
                    }
                });
            }
        });

        usernameTv = view.findViewById(R.id.post_username_tv);
        //TODO:
        postTimeTv = view.findViewById(R.id.post_post_time_tv);
        petTextTv = view.findViewById(R.id.post_pet_text_tv);
        profileImage = view.findViewById(R.id.post_user_img);
        petImage = view.findViewById(R.id.post_pet_img);
        //TODO: do we need the back button?
//        Button backBtn = view.findViewById(R.id.details_back_btn);
//        backBtn.setOnClickListener((v)->{
//            Navigation.findNavController(v).navigateUp();
//        });
        return view;
    }
}