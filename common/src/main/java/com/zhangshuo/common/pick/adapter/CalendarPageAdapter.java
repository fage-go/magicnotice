package com.zhangshuo.common.pick.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.zhangshuo.common.pick.dialog.CalendarDialog;
import com.zhangshuo.common.pick.widget.CalendarFragment;

import java.util.Calendar;

public class CalendarPageAdapter extends FragmentStateAdapter {
    private boolean isChoiceModelSingle;
    private CalendarDialog.IDataBridge dataBridge;

    public void setInfo(boolean isChoiceModelSingle, CalendarDialog.IDataBridge dataBridge) {
        this.isChoiceModelSingle = isChoiceModelSingle;
        this.dataBridge = dataBridge;
    }


    public CalendarPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public CalendarPageAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public CalendarPageAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MONTH, position);
        return CalendarFragment.newInstance(instance.get(Calendar.YEAR),
                instance.get(Calendar.MONTH) + 1, isChoiceModelSingle, dataBridge);
    }

    @Override
    public int getItemCount() {
        return 12;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}
