package com.zhangshuo.common.pick.picker.option;

import com.zhangshuo.common.pick.dataset.OptionDataSet;
import com.zhangshuo.common.pick.picker.OptionPicker;

import java.util.List;


/**
 * Created by fuchaoyang on 2018/7/6.<br/>
 * description：
 */

public interface IOptionDelegate {
  //void init(int hierarchy, List<PickerView> pickerViews, int[] selectedPosition);
  void init(OptionPicker.Delegate delegate);

  void setData(List<? extends OptionDataSet>... options);

  void setSelectedWithValues(String... values);

  OptionDataSet[] getSelectedOptions();

  void reset();
}
