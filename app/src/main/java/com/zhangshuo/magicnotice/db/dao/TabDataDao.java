package com.zhangshuo.magicnotice.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.zhangshuo.magicnotice.db.model.TabData;

import java.util.List;

@Dao
public interface TabDataDao {
    @Insert
    void insert(TabData tabData);

    @Update
    void update(TabData tabData);

    @Query("select * from tabdata where id=:id")
    TabData findTabDataById(int id);

    @Query("delete from tabdata where id=:id")
    void deleteById(int id);

    @Query("select * from tabdata where name=:name")
    List<TabData> findTabDataByName(String name);

    @Query("select * from tabdata ")
    List<TabData> findAllTabData();

    @Query("select * from tabdata where openFlag=1")
    List<TabData> findActTabData();
}
