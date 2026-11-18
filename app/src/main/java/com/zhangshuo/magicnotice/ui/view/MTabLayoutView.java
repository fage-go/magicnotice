package com.zhangshuo.magicnotice.ui.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.ui.model.TabInfo;

import java.util.List;

public class MTabLayoutView extends FrameLayout {
    private TabLayout tabLayout;
    private OnCallback onCallback;
    private OnTabDoubleTapListener onTabDoubleTapListener;
    private TabLayout.Tab lastClickedTab;
    private long lastTabClickTime;
    private boolean openLeftLine;

    public void setOnCallback(OnCallback onCallback) {
        this.onCallback = onCallback;
    }

    public void setOnTabDoubleTapListener(OnTabDoubleTapListener listener) {
        onTabDoubleTapListener = listener;
        resetTabClick();
    }

    private void onTabClicked(TabLayout.Tab tab) {
        if (onTabDoubleTapListener == null) return;
        long clickTime = SystemClock.uptimeMillis();
        if (lastClickedTab == tab
                && clickTime - lastTabClickTime <= ViewConfiguration.getDoubleTapTimeout()) {
            resetTabClick();
            onTabDoubleTapListener.onDoubleTap(tab.getPosition());
        } else {
            lastClickedTab = tab;
            lastTabClickTime = clickTime;
        }
    }

    private void resetTabClick() {
        lastClickedTab = null;
        lastTabClickTime = 0;
    }

    public void setRipperColor(int color) {
        tabLayout.setTabRippleColor(ColorStateList.valueOf(color));
    }

    public void setIndicator(int resId, int color) {
        tabLayout.setSelectedTabIndicator(getResources().getDrawable(resId));
        tabLayout.setSelectedTabIndicatorColor(color);
        tabLayout.setTabIndicatorFullWidth(false);
    }

    public void openLeftLineTip(boolean open) {
        this.openLeftLine = open;
    }


    public void bindTabList(List<String> tabTxtList) {
        for (int i = 0; i < tabTxtList.size(); i++) {
            String txt = tabTxtList.get(i);
            TabLayout.Tab tab = tabLayout.newTab();
            View view = LayoutInflater.from(getContext()).inflate(R.layout.tab_item_view, null);
            View line = view.findViewById(R.id.v_line);
            TextView textView = view.findViewById(R.id.tab_tv_txt);
            textView.setText(txt);
            if (openLeftLine) {
                line.setVisibility(i == 0 ? VISIBLE : GONE);
            } else {
                line.setVisibility(GONE);
            }
            textView.setTextAppearance(i == 0 ? R.style.TabLayoutTextNormalSelect : R.style.TabLayoutTextNormal);
            tab.setCustomView(view);
            tabLayout.addTab(tab);
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View customView = tab.getCustomView();
                if (customView == null) {
                    tab.setCustomView(R.layout.tab_item_view);
                }
                View line = tab.getCustomView().findViewById(R.id.v_line);
                if (openLeftLine) {
                    line.setVisibility(VISIBLE);
                }
                TextView textView = tab.getCustomView().findViewById(R.id.tab_tv_txt);
                textView.setTextAppearance(R.style.TabLayoutTextNormalSelect);
                if (onCallback != null) {
                    onCallback.select(tab.getPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View customView = tab.getCustomView();
                if (customView == null) {
                    tab.setCustomView(R.layout.tab_item_view);
                }
                View line = tab.getCustomView().findViewById(R.id.v_line);
                line.setVisibility(GONE);
                TextView textView = tab.getCustomView().findViewById(R.id.tab_tv_txt);
                textView.setTextAppearance(R.style.TabLayoutTextNormal);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public void bindViewPager2(ViewPager2 viewPager2, List<TabInfo> tabInfoList) {
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                resetTabClick();
                if (tabInfoList.size() > position) {
                    View customView = tab.getCustomView();
                    if (customView == null) {
                        tab.setCustomView(R.layout.tab_item_view);
                    }
                    TextView textView = tab.getCustomView().findViewById(R.id.tab_tv_txt);
                    textView.setText(tabInfoList.get(position).getTabName());
                    // TabView.performClick still selects the tab after this listener runs.
                    // Page swipes and programmatic selection do not count as clicks.
                    tab.view.setOnClickListener(v -> onTabClicked(tab));
                }
            }
        });
        tabLayoutMediator.attach();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View customView = tab.getCustomView();
                if (customView == null) {
                    tab.setCustomView(R.layout.tab_item_view);
                }
                TextView textView = tab.getCustomView().findViewById(R.id.tab_tv_txt);
                textView.setTextAppearance(getContext(), R.style.TabLayoutTextSelected);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (lastClickedTab == tab) {
                    resetTabClick();
                }
                View customView = tab.getCustomView();
                if (customView == null) {
                    tab.setCustomView(R.layout.tab_item_view);
                }
                TextView textView = tab.getCustomView().findViewById(R.id.tab_tv_txt);
                textView.setTextAppearance(getContext(), R.style.TabLayoutTextUnSelected);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public void select(int position) {
        TabLayout.Tab tabAt = getTabAt(position);
        if (tabAt != null) {
            tabAt.select();
        }
    }

    public TabLayout.Tab getTabAt(int position) {
        return tabLayout.getTabAt(position);
    }


    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_tab_layout, this);
        tabLayout = findViewById(R.id.view_tab_layout);
    }


    public interface OnCallback {
        void select(int position);
    }

    public interface OnTabDoubleTapListener {
        void onDoubleTap(int position);
    }

    @Override
    protected void onDetachedFromWindow() {
        resetTabClick();
        super.onDetachedFromWindow();
    }

    public MTabLayoutView(@NonNull Context context) {
        super(context);
        init();
    }

    public MTabLayoutView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MTabLayoutView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MTabLayoutView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
}
