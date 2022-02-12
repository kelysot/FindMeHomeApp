package com.example.findmehomeapp.ui.Profile;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
import com.example.findmehomeapp.Model.Post;
import com.example.findmehomeapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.example.findmehomeapp.ui.Post.PostFragmentArgs;
import com.example.findmehomeapp.ui.home.HomeFragment;
import com.example.findmehomeapp.ui.home.HomeFragmentDirections;
import com.example.findmehomeapp.ui.Profile.ProfileViewModel;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    TextView nameTv;
    TextView phoneTv;
    CircleImageView avatarImv;
    Button addPostBtn;
    Button editProfileBtn;
    RecyclerView postList;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    String userId;

    private ProfileViewModel profileViewModel;
    ProfileFragment.MyAdapter adapter;

    SwipeRefreshLayout swipeRefresh;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
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
                if (user.getAvatarUrl() != null) {
                    Picasso.get().load(user.getAvatarUrl()).into(avatarImv);
                }
            }
        });

        nameTv = view.findViewById(R.id.profile_user_name);
        phoneTv = view.findViewById(R.id.profile_user_phone);
        avatarImv = view.findViewById(R.id.profile_image);

        swipeRefresh = view.findViewById(R.id.postslist_swiperefresh);
        swipeRefresh.setOnRefreshListener(() -> Model.instance.refreshPostsList());

        RecyclerView list = view.findViewById(R.id.profile_post_rv) ;
        list.setHasFixedSize(true);

        list.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MyAdapter();
        list.setAdapter(adapter);

        adapter.setOnItemClickListener(new ProfileFragment.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                String postId = profileViewModel.getData().getValue().get(position).getId().toString();
                Navigation.findNavController(v).navigate(ProfileFragmentDirections.actionNavProfileToNavPost2(postId));
            }
        });

        setHasOptionsMenu(true);
        profileViewModel.getData().observe(getViewLifecycleOwner(), list1 -> refresh());

        swipeRefresh.setRefreshing(Model.instance.getPostListLoadingState().getValue() == Model.PostListLoadingState.loading);

        Model.instance.getPostListLoadingState().observe(getViewLifecycleOwner(), postListLoadingState -> {
            if(postListLoadingState == Model.PostListLoadingState.loading) {
                swipeRefresh.setRefreshing(true);
            } else {
                swipeRefresh.setRefreshing(false);
            }
        });

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
    private void refresh() {
        adapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView petTextTv;
        ImageView petImage;
        CircleImageView userImage;
        TextView userName;
        ImageView editPost;
//        TextView idTv;
//        CheckBox cb;

        public MyViewHolder(@NonNull View itemView, ProfileFragment.OnItemClickListener listener) {
            super(itemView);
            petTextTv = itemView.findViewById(R.id.post_pet_text_tv);
            petImage = itemView.findViewById(R.id.post_pet_img);
            userImage = itemView.findViewById(R.id.post_user_img);
            userName = itemView.findViewById(R.id.post_user_name);
            editPost = itemView.findViewById(R.id.post_edit_post);
//            idTv = itemView.findViewById(R.id.listrow_id_tv);
//            cb = itemView.findViewById(R.id.listrow_cb);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    listener.onItemClick(v, pos);
                }
            });
        }
    }

    interface OnItemClickListener{
        void onItemClick(View v, int position);
    }
    class MyAdapter extends RecyclerView.Adapter<ProfileFragment.MyViewHolder>{

        ProfileFragment.OnItemClickListener listener;
        public void setOnItemClickListener(ProfileFragment.OnItemClickListener listener){
            this.listener = listener;
        }

        @NonNull
        @Override
        public ProfileFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.post_list_row,parent,false);
            ProfileFragment.MyViewHolder holder = new ProfileFragment.MyViewHolder(view,listener);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ProfileFragment.MyViewHolder holder, int position) {
            Post post = profileViewModel.getData().getValue().get(position);
            //TODO: set relevants from holder
            holder.petTextTv.setText(post.getText());
//            holder.idTv.setText(student.getId());
            if (post.getImage() != null) {
                Picasso.get().load(post.getImage()).into(holder.petImage);
            }
            Model.instance.getUserById(post.getUserId(), new Model.GetUserById() {
                @Override
                public void onComplete(User user) {
                    holder.userName.setText(user.getName());
                    if (user.getAvatarUrl() != null) {
                        Picasso.get().load(user.getAvatarUrl()).into(holder.userImage);
                    }
                }
            });

            if(!Model.instance.getConnectedUserId().equals(post.getUserId())){
                holder.editPost.setVisibility(View.GONE);
                holder.editPost.setEnabled(false);
            }

            holder.editPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.findNavController(v).navigate(ProfileFragmentDirections.actionNavProfileToNavEditPost(post.getId()));
                }
            });
        }

        @Override
        public int getItemCount() {
            if(profileViewModel.getData().getValue() == null) {
                return 0;
            }
            return profileViewModel.getData().getValue().size();
        }
    }
}