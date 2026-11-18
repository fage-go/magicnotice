package com.zhangshuo.magicnotice.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zhangshuo.common.dialog.BottomOptionDialog;
import com.zhangshuo.common.pick.dataset.OptionDataSet;
import com.zhangshuo.common.router.RouterUtil;
import com.zhangshuo.common.toast.ToastManager;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.LocalDataUtil;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.LocalDataKeys;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.db.service.ConfigRuleDataService;
import com.zhangshuo.magicnotice.ui.dialog.SelectOptionsDialog;
import com.zhangshuo.magicnotice.ui.model.OptionModel;

import java.text.DecimalFormat;
import java.util.Arrays;

public class NoHitRuleView extends FrameLayout {
    private TextView tvTitle;
    private TextView tvHitCount;
    private String cleanTime = "0";

    private void init() {
        cleanTime = LocalDataUtil.get().getData(LocalDataKeys.SKEY_NO_HIT_CLEAN_TIME, "0");
        LayoutInflater.from(getContext()).inflate(R.layout.view_no_hit_rule, this);
        tvTitle = findViewById(R.id.tv_title);
        tvHitCount = findViewById(R.id.tv_count);
        tvHitCount.getPaint().setFakeBoldText(true);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsDialog();
            }
        });
    }

    public void refreshCount() {
        ConfigRuleDataService.countNotificationByRuleId(0, new AsyncCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                DecimalFormat df = new DecimalFormat("#,###");
                tvHitCount.setText(df.format(result));
            }
        });
    }

    private void showOptionsDialog() {
        BottomOptionDialog.create(getContext(),
                Arrays.asList(
                        StringUtil.getString(R.string.not_hit_rule_opt_history),
                        String.format(StringUtil.getString(R.string.not_hit_rule_opt_set), OptionModel.getNameByValue(cleanTime))
                ),
                new BottomOptionDialog.OnOptionCallback() {
                    @Override
                    public void onClick(int position) {
                        if (position == 0) {
                            Bundle bundle = new Bundle();
                            bundle.putInt("ruleId", 0);
                            RouterUtil.get().navigation(RoutePaths.APP_ACTIVITY_RULE_HIT_LIST, bundle);
                        } else {
                            //设置时间
                            SelectOptionsDialog cleanOptionsDialog = new SelectOptionsDialog(OptionModel.getModelList());
                            cleanOptionsDialog.setOnCallback(new SelectOptionsDialog.OnCallback() {
                                @Override
                                public void onResult(int index, OptionDataSet optionDataSet) {
                                    cleanTime = optionDataSet.getValue();
                                    LocalDataUtil.get().saveData(LocalDataKeys.SKEY_NO_HIT_CLEAN_TIME, cleanTime);
                                    ToastManager.get().showShortToast(
                                            String.format(StringUtil.getString(R.string.not_hit_rule_opt_set_tip), OptionModel.getNameByValue(cleanTime))
                                    );
                                }
                            });
                            cleanOptionsDialog.setSelectValue(cleanTime);
                            cleanOptionsDialog.show(getContext());
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }


    public NoHitRuleView(@NonNull Context context) {
        super(context);
        init();
    }

    public NoHitRuleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoHitRuleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public NoHitRuleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
}
