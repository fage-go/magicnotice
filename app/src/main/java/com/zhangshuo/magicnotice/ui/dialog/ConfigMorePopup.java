package com.zhangshuo.magicnotice.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import com.zhangshuo.common.dialog.CommonDialog;
import com.zhangshuo.common.listener.OnClickCallback;
import com.zhangshuo.common.model.EventMessage;
import com.zhangshuo.common.router.RouterUtil;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.EventBusUtil;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.barrage.BehaviorHelper;
import com.zhangshuo.magicnotice.config.EventCodes;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.db.service.ConfigRuleDataService;
import com.zhangshuo.magicnotice.ui.model.ConfigRuleModel;
import com.zhangshuo.magicnotice.widget.ShortcutsUtil;
import com.zhangshuo.magicnotice.widget.WidgetUtil;
import com.zhangshuo.magicnotice.widget.model.WidgetInfo;

import razerdp.basepopup.BasePopupWindow;

public class ConfigMorePopup extends BasePopupWindow {
    private TextView tvShortcuts, tvWidget, tvCopy, tvPreview, tvTest, tvDel;
    private View vLineShortcuts, vLineWidget;
    private ConfigRuleModel configRuleModel;

    public ConfigMorePopup(Context context) {
        super(context);
        setContentView(R.layout.popup_config_more);
        tvWidget = findViewById(R.id.item_tv_widget);
        vLineWidget = findViewById(R.id.item_v_widget);
        tvShortcuts = findViewById(R.id.item_tv_shortcuts);
        vLineShortcuts = findViewById(R.id.item_v_shortcuts);
        tvCopy = findViewById(R.id.item_tv_copy);
        tvPreview = findViewById(R.id.item_tv_preview);
        tvTest = findViewById(R.id.item_tv_test);
        tvDel = findViewById(R.id.item_tv_del);

        if (WidgetUtil.showWidget(getContext())) {
            tvWidget.setVisibility(View.VISIBLE);
            vLineWidget.setVisibility(View.VISIBLE);
        } else {
            tvWidget.setVisibility(View.GONE);
            vLineWidget.setVisibility(View.GONE);
        }
        if (ShortcutsUtil.isShortCuts()) {
            tvShortcuts.setVisibility(View.VISIBLE);
            vLineShortcuts.setVisibility(View.VISIBLE);
        } else {
            tvShortcuts.setVisibility(View.GONE);
            vLineShortcuts.setVisibility(View.GONE);
        }


        tvWidget.setOnClickListener(new OnClickCallback() {
            @Override
            public void click(View v) {
                if (configRuleModel == null) return;
                WidgetInfo widgetInfo = new WidgetInfo();
                widgetInfo.setConfigId(configRuleModel.getId());
                widgetInfo.setConfigName(configRuleModel.getRuleName());
                widgetInfo.setConfigToggle(configRuleModel.isOpen());
                WidgetUtil.get().addWidget(getContext(), widgetInfo);
                dismiss();
            }
        });
        if (ShortcutsUtil.isShortCuts()) {
            tvShortcuts.setVisibility(View.VISIBLE);
        } else {
            tvShortcuts.setVisibility(View.GONE);
        }
        tvShortcuts.setOnClickListener(new OnClickCallback() {
            @Override
            public void click(View v) {
                if (ShortcutsUtil.get().haveShortcuts(configRuleModel.getId())) {
                    ShortcutsUtil.get().removeShortcutsInfo(configRuleModel.getId());
                    tvShortcuts.setText(StringUtil.getString(R.string.config_more_shortcuts_add));
                } else {
                    ShortcutsUtil.get().addShortcutsInfo(configRuleModel.getId());
                    tvShortcuts.setText(StringUtil.getString(R.string.config_more_shortcuts_rm));
                }
                dismiss();
            }
        });
        tvCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (configRuleModel == null) return;
                dismiss();
                Bundle bundle = new Bundle();
                bundle.putInt("copyRuleId", configRuleModel.getId());
                RouterUtil.get().navigation(RoutePaths.APP_ACTIVITY_CONFIG_RULE, bundle);
            }
        });
        tvPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (configRuleModel == null) return;
                dismiss();
                BehaviorHelper.exec(configRuleModel);
            }
        });
        tvTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (configRuleModel == null) return;
                Bundle bundle = new Bundle();
                bundle.putInt("ruleId", configRuleModel.getId());
                RouterUtil.get().navigation(RoutePaths.APP_ACTIVITY_CONFIG_TEST, bundle);
                dismiss();
            }
        });
        tvDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (configRuleModel == null) return;
                //删除
                String ruleName = "";
                if (TextUtils.isEmpty(ruleName) && configRuleModel != null) {
                    ruleName = configRuleModel.getRuleName();
                }
                SpannableString spannableString = new SpannableString(String.format(StringUtil.getString(R.string.delete_search_tip), ruleName));
                int startIndex = spannableString.toString().indexOf(ruleName);
                spannableString.setSpan(new StyleSpan(Typeface.BOLD),
                        startIndex, startIndex + ruleName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                CommonDialog.newInstance()
                        .setContent(spannableString)
                        .setLeftAction(StringUtil.getString(R.string.delete_dialog_cancel))
                        .setCommonAction(StringUtil.getString(R.string.delete_dialog_confirm))
                        .setAutoClose(true)
                        .setCommonActionColor(Color.parseColor("#E54D42"))
                        .setCommonActionListener(new CommonDialog.ActionListener() {
                            @Override
                            public void onClick(CommonDialog dialog) {
                                ConfigRuleDataService.deleteConfig(configRuleModel.getId(), new AsyncCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean result) {
                                        EventBusUtil.sendEvent(new EventMessage<>(EventCodes.UPDATE_RULE));
                                        dismiss();
                                    }
                                });
                            }
                        }).show(context);
            }
        });
    }

    public void showByConfigRuleModel(View view, ConfigRuleModel configRuleModel) {
        this.configRuleModel = configRuleModel;
        setOffsetY(-10);
        showPopupWindow(view);
        if (ShortcutsUtil.get().haveShortcuts(configRuleModel.getId())) {
            tvShortcuts.setText(StringUtil.getString(R.string.config_more_shortcuts_rm));
        } else {
            tvShortcuts.setText(StringUtil.getString(R.string.config_more_shortcuts_add));
        }
    }


}
