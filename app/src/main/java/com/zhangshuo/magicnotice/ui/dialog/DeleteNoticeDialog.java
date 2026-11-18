package com.zhangshuo.magicnotice.ui.dialog;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.zhangshuo.common.dialog.BaseBottomDialog;
import com.zhangshuo.common.dialog.CommonDialog;
import com.zhangshuo.common.dialog.LoadingFactory;
import com.zhangshuo.common.model.EventMessage;
import com.zhangshuo.common.pick.picker.BasePicker;
import com.zhangshuo.common.pick.picker.TimePicker;
import com.zhangshuo.common.pick.widget.DefaultCenterDecoration;
import com.zhangshuo.common.pick.widget.PickerView;
import com.zhangshuo.common.toast.ToastManager;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.EventBusUtil;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.EventCodes;
import com.zhangshuo.magicnotice.databinding.DialogDeleteNoticeBinding;
import com.zhangshuo.magicnotice.db.model.CountInfo;
import com.zhangshuo.magicnotice.db.service.NotificationDataService;
import com.zhangshuo.magicnotice.util.TimeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DeleteNoticeDialog extends BaseBottomDialog<DialogDeleteNoticeBinding> {


    private SelectAppDialog selectAppDialog;
    private List<String> selectPkgNameList = new ArrayList<>();
    private List<String> selectAppNameList = new ArrayList<>();

    private SelectRuleDialog selectRuleDialog;
    private List<Integer> selectRuleIdList = new ArrayList<>();
    private List<String> selectRuleNameList = new ArrayList<>();
    private TimePicker timePicker;

    @Override
    public void initView() {
        binding.tvCancel.setOnClickListener(v -> close());
        binding.tvConfirm.setOnClickListener(v -> doDelete());

        binding.tvFilterApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectAppDialog == null) {
                    selectAppDialog = new SelectAppDialog(false);
                    selectAppDialog.setOnCallback(new SelectAppDialog.OnCallback() {
                        @Override
                        public void onResult(List<String> packageNameList, List<String> nameList) {
                            selectPkgNameList.clear();
                            selectPkgNameList.addAll(packageNameList);
                            selectAppNameList.clear();
                            selectAppNameList.addAll(nameList);
                            if (packageNameList.isEmpty()) {
                                binding.tvFilterApp.setText(getText(R.string.search_filter_all_app));
                            } else {
                                binding.tvFilterApp.setText(String.format(getString(R.string.delete_dialog_select_app), packageNameList.size() + ""));
                            }
                        }
                    });
                }
                selectAppDialog.setSelectedPackageNameList(selectPkgNameList);
                selectAppDialog.show(getContext());
            }
        });
        binding.tvFilterRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectRuleDialog == null) {
                    selectRuleDialog = new SelectRuleDialog(false);
                    selectRuleDialog.setOnCallback(new SelectRuleDialog.OnCallback() {
                        @Override
                        public void onResult(List<Integer> ruleIdList, List<String> ruleNameList) {
                            selectRuleIdList.clear();
                            selectRuleIdList.addAll(ruleIdList);
                            selectRuleNameList.clear();
                            selectRuleNameList.addAll(ruleNameList);
                            if (selectRuleIdList.isEmpty()) {
                                binding.tvFilterRule.setText(getText(R.string.search_filter_all_rule));
                            } else {
                                binding.tvFilterRule.setText(String.format(getString(R.string.delete_dialog_select_rule), selectRuleIdList.size() + ""));
                            }
                        }
                    });
                }
                selectRuleDialog.setSelectedIdList(selectRuleIdList);
                selectRuleDialog.show(getContext());
            }
        });

        //处理日期
        timePicker = createTimePicker();
        binding.flFilterTime.addView(timePicker.view());
    }

    private TimePicker createTimePicker() {
        long startTime = 0;
        long endTime = 0;
        try {
            PackageInfo packageInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            startTime = packageInfo.firstInstallTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        endTime = calendar.getTimeInMillis();
        return new TimePicker.Builder(getContext(), TimePicker.TYPE_DATE, null)
                .dialog(null)
                .setRangDate(startTime, endTime)
                .setInterceptor(new BasePicker.Interceptor() {
                    @Override
                    public void intercept(PickerView pickerView, LinearLayout.LayoutParams params) {
                        DefaultCenterDecoration mDefaultCenterDecoration = new DefaultCenterDecoration(getContext());
                        mDefaultCenterDecoration.setLineColor(Color.parseColor("#EAEBED"));
                        pickerView.setCenterDecoration(mDefaultCenterDecoration);
                        pickerView.setColor(Color.parseColor("#0081FF"), Color.parseColor("#989CA8"));
                        pickerView.setVisibleItemCount(5);
                        pickerView.setTextSize(14, 18);
                        pickerView.setItemSize(30);
                    }
                })
                .create();
    }

    private void doDelete() {
        long time = timePicker.getSelectedDates().getTime();
        String timeStr = TimeUtil.formatYMDTime(time);
        String appNameStr = new String();
        String ruleNameStr = new String();
        for (int i = 0; i < selectAppNameList.size() && i < 3; i++) {
            appNameStr = appNameStr + "," + selectAppNameList.get(i);
        }
        if (TextUtils.isEmpty(appNameStr)) {
            appNameStr = getString(R.string.search_filter_all_app);
        } else {
            appNameStr = appNameStr.substring(1);
            if (selectAppNameList.size() > 3) {
                appNameStr = appNameStr + "..." + String.format(getString(R.string.delete_dialog_select_app), selectAppNameList.size() + "");
            }
        }
        for (int i = 0; i < selectRuleNameList.size() && i < 3; i++) {
            ruleNameStr = ruleNameStr + "," + selectRuleNameList.get(i);
        }
        if (TextUtils.isEmpty(ruleNameStr)) {
            ruleNameStr = getString(R.string.search_filter_all_rule);
        } else {
            ruleNameStr = ruleNameStr.substring(1);
            if (selectRuleNameList.size() > 3) {
                ruleNameStr = ruleNameStr + "..." + String.format(getString(R.string.delete_dialog_select_rule), selectRuleNameList.size() + "");
            }
        }
        String content = "\n\t\t应用· " + appNameStr;
        content = content + "\n\t\t规则· " + ruleNameStr;
        content = content + "\n\t\t时间· " + String.format(getString(R.string.set_clean_tip_time), timeStr) + "\n";
        SpannableString spannableString = new SpannableString(String.format(getString(R.string.set_clean_tip_content), content));
        int startIndex = spannableString.toString().indexOf(content);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, startIndex + content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(12, true), startIndex, startIndex + content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        CommonDialog.newInstance()
                .setContent(spannableString)
                .setTitle(getString(R.string.set_clean_tip_title))
                .setLeftAction(getString(R.string.set_clean_tip_cancel))
                .setContentGravity(Gravity.START)
                .setCommonAction(getString(R.string.set_clean_tip_confirm))
                .setCommonActionColor(getContext().getColor(R.color.base_color_red))
                .setCommonActionListener(new CommonDialog.ActionListener() {
                    @Override
                    public void onClick(CommonDialog dialog) {
                        LoadingFactory.showLoadingDialog(getContext(), getString(R.string.delete_delete_loading), false);
                        NotificationDataService.deleteWithConditions(
                                selectPkgNameList, selectRuleIdList, time, new AsyncCallback<CountInfo>() {
                                    @Override
                                    public void onSuccess(CountInfo result) {
                                        LoadingFactory.dismissLoadingDialog();
                                        ToastManager.get().noticeShortMessage(StringUtil.getString(R.string.set_clean_tip_done));
                                        EventBusUtil.sendEvent(new EventMessage<>(EventCodes.UPDATE_INDEX_NOTIFICATION));
                                    }
                                });
                    }
                }).show(getContext());
    }
}
