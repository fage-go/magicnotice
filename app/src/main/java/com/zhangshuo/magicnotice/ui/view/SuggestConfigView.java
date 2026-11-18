package com.zhangshuo.magicnotice.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayoutManager;
import com.zhangshuo.common.util.UIUtil;
import com.zhangshuo.magicnotice.db.model.ConfigRuleData;
import com.zhangshuo.magicnotice.ui.adapter.ConfigSuggestAdapter;
import com.zhangshuo.magicnotice.ui.model.ConfigRuleModel;

import java.util.ArrayList;
import java.util.List;

public class SuggestConfigView extends FrameLayout {
    private RecyclerView recyclerView;
    private ConfigSuggestAdapter configSuggestAdapter;
    public static List<ConfigRuleData> suggestDataList = new ArrayList<>();

    public static ConfigRuleModel makeSuggestConfig(String name) {
        for (int i = 0; i < suggestDataList.size(); i++) {
            ConfigRuleData configRuleData = suggestDataList.get(i);
            if (configRuleData.getName().equals(name)) {
                return new ConfigRuleModel(configRuleData);
            }
        }
        return null;
    }

    public void getData() {
//        RetrofitUtil.get()
//                .create(IConfigService.class)
//                .getSuggestConfig()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(new MObserver<String>() {
//                    @Override
//                    public void onSuccess(String response) {
//                        setVisibility(VISIBLE);
//                        List<ConfigRuleData> configRuleDataList = GsonUtils.toBeanArray(
//                                response, ConfigRuleData.class
//                        );
//                        suggestDataList.clear();
//                        suggestDataList.addAll(configRuleDataList);
//                        configSuggestAdapter.getDataList().clear();
//                        configSuggestAdapter.getDataList().addAll(configRuleDataList);
//                        configSuggestAdapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onFailure(Throwable e, String errorMsg) {
//                        setVisibility(GONE);
//                    }
//                });

    }

    private void init() {
        recyclerView = new RecyclerView(getContext());
        LayoutParams layoutParams = new LayoutParams(-1, -2);
        layoutParams.topMargin = UIUtil.dp2Px(20);
        addView(recyclerView, layoutParams);
        FlexboxLayoutManager flexboxLayoutManager=new FlexboxLayoutManager(getContext());
        recyclerView.setLayoutManager(flexboxLayoutManager);
        recyclerView.setNestedScrollingEnabled(true);
        configSuggestAdapter = new ConfigSuggestAdapter(getContext());
        recyclerView.setAdapter(configSuggestAdapter);
        getData();
    }

    public SuggestConfigView(@NonNull Context context) {
        super(context);
        init();
    }

    public SuggestConfigView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SuggestConfigView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SuggestConfigView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
}
