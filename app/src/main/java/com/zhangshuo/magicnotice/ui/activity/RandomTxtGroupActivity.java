package com.zhangshuo.magicnotice.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zhangshuo.common.base.BaseActivity;
import com.zhangshuo.common.model.EventMessage;
import com.zhangshuo.common.router.RouterUtil;
import com.zhangshuo.common.toast.ToastManager;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.EventBusUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.EventCodes;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.databinding.ActivityRandomTxtGroupBinding;
import com.zhangshuo.magicnotice.db.service.TxtDataService;
import com.zhangshuo.magicnotice.ui.adapter.RandomTxtGroupAdapter;
import com.zhangshuo.magicnotice.ui.dialog.RandomGroupTxtDialog;
import com.zhangshuo.magicnotice.ui.model.TxtClassifyInfo;

import java.util.List;

@Route(path = RoutePaths.APP_ACTIVITY_RANDOM_TXT_GROUP)
public class RandomTxtGroupActivity extends BaseActivity<ActivityRandomTxtGroupBinding> {
    private RandomTxtGroupAdapter adapter;
    private int randomTxtId = 0;

    @Override
    protected void initView() {
        setTitleBar(getString(R.string.title_random_txt_group));
        adapter = new RandomTxtGroupAdapter(getCtx());
        adapter.setOnCallback(new RandomTxtGroupAdapter.OnCallback() {
            @Override
            public void onSelect(int position) {
                TxtClassifyInfo item = adapter.getItem(position);
                if (item.getCount() > 0) {
                    EventBusUtil.sendEvent(new EventMessage<>(EventCodes.REFRESH_SCHEDULER_RANDOM_TXT, item));
                    finishAct();
                } else {
                    ToastManager.get().showShortToast(R.string.random_txt_group_no_tip);
                }
            }

            @Override
            public void onGoEdit(int position) {
                TxtClassifyInfo item = adapter.getItem(position);
                showClassifyDialog(item);
            }

            @Override
            public void onOptTxt(int position) {
                TxtClassifyInfo item = adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString("classifyName", item.getTitle());
                bundle.putInt("classifyId", item.getId());
                RouterUtil.get().navigation(RoutePaths.APP_ACTIVITY_RANDOM_TXT, bundle);
                randomTxtId = item.getId();
            }
        });
        binding.rvList.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvList.setAdapter(adapter);
        binding.mbNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClassifyDialog(null);
            }
        });
    }

    @Override
    public void onReceiveEvent(EventMessage eventMessage) {
        super.onReceiveEvent(eventMessage);
        if (eventMessage.getCode() == EventCodes.REFRESH_SCHEDULER_RANDOM_GROUP) {
            getData();
        }
    }

    @Override
    protected void initData() {
        getData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (randomTxtId > 0) {
            getData();
            randomTxtId = 0;
        }
    }

    private void showClassifyDialog(TxtClassifyInfo txtClassifyInfo) {
        new RandomGroupTxtDialog()
                .setTxtClassifyInfo(txtClassifyInfo)
                .setOnCallback(new RandomGroupTxtDialog.OnCallback() {
                    @Override
                    public void onHaveAdd() {
                        getData();
                    }

                    @Override
                    public void onHaveDelete() {
                        getData();
                    }
                }).show(getCtx());
    }

    private void getData() {
        TxtDataService.findClassify(new AsyncCallback<List<TxtClassifyInfo>>() {
            @Override
            public void onSuccess(List<TxtClassifyInfo> result) {
                adapter.addData(result, true);
            }
        });
    }

}
