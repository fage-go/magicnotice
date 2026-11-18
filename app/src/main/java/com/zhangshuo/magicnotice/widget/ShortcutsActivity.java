package com.zhangshuo.magicnotice.widget;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.zhangshuo.common.model.EventMessage;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.EventBusUtil;
import com.zhangshuo.magicnotice.config.EventCodes;
import com.zhangshuo.magicnotice.db.model.ConfigRuleData;
import com.zhangshuo.magicnotice.db.service.ConfigRuleDataService;
import com.zhangshuo.magicnotice.ui.model.ConfigRuleModel;

import java.util.List;

public class ShortcutsActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String toggle = getIntent().getStringExtra("toggle");
        if (TextUtils.isEmpty(toggle)) {
            finish();
            return;
        }
        try {
            List<Integer> configidList = ShortcutsUtil.get().getConfigidList();
            ConfigRuleDataService.findRuleDataById(configidList, new AsyncCallback<List<ConfigRuleData>>() {
                @Override
                public void onSuccess(List<ConfigRuleData> result) {
                    if (result == null || result.size() == 0) return;
                    ConfigRuleModel[] configRuleModelArray = new ConfigRuleModel[result.size()];
                    for (int i = 0; i < result.size(); i++) {
                        ConfigRuleData configRuleData = result.get(i);
                        configRuleData.setOpen("open".equals(toggle));
                        configRuleModelArray[i] = new ConfigRuleModel(configRuleData);

                    }
                    ConfigRuleDataService.insertOrUpdate(configRuleModelArray, new AsyncCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            EventBusUtil.sendEvent(new EventMessage(EventCodes.UPDATE_RULE));
                            finish();
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

}
