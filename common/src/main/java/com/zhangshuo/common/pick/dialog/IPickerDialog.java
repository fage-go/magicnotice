package com.zhangshuo.common.pick.dialog;

import android.content.Context;

import com.zhangshuo.common.pick.picker.BasePicker;

public interface IPickerDialog {
  /**
   * 其实可以不提供这个方法，为了方便在{@link BasePicker#show()}
   * @param context
   */
  void showDialog(Context context,BasePicker picker);
}
