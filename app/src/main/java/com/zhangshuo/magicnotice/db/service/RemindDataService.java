package com.zhangshuo.magicnotice.db.service;

import com.zhangshuo.common.util.AsyncCallUtils;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.GsonUtils;
import com.zhangshuo.magicnotice.db.AppDatabase;
import com.zhangshuo.magicnotice.db.model.RemindData;
import com.zhangshuo.magicnotice.db.model.TxtClassifyData;
import com.zhangshuo.magicnotice.db.model.TxtData;
import com.zhangshuo.magicnotice.scheduler.SchedulerUtil;
import com.zhangshuo.magicnotice.ui.model.RemindInfo;
import com.zhangshuo.magicnotice.ui.model.RemindTimeInfo;
import com.zhangshuo.magicnotice.ui.model.TxtClassifyInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class RemindDataService {

    public static void insertOrUpdate(RemindInfo remindInfo, AsyncCallback<Boolean> callback) {
        AsyncCallUtils.asyncCall(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                RemindData remindData = remindInfo.convert();
                if (remindData.getId() > 0) {
                    AppDatabase.getInstance().remindDataDao().update(remindData);
                } else {
                    long insert = AppDatabase.getInstance().remindDataDao().insert(remindData);
                    remindInfo.setId((int) insert);
                }
                return true;
            }
        }, callback);
    }

    public static void delete(int id, AsyncCallback<Boolean> callback) {
        AsyncCallUtils.asyncCall(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                AppDatabase.getInstance().remindDataDao().delete(id);
                return true;
            }
        }, callback);
    }

    public static void findRemindList(int page, AsyncCallback<List<RemindInfo>> callback) {
        AsyncCallUtils.asyncCall(new Callable<List<RemindInfo>>() {
            @Override
            public List<RemindInfo> call() throws Exception {
                int pageSize = 20;
                int start = (page - 1) * pageSize;
                List<RemindData> remindDataList = AppDatabase.getInstance().remindDataDao().findRemindList(start, pageSize);
                List<RemindInfo> schedulerInfoList = new ArrayList<>();
                for (RemindData remindData : remindDataList) {
                    schedulerInfoList.add(convertDataToInfo(remindData));
                }
                return schedulerInfoList;
            }
        }, callback);
    }

    public static void findRemindById(int id, AsyncCallback<RemindInfo> callback) {
        AsyncCallUtils.asyncCall(new Callable<RemindInfo>() {
            @Override
            public RemindInfo call() throws Exception {
                RemindData remindData = AppDatabase.getInstance().remindDataDao().findRemindDataById(id);
                return convertDataToInfo(remindData);
            }
        }, callback);
    }

    private static RemindInfo convertDataToInfo(RemindData remindData) {
        RemindInfo remindInfo = new RemindInfo(remindData);
        //设置时间配置
        try {
            remindInfo.setRemindTimeInfo(GsonUtils.toBean(remindInfo.getTimeJson(), RemindTimeInfo.class));
            remindInfo.setDateStrTip(SchedulerUtil.getRemindDateTip(remindInfo.getRemindTimeInfo()));
            remindInfo.setRepeatTip(SchedulerUtil.getRepeatTip(remindInfo.getRemindTimeInfo()));
        } catch (Exception e) {

        }
        //随机状况下的设置
        if (remindInfo.getRemindType() == RemindInfo.TYPE_REMIND) {
            //设置为关联的文案库
            TxtClassifyData txtClassifyData = AppDatabase.getInstance().txtDataDao().findClassifyById(remindData.getClassifyId());
            if (txtClassifyData != null) {
                TxtClassifyInfo txtClassifyInfo = new TxtClassifyInfo(txtClassifyData);
                txtClassifyInfo.setCount(AppDatabase.getInstance().txtDataDao().countByClassifyId(txtClassifyData.getId()));
                remindInfo.setTxtClassifyInfo(txtClassifyInfo);
            }
            //设置随机提醒文案
            List<TxtData> txtDataList = AppDatabase.getInstance().txtDataDao().findDataByClassifyId(remindData.getClassifyId());
            if (txtDataList.size() > 0) {
                remindInfo.setRandomTxtData(txtDataList.get((int) (Math.random() * (txtDataList.size() - 1))));
            }

        }
        return remindInfo;
    }


}
