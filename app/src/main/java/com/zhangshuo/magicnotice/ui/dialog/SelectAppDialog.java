package com.zhangshuo.magicnotice.ui.dialog;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.Utils;
import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.common.dialog.BaseBottomDialog;
import com.zhangshuo.common.util.AsyncCallUtils;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.CacheMemory;
import com.zhangshuo.magicnotice.databinding.DialogSelectAppBinding;
import com.zhangshuo.magicnotice.ui.adapter.AppSelectedAdapter;
import com.zhangshuo.magicnotice.ui.model.AppSelectedInfo;
import com.zhangshuo.magicnotice.util.SelectedListHelper;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

public class SelectAppDialog extends BaseBottomDialog<DialogSelectAppBinding> {

    private AppSelectedAdapter adapter;
    private OnCallback onCallback;

    private List<String> selectedPackageNameList = new ArrayList<>();
    private boolean isSingle = true;
    private SelectedListHelper<AppSelectedInfo> selectedListHelper;

    public SelectAppDialog() {
        this.isSingle = true;
    }

    public SelectAppDialog(boolean isSingle) {
        this.isSingle = isSingle;
    }

    public void setSelectedPackageNameList(List<String> pckList) {
        this.selectedPackageNameList.clear();
        this.selectedPackageNameList.addAll(pckList);
    }

    public void setOnCallback(OnCallback onCallback) {
        this.onCallback = onCallback;
    }

    @Override
    public void initView() {
        adapter = new AppSelectedAdapter(getContext());
        binding.rvList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), RecyclerView.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.item_line_drawable));
        binding.rvList.addItemDecoration(dividerItemDecoration);
        binding.rvList.setAdapter(adapter);
        selectedListHelper = new SelectedListHelper<>(isSingle, new SelectedListHelper.OnJudgeCallback<AppSelectedInfo>() {
            @Override
            public boolean isSame(AppSelectedInfo data, AppSelectedInfo judgeData) {
                return data.getPackageName().equals(judgeData.getPackageName());
            }
        });
        adapter.setOnItemClickListener(new BaseRAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    AppSelectedInfo appSelectedInfo = adapter.getItem(i);
                    if (i == position) {
                        appSelectedInfo.setSelected(!appSelectedInfo.isSelected());
                        adapter.notifyItemChanged(i);
                        selectedListHelper.onSelected(appSelectedInfo, appSelectedInfo.isSelected());
                    } else if (isSingle) {
                        if (appSelectedInfo.isSelected()) {
                            appSelectedInfo.setSelected(false);
                            adapter.notifyItemChanged(i);
                        }
                    }
                }
                selectedPackageNameList.clear();
                for (int i = 0; i < selectedListHelper.getDataList().size(); i++) {
                    AppSelectedInfo appSelectedInfo = selectedListHelper.getDataList().get(i);
                    if (appSelectedInfo.isSelected()) {
                        selectedPackageNameList.add(appSelectedInfo.getPackageName());
                    }
                }
            }
        });
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString();
                getData(keyword);
            }
        });
        binding.tvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    AppSelectedInfo item = adapter.getItem(i);
                    if (item.isSelected()) {
                        item.setSelected(false);
                        adapter.notifyItemChanged(i);
                    }
                }
                close();
                if (onCallback != null) {
                    onCallback.onResult(new ArrayList<>(), new ArrayList<>());
                }
            }
        });
        binding.tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
                if (onCallback != null) {
                    List<String> pkgName = new ArrayList<>();
                    List<String> appName = new ArrayList<>();
                    getNameList(pkgName, appName);
                    onCallback.onResult(pkgName, appName);
                }
            }
        });
        getData(null);
    }

    private void getData(String keyword) {
        List<AppSelectedInfo> appSelectedInfoList = (List<AppSelectedInfo>) CacheMemory.cacheData.get("App_Cache");
        if (appSelectedInfoList == null || appSelectedInfoList.size() == 0) {
            binding.pbLoading.setVisibility(View.VISIBLE);
            AsyncCallUtils.asyncCall(new Callable<List<AppSelectedInfo>>() {
                @Override
                public List<AppSelectedInfo> call() throws Exception {
                    PackageManager pm = Utils.getApp().getPackageManager();
                    Intent filterIntent = new Intent(Intent.ACTION_MAIN, null);
                    filterIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    List<ResolveInfo> apps = pm.queryIntentActivities(filterIntent, 0);
                    List<AppSelectedInfo> infoList = new ArrayList<>();
                    for (int i = 0; i < apps.size(); i++) {
                        ResolveInfo resolveInfo = apps.get(i);
                        AppSelectedInfo appSelectedInfo = new AppSelectedInfo();
                        appSelectedInfo.setName(resolveInfo.loadLabel(pm).toString());
                        appSelectedInfo.setIcon(resolveInfo.loadIcon(pm));
                        String packageName = resolveInfo.activityInfo.applicationInfo.packageName;
                        appSelectedInfo.setPackageName(packageName);
                        infoList.add(appSelectedInfo);
                    }
                    Collections.sort(infoList, new Comparator<AppSelectedInfo>() {
                        @Override
                        public int compare(AppSelectedInfo o1, AppSelectedInfo o2) {
                            return Collator.getInstance(Locale.CANADA).compare(o1.getName(), o2.getName());
                        }
                    });
                    CacheMemory.cacheData.put("App_Cache", infoList);
                    return infoList;
                }
            }, new AsyncCallback<List<AppSelectedInfo>>() {
                @Override
                public void onSuccess(List<AppSelectedInfo> result) {
                    binding.pbLoading.setVisibility(View.GONE);
                    showDataList(result, keyword);
                }
            });
        } else {
            showDataList(appSelectedInfoList, keyword);
        }
    }

    private void showDataList(List<AppSelectedInfo> appSelectedInfoList, String keyword) {
        List<AppSelectedInfo> initSelectedList = new ArrayList<>();
        for (int i = 0; i < appSelectedInfoList.size(); i++) {
            AppSelectedInfo appSelectedInfo = appSelectedInfoList.get(i);
            if (selectedPackageNameList != null)
                appSelectedInfo.setSelected(selectedPackageNameList.contains(appSelectedInfo.getPackageName()));
            if (appSelectedInfo.isSelected()) {
                initSelectedList.add(appSelectedInfo);
            }
        }

        List<AppSelectedInfo> dataList = new ArrayList<>();
        if (appSelectedInfoList.size() > 0)
            for (AppSelectedInfo selectedInfo : appSelectedInfoList) {
                if (TextUtils.isEmpty(keyword) || selectedInfo.getName().toLowerCase().contains(keyword.toLowerCase())) {
                    dataList.add(selectedInfo);
                }
            }
        adapter.addData(dataList, true);
        selectedListHelper.addSelectedList(initSelectedList);
    }

    private void getNameList(List<String> pkgNameList, List<String> appNameList) {
        List<AppSelectedInfo> dataList = selectedListHelper.getDataList();
        for (int j = 0; j < dataList.size(); j++) {
            AppSelectedInfo item = dataList.get(j);
            if (item.isSelected()) {
                pkgNameList.add(item.getPackageName());
                appNameList.add(item.getName());
            }
        }
    }

    public interface OnCallback {
        void onResult(List<String> packageNameList, List<String> appNameList);
    }
}
