package com.zhangshuo.magicnotice.ui.view.remind;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zhangshuo.magicnotice.scheduler.SchedulerUtil;

import java.util.WeakHashMap;

public class RemindRepeatView extends FrameLayout {
    private int currentType = 0;
    private WeakHashMap<Integer, IRemindRepeatFactory> factoryMap = new WeakHashMap<>();
    private IDataRemindCallback dataRemindCallback;

    public void setDataRemindCallback(IDataRemindCallback dataRemindCallback) {
        this.dataRemindCallback = dataRemindCallback;
    }

    public void setRepeatType(int type) {
        currentType = type;
        IRemindRepeatFactory remindRepeatFactory = getFactory(type);
        remindRepeatFactory.setViewCallback(dataRemindCallback);
        View view = remindRepeatFactory.buildRepeatView(getContext());
        removeAllViews();
        if (view != null)
            addView(view, new FrameLayout.LayoutParams(-1, -2));
    }

    public int getRepeatType() {
        return currentType;
    }

    public void setRepeatData(String repeatData) {
        IRemindRepeatFactory factory = getFactory(currentType);
        factory.setRepeatData(repeatData);
    }

    public String getRepeatData() {
        IRemindRepeatFactory remindRepeatFactory = getFactory(currentType);
        return remindRepeatFactory.getRepeatData();
    }

    public CharSequence getRepeatTip() {
        IRemindRepeatFactory remindRepeatFactory = getFactory(currentType);
        return remindRepeatFactory.handlerRepeatTip(remindRepeatFactory.getRepeatData());
    }

    public void setSelectTime(String startTime, String endTime) {
        IRemindRepeatFactory remindRepeatFactory = getFactory(currentType);
        remindRepeatFactory.setTimeData(startTime, endTime);
    }


    private IRemindRepeatFactory getFactory(int type) {
        IRemindRepeatFactory remindRepeatFactory = factoryMap.get(type);
        if (remindRepeatFactory == null) {
            remindRepeatFactory = SchedulerUtil.getRemindRepeatFactory(type);
        }
        factoryMap.put(type, remindRepeatFactory);
        return remindRepeatFactory;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        factoryMap.clear();
    }

    private void init() {

    }

    public RemindRepeatView(@NonNull Context context) {
        super(context);
        init();
    }

    public RemindRepeatView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RemindRepeatView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RemindRepeatView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
}
