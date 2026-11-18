package com.zhangshuo.common.pick.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.CallSuper;

import com.zhangshuo.common.R;
import com.zhangshuo.common.databinding.DialogPickerviewDefaultBinding;
import com.zhangshuo.common.dialog.BaseBottomDialog;
import com.zhangshuo.common.pick.picker.BasePicker;


/**
 * picker.dialog(IPickerDialog接口)自定义Dialog。提供DefaultPickerDialog，支持全局设定
 */
public class DefaultPickerDialog extends BaseBottomDialog<DialogPickerviewDefaultBinding> implements IPickerDialog, View.OnClickListener {
    private BasePicker picker;
    protected OnPickerChooseListener mOnPickerChooseListener;


    public void setOnPickerChooseListener(OnPickerChooseListener onPickerChooseListener) {
        this.mOnPickerChooseListener = onPickerChooseListener;
    }


    @Override
    public void showDialog(Context context, BasePicker picker) {
        this.picker = picker;
        show(context);
    }

    @CallSuper
    @Override
    public void onClick(View v) {
        if (!picker.canSelected()) return;//  滑动未停止不响应点击事件
        if (v.getId() == R.id.btn_confirm) {
            // 给用户拦截
            if (mOnPickerChooseListener == null || mOnPickerChooseListener.onConfirm()) {
                // 抛给picker去处理
                dismiss();
                picker.onConfirm();
            }
        } else if (v.getId() == R.id.btn_cancel) {
            dismiss();
            if (mOnPickerChooseListener != null) {
                mOnPickerChooseListener.onCancel();
            }
        }
    }

    @Override
    public void initView() {
        binding.btnCancel.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);
        binding.llRoot.addView(picker.view());
    }
}
