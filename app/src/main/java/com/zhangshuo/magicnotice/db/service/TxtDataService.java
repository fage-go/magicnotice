package com.zhangshuo.magicnotice.db.service;

import com.zhangshuo.common.util.AsyncCallUtils;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.magicnotice.db.AppDatabase;
import com.zhangshuo.magicnotice.db.model.TxtClassifyData;
import com.zhangshuo.magicnotice.db.model.TxtData;
import com.zhangshuo.magicnotice.ui.model.RandomTxtInfo;
import com.zhangshuo.magicnotice.ui.model.TxtClassifyInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class TxtDataService {

    public static void insert(TxtData txtData, AsyncCallback<Boolean> callback) {
        AsyncCallUtils.asyncCall(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                AppDatabase.getInstance().txtDataDao().insert(txtData);
                return true;
            }
        }, callback);
    }

    public static void insertAll(List<RandomTxtInfo> randomList, AsyncCallback<Boolean> callback) {
        AsyncCallUtils.asyncCall(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                List<TxtData> txtDataList = new ArrayList<>();
                if (randomList != null && randomList.size() > 0) {
                    for (RandomTxtInfo txtInfo : randomList) {
                        txtDataList.add(txtInfo.convert());
                    }
                }
                AppDatabase.getInstance().txtDataDao().insert(txtDataList);
                return true;
            }
        }, callback);
    }

    public static void delete(int id, AsyncCallback<Boolean> callback) {
        AsyncCallUtils.asyncCall(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                AppDatabase.getInstance().txtDataDao().deleteById(id);
                return true;
            }
        }, callback);
    }

    public static void deleteList(List<TxtData> txtDataList, AsyncCallback<Boolean> callback) {
        AsyncCallUtils.asyncCall(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                List<Integer> idList = new ArrayList<>();
                for (TxtData txtData : txtDataList) {
                    idList.add(txtData.getId());
                }
                AppDatabase.getInstance().txtDataDao().deleteByIds(idList);
                return true;
            }
        }, callback);
    }

    public static void findClassify(AsyncCallback<List<TxtClassifyInfo>> callback) {
        AsyncCallUtils.asyncCall(new Callable<List<TxtClassifyInfo>>() {
            @Override
            public List<TxtClassifyInfo> call() throws Exception {
                List<TxtClassifyData> classifyList = AppDatabase.getInstance().txtDataDao().findClassifyList();
                List<TxtClassifyInfo> classifyInfoList = new ArrayList<>();
                if (classifyList != null)
                    for (TxtClassifyData txtClassifyData : classifyList) {
                        TxtClassifyInfo txtClassifyInfo = new TxtClassifyInfo(txtClassifyData);
                        txtClassifyInfo.setCount(AppDatabase.getInstance().txtDataDao().countByClassifyId(txtClassifyData.getId()));
                        classifyInfoList.add(txtClassifyInfo);
                    }
                return classifyInfoList;
            }
        }, callback);
    }

    public static void insertClassify(TxtClassifyData txtClassifyData, AsyncCallback<Boolean> callback) {
        AsyncCallUtils.asyncCall(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (txtClassifyData.getId() > 0) {
                    AppDatabase.getInstance().txtDataDao().updateClassify(txtClassifyData);
                } else {
                    AppDatabase.getInstance().txtDataDao().insertClassify(txtClassifyData);
                }
                return true;
            }
        }, callback);
    }

    public static void deleteClassify(int id, AsyncCallback<Boolean> callback) {
        AsyncCallUtils.asyncCall(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                AppDatabase.getInstance().txtDataDao().deleteClassifyById(id);
                return true;
            }
        }, callback);
    }

    public static void getList(int classifyId, int page, int pageSize, AsyncCallback<List<RandomTxtInfo>> callback) {
        AsyncCallUtils.asyncCall(new Callable<List<RandomTxtInfo>>() {
            @Override
            public List<RandomTxtInfo> call() throws Exception {
                int start = (page - 1) * pageSize;
                List<TxtData> dataList = AppDatabase.getInstance().txtDataDao().findDataList(classifyId, start, pageSize);
                List<RandomTxtInfo> txtInfoList = new ArrayList<>();
                if (dataList != null && dataList.size() > 0) {
                    for (TxtData txtData : dataList) {
                        txtInfoList.add(new RandomTxtInfo(txtData));
                    }
                }
                return txtInfoList;
            }
        }, callback);
    }
}
