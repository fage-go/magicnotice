package com.zhangshuo.magicnotice.ui.activity;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zhangshuo.common.base.BaseActivity;
import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.LocalDataUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.LocalDataKeys;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.databinding.ActivityTabConfigBinding;
import com.zhangshuo.magicnotice.db.service.TabDataService;
import com.zhangshuo.magicnotice.notice.factory.ProcessFactory;
import com.zhangshuo.magicnotice.ui.adapter.TabConfigAdapter;
import com.zhangshuo.magicnotice.ui.dialog.TabConfigDialog;
import com.zhangshuo.magicnotice.ui.model.TabInfo;
import com.zhangshuo.magicnotice.ui.view.DefaultItemTouchHelperCallback;

import java.util.Collections;
import java.util.List;

@Route(path = RoutePaths.APP_ACTIVITY_TAB_CONFIG)
public class TabConfigActivity extends BaseActivity<ActivityTabConfigBinding>
        implements TabConfigDialog.OnCallback {

    private TabConfigAdapter adapter;

    @Override
    protected void initView() {
        setTitleBar(getString(R.string.title_tab_config));
        adapter = new TabConfigAdapter(getCtx());
        binding.rvList.setLayoutManager(new LinearLayoutManager(getCtx()));
        binding.rvList.setAdapter(adapter);
        binding.fabNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TabConfigDialog.create(getCtx(), null, TabConfigActivity.this);
            }
        });
        adapter.setOnItemClickListener(new BaseRAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                TabConfigDialog.create(getCtx(), adapter.getItem(position), TabConfigActivity.this);
            }
        });


        new ItemTouchHelper(new DefaultItemTouchHelperCallback(new DefaultItemTouchHelperCallback.OnItemTouchCallbackListener() {
            @Override
            public boolean currentPositionLongPressEnabled(int position) {
                return adapter.getItemViewType(position) != BaseRAdapter.TYPE_HEADER_VIEW &&
                        adapter.getItemViewType(position) != BaseRAdapter.TYPE_FOOTER_VIEW;
            }

            @Override
            public boolean onMove(int fromPosition, int targetPosition) {
                if (fromPosition < targetPosition) {
                    for (int i = fromPosition; i < targetPosition; i++) {
                        Collections.swap(adapter.getDataList(), i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > targetPosition; i--) {
                        Collections.swap(adapter.getDataList(), i, i - 1);
                    }
                }
                adapter.notifyItemMoved(fromPosition, targetPosition);
                mainHandler.removeMessages(1000);
                mainHandler.sendEmptyMessageDelayed(1000, 500);
                return true;
            }

        })).attachToRecyclerView(binding.rvList);
    }

    private Handler mainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1000) {
                refreshConfigTab();
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void initData() {
        getAllTabList();
    }

    private void getAllTabList() {
        TabDataService.findAllTab(new AsyncCallback<List<TabInfo>>() {
            @Override
            public void onSuccess(List<TabInfo> result) {
                adapter.addData(result, true);
            }
        });
    }

    @Override
    public void onConfirm(TabInfo tabInfo) {
        getAllTabList();
        //新增的时候刷新
        ProcessFactory.get().getMagicProcess().refreshTabData();
    }

    @Override
    public void onDelete(TabInfo tabInfo) {
        getAllTabList();
        //被删除掉的时候刷新
        ProcessFactory.get().getMagicProcess().refreshTabData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainHandler = null;
    }

    private void refreshConfigTab() {
        StringBuilder configIdBuilder = new StringBuilder();
        for (int i = 0; i < adapter.getDataList().size(); i++) {
            TabInfo tabInfo = adapter.getDataList().get(i);
            configIdBuilder.append(tabInfo.getId())
                    .append(";");
        }
        LocalDataUtil.get().saveData(LocalDataKeys.SKEY_SORT_TAB_IDS, configIdBuilder.toString());
    }
}
