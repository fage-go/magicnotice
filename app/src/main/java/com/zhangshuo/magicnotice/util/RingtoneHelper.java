package com.zhangshuo.magicnotice.util;

import android.content.Context;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import com.zhangshuo.common.AppUtil;
import com.zhangshuo.common.util.AsyncCallUtils;
import com.zhangshuo.common.util.AsyncCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class RingtoneHelper {


    public static Ringtone playRingtone(String ringtoneUri) {
        try {
            Ringtone ringtone = RingtoneManager.getRingtone(AppUtil.get().getApplication(), Uri.parse(ringtoneUri));
            if (ringtone != null) {
                ringtone.play();
            }
            return ringtone;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void getRingtoneInfoList(Context context, AsyncCallback<List<RingtoneInfo>> callback) {
        AsyncCallUtils.asyncCall(new Callable<List<RingtoneInfo>>() {
            @Override
            public List<RingtoneInfo> call() throws Exception {
                List<RingtoneInfo> ringtoneInfoList = new ArrayList<>();
                RingtoneManager ringtoneManager = new RingtoneManager(context); // 铃声管理器
                ringtoneManager.setType(RingtoneManager.TYPE_NOTIFICATION);
                Cursor cursor = ringtoneManager.getCursor(); //获取铃声表,根据表名取值
                int count = cursor.getCount(); //获取铃声列表数量
                for (int i = 0; i < count; i++) {
                    Ringtone ringtone = ringtoneManager.getRingtone(i);
                    String title = ringtone.getTitle(context);
                    Uri ringtoneUri = ringtoneManager.getRingtoneUri(i);
                    RingtoneInfo ringtoneInfo = new RingtoneInfo(title, ringtoneUri.toString());
                    ringtoneInfoList.add(ringtoneInfo);
                }
                return ringtoneInfoList;
            }
        }, callback);
    }


    public static class RingtoneInfo {
        private String name;
        private String uri;

        public RingtoneInfo(String name, String uri) {
            this.name = name;
            this.uri = uri;
        }


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }
    }
}
