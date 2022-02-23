package com.example.findmehomeapp.Model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PostDao {

    @Query("select * from Post")
    List<Post> getAll();

    @Query("select * from Post WHERE userId LIKE :id")
    List<Post> getUserAll(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Post... posts);

    @Delete
    void delete(Post post);

    @Query("DELETE FROM Post")
    void deleteAll();

    @Query("DELETE FROM Post WHERE id = :id")
    void deleteById(String id);
}
