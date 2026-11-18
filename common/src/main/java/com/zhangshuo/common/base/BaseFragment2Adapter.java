package com.zhangshuo.common.base;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class BaseFragment2Adapter extends FragmentStateAdapter {
    private List<BaseFragment> mFragmentList = new ArrayList<>();

    public void setFragmentList(List<BaseFragment> fragmentList) {
        mFragmentList = fragmentList;
    }

    public BaseFragment2Adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return mFragmentList.size();
    }
}
