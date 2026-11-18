package com.zhangshuo.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.zhangshuo.common.R;

/**
 * @author: zhangshuo
 * Date:2021/9/13
 * @doc:
 */
public class CheckView extends androidx.appcompat.widget.AppCompatImageView {
    private int checkedImageId = R.mipmap.ic_check_yes;
    private int unCheckedImageId = R.mipmap.ic_check_no;
    private boolean checked = false;
    private OnCheckListener onCheckListener;

    public void setChecked(boolean checked) {
        this.checked = checked;
        setBackgroundResource(checked ? checkedImageId : unCheckedImageId);
    }

    public boolean isChecked() {
        return checked;
    }

    private void init() {
        setChecked(false);
    }

    public void toggleClick(boolean toggle) {
        if (toggle) {
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setChecked(!checked);
                    if (onCheckListener != null) {
                        onCheckListener.onCheck(checked);
                    }
                }
            });
        } else {
            setOnCheckListener(null);
        }
    }

    public void setOnCheckListener(OnCheckListener onCheckListener) {
        this.onCheckListener = onCheckListener;
    }

    public interface OnCheckListener {
        void onCheck(boolean isChecked);
    }


    public CheckView(Context context) {
        super(context);
        init();
    }

    public CheckView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CheckView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

}
