package com.zhangshuo.magicnotice.ui.activity;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.zhangshuo.common.base.BaseActivity;
import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.common.dialog.CommonDialog;
import com.zhangshuo.common.model.EventMessage;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.EventCodes;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.databinding.ActivityRandomTxtBinding;
import com.zhangshuo.magicnotice.db.service.TxtDataService;
import com.zhangshuo.magicnotice.ui.adapter.RandomTxtAdapter;
import com.zhangshuo.magicnotice.ui.dialog.RandomTxtDialog;
import com.zhangshuo.magicnotice.ui.model.RandomTxtInfo;

import java.util.List;

@Route(path = RoutePaths.APP_ACTIVITY_RANDOM_TXT)
public class RandomTxtActivity extends BaseActivity<ActivityRandomTxtBinding> {
    private RandomTxtAdapter adapter;
    private int pageNo = 1;
    private int pageSize = 20;

    @Autowired
    String classifyName = "";
    @Autowired
    int classifyId = 0;

    @Override
    protected void initView() {
        setTitleBar(String.format(getString(R.string.title_random_txt), classifyName));
        adapter = new RandomTxtAdapter(this);
        adapter.setOnCallback(new RandomTxtAdapter.OnCallback() {
            @Override
            public void onDelete(int hashCode) {
                new CommonDialog()
                        .setContent(getString(R.string.random_txt_del_tip))
                        .setLeftAction(getString(R.string.random_txt_del_cancel))
                        .setAutoClose(true)
                        .setCommonAction(getString(R.string.random_txt_del_btn))
                        .setCommonActionColor(Color.parseColor("#E54D42"))
                        .setCommonActionListener(new CommonDialog.ActionListener() {
                            @Override
                            public void onClick(CommonDialog dialog) {
                                for (int i = 0; i < adapter.getDataList().size(); i++) {
                                    RandomTxtInfo txtInfo = adapter.getDataList().get(i);
                                    if (txtInfo.hashCode() == hashCode) {
                                        adapter.removeItem(i);
                                        TxtDataService.delete(txtInfo.getId(), null);
                                        break;
                                    }
                                }
                            }
                        }).show(getCtx());
            }
        });
        adapter.setOnItemClickListener(new BaseRAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                RandomTxtInfo txtInfo = adapter.getDataList().get(position);
                txtInfo.setSelected(!txtInfo.isSelected());
                adapter.notifyItemChanged(position);
            }
        });
        binding.rvList.setAdapter(adapter);
        binding.rvList.setLayoutManager(new LinearLayoutManager(this));
        binding.refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                pageSize++;
                getData();
            }
        });
        binding.mbNext.setOnClickListener(v -> {
            new RandomTxtDialog().setClassifyId(classifyId).show(getCtx());
        });
    }

    @Override
    protected void initData() {
        getData();
    }

    @Override
    public void onReceiveEvent(EventMessage eventMessage) {
        super.onReceiveEvent(eventMessage);
        if (eventMessage.getCode() == EventCodes.IMPORT_RANDOM_TXT) {
            pageNo = 1;
            getData();
        }
    }

    private void getData() {
        TxtDataService.getList(classifyId,pageNo, pageSize, new AsyncCallback<List<RandomTxtInfo>>() {
            @Override
            public void onSuccess(List<RandomTxtInfo> resultList) {
                adapter.addData(resultList, pageNo == 1);
                binding.refreshLayout.finishLoadMore();
                binding.refreshLayout.setNoMoreData(resultList == null || resultList.size() == 0);
            }
        });
    }
}
