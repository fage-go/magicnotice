package com.zhangshuo.magicnotice.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SpanUtils;
import com.zhangshuo.common.base.BaseActivity;
import com.zhangshuo.common.router.RouterUtil;
import com.zhangshuo.common.toast.ToastManager;
import com.zhangshuo.common.util.AsyncCallUtils;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.databinding.ActivityConfigTestBinding;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.db.service.ConfigRuleDataService;
import com.zhangshuo.magicnotice.db.service.NotificationDataService;
import com.zhangshuo.magicnotice.notice.model.NotificationInfo;
import com.zhangshuo.magicnotice.ui.adapter.TestLogAdapter;
import com.zhangshuo.magicnotice.ui.dialog.SelectAppDialog;
import com.zhangshuo.magicnotice.ui.dialog.SelectTimeDialog;
import com.zhangshuo.magicnotice.ui.model.ConditionsModel;
import com.zhangshuo.magicnotice.ui.model.ConfigRuleModel;
import com.zhangshuo.magicnotice.ui.model.TestLogInfo;
import com.zhangshuo.magicnotice.ui.model.TestLogItemInfo;
import com.zhangshuo.magicnotice.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@Route(path = RoutePaths.APP_ACTIVITY_CONFIG_TEST)
public class TestConfigActivity extends BaseActivity<ActivityConfigTestBinding> {

    @Autowired
    int ruleId = -1;

    private NotificationInfo notificationInfo = new NotificationInfo();

    private List<ConfigRuleModel> configRuleModelList = new ArrayList<>();
    private TestLogAdapter testLogAdapter;
    private SelectAppDialog selectAppDialog;
    private SelectTimeDialog selectTimeDialog;
    private Handler handler;

    @Override
    protected void initView() {
        setTitleBar(getString(R.string.title_test_config));
        testLogAdapter = new TestLogAdapter(this);
        binding.rvLog.setLayoutManager(new LinearLayoutManager(this));
        binding.rvLog.setAdapter(testLogAdapter);
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
        notificationInfo.setTitle(binding.etTitle.getText().toString());
        notificationInfo.setContent(binding.etContent.getText().toString());
        if (TextUtils.isEmpty(notificationInfo.getPackageName())) {
            ToastManager.get().showShortToast(R.string.test_config_select_app);
            return;
        }
        if (TextUtils.isEmpty(notificationInfo.getTitle())) {
            ToastManager.get().showShortToast(R.string.test_config_input_title);
            return;
        }
        if (TextUtils.isEmpty(notificationInfo.getContent())) {
            ToastManager.get().showShortToast(R.string.test_config_input_txt);
            return;
        }
        if (notificationInfo.getPostTime() == 0) {
            ToastManager.get().showShortToast(R.string.test_config_select_time);
            return;
        }
        if (handler == null) {
            handler = new Handler(Looper.myLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == 1) {
                        int index = msg.arg1;
                        TestLogInfo testLogInfo = (TestLogInfo) msg.obj;
                        if (index >= testLogAdapter.getDataList().size()) {
                            testLogAdapter.getDataList().add(testLogInfo);
                            testLogAdapter.notifyItemInserted(index);
                        } else {
                            testLogAdapter.getDataList().set(index, testLogInfo);
                            testLogAdapter.notifyItemChanged(index);
                        }
                        binding.nestedScrollView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                binding.nestedScrollView.smoothScrollTo(0, binding.llRoot.getMeasuredHeight());
                            }
                        }, 50);
                    }
                }
            };
        }
        testLogAdapter.cleanData();
        toggleShow(true);
        AsyncCallUtils.asyncCall(new Callable<TestLogInfo>() {
            @Override
            public TestLogInfo call() throws Exception {
                TestLogInfo haveHintLog = null;
                for (int i = 0; i < configRuleModelList.size(); i++) {
                    ConfigRuleModel configRuleModel = configRuleModelList.get(i);
                    List<ConditionsModel> conditionList = configRuleModel.getConditionList();
                    //提示 规则名称、条件
                    TestLogInfo testLogInfo = new TestLogInfo();
                    testLogInfo.setName(configRuleModel.getRuleName());
                    testLogInfo.setStep(0);
                    testLogInfo.setMaxStep(conditionList.size());
                    //发送信息
                    boolean allFilterResult = true;
                    for (int j = 0; j < conditionList.size(); j++) {
                        testLogInfo.setStep(j + 1);
                        Thread.sleep(200);
                        ConditionsModel conditionsModel = conditionList.get(j);
                        TestLogItemInfo testLogItemInfo = conditionsModel.getFilterLog(notificationInfo);
                        if (!testLogItemInfo.isResult()) {
                            allFilterResult = false;
                        }
                        testLogInfo.getLogItemInfoList().add(testLogItemInfo);
                        //提示 当前条件是否命中
                        Message itemMsg = handler.obtainMessage();
                        itemMsg.what = 1;
                        itemMsg.obj = testLogInfo;
                        itemMsg.arg1 = i;
                        handler.sendMessage(itemMsg);
                    }
                    Thread.sleep(200);
                    testLogInfo.setResult(allFilterResult);

                    Message msg = handler.obtainMessage();
                    msg.what = 1;
                    msg.obj = testLogInfo;
                    msg.arg1 = i;
                    handler.sendMessage(msg);

                    if (allFilterResult) {
                        haveHintLog = testLogInfo;
                        break;
                    }
                }
                if (haveHintLog == null) {
                    haveHintLog = new TestLogInfo();
                }
                return haveHintLog;
            }
        }, new AsyncCallback<TestLogInfo>() {
            @Override
            public void onSuccess(TestLogInfo result) {
                toggleShow(false);
                setTestResult(result);
            }
        });
    }

    private void toggleShow(boolean startTest) {
        binding.llConfigTest.setClickable(!startTest);
        binding.ivLoad.setVisibility(startTest ? View.VISIBLE : View.GONE);
        if (startTest) {
            RotateAnimation rotateAnimation = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(300);
            rotateAnimation.setInterpolator(new LinearInterpolator());
            rotateAnimation.setRepeatCount(-1);
            binding.ivLoad.startAnimation(rotateAnimation);
        } else {
            binding.ivLoad.clearAnimation();
        }
        binding.tvLoad.setText(startTest ? getString(R.string.test_config_testing) : getString(R.string.test_config_test_start));
        binding.tvTestResult.setVisibility(startTest ? View.GONE : View.VISIBLE);
    }

    private void setTestResult(TestLogInfo testLogInfo) {
        binding.tvTestResult.setTextColor(testLogInfo.isResult() ? Color.parseColor("#39B54A") : Color.parseColor("#E54D42"));
        if (TextUtils.isEmpty(testLogInfo.getName())) {
            binding.tvTestResult.setText(R.string.notification_rule_none);
        } else {
            SpanUtils.with(binding.tvTestResult)
                    .append(getString(R.string.test_config_result_hit))
                    .append(testLogInfo.getName())
                    .setBold()
                    .append(getString(R.string.test_config_result_hit_rule))
                    .create();
        }
    }

    private void setRuleName() {
        if (ruleId == -1) {
            binding.tvRuleName.setText(getString(R.string.no_hit_rule_all_tip));
            ConfigRuleDataService.findConfigRuleList(new AsyncCallback<List<ConfigRuleModel>>() {
                @Override
                public void onSuccess(List<ConfigRuleModel> result) {
                    configRuleModelList.clear();
                    configRuleModelList.addAll(result);
                }
            });
        } else {
            ConfigRuleDataService.findById(ruleId, new AsyncCallback<ConfigRuleModel>() {
                @Override
                public void onSuccess(ConfigRuleModel result) {
                    configRuleModelList.clear();
                    configRuleModelList.add(result);
                    binding.tvRuleName.setText(String.format(StringUtil.getString(R.string.no_hit_rule_tip), result.getRuleName()));
                }
            });
        }
    }

    private void showSelectApp() {
        if (selectAppDialog == null) {
            selectAppDialog = new SelectAppDialog(true);
            selectAppDialog.setOnCallback(new SelectAppDialog.OnCallback() {
                @Override
                public void onResult(List<String> packageNameList, List<String> nameList) {
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
        binding.tvTimeSelect.setText(TimeUtil.formatYMDHMSDate(time));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            int noticeId = data.getIntExtra("noticeId", 0);
            if (noticeId > 0) {
                NotificationDataService.findNotificationById(noticeId, new AsyncCallback<NotificationData>() {
                    @Override
                    public void onSuccess(NotificationData result) {
                        if (binding != null) {
                            setAppLogo(result.getPackageName());
                            binding.etTitle.setText(result.getTitle());
                            binding.etContent.setText(result.getContent());
                            setSelectTime(result.getPostTime());
                            //build info
                            notificationInfo.setPackageName(result.getPackageName());
                            notificationInfo.setTitle(result.getTitle());
                            notificationInfo.setContent(result.getContent());
                            notificationInfo.setPostTime(result.getPostTime());
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler = null;
    }
}
