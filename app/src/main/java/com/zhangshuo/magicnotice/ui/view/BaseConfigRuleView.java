package com.zhangshuo.magicnotice.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

public abstract class BaseConfigRuleView extends FrameLayout {
    private Dialog dialog;

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void setResult(Object oldResult){

    }

    public abstract Object getResult();

    public BaseConfigRuleView(@NonNull Context context) {
        super(context);
    }


    public interface OnCallback {
        void onResult(Object object);
    }

    @Override
    protected void onDetachedFromWindow() {
        dialog = null;
        super.onDetachedFromWindow();
    }
}
