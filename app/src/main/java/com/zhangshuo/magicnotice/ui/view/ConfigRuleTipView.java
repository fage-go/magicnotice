package com.zhangshuo.magicnotice.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.zhangshuo.magicnotice.R;
import com.zhangshuo.common.util.UIUtil;

public class ConfigRuleTipView extends FrameLayout {
    private ConstraintLayout clRoot;
    private TextView tvTitle;
    private TextView tvValue;
    private CircleColorView cvColor;
    private String title;
    private int type = 0;
    private String objValue;
    private int titleColor;
    private int titleSize;
    private int valueColor;
    private int valueSize;

    public void setTitle(String title) {
        setTitle(title, 1);
    }

    public void setTitle(String title, int type) {
        tvTitle.setText(title);
        this.type = type;
        if (type == 0) {
            tvValue.setVisibility(GONE);
            cvColor.setVisibility(VISIBLE);
        } else {
            tvValue.setVisibility(VISIBLE);
            cvColor.setVisibility(GONE);
        }
    }

    public void setValue(String objValue) {
        this.objValue = objValue;
        if (type == 0) {
            //颜色
            cvColor.setColor(String.valueOf(objValue));
        } else if (type == 1) {
            //内容
            if (TextUtils.isEmpty(objValue)) {
                tvValue.setText("");
            } else {
                tvValue.setText(String.valueOf(objValue));
            }
        }
    }

    public void setValueTip(String tip) {
        tvValue.setHint(tip);
    }

    public String getValue() {
        return objValue;
    }

    public void setSelect(boolean select) {
        clRoot.setBackgroundResource(select ? R.drawable.bg_content_round_select : R.drawable.bg_content_round);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.ConfigRuleTipView);
            title = arr.getString(R.styleable.ConfigRuleTipView_title);
            type = arr.getInt(R.styleable.ConfigRuleTipView_type, 0);
            titleColor = arr.getColor(R.styleable.ConfigRuleTipView_titleColor, getContext().getColor(R.color.base_font_txt));
            titleSize = arr.getDimensionPixelSize(R.styleable.ConfigRuleTipView_titleSize, UIUtil.dp2Px(14));
            titleColor = arr.getColor(R.styleable.ConfigRuleTipView_titleColor, getContext().getColor(R.color.base_font_txt));
            valueColor = arr.getColor(R.styleable.ConfigRuleTipView_valueColor, getContext().getColor(R.color.base_font_blue));
            valueSize = arr.getDimensionPixelSize(R.styleable.ConfigRuleTipView_valueSize, UIUtil.dp2Px(16));
            arr.recycle();
        }
        LayoutInflater.from(getContext()).inflate(R.layout.view_config_rule_tip, this);
        clRoot = findViewById(R.id.cl_rule);
        tvTitle = findViewById(R.id.tv_title);
        tvValue = findViewById(R.id.tv_value);
        cvColor = findViewById(R.id.cv_color);
        tvTitle.setTextColor(titleColor);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
        setTitle(title, type);
        tvValue.setTextColor(valueColor);
        tvValue.setTextSize(TypedValue.COMPLEX_UNIT_PX, valueSize);
    }

    public ConfigRuleTipView(@NonNull Context context) {
        super(context);
        init(null);
    }

    public ConfigRuleTipView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ConfigRuleTipView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public ConfigRuleTipView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public interface OnCallback {
        void onResult(Object value);
    }
}
