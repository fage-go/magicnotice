package com.zhangshuo.magicnotice.led;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.zhangshuo.common.util.GsonUtils;
import com.zhangshuo.common.util.LocalDataUtil;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.led.model.CutoutInfo;
import com.zhangshuo.magicnotice.ui.model.behavior.LightModel;

import java.util.HashMap;
import java.util.Map;

public class LightUtil {

    public CutoutInfo cutoutInfo;
    private Map<Integer, LightViewWindow> viewWindowMap = new HashMap<>();


    public boolean needSetCutout() {
        getCutoutInfo();
        if (cutoutInfo == null) return false;
        return true;
    }

    @Nullable
    public CutoutInfo getCutoutInfo() {
        if (cutoutInfo == null) {
            String ledCutoutinfo = LocalDataUtil.get().getData("led_cutoutinfo", "");
            if (!TextUtils.isEmpty(ledCutoutinfo)) {
                cutoutInfo = GsonUtils.convert(ledCutoutinfo, CutoutInfo.class);
            }
        }
        return cutoutInfo;
    }

    public void updateCutoutInfo(CutoutInfo cutoutInfo) {
        this.cutoutInfo = cutoutInfo;
        if (cutoutInfo == null) {
            LocalDataUtil.get().saveData("led_cutoutinfo", "");
        } else {
            LocalDataUtil.get().saveData("led_cutoutinfo", GsonUtils.toString(cutoutInfo));
        }
    }

    public void addLight(NotificationData notificationData, LightModel lightModel) {
        LightViewWindow lightViewWindow = viewWindowMap.get(lightModel.getViewType());
        if (lightViewWindow == null) {
            lightViewWindow = LightViewWindow.makeViewWindow(lightModel);
            viewWindowMap.put(lightModel.getViewType(), lightViewWindow);
            lightViewWindow.setOnDismissListener(new LightView.OnDismissListener() {
                @Override
                public void onDismiss() {
                    LightViewWindow remove = viewWindowMap.remove(lightModel.getViewType());
                    if (remove != null) {
                        remove.dismiss();
                    }
                }
            });
        }
        if (lightViewWindow.isShowing()) return;
        lightViewWindow.show();
    }

    private LightUtil() {

    }

    public static LightUtil getInstance() {
        return Holder.instance;
    }

    private static final class Holder {
        private static final LightUtil instance = new LightUtil();
    }
}
