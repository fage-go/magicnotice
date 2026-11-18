package com.zhangshuo.magicnotice.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zhangshuo.magicnotice.R;

public class FilterConfigView extends FrameLayout {
    private TextView tvTip;
    private Switch switchFilter;
    private int[] tipIdsArray;

    public void setConfig(boolean checked, int... tips) {
        this.tipIdsArray = tips;
        switchFilter.setChecked(checked);
        tvTip.setText(switchFilter.isChecked() ? tipIdsArray[0] : tipIdsArray[1]);
    }

    public boolean isChecked() {
        return switchFilter.isChecked();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_filter_config, this);
        tvTip = findViewById(R.id.tv_config_tip);
        switchFilter = findViewById(R.id.switch_filter);
        switchFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tvTip.setText(switchFilter.isChecked() ? tipIdsArray[0] : tipIdsArray[1]);
            }
        });
    }

    public FilterConfigView(@NonNull Context context) {
        super(context);
        init();
    }

    public FilterConfigView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FilterConfigView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public FilterConfigView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
}
