package com.zhangshuo.magicnotice.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.zhangshuo.magicnotice.db.model.RemindData;

import java.util.List;

@Dao
public interface RemindDataDao {
    @Insert
    long insert(RemindData remindData);

    @Update
    void update(RemindData remindData);

    @Query("delete from reminddata where id=:id")
    void delete(int id);

    @Query("select * from reminddata order by id desc limit :start,:pageSize")
    List<RemindData> findRemindList(long start, int pageSize);

    @Query("select * from reminddata where id=:id")
    RemindData findRemindDataById(int id);
}
