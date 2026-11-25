package com.zhangshuo.magicnotice.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.AppUtils;
import com.zhangshuo.common.base.BaseActivity;
import com.zhangshuo.common.router.RouterUtil;
import com.zhangshuo.common.toast.ToastManager;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.databinding.ActivityConfigTestBinding;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.db.service.ConfigRuleDataService;
import com.zhangshuo.magicnotice.db.service.NotificationDataService;
import com.zhangshuo.magicnotice.notice.model.NotificationInfo;
import com.zhangshuo.magicnotice.notice.factory.RuleEvaluation;
import com.zhangshuo.magicnotice.notice.factory.NotificationRuleInputs;
import com.zhangshuo.magicnotice.ui.adapter.TestLogAdapter;
import com.zhangshuo.magicnotice.ui.dialog.SelectAppDialog;
import com.zhangshuo.magicnotice.ui.dialog.SelectTimeDialog;
import com.zhangshuo.magicnotice.ui.model.ConditionsModel;
import com.zhangshuo.magicnotice.ui.model.ConfigRuleModel;
import com.zhangshuo.magicnotice.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

@Route(path = RoutePaths.APP_ACTIVITY_CONFIG_TEST)
public class TestConfigActivity extends BaseActivity<ActivityConfigTestBinding> {

    @Autowired
    int ruleId = -1;

    private NotificationInfo notificationInfo = new NotificationInfo();

    private TestLogAdapter testLogAdapter;
    private SelectAppDialog selectAppDialog;
    private SelectTimeDialog selectTimeDialog;
    private boolean historical;
    private boolean loadingHistory;
    private int formRevision;
    private int testGeneration;

    @Override
    protected void initView() {
        setTitleBar(getString(R.string.title_test_config));
        testLogAdapter = new TestLogAdapter(this);
        binding.rvLog.setLayoutManager(new LinearLayoutManager(this));
        binding.rvLog.setAdapter(testLogAdapter);
        TextWatcher edit = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!loadingHistory) markManual();
            }
            @Override public void afterTextChanged(Editable s) { }
        };
        binding.etTitle.addTextChangedListener(edit);
        binding.etContent.addTextChangedListener(edit);
        binding.ivLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectApp();
            }
        });
        binding.clTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectTime();
            }
        });
        binding.tvNoticeSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouterUtil.get().navigation(TestConfigActivity.this, RoutePaths.APP_ACTIVITY_SELECT_NOTICE, 1001);
            }
        });
        binding.llConfigTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRunTest();
            }
        });
    }

    @Override
    protected void initData() {
        setRuleName();
    }

    private void startRunTest() {
        if (TextUtils.isEmpty(notificationInfo.getPackageName())) {
            ToastManager.get().showShortToast(R.string.test_config_select_app);
            return;
        }
        if (notificationInfo.getPostTime() == 0) {
            ToastManager.get().showShortToast(R.string.test_config_select_time);
            return;
        }
        final NotificationInfo sample = new NotificationInfo();
        sample.setPackageName(notificationInfo.getPackageName());
        sample.setPostTime(notificationInfo.getPostTime());
        sample.setKey(notificationInfo.getKey());
        sample.setTitle(historical ? notificationInfo.getTitle() : binding.etTitle.getText().toString());
        sample.setContent(historical ? notificationInfo.getContent() : binding.etContent.getText().toString());
        final boolean replay = historical;
        final int revision = formRevision;
        final int generation = ++testGeneration;
        binding.tvTestResult.setVisibility(View.VISIBLE);
        binding.tvTestResult.setText(R.string.test_config_testing);
        testLogAdapter.showResult(new ArrayList<>(), ruleId);
        ConfigRuleDataService.findConfigRuleEvaluationList(new AsyncCallback<List<ConfigRuleModel>>() {
            @Override public void onSuccess(List<ConfigRuleModel> rules) {
                if (isFinishing() || isDestroyed() || generation != testGeneration || revision != formRevision) return;
                try {
                    RuleEvaluation.Result<ConfigRuleModel, ConditionsModel> result =
                            RuleEvaluation.evaluate(rules, new NotificationRuleInputs(sample, replay), true);
                    if (result.winner != null) {
                        binding.tvTestResult.setText(getString(R.string.rule_test_winner, result.winner.getRuleName()));
                    } else if (result.firstPossible != null) {
                        binding.tvTestResult.setText(result.conditionalWinner == null
                                ? getString(R.string.rule_test_uncertain, result.firstPossible.getRuleName())
                                : getString(R.string.rule_test_uncertain_fallback, result.firstPossible.getRuleName(),
                                        result.conditionalWinner.getRuleName()));
                    } else {
                        binding.tvTestResult.setText(R.string.notification_rule_none);
                    }
                    testLogAdapter.showResult(result.rows, ruleId);
                } catch (RuntimeException error) {
                    binding.tvTestResult.setText(R.string.rule_test_error);
                }
            }

            @Override public void onError(Throwable error) {
                if (!isFinishing() && !isDestroyed() && generation == testGeneration && revision == formRevision) {
                    binding.tvTestResult.setText(R.string.rule_test_error);
                }
            }
        });
    }

    private void markManual() {
        ++formRevision;
        historical = false;
        notificationInfo.setKey(null);
        binding.tvInputOrigin.setText(R.string.rule_test_manual);
        binding.tvTestResult.setVisibility(View.GONE);
        testLogAdapter.showResult(new ArrayList<>(), ruleId);
    }

    private void setRuleName() {
        if (ruleId < 0) return;
        ConfigRuleDataService.findById(ruleId, new AsyncCallback<ConfigRuleModel>() {
            @Override public void onSuccess(ConfigRuleModel result) {
                if (result != null && !isFinishing() && !isDestroyed()) {
                    binding.tvRuleName.setText(getString(R.string.rule_test_selected, result.getRuleName()));
                }
            }
        });
    }

    private void showSelectApp() {
        if (selectAppDialog == null) {
            selectAppDialog = new SelectAppDialog(true);
            selectAppDialog.setOnCallback(new SelectAppDialog.OnCallback() {
                @Override
                public void onResult(List<String> packageNameList, List<String> nameList) {
                    markManual();
                    if (packageNameList.isEmpty()) {
                        binding.ivLogo.setBackground(null);
                        notificationInfo.setPackageName("");
                    } else {
                        setAppLogo(packageNameList.get(0));
                        notificationInfo.setPackageName(packageNameList.get(0));
                    }
                }
            });
        }
        List<String> selectPkgList = new ArrayList<>();
        if (!TextUtils.isEmpty(notificationInfo.getPackageName())) {
            selectPkgList.add(notificationInfo.getPackageName());
        }
        selectAppDialog.setSelectedPackageNameList(selectPkgList);
        selectAppDialog.show(this);
    }

    private void showSelectTime() {
        if (selectTimeDialog == null) {
            selectTimeDialog = new SelectTimeDialog();
            selectTimeDialog.setOnCallback(new SelectTimeDialog.OnCallback() {
                @Override
                public void onResult(long selectTime) {
                    markManual();
                    setSelectTime(TimeUtil.getHMDate(TimeUtil.getHMTime(selectTime)));
                }
            });
        }
        selectTimeDialog.setSelectTime(notificationInfo.getPostTime());
        selectTimeDialog.show(this);
    }

    private void setAppLogo(String packageName) {
        binding.ivLogo.setBackground(AppUtils.getAppIcon(packageName));
    }

    private void setSelectTime(long time) {
        notificationInfo.setPostTime(time);
        binding.tvTimeSelect.setText(android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss", time));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            int noticeId = data.getIntExtra("noticeId", 0);
            if (noticeId <= 0) return;
            final int revision = ++formRevision;
            NotificationDataService.findNotificationById(noticeId, new AsyncCallback<NotificationData>() {
                @Override public void onSuccess(NotificationData result) {
                    if (result == null || isFinishing() || isDestroyed() || revision != formRevision) return;
                    loadingHistory = true;
                    try {
                        setAppLogo(result.getPackageName());
                        binding.etTitle.setText(result.getTitle());
                        binding.etContent.setText(result.getContent());
                        setSelectTime(result.getPostTime());
                        notificationInfo.setPackageName(result.getPackageName());
                        notificationInfo.setTitle(result.getTitle());
                        notificationInfo.setContent(result.getContent());
                        notificationInfo.setKey(result.getKey());
                        historical = true;
                        binding.tvInputOrigin.setText(R.string.rule_test_history);
                        binding.tvTestResult.setVisibility(View.GONE);
                        testLogAdapter.showResult(new ArrayList<>(), ruleId);
                    } finally {
                        loadingHistory = false;
                    }
                }
            });
        }
    }
}
