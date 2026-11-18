package com.zhangshuo.magicnotice.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.zhangshuo.magicnotice.db.model.TxtClassifyData;
import com.zhangshuo.magicnotice.db.model.TxtData;

import java.util.List;

@Dao
public interface TxtDataDao {
    @Insert
    void insert(TxtData txtData);

    @Insert
    void insert(List<TxtData> txtDataList);

    @Query("delete from txtdata where id=:id")
    void deleteById(int id);

    @Query("delete from txtdata where id in (:ids)")
    void deleteByIds(List<Integer> ids);

    @Query("select * from txtdata where classifyId=:classifyId order by id desc limit :start,:pageSize")
    List<TxtData> findDataList(int classifyId, long start, int pageSize);

    @Query("select * from txtdata where id in (:ids) order by id desc")
    List<TxtData> findDataListByIds(List<Integer> ids);

    @Query("select * from txtclassifydata order by id desc")
    List<TxtClassifyData> findClassifyList();

    @Query("select * from txtdata where classifyId=:classifyId")
    List<TxtData> findDataByClassifyId(int classifyId);

    @Query("select count(*) from txtdata where classifyId=:classifyId")
    Long countByClassifyId(int classifyId);

    @Insert
    void insertClassify(TxtClassifyData txtClassifyData);

    @Update
    void updateClassify(TxtClassifyData txtClassifyData);

    @Query("delete from txtclassifydata where id=:id")
    void deleteClassifyById(int id);

    @Query("select * from txtclassifydata where id=:id")
    TxtClassifyData findClassifyById(int id);
}
