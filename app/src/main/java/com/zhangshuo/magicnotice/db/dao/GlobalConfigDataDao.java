package com.zhangshuo.magicnotice.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.zhangshuo.magicnotice.db.model.GlobalConfigData;

@Dao
public interface GlobalConfigDataDao {

    @Insert
    void insert(GlobalConfigData globalConfigData);

    @Query("select * from globalconfigdata where code=:code")
    GlobalConfigData findByCode(String code);


    @Query("delete from globalconfigdata where code=:code")
    void deleteByCode(String code);
}
