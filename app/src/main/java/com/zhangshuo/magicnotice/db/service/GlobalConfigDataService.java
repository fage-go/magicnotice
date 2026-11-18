package com.zhangshuo.magicnotice.db.service;

import com.zhangshuo.common.util.AsyncCallUtils;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.magicnotice.db.AppDatabase;
import com.zhangshuo.magicnotice.db.model.GlobalConfigData;

import java.util.concurrent.Callable;

public class GlobalConfigDataService {

    public static void saveOrUpdate(String code, String value, AsyncCallback<Boolean> callback) {
        AsyncCallUtils.asyncCall(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                AppDatabase.getInstance().globalConfigDataDao().deleteByCode(code);
                GlobalConfigData globalConfigData = new GlobalConfigData();
                globalConfigData.setCode(code);
                globalConfigData.setValue(value);
                AppDatabase.getInstance().globalConfigDataDao().insert(globalConfigData);
                return true;
            }
        }, callback);
    }

    public static void getGlobalConfig(String code, AsyncCallback<GlobalConfigData> callback) {
        AsyncCallUtils.asyncCall(new Callable<GlobalConfigData>() {
            @Override
            public GlobalConfigData call() throws Exception {
                return AppDatabase.getInstance().globalConfigDataDao().findByCode(code);
            }
        }, callback);
    }
}
