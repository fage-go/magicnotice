package com.zhangshuo.magicnotice.window;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.db.service.NotificationDataService;
import com.zhangshuo.magicnotice.ui.adapter.NotificationAdapter;

import java.util.ArrayList;
import java.util.List;

public class NotReadView extends FrameLayout {
    private TextView tvTitle;
    private ImageView ivMore;
    private RecyclerView rvList;
    private ConstraintLayout clClose;
    private ImageView ivClose;
    private ConstraintLayout clBar;
    int ruleId;
    int unReadNum;
    private List<Integer> clickedList = new ArrayList<>();

    private NotificationAdapter notificationAdapter;
    private OnCallback onCallback;

    public void setInfo(int ruleId, int unReadNum, OnCallback onCallback) {
        this.ruleId = ruleId;
        this.unReadNum = unReadNum;
        this.onCallback = onCallback;
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_not_read_window, this);
        clBar = findViewById(R.id.cl_title);
        ivMore = findViewById(R.id.iv_more);
        tvTitle = findViewById(R.id.tv_title);
        rvList = findViewById(R.id.rv_notice_list);
        clClose = findViewById(R.id.cl_close);
        ivClose = findViewById(R.id.iv_close);
        rvList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        tvTitle.getPaint().setFakeBoldText(true);
        notificationAdapter = new NotificationAdapter(getContext());
        notificationAdapter.setShowSmallMode(true);
        notificationAdapter.setShowRuleInfo(false);
        notificationAdapter.setOnClickCallback(new NotificationAdapter.OnClickCallback() {
            @Override
            public boolean onIntercept(NotificationData notificationData) {
                return false;
            }

            @Override
            public void onClick(NotificationData notificationData) {
                clickedList.add(notificationData.getId());
                notificationData.setReadType(2);
                notificationAdapter.notifyItemChanged(notificationAdapter.getPositionById(notificationData.getId()));
                if (onCallback != null) {
                    onCallback.onClose();
                }
                if (onCallback != null) {
                    onCallback.needShowFloatBall(String.valueOf(ruleId), true);
                }
            }
        });
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), RecyclerView.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.line_drawable));
        rvList.addItemDecoration(dividerItemDecoration);
        rvList.setAdapter(notificationAdapter);
        clClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCallback != null) {
                    onCallback.onClose();
                }
            }
        });
    }

    public void queryDataListByRule() {
        NotificationDataService.findNotificationList(ruleId, 1, unReadNum, new AsyncCallback<List<NotificationData>>() {
            @Override
            public void onSuccess(List<NotificationData> result) {
                notificationAdapter.addData(result);
            }
        });
    }

    public interface OnCallback {
        void needShowFloatBall(String ruleId, boolean show);

        void onClose();
    }

    public NotReadView(@NonNull Context context) {
        super(context);
        init();
    }

    public NotReadView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NotReadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public NotReadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
}
