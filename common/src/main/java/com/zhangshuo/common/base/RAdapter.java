package com.zhangshuo.common.base;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author: zhangshuo
 * Date:2020/12/24
 * @doc: WwdzRAdapter
 */
public abstract class RAdapter extends RecyclerView.Adapter<RAdapter.ViewHolder> {

    private Context context;

    public RAdapter(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    protected abstract int onBindView(int viewType);

    protected abstract void convertView(ViewHolder holder, int position);

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(onBindView(viewType), parent, false));
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    public class ViewHolder<T> extends RecyclerView.ViewHolder {

        private final SparseArray<View> mViews;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mViews = new SparseArray<View>();
        }

        /**
         * 通过控件的Id获取对于的控件，如果没有则加入views
         */
        public <T extends View> T $(int viewId) {
            View view = mViews.get(viewId);
            if (view == null) {
                view = itemView.findViewById(viewId);
                mViews.put(viewId, view);
            }
            return (T) view;
        }
    }
}
