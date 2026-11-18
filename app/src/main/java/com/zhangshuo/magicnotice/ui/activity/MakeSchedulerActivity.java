package com.zhangshuo.magicnotice.ui.activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.AppUtils;
import com.zhangshuo.common.base.BaseActivity;
import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.common.listener.OnClickCallback;
import com.zhangshuo.common.model.EventMessage;
import com.zhangshuo.common.toast.ToastManager;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.EventBusUtil;
import com.zhangshuo.common.util.GsonUtils;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.EventCodes;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.databinding.ActivityMakeSchedulerBinding;
import com.zhangshuo.magicnotice.db.service.RemindDataService;
import com.zhangshuo.magicnotice.scheduler.SchedulerUtil;
import com.zhangshuo.magicnotice.ui.dialog.SelectAppDialog;
import com.zhangshuo.magicnotice.ui.dialog.SelectTimeDialog;
import com.zhangshuo.magicnotice.ui.model.RemindInfo;
import com.zhangshuo.magicnotice.ui.model.RemindTimeInfo;
import com.zhangshuo.magicnotice.ui.model.TxtClassifyInfo;
import com.zhangshuo.magicnotice.ui.view.CustomTxtView;
import com.zhangshuo.magicnotice.ui.view.MTabLayoutView;
import com.zhangshuo.magicnotice.ui.view.RandomTxtView;
import com.zhangshuo.magicnotice.ui.view.remind.IDataRemindCallback;
import com.zhangshuo.magicnotice.util.ConfigInit;
import com.zhangshuo.magicnotice.util.TimeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Route(path = RoutePaths.APP_ACTIVITY_MAKE_SCHEDULER)
public class MakeSchedulerActivity extends BaseActivity<ActivityMakeSchedulerBinding> {

    @Autowired
    int remindId;
    @Autowired
    String title;
    @Autowired
    String content;

    private List<String> tabTxtList = Arrays.asList(StringUtil.getString(R.string.make_scheduler_custom),
            StringUtil.getString(R.string.make_scheduler_random));
    private int selectTab = 0;
    private CustomTxtView customTxtView;
    private RandomTxtView randomTxtView;

    private RemindInfo remindInfo;

    private List<String> repeatTypeNameList = Arrays.asList(StringUtil.getString(R.string.make_shceduler_week),
            StringUtil.getString(R.string.make_shceduler_date), StringUtil.getString(R.string.make_shceduler_day),
            StringUtil.getString(R.string.make_shceduler_month), StringUtil.getString(R.string.make_shceduler_year));
    private List<Integer> repeatTypeValueList = Arrays.asList(RemindTimeInfo.TYPE_REPEAT_WEEK, RemindTimeInfo.TYPE_REPEAT_NONE,
            RemindTimeInfo.TYPE_REPEAT_DAY, RemindTimeInfo.TYPE_REPEAT_MONTH, RemindTimeInfo.TYPE_REPEAT_YEAR);
    private int selectRepeatPosition = 0;

    private int handlerCount = 1;
    private String mockPackage = "";

    @Override
    protected void initView() {
        setTitleBar(getString(R.string.title_make_scheduler));
        binding.tabTxt.openLeftLineTip(true);
        binding.tabTxt.bindTabList(tabTxtList);
        binding.tabTxt.setIndicator(R.drawable.tab_indicator, Color.TRANSPARENT);
        binding.tabTxt.setOnCallback(new MTabLayoutView.OnCallback() {
            @Override
            public void select(int position) {
                selectTab = position;
                checkShowTxtView();
            }
        });
        checkShowTxtView();
        binding.crStartTime.setValueTip(getString(R.string.make_scheduler_set));
        binding.crEndTime.setValueTip(getString(R.string.make_scheduler_set_select));
        initRepeatList();
    }

    @Override
    protected void initData() {
        if (customTxtView != null) {
            customTxtView.setDefault(title, content);
        }
        if (remindId > 0) {
            binding.btnDelete.setVisibility(View.VISIBLE);
            getSchedulerData();
        } else {
            binding.btnDelete.setVisibility(View.GONE);
        }
        binding.crStartTime.setValue(TimeUtil.formatHHMMTime(TimeUtil.getCurrentYMDHM()));
        binding.crStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeSelectDialog(binding.crStartTime.getValue(), getString(R.string.make_scheduler_cancel), new SelectTimeDialog.OnCallback() {
                    @Override
                    public void onResult(long selectTime) {
                        if (selectTime > 0) {
                            binding.crStartTime.setValue(TimeUtil.formatHHMMTime(selectTime));
                        }
                        //变更重复数据
                        binding.rrvRepeat.setSelectTime(binding.crStartTime.getValue(),
                                binding.crEndTime.getValue());
                    }
                });
            }
        });
        binding.crEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeSelectDialog(binding.crEndTime.getValue(), getString(R.string.make_scheduler_reset), new SelectTimeDialog.OnCallback() {
                    @Override
                    public void onResult(long selectTime) {
                        if (selectTime == 0) {
                            binding.crEndTime.setValue("");
                            binding.clCount.setVisibility(View.GONE);
                        } else {
                            binding.crEndTime.setValue(TimeUtil.formatHHMMTime(selectTime));
                            binding.clCount.setVisibility(View.VISIBLE);
                        }
                        //变更重复数据
                        binding.rrvRepeat.setSelectTime(binding.crStartTime.getValue(),
                                binding.crEndTime.getValue());
                    }
                });
            }
        });
        binding.ivCountAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerCount++;
                handlerCount = Math.min(handlerCount, 1000);
                String handlerInfo = String.valueOf(handlerCount);
                binding.edtCount.setText(handlerInfo);
                binding.edtCount.setSelection(handlerInfo.length());
            }
        });
        binding.ivCountReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerCount--;
                handlerCount = Math.max(1, handlerCount);
                String handlerInfo = String.valueOf(handlerCount);
                binding.edtCount.setText(String.valueOf(handlerCount));
                binding.edtCount.setSelection(handlerInfo.length());
            }
        });
        binding.edtCount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Editable text = binding.edtCount.getText();
                    if (text == null || TextUtils.isEmpty(text.toString())) {
                        handlerCount = 1;
                        String handlerInfo = String.valueOf(handlerCount);
                        binding.edtCount.setText(handlerInfo);
                        binding.edtCount.setSelection(handlerInfo.length());
                    }
                }
            }
        });
        binding.edtCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String data = s.toString();
                if (TextUtils.isEmpty(data) || "0".equalsIgnoreCase(data)) {
                    handlerCount = 1;
                } else {
                    try {
                        int value = Math.max(1, Integer.parseInt(data));
                        if (value > 1000) {
                            handlerCount = 1000;
                            String handlerInfo = String.valueOf(handlerCount);
                            binding.edtCount.setText(handlerInfo);
                            binding.edtCount.setSelection(handlerInfo.length());
                        } else {
                            handlerCount = value;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        //删除
        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemindDataService.delete(remindId, new AsyncCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        if (remindInfo != null) {
                            SchedulerUtil.get().deleteAlarm(remindInfo);
                        }
                        ToastManager.get().showShortToast(getString(R.string.toast_del_suc));
                        EventBusUtil.sendEvent(new EventMessage<>(EventCodes.REFRESH_SCHEDULER));
                        finishAct();
                    }
                });
            }
        });
        binding.rrvRepeat.setDataRemindCallback(new IDataRemindCallback() {
            @Override
            public void onViewChanged(Object result) {
                CharSequence repeatTip = binding.rrvRepeat.getRepeatTip();
                if (TextUtils.isEmpty(repeatTip)) {
                    binding.tvTimeTip.setText(R.string.make_scheduler_none);
                } else {
                    binding.tvTimeTip.setText(repeatTip);
                }
                binding.nestedScrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.nestedScrollView.fullScroll(NestedScrollView.FOCUS_DOWN);
                    }
                }, 350);
            }
        });
        //保存或者删除
        binding.mbNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        //模拟应用通知
        binding.tvMockApp.setOnClickListener(new OnClickCallback() {
            @Override
            public void click(View v) {
                List<String> hasMockedList = new ArrayList<>();
                if (!TextUtils.isEmpty(mockPackage)) {
                    hasMockedList.add(mockPackage);
                }
                SelectAppDialog selectAppDialog = new SelectAppDialog(true);
                selectAppDialog.setSelectedPackageNameList(hasMockedList);
                selectAppDialog.setOnCallback(new SelectAppDialog.OnCallback() {
                    @Override
                    public void onResult(List<String> packageNameList, List<String> appNameList) {
                        if (packageNameList.size() > 0) {
                            mockPackage = packageNameList.get(0);
                        } else {
                            mockPackage = "";
                        }
                        checkShowMockAppInfo();
                    }
                });
                selectAppDialog.show(getCtx());
            }
        });

    }

    @Override
    public void onReceiveEvent(EventMessage eventMessage) {
        super.onReceiveEvent(eventMessage);
        if (eventMessage.getCode() == EventCodes.REFRESH_SCHEDULER_RANDOM_TXT) {
            TxtClassifyInfo txtClassifyInfo = (TxtClassifyInfo) eventMessage.getData();
            if (randomTxtView != null) {
                randomTxtView.setClassifyData(txtClassifyInfo);
            }
        }
    }


    private void initRepeatList() {
        binding.rvRepeat.setLayoutManager(new LinearLayoutManager(getCtx(), RecyclerView.HORIZONTAL, false));
        BaseRAdapter baseRAdapter = new BaseRAdapter<String>(getCtx()) {
            @Override
            protected void onConvertView(ViewHolder viewHolder, String s, int position) {
                TextView tvContent = (TextView) viewHolder.$(R.id.item_tv_content);
                tvContent.setText(s);
                if (position == selectRepeatPosition) {
                    tvContent.setTextSize(14);
                    tvContent.getPaint().setFakeBoldText(true);
                    tvContent.setTextColor(getColor(R.color.base_font_blue));
                } else {
                    tvContent.setTextSize(12);
                    tvContent.getPaint().setFakeBoldText(false);
                    tvContent.setTextColor(getColor(R.color.base_font_txt_sub));
                }
            }

            @Override
            protected int onSetItemViewType(String s, int position) {
                return 0;
            }

            @Override
            protected int onBindView(int viewType) {
                return R.layout.item_remind_repeat;
            }
        };
        binding.rvRepeat.setAdapter(baseRAdapter);
        baseRAdapter.addData(repeatTypeNameList);
        baseRAdapter.setOnItemClickListener(new BaseRAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                if (selectRepeatPosition != position) {
                    baseRAdapter.notifyItemChanged(position);
                    baseRAdapter.notifyItemChanged(selectRepeatPosition);
                    selectRepeatPosition = position;
                    binding.rrvRepeat.setRepeatType(repeatTypeValueList.get(selectRepeatPosition));
                    binding.nestedScrollView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.nestedScrollView.fullScroll(NestedScrollView.FOCUS_DOWN);
                            if (position < 3) {
                                binding.rvRepeat.scrollToPosition(0);
                            } else {
                                binding.rvRepeat.scrollToPosition(repeatTypeNameList.size() - 1);
                            }
                        }
                    }, 200);
                }
            }
        });
        binding.rrvRepeat.setRepeatType(repeatTypeValueList.get(selectRepeatPosition));
    }

    private void checkShowMockAppInfo() {
        Drawable drawable = null;
        if (!TextUtils.isEmpty(mockPackage)) {
            drawable = AppUtils.getAppIcon(mockPackage);
        }
        if (customTxtView != null) {
            customTxtView.setLogo(drawable);
        } else if (randomTxtView != null) {
            randomTxtView.setLogo(drawable);
        }
    }

    private void checkShowTxtView() {
        if (selectTab == 0) {
            if (customTxtView == null) {
                customTxtView = new CustomTxtView(getCtx());
            }
            binding.flTxt.removeAllViews();
            binding.flTxt.addView(customTxtView, new FrameLayout.LayoutParams(-1, -2));
        } else if (selectTab == 1) {
            if (randomTxtView == null) {
                randomTxtView = new RandomTxtView(getCtx());
            }
            binding.flTxt.removeAllViews();
            binding.flTxt.addView(randomTxtView, new FrameLayout.LayoutParams(-1, -2));
        }
    }


    private void showTimeSelectDialog(String selectDate, String cancelTip, SelectTimeDialog.OnCallback onCallback) {
        Long selectTime = TimeUtil.parseHMTime(selectDate);
        new SelectTimeDialog()
                .setCancelTip(cancelTip)
                .setCancelCallback(true).setSelectTime(selectTime).setOnCallback(onCallback).show(getCtx());
    }


    private void getSchedulerData() {
        RemindDataService.findRemindById(remindId, new AsyncCallback<RemindInfo>() {
            @Override
            public void onSuccess(RemindInfo result) {
                remindInfo = result;
                //设置文案
                TxtClassifyInfo txtClassifyInfo = result.getTxtClassifyInfo();
                if (txtClassifyInfo == null) {
                    binding.tabTxt.select(0);
                    if (customTxtView != null) {
                        customTxtView.setDefault(result.getTitle(), result.getContent());
                    }
                } else {
                    binding.tabTxt.select(1);
                    if (randomTxtView != null) {
                        randomTxtView.setClassifyData(txtClassifyInfo);
                    }
                }
                mockPackage = result.getMockPkg();
                checkShowMockAppInfo();
                //设置时间
                RemindTimeInfo remindTimeInfo = remindInfo.getRemindTimeInfo();
                if (remindTimeInfo != null) {
                    //基础日期
                    boolean haveEndDateTime = !TextUtils.isEmpty(remindTimeInfo.getEndTime());
                    binding.crStartTime.setValue(remindTimeInfo.getStartTime());
                    if (haveEndDateTime) {
                        binding.crEndTime.setValue(remindTimeInfo.getEndTime());
                        binding.clCount.setVisibility(View.VISIBLE);
                    }
                    //执行次数
                    handlerCount = remindTimeInfo.getHandlerCount();
                    String handlerInfo = String.valueOf(handlerCount);
                    binding.edtCount.setText(handlerInfo);
                    binding.edtCount.setSelection(handlerInfo.length());
                    //重复方式
                    selectRepeatPosition = repeatTypeValueList.indexOf(remindTimeInfo.getRepeatType());
                    binding.rvRepeat.getAdapter().notifyDataSetChanged();
                    if (selectRepeatPosition > 2) {
                        binding.rvRepeat.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                binding.rvRepeat.smoothScrollToPosition(selectRepeatPosition);
                            }
                        }, 300);
                    }
                    binding.rrvRepeat.setRepeatType(remindTimeInfo.getRepeatType());
                    binding.rrvRepeat.setRepeatData(remindTimeInfo.getRepeatData());
                }
            }
        });
    }

    private RemindTimeInfo checkRemindDateTip() {
        //设置重复方式
        RemindTimeInfo remindTimeInfo = new RemindTimeInfo();
        String startTime = binding.crStartTime.getValue();
        String endTime = binding.crEndTime.getValue();
        if (TextUtils.isEmpty(startTime)) {
            ToastManager.get().showShortToast(R.string.make_scheduler_no_time_tip);
            return null;
        }
        remindTimeInfo.setRepeatType(repeatTypeValueList.get(selectRepeatPosition));
        remindTimeInfo.setStartTime(startTime);
        remindTimeInfo.setEndTime(endTime);
        //设置提醒次数
        handlerCount = Math.max(1, handlerCount);
        remindTimeInfo.setHandlerCount(handlerCount);
        //提醒数据
        String repeatData = binding.rrvRepeat.getRepeatData();
        if (TextUtils.isEmpty(repeatData)) {
            ToastManager.get().showShortToast(R.string.make_scheduler_repeat_none);
            return null;
        }
        remindTimeInfo.setRepeatData(repeatData);
        return remindTimeInfo;
    }

    private void save() {
        if (remindInfo == null) {
            remindInfo = new RemindInfo();
        }
        remindInfo.setMockPkg(mockPackage);
        //设置提醒文案
        if (selectTab == 0 && customTxtView != null) {
            String title = customTxtView.getInputTitle();
            String content = customTxtView.getInputContent();
            if (TextUtils.isEmpty(title)) {
                ToastManager.get().showShortToast(R.string.make_scheduler_input_title);
                return;
            }
            if (TextUtils.isEmpty(content)) {
                ToastManager.get().showShortToast(R.string.make_scheduler_input_txt);
                return;
            }
            remindInfo.setTitle(title);
            remindInfo.setContent(content);
            remindInfo.setRemindType(RemindInfo.TYPE_EXACT);
            remindInfo.setClassifyId(0);
        } else if (selectTab == 1 && randomTxtView != null) {
            int classifyId = randomTxtView.getClassifyId();
            if (classifyId <= 0) {
                ToastManager.get().showShortToast(R.string.make_scheduler_input_select);
                return;
            }
            remindInfo.setTitle("");
            remindInfo.setContent("");
            remindInfo.setRemindType(RemindInfo.TYPE_REMIND);
            remindInfo.setClassifyId(classifyId);
        }
        // 设置时间
        RemindTimeInfo remindTimeInfo = checkRemindDateTip();
        if (remindTimeInfo == null) {
            return;
        }
        remindInfo.setRemindTimeInfo(remindTimeInfo);
        remindInfo.setTimeJson(GsonUtils.toString(remindTimeInfo));
        remindInfo.setNotifyNumber(0);
        RemindDataService.insertOrUpdate(remindInfo, new AsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                ToastManager.get().showShortToast(remindId > 0 ? getString(R.string.make_scheduler_update_suc) : getString(R.string.make_scheduler_save_suc));
                SchedulerUtil.get().configAlarm(remindInfo);
                EventBusUtil.sendEvent(new EventMessage<>(EventCodes.REFRESH_SCHEDULER));
                ConfigInit.checkAndCreateSchedulerConfig(null);
                finishAct();
            }
        });
    }


}
