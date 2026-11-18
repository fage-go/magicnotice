package com.zhangshuo.common.toast;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zhangshuo.common.R;


public class ToastView extends FrameLayout {

    private TextView textView;

    public void setData(String msg, int resId) {
        if (resId > 0) {
            View view = addToastView(R.layout.base_item_toast_msg_icon);

            ImageView imageView = view.findViewById(R.id.item_toast_iv_icon);
            imageView.setBackgroundResource(resId);
            textView = view.findViewById(R.id.item_toast_tv_text);
            textView.setText(msg);
        } else {
            setData(msg);
        }

    }

    public void setData(String msg) {
        View view = addToastView(R.layout.base_item_toast_msg);
        textView = view.findViewById(R.id.item_toast_tv_text);
        textView.setText(msg);
    }


    private View addToastView(int viewId) {
        removeAllViews();
        View inflater = LayoutInflater.from(getContext()).inflate(viewId, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
        int margin = (int) getDp(5);
        layoutParams.setMargins(margin, margin, margin, margin);
        layoutParams.gravity = Gravity.CENTER;
        addView(inflater, layoutParams);
        return inflater;
    }

    public void setText(String text) {
        if (textView != null) {
            textView.setText(text + "");
        }
    }

    private void init() {

    }

    public float getDp(float dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public ToastView(@NonNull Context context) {
        super(context);
        init();
    }

    public ToastView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ToastView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ToastView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
}
