package com.example.findmehomeapp.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.os.HandlerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.findmehomeapp.MyApplication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Model {
    public static final Model instance = new Model();

    Executor executor = Executors.newFixedThreadPool(1);
    Handler mainThread = HandlerCompat.createAsync(Looper.getMainLooper());
    FirebaseAuth firebaseAuth;

    public enum PostListLoadingState{
        loading,
        loaded
    }
    MutableLiveData<PostListLoadingState> postListLoadingState = new MutableLiveData<PostListLoadingState>();
    public LiveData<PostListLoadingState> getPostListLoadingState() {
        return postListLoadingState;
    }

    ModelFirebase modelFirebase = new ModelFirebase();

    private Model(){
        postListLoadingState.setValue(PostListLoadingState.loaded);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    MutableLiveData<List<Post>> postsList = new MutableLiveData<List<Post>>();
    MutableLiveData<List<Post>> userPostsList = new MutableLiveData<List<Post>>();
//    MutableLiveData<String> userId = new MutableLiveData<String>();

    public LiveData<List<Post>> getAllPosts() {
        if (postsList.getValue() == null ) {
            refreshPostsList();
        }
        return postsList;
    }

    public LiveData<List<Post>> getAllUserPosts() {
        if (userPostsList.getValue() == null ) {
            refreshUserPostsList();
        }
        return userPostsList;
    }

    public void refreshUserPostsList() {
        refreshPostsList();
        postListLoadingState.setValue(PostListLoadingState.loading);
        String userId = firebaseAuth.getCurrentUser().getUid();

        List<Post> posts = postsList.getValue();
        List<Post> filteredPosts = new ArrayList<Post>();

        if (posts != null) {
            for (Post post: posts) {
                if (post.getId() == userId) {
                    filteredPosts.add(post);
                }
            }
        }

        userPostsList.postValue(filteredPosts);
        postListLoadingState.postValue(PostListLoadingState.loaded);
    }

    public void refreshPostsList() {
        postListLoadingState.setValue(PostListLoadingState.loading);

        // get last local update date
//        Long lastUpdateDate = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE).getLong("PostsLastUpdateDate",0);

        // firebase get all updates since lastLocalUpdateDate
        modelFirebase.getAllPosts(new ModelFirebase.GetAllPostsListener() {
            @Override
            public void onComplete(List<Post> list) {
                // add all records to the local db
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
//                        Long lud = new Long(0);
                        Log.d("TAG","fb returned " + list.size());
                        for (Post post: list) {
                            AppLocalDb.db.postDao().insertAll(post);
//                            if (lud < student.getUpdateDate()){
//                                lud = student.getUpdateDate();
//                            }
                        }
                        // update last local update date
                        MyApplication.getContext()
                                .getSharedPreferences("TAG",Context.MODE_PRIVATE)
                                .edit()
//                                .putLong("PostsLastUpdateDate",lud)
                                .commit();

                        //return all data to caller
                        List<Post> stList = AppLocalDb.db.postDao().getAll();
                        postsList.postValue(stList);

                        postListLoadingState.postValue(PostListLoadingState.loaded);
                    }
                });
            }
        });
    }

    public interface GetAllUsersListener{
        void onComplete(List<User> list);
    }

    public void getAllUsers(GetAllUsersListener listener){
        executor.execute(()->{
            List<User> list = AppLocalDb.db.userDao().getAll();
            mainThread.post(()->{
                listener.onComplete(list);

            });
        });
    }

    public interface GetAllPostsListener{
        void onComplete(List<Post> list);
    }

    public void getAllPosts(GetAllPostsListener listener){
        executor.execute(()-> {
            List<Post> list = AppLocalDb.db.postDao().getAll();
            mainThread.post(()->{
                listener.onComplete(list);
            });
        });
    }

    public interface AddUserListener{
        void onComplete();
    }

    public void addUser(User user, AddUserListener listener){
        //TODO: should we save in the localdb..?
        modelFirebase.addUser( user, listener);
    }

    public interface AddPostListener{
        void onComplete();
    }

    public void addPost(Post post, AddPostListener listener){
        executor.execute(()->{
            AppLocalDb.db.postDao().insertAll(post);
            mainThread.post(()->{
                listener.onComplete();
            });
        });

    }

    public interface GetUserById{
        void onComplete(User user);
    }

    public User getUserById(String userId, GetUserById listener){
        modelFirebase.getUserById(userId, listener);
        return null;
    }

    public interface GetPostById {
        void onComplete(Post post);
    }
    public Post getPostById(String postId, GetPostById listener){
        modelFirebase.getPostById(postId, listener);
        return null;
    }

    public interface SaveImageListener{
        void onComplete(String url);
    }
    public void saveImage(Bitmap imageBitmap, String imageName, SaveImageListener listener) {
        modelFirebase.saveImage(imageBitmap,imageName,listener);
    }
    public interface EditUserListener {
        void onComplete();
    }

    public void editUser(User newUser, EditUserListener listener){
        modelFirebase.editUser(newUser, ()->{
            listener.onComplete();
        });
    }

//    public interface GetPostsByUserId {
//        void onComplete(List<Post> post);
//    }
//    public Post getPostByUserId(String userId){
//        modelFirebase.getPostsByUserId(postId, listener);
//        return null;
//    }
}
