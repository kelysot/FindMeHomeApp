package com.example.findmehomeapp.Model;

import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

    }

    MutableLiveData<List<Post>> postsList = new MutableLiveData<List<Post>>();
    public LiveData<List<Post>> getAllPosts() {
        if (postsList.getValue() == null ) {
            refreshPostsList();
        }
        return postsList;
    }

    public void refreshPostsList() {
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
        modelFirebase.addUser( user,  listener);
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

    public User getUserById(String userId){

        return null;
    }

    public interface GetPostById {
        void onComplete(Post post);
    }
    public Post getPostById(String postId, GetPostById listener){
        modelFirebase.getPostById(postId, listener);
        return null;
    }
}
