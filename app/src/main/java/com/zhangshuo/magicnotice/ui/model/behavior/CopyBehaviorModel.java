package com.zhangshuo.magicnotice.ui.model.behavior;

import android.text.TextUtils;
import android.widget.Toast;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.VibrateUtils;
import com.zhangshuo.common.AppUtil;
import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.barrage.BehaviorHelper;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.ui.model.BehaviorModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CopyBehaviorModel extends BehaviorModel {
    private String name;
    private String regular;

    public CopyBehaviorModel() {
        super(BehaviorType.BEHAVIOR_COPY);
    }

    public String getRegular() {
        return regular;
    }

    public void setRegular(String regular) {
        this.regular = regular;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isEmpty() {
        return TextUtils.isEmpty(regular);
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void exec(NotificationData notificationData) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(regular)) return;
        String content = notificationData.getContent();
        if (TextUtils.isEmpty(content)) return;
        if (notificationData.getId() == BehaviorHelper.TEST_NOTIFICATION_ID) {
            Toast.makeText(AppUtil.get().getApplication(), String.format(StringUtil.getString(R.string.b_copy_tip), name, ""), Toast.LENGTH_SHORT).show();
        } else {
            if ("()".equals(regular)) {
                Toast.makeText(AppUtil.get().getApplication(), String.format(StringUtil.getString(R.string.b_copy_tip), name, ""), Toast.LENGTH_SHORT).show();
                ClipboardUtils.copyText(content);
            } else {
                Pattern pattern = Pattern.compile(regular);
                Matcher matcher = pattern.matcher(content);
                String group = "";
                while (matcher.find()) {
                    String tempGroup = matcher.group();
                    if (tempGroup.length() >= group.length()) {
                        group = tempGroup;
                    }
                }
                if (!TextUtils.isEmpty(group)) {
                    LogUtils.i("CopyBehavior> name:" + name + " result:" + group);
                    ClipboardUtils.copyText(group);
                    String tipMsg = String.format(StringUtil.getString(R.string.b_copy_tip), name, group);
                    Toast.makeText(AppUtil.get().getApplication(), tipMsg, Toast.LENGTH_SHORT).show();
                    VibrateUtils.vibrate(100);
                }
            }
        }
        LogUtils.i("CopyBehavior exec " + name);
    }
}
