package com.example.findmehomeapp.ui.Post;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.findmehomeapp.Model.Model;
import com.example.findmehomeapp.Model.Post;
import com.example.findmehomeapp.R;

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
                usernameTv.setText(post.getName());
                postTimeTv.setText(post.getPostTime().toString());
                petTextTv.setText(post.getText());
            }
        });

        usernameTv = view.findViewById(R.id.post_username_tv);
        postTimeTv = view.findViewById(R.id.post_post_time_tv);
        petTextTv = view.findViewById(R.id.post_pet_text_tv);

        //TODO: do we need the back button?
//        Button backBtn = view.findViewById(R.id.details_back_btn);
//        backBtn.setOnClickListener((v)->{
//            Navigation.findNavController(v).navigateUp();
//        });
        return view;
    }
}