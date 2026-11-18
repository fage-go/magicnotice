package com.zhangshuo.magicnotice.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zhangshuo.magicnotice.R;

public class CustomTxtView extends FrameLayout {
    private ImageView ivLogo;
    private EditText etTitle;
    private EditText etContent;

    public void setDefault(String title, String content) {
        if (!TextUtils.isEmpty(title)) {
            etTitle.setText(title);
        }
        if (!TextUtils.isEmpty(content)) {
            etContent.setText(content);
        }
    }

    public void setLogo(Drawable drawable) {
        if (drawable == null) {
            ivLogo.setBackgroundResource(R.mipmap.ic_launcher);
        } else {
            ivLogo.setBackground(drawable);
        }
    }

    public String getInputTitle() {
        return etTitle.getText().toString();
    }

    public String getInputContent() {
        return etContent.getText().toString();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_custom_txt, this);
        ivLogo = findViewById(R.id.iv_logo);
        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
    }

    public CustomTxtView(@NonNull Context context) {
        super(context);
        init();
    }

    public CustomTxtView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTxtView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CustomTxtView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
}
