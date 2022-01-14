package com.example.findmehomeapp.ui.Profile;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.findmehomeapp.Model.Model;
import com.example.findmehomeapp.Model.Post;
import com.example.findmehomeapp.R;
import com.example.findmehomeapp.ui.Post.PostFragmentArgs;
import com.example.findmehomeapp.ui.home.HomeFragment;
import com.example.findmehomeapp.ui.home.HomeFragmentDirections;
import com.example.findmehomeapp.ui.home.HomeViewModel;

public class ProfileFragment extends Fragment {

    private HomeViewModel homeViewModel;
    ProfileFragment.MyAdapter adapter;

    SwipeRefreshLayout swipeRefresh;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        String stId = ProfileFragmentArgs.fromBundle(getArguments()).getEmail();

        swipeRefresh = view.findViewById(R.id.postslist_swiperefresh);
        swipeRefresh.setOnRefreshListener(() -> Model.instance.refreshUserPostsList(stId));

        RecyclerView list = view.findViewById(R.id.profile_post_rv) ;
        list.setHasFixedSize(true);

        list.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MyAdapter();
        list.setAdapter(adapter);

        adapter.setOnItemClickListener(new ProfileFragment.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                String postId = homeViewModel.getData().getValue().get(position).getId();
                Navigation.findNavController(v).navigate(HomeFragmentDirections.actionNavHomeToNavPost(postId));
            }
        });

        setHasOptionsMenu(true);
        homeViewModel.getData().observe(getViewLifecycleOwner(), list1 -> refresh());

        swipeRefresh.setRefreshing(Model.instance.getPostListLoadingState().getValue() == Model.PostListLoadingState.loading);

        Model.instance.getPostListLoadingState().observe(getViewLifecycleOwner(), postListLoadingState -> {
            if(postListLoadingState == Model.PostListLoadingState.loading) {
                swipeRefresh.setRefreshing(true);
            } else {
                swipeRefresh.setRefreshing(false);
            }
        });

        return view;
    }

    private void refresh() {
        adapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView petTextTv;
//        TextView idTv;
//        CheckBox cb;

        public MyViewHolder(@NonNull View itemView, ProfileFragment.OnItemClickListener listener) {
            super(itemView);
            petTextTv = itemView.findViewById(R.id.post_pet_text_tv);
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
            Post post = homeViewModel.getData().getValue().get(position);
            //TODO: set relevants from holder
            holder.petTextTv.setText(post.getText());
//            holder.idTv.setText(student.getId());
        }

        @Override
        public int getItemCount() {
            if(homeViewModel.getData().getValue() == null) {
                return 0;
            }
            return homeViewModel.getData().getValue().size();
        }
    }
}