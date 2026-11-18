package com.zhangshuo.magicnotice.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zhangshuo.common.router.RouterUtil;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.ui.model.TxtClassifyInfo;

public class RandomTxtView extends FrameLayout {
    private int classifyId;
    private ImageView ivLogo;
    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvReset;

    public void setClassifyData(TxtClassifyInfo txtClassifyInfo) {
        if (txtClassifyInfo != null) {
            classifyId = txtClassifyInfo.getId();
            tvTitle.setText(txtClassifyInfo.getTitle());
            tvContent.setText(String.format(StringUtil.getString(R.string.random_txt_classify_tip), txtClassifyInfo.getCount()));
        } else {
            classifyId = 0;
        }
    }

    public int getClassifyId() {
        return classifyId;
    }

    public void setLogo(Drawable drawable) {
        if (drawable == null) {
            ivLogo.setBackgroundResource(R.mipmap.ic_launcher);
        } else {
            ivLogo.setBackground(drawable);
        }
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_random_txt, this);
        ivLogo = findViewById(R.id.iv_logo);
        tvTitle = findViewById(R.id.item_tv_title);
        tvContent = findViewById(R.id.item_tv_content);
        tvReset = findViewById(R.id.item_tv_rest);
        tvReset.getPaint().setFakeBoldText(true);
        tvReset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RouterUtil.get().navigation(RoutePaths.APP_ACTIVITY_RANDOM_TXT_GROUP);
            }
        });
    }

    public RandomTxtView(@NonNull Context context) {
        super(context);
        init();
    }

    public RandomTxtView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RandomTxtView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RandomTxtView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
}
