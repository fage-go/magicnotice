package com.zhangshuo.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.zhangshuo.common.mvp.BaseViewBinder;

import razerdp.basepopup.BasePopupWindow;

public class BasePopupDialog<D extends ViewBinding> extends BasePopupWindow {
    protected D binding;

    private void init(Context context) {
        binding = (D) BaseViewBinder.createViewBindingObject(this, LayoutInflater.from(context));
        setContentView(binding.getRoot());
    }


    public BasePopupDialog(Context context) {
        super(context);
        init(context);
    }

    public BasePopupDialog(Context context, int width, int height) {
        super(context, width, height);
        init(context);
    }

    public BasePopupDialog(Fragment fragment) {
        super(fragment);
        init(fragment.getContext());
    }

    public BasePopupDialog(Fragment fragment, int width, int height) {
        super(fragment, width, height);
        init(fragment.getContext());
    }

    public BasePopupDialog(Dialog dialog) {
        super(dialog);
        init(dialog.getContext());
    }

    public BasePopupDialog(Dialog dialog, int width, int height) {
        super(dialog, width, height);
        init(dialog.getContext());
    }
}
