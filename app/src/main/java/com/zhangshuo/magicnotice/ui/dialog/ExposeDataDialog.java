package com.zhangshuo.magicnotice.ui.dialog;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.UriUtils;
import com.zhangshuo.common.dialog.BaseBottomDialog;
import com.zhangshuo.common.listener.OnClickCallback;
import com.zhangshuo.common.model.EventMessage;
import com.zhangshuo.common.toast.ToastManager;
import com.zhangshuo.common.util.AsyncCallUtils;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.EventBusUtil;
import com.zhangshuo.common.util.FileUtil;
import com.zhangshuo.common.util.GsonUtils;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.EventCodes;
import com.zhangshuo.magicnotice.databinding.DialogExposeDataBinding;
import com.zhangshuo.magicnotice.db.service.ConfigRuleDataService;
import com.zhangshuo.magicnotice.ui.model.ConfigRuleModel;
import com.zhangshuo.magicnotice.util.DESUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ExposeDataDialog extends BaseBottomDialog<DialogExposeDataBinding> {

    private DESUtil desUtil;

    @Override
    public void initView() {
        desUtil = new DESUtil();
        binding.mbExposeRule.setOnClickListener(new OnClickCallback() {
            @Override
            public void click(View v) {
                //导出规则
                if (checkLegacyRuleExchange()) {
                    showExposeRuleDialog();
                }
            }
        });
        binding.mbExposeAll.setOnClickListener(new OnClickCallback() {
            @Override
            public void click(View v) {

            }
        });
        binding.mbExposeNotify.setOnClickListener(new OnClickCallback() {
            @Override
            public void click(View v) {

            }
        });
        binding.mbInputData.setOnClickListener(new OnClickCallback() {
            @Override
            public void click(View v) {
                //导入规则
                if (checkLegacyRuleExchange()) {
                    toSelectFile();
                }
            }
        });
    }


    /**
     * 导出规则数据弹窗
     */
    private void showExposeRuleDialog() {
        dismiss();
        SelectRuleDialog selectRuleDialog = new SelectRuleDialog(false);
        selectRuleDialog.setOnCallback(new SelectRuleDialog.OnCallback() {
            @Override
            public void onResult(List<Integer> ruleIdList, List<String> ruleNameList) {
                showLoading();
                exposeRule(ruleIdList);
            }
        });
        selectRuleDialog.show(getContext());
    }

    private boolean checkLegacyRuleExchange() {
        if (DESUtil.isLegacyExportConfigured()) {
            return true;
        }
        Toast.makeText(getContext(), R.string.legacy_rule_exchange_unavailable, Toast.LENGTH_LONG).show();
        return false;
    }

    private void toSelectFile() {
        Toast.makeText(getContext(), R.string.expose_data_suffix_name, Toast.LENGTH_SHORT).show();
        EventBusUtil.register(this);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, 123);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(EventMessage eventMessage) {
        if (eventMessage.getCode() == EventCodes.FILE_SELECT) {
            EventBusUtil.unregister(this);
            Object data = eventMessage.getData();
            if (data != null) {
                Uri uri = (Uri) data;
                byte[] bytes = UriUtils.uri2Bytes(uri);
                String jsonData = new String(bytes);
                if (jsonData.startsWith("Rule")) {
                    inputNotify(jsonData.substring(4));
                }
            }
            dismiss();
        }
    }


    /**
     * 导出规则数据
     *
     * @param ruleIdList
     */
    private void exposeRule(List<Integer> ruleIdList) {
        ConfigRuleDataService.findConfigRuleInfoList(new AsyncCallback<List<ConfigRuleModel>>() {
            @Override
            public void onSuccess(List<ConfigRuleModel> result) {
                List<ConfigRuleModel> modelList = new ArrayList<>();
                if (result != null) {
                    for (ConfigRuleModel model : result) {
                        for (Integer ruleId : ruleIdList) {
                            if (model.getId() == ruleId) {
                                model.setId(0);
                                modelList.add(model);
                            }
                        }
                    }
                }
                AsyncCallUtils.asyncCall(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        String jsonData = GsonUtils.toString(modelList);
                        String desc = desUtil.encrypt(DESUtil.requireLegacyKey(), jsonData);
                        return "Rule" + desc;
                    }
                }, new AsyncCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        checkPermissionAndWrite(result);
                    }
                });
            }
        });
    }

    private void inputNotify(String data) {
        AsyncCallUtils.asyncCall(new Callable<ConfigRuleModel[]>() {
            @Override
            public ConfigRuleModel[] call() throws Exception {
                String decrypt = desUtil.decrypt(DESUtil.requireLegacyKey(), data);
                List<ConfigRuleModel> dataList = null;
                try {
                    dataList = GsonUtils.toBeanArray(decrypt, ConfigRuleModel.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (dataList == null) return null;
                for (ConfigRuleModel configRuleModel : dataList) {
                    String ruleName = configRuleModel.getRuleName();
                    configRuleModel.setRuleName(ruleName + "_" + System.currentTimeMillis());
                }
                return dataList.toArray(new ConfigRuleModel[0]);
            }
        }, new AsyncCallback<ConfigRuleModel[]>() {
            @Override
            public void onSuccess(ConfigRuleModel[] result) {
                if (result == null) {
                    ToastManager.get().showShortToast(R.string.expose_data_import_failed);
                    return;
                }
                ConfigRuleDataService.insertOrUpdate(result, new AsyncCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        ToastManager.get().showShortToast(R.string.expose_data_import_suc);
                        EventBusUtil.sendEvent(new EventMessage<>(EventCodes.UPDATE_RULE));
                    }
                });
            }
        });
    }


    private void checkPermissionAndWrite(String data) {
        hideLoading();
        if (PermissionUtils.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            writeFileData(data);
        } else {
            PermissionUtils.permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .callback(new PermissionUtils.SingleCallback() {
                        @Override
                        public void callback(boolean isAllGranted, @NonNull List<String> granted, @NonNull List<String> deniedForever, @NonNull List<String> denied) {
                            if (isAllGranted) {
                                writeFileData(data);
                            } else {
                                ToastManager.get().showShortToast(R.string.expose_data_permission_tip);
                            }
                        }
                    })
                    .request();
        }
    }

    private void writeFileData(String data) {
        String fileName = System.currentTimeMillis() + ".cicada";
        File file = new File(FileUtil.getDownloadFile(), fileName);
        FileIOUtils.writeFileFromString(file, data);
        ToastManager.get().showShortToast(String.format(StringUtil.getString(R.string.expose_data_save_tip), file.getAbsolutePath()));
    }

}
