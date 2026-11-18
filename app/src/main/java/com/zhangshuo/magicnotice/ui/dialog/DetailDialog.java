package com.zhangshuo.magicnotice.ui.dialog;


import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.VibrateUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.common.dialog.BaseBottomSheetDialog;
import com.zhangshuo.common.model.EventMessage;
import com.zhangshuo.common.router.RouterUtil;
import com.zhangshuo.common.toast.ToastManager;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.EventBusUtil;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.common.util.UIUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.barrage.BehaviorHelper;
import com.zhangshuo.magicnotice.config.EventCodes;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.databinding.DialogDetailBinding;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.db.service.ConfigRuleDataService;
import com.zhangshuo.magicnotice.db.service.NotificationDataService;
import com.zhangshuo.magicnotice.notice.NotificationHelper;
import com.zhangshuo.magicnotice.notice.model.PendingInfo;
import com.zhangshuo.magicnotice.ui.activity.ConfigRuleActivity;
import com.zhangshuo.magicnotice.ui.adapter.DetailButtonAdapter;
import com.zhangshuo.magicnotice.ui.adapter.DetailInfoAdapter;
import com.zhangshuo.magicnotice.ui.model.BehaviorModel;
import com.zhangshuo.magicnotice.ui.model.ConfigRuleModel;
import com.zhangshuo.magicnotice.ui.model.DetailButton;
import com.zhangshuo.magicnotice.ui.model.DetailInfo;
import com.zhangshuo.magicnotice.ui.model.behavior.BarrageBehaviorModel;
import com.zhangshuo.magicnotice.ui.model.conditions.AppConditionsModel;
import com.zhangshuo.magicnotice.util.TimeUtil;

import java.util.Arrays;
import java.util.List;

public class DetailDialog extends BaseBottomSheetDialog<DialogDetailBinding> {

    private NotificationData notificationData;
    private DetailButtonAdapter detailButtonAdapter;

    @Override
    public void initView() {
        binding.tvTitleTip.getPaint().setFakeBoldText(true);
        binding.tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notificationData != null) {
                    NotificationHelper.get().openApp(notificationData, BarrageBehaviorModel.TYPE_CLICK_MSG);
                }
            }
        });
        if (notificationData == null) {
            binding.tvTitleTip.postDelayed(new Runnable() {
                @Override
                public void run() {
                    close();
                }
            }, 300);
        } else {
            onSetDataShow();
            buildBtnList();
        }
    }

    private void buildBtnList() {
        if (notificationData == null) {
            binding.tvTitleTip.postDelayed(new Runnable() {
                @Override
                public void run() {
                    close();
                }
            }, 300);
            return;
        }
        detailButtonAdapter = new DetailButtonAdapter(getContext());
        detailButtonAdapter.getDataList().add(new DetailButton(StringUtil.getString(R.string.detail_btn_copy), Color.parseColor("#2E333B")));
        if (notificationData.getRuleId() >= 0)
            detailButtonAdapter.getDataList().add(new DetailButton(notificationData.getLock() == 0 ? StringUtil.getString(R.string.detail_btn_lock)
                    : StringUtil.getString(R.string.detail_btn_unlock), Color.parseColor("#F37B1D")));
        if (notificationData.getRuleId() > 0) {
            detailButtonAdapter.getDataList().add(new DetailButton(StringUtil.getString(R.string.detail_btn_reHit), Color.parseColor("#0081ff")));
        } else if (notificationData.getRuleId() == 0) {
            detailButtonAdapter.getDataList().add(new DetailButton(StringUtil.getString(R.string.detail_btn_create_rule), Color.parseColor("#0081ff")));
        }
        if (notificationData.getRuleId() >= 0)
            detailButtonAdapter.getDataList().add(new DetailButton(StringUtil.getString(R.string.detail_btn_save_schedule), Color.parseColor("#2E333B")));
        detailButtonAdapter.getDataList().add(new DetailButton(StringUtil.getString(R.string.detail_btn_save_pic), Color.parseColor("#1CBBB4")));
        detailButtonAdapter.getDataList().add(new DetailButton(StringUtil.getString(R.string.detail_btn_del), Color.parseColor("#E54D42")));
        binding.rvFunction.setLayoutManager(new FlexboxLayoutManager(getContext()));
        binding.rvFunction.setAdapter(detailButtonAdapter);
        detailButtonAdapter.setOnItemClickListener(new BaseRAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                VibrateUtils.vibrate(30L);
                DetailButton item = detailButtonAdapter.getItem(position);
                if (item.getName().startsWith(StringUtil.getString(R.string.detail_btn_copy))) {
                    //复制
                    ClipboardUtils.copyText(notificationData.getContent());
                    ToastManager.get().noticeShortMessage(StringUtil.getString(R.string.detail_btn_copy_tip));
                } else if (item.getName().startsWith(StringUtil.getString(R.string.detail_btn_del))) {
                    //删除
                    if (notificationData.getLock() == 1) {
                        ToastManager.get().showShortToast(StringUtil.getString(R.string.detail_collect_none_del));
                        return;
                    }
                    EventBusUtil.sendEvent(new EventMessage<>(EventCodes.REFRESH_NOTIFICATION_DELETE, notificationData));
                    close();
                } else if (item.getName().startsWith(StringUtil.getString(R.string.detail_btn_lock))
                        || item.getName().startsWith(StringUtil.getString(R.string.detail_btn_unlock))) {
                    //收藏功能
                    int lock = notificationData.getLock();
                    lock = lock == 0 ? 1 : 0;
                    notificationData.setLock(lock);
                    NotificationDataService.updateNotificationLock(notificationData.getId(), notificationData.getLock(), null);
                    if (lock == 1) {
                        ToastManager.get().noticeShortMessage(StringUtil.getString(R.string.detail_btn_lock_yes));
                    } else {
                        ToastManager.get().noticeShortMessage(StringUtil.getString(R.string.detail_btn_lock_no));
                    }
                    binding.ivLock.setVisibility(notificationData.getLock() == 1 ? View.VISIBLE : View.GONE);
                    EventBusUtil.sendEvent(new EventMessage<>(EventCodes.REFRESH_NOTIFICATION_LOCK, notificationData));
                    buildBtnList();
                } else if (item.getName().startsWith(StringUtil.getString(R.string.detail_btn_reHit))) {
                    //重新触发规则效果
                    int ruleId = notificationData.getRuleId();
                    if (ruleId > 0) {
                        ConfigRuleDataService.findById(ruleId, new AsyncCallback<ConfigRuleModel>() {
                            @Override
                            public void onSuccess(ConfigRuleModel result) {
                                if (result != null) {
                                    List<BehaviorModel> behaviorList = result.getBehaviorList();
                                    BehaviorHelper.exec(notificationData, behaviorList);
                                }
                            }
                        });
                    }
                } else if (item.getName().startsWith(StringUtil.getString(R.string.detail_btn_create_rule))) {
                    //快捷创建规则
                    String fastRuleName = String.format(StringUtil.getString(R.string.detail_create_rule_tip), notificationData.getAppName());
                    Bundle bundle = new Bundle();
                    bundle.putString("fastRuleName", fastRuleName);
                    ConfigRuleModel configRuleModel = new ConfigRuleModel();
                    configRuleModel.setRuleName(fastRuleName);
                    AppConditionsModel appConditionsModel = new AppConditionsModel();
                    appConditionsModel.setPackageNameList(Arrays.asList(notificationData.getPackageName()));
                    configRuleModel.setConditionList(Arrays.asList(
                            appConditionsModel
                    ));
                    ConfigRuleActivity.tmpConfigRuleMode = configRuleModel;
                    RouterUtil.get().navigation(RoutePaths.APP_ACTIVITY_CONFIG_RULE, bundle);
                } else if (item.getName().startsWith(StringUtil.getString(R.string.detail_btn_save_pic))) {
                    //留影
                    SavePictureDialog.showDialog(getContext(), notificationData);
                } else if (item.getName().startsWith(StringUtil.getString(R.string.detail_btn_save_schedule))) {
                    //定时
                    Bundle bundle = new Bundle();
                    bundle.putString("title", notificationData.getTitle());
                    bundle.putString("content", notificationData.getContent());
                    RouterUtil.get().navigation(RoutePaths.APP_ACTIVITY_MAKE_SCHEDULER, bundle);
                }
            }
        });
    }

    private void onSetDataShow() {
        if (notificationData == null || binding == null) return;
        String titleIcon = notificationData.getIconPath();
        if (TextUtils.isEmpty(titleIcon)) {
            titleIcon = notificationData.getLogoPath();
        }
        Glide.with(getContext())
                .load(titleIcon)
                .transform(new RoundedCorners(UIUtil.dp2Px(4)))
                .into(binding.ivTitle);
        binding.tvTitle.setText(notificationData.getTitle());
        binding.tvContent.setText(notificationData.getContent());
        binding.ivLock.setVisibility(notificationData.getLock() == 1 ? View.VISIBLE : View.GONE);
        PendingInfo directLink = NotificationHelper.get().findDirectLink(notificationData);
        binding.tvConfirm.setText((directLink == null || directLink.getPendingIntent() == null) ? getString(R.string.detail_btn_open_app) : getString(R.string.detail_btn_open_notify));
        //创建基本信息
        DetailInfoAdapter infoAdapter = new DetailInfoAdapter(getContext());
        infoAdapter.addData(buildInfoTitle());
        binding.rvInfo.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.rvInfo.setAdapter(infoAdapter);
        infoAdapter.setOnCallback(new DetailInfoAdapter.OnCallback() {
            @Override
            public void onClick(DetailInfo detailInfo) {
                if (detailInfo.getName().startsWith(StringUtil.getString(R.string.detail_info_source))) {
                    Bundle bundle = new Bundle();
                    bundle.putString("packageName", notificationData.getPackageName());
                    RouterUtil.get().navigation(RoutePaths.APP_ACTIVITY_SEARCH, bundle);
                } else if (detailInfo.getName().startsWith(StringUtil.getString(R.string.detail_info_rule))) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("ruleId", notificationData.getRuleId());
                    RouterUtil.get().navigation(RoutePaths.APP_ACTIVITY_SEARCH, bundle);
                }
            }
        });

    }

    private List<DetailInfo> buildInfoTitle() {
        return Arrays.asList(
                new DetailInfo(StringUtil.getString(R.string.detail_info_time), TimeUtil.dateFormatTime(notificationData.getPostTime())),
                new DetailInfo(StringUtil.getString(R.string.detail_info_source), notificationData.getAppName(), true),
                new DetailInfo(StringUtil.getString(R.string.detail_info_rule), notificationData.getRuleName(), true)
        );
    }


    public void setNotification(NotificationData notificationData) {
        this.notificationData = notificationData;
    }

}
