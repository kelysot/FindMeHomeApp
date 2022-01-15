package com.example.findmehomeapp.Model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.os.HandlerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.findmehomeapp.MyApplication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Model {
    public static final Model instance = new Model();

    Executor executor = Executors.newFixedThreadPool(1);
    Handler mainThread = HandlerCompat.createAsync(Looper.getMainLooper());

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
    }

    MutableLiveData<List<Post>> postsList = new MutableLiveData<List<Post>>();
    public LiveData<List<Post>> getAllPosts() {
        if (postsList.getValue() == null ) {
            refreshPostsList();
        }
        return postsList;
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
}
