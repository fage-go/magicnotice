package com.zhangshuo.magicnotice.ui.view.configrule.conditions;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.Utils;
import com.zhangshuo.common.base.BaseFragment;
import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.common.util.AsyncCallUtils;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.CacheMemory;
import com.zhangshuo.magicnotice.databinding.FragmentCrAppBinding;
import com.zhangshuo.magicnotice.ui.adapter.AppSelectedAdapter;
import com.zhangshuo.magicnotice.ui.model.AppSelectedInfo;
import com.zhangshuo.magicnotice.ui.model.conditions.AppConditionsModel;
import com.zhangshuo.magicnotice.ui.view.configrule.IConfigRule;
import com.zhangshuo.magicnotice.util.SelectedListHelper;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

public class AppCRFragment extends BaseFragment<FragmentCrAppBinding>
        implements IConfigRule {

    private AppSelectedAdapter adapter;
    private AppConditionsModel appConditionsModel;

    private SelectedListHelper<AppSelectedInfo> selectedListHelper;

    @Override
    protected void initView() {
        adapter = new AppSelectedAdapter(getContext());
        binding.rvList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), RecyclerView.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.item_line_drawable));
        binding.rvList.addItemDecoration(dividerItemDecoration);
        binding.rvList.setAdapter(adapter);
        selectedListHelper = new SelectedListHelper<AppSelectedInfo>(false, new SelectedListHelper.OnJudgeCallback<AppSelectedInfo>() {
            @Override
            public boolean isSame(AppSelectedInfo data, AppSelectedInfo judgeData) {
                return data.getPackageName().equals(judgeData.getPackageName());
            }
        });
        adapter.setOnItemClickListener(new BaseRAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                adapter.getDataList().get(position).setSelected(!adapter.getItem(position).isSelected());
                adapter.notifyItemChanged(position);
                selectedListHelper.onSelected(adapter.getDataList().get(position),
                        adapter.getItem(position).isSelected());

            }
        });
        binding.etAppSearch.addTextChangedListener(new TextWatcher() {
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
    }

    @Override
    protected void initData() {
        binding.filterConfig.setConfig(appConditionsModel.isFilterType(), R.string.c_filter_tip_app_y, R.string.c_filter_tip_app_n);
        getData(null);
    }

    @Override
    public BaseFragment getFragment() {
        return this;
    }

    @Override
    public void onInitData(Object object) {
        if (object instanceof AppConditionsModel) {
            appConditionsModel = ((AppConditionsModel) object);
        }
        if (appConditionsModel == null) {
            appConditionsModel = new AppConditionsModel();
        }
    }

    @Override
    public Object onResultData() {
        appConditionsModel.setFilterType(binding.filterConfig.isChecked());
        appConditionsModel.getPackageNameList().clear();
        List<AppSelectedInfo> dataList = selectedListHelper.getDataList();
        for (int i = 0; i < dataList.size(); i++) {
            appConditionsModel.getPackageNameList().add(dataList.get(i).getPackageName());
        }
        return appConditionsModel;
    }


    private void getData(String keyword) {
        List<AppSelectedInfo> appSelectedInfoList = (List<AppSelectedInfo>) CacheMemory.cacheData.get("App_Cache");
        if (appSelectedInfoList == null || appSelectedInfoList.size() == 0) {
            showLoading(false);
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
                        appSelectedInfo.setSelected(appConditionsModel.getPackageNameList().contains(packageName));
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
                    hideLoading();
                    showDataList(result, keyword);
                }
            });
        } else {
            showDataList(appSelectedInfoList, keyword);
        }
    }

    private void showDataList(List<AppSelectedInfo> appSelectedInfoList, String keyword) {
        for (int i = 0; i < appSelectedInfoList.size(); i++) {
            AppSelectedInfo appSelectedInfo = appSelectedInfoList.get(i);
            appSelectedInfo.setSelected(appConditionsModel.getPackageNameList().contains(appSelectedInfo.getPackageName()));
        }
        List<AppSelectedInfo> initSelectedList = new ArrayList<>();
        List<AppSelectedInfo> dataList = new ArrayList<>();
        if (appSelectedInfoList.size() > 0)
            for (AppSelectedInfo selectedInfo : appSelectedInfoList) {
                if (TextUtils.isEmpty(keyword) || selectedInfo.getName().toLowerCase().contains(keyword.toLowerCase())) {
                    dataList.add(selectedInfo);
                }
                if (selectedInfo.isSelected()) {
                    initSelectedList.add(selectedInfo);
                }
            }
        adapter.addData(dataList, true);
        selectedListHelper.addSelectedList(initSelectedList);
    }
}
