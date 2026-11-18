package com.zhangshuo.common.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhangshuo.common.R;
import com.zhangshuo.common.listener.OnClickCallback;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseRAdapter<T> extends RAdapter {

    public static final int TYPE_HEADER_VIEW = -1000;
    public static final int TYPE_FOOTER_VIEW = -2000;

    private Context context;
    private List<View> headerViewList = new ArrayList<>();//头部view信息
    private List<View> footerViewList = new ArrayList<>();//底部view信息
    private List<T> dataList = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    protected abstract void onConvertView(ViewHolder viewHolder, T t, int position);

    protected abstract int onSetItemViewType(T t, int position);

    public BaseRAdapter(Context context) {
        super(context);
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_HEADER_VIEW || viewType == TYPE_FOOTER_VIEW) {
            //添加容器
            view = LayoutInflater.from(context).inflate(R.layout.base_item_container, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(onBindView(viewType), parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        //判断是否为空布局

        if (position < headerViewList.size()) {
            return TYPE_HEADER_VIEW;
        } else if (position >= headerViewList.size() + dataList.size()) {
            return TYPE_FOOTER_VIEW;
        } else {
            int dataPosition = position - headerViewList.size();
            return onSetItemViewType(dataList.get(dataPosition), dataPosition);
        }
    }

    @Override
    protected void convertView(ViewHolder holder, final int position) {
        try {
            if (holder.getItemViewType() == TYPE_HEADER_VIEW) {
                //头部添加view
                LinearLayout linearLayout = (LinearLayout) holder.itemView;
                linearLayout.removeAllViews();
                linearLayout.addView(headerViewList.get(position));
            } else if (holder.getItemViewType() == TYPE_FOOTER_VIEW) {
                //尾部添加view
                LinearLayout linearLayout = (LinearLayout) holder.itemView;
                linearLayout.removeAllViews();
                linearLayout.addView(footerViewList.get(position - dataList.size() - headerViewList.size()));
            } else {
                //其他的点击和长按事件
                holder.itemView.setOnClickListener(new OnClickCallback() {
                    @Override
                    public void click(View v) {
                        if (onItemClickListener != null)
                            onItemClickListener.onClick(position);
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (onItemLongClickListener != null)
                            onItemLongClickListener.onLongClick(position);
                        return false;
                    }
                });
                int realDataPosition = position - headerViewList.size();
                onConvertView(holder, dataList.get(realDataPosition), realDataPosition);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RAdapter.ViewHolder holder, int position) {
        convertView(holder, position);
    }

    @Override
    public int getItemCount() {
        return headerViewList.size() + footerViewList.size() + dataList.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            ((GridLayoutManager) layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    //spanszie是为 用多少个view去显示这个
                    int itemViewType = getItemViewType(position);
                    return (itemViewType == -1 || itemViewType == -2
                            || itemViewType == TYPE_HEADER_VIEW || itemViewType == TYPE_FOOTER_VIEW) ?
                            ((GridLayoutManager) layoutManager).getSpanCount() : 1;
                }
            });
        }
    }

    /**
     * 添加头部信息
     *
     * @param views
     */
    public void addHeaderView(View... views) {
        //需要判断是否已经添加进去过
        for (View view : views) {
            if (headerViewList.contains(view)) continue;
            headerViewList.add(view);
        }
        notifyDataSetChanged();
    }

    /**
     * 移除头部view
     *
     * @param views
     */
    public void removeHeaderView(View... views) {
        for (View view : views) {
            headerViewList.remove(view);
        }
        notifyDataSetChanged();
    }

    /**
     * 添加尾部view
     *
     * @param views
     */
    public void addFooterView(View... views) {
        for (View view : views) {
            if (footerViewList.contains(view)) continue;
            footerViewList.add(view);
        }
        notifyDataSetChanged();
    }

    /**
     * 移除尾部view
     *
     * @param views
     */
    public void removeFooterView(View... views) {
        for (View view : views) {
            footerViewList.remove(view);
        }
        notifyDataSetChanged();
    }

    /**
     * 添加原数据
     *
     * @param dataList
     */
    public void addData(List<T> dataList) {
        addData(dataList, false);
    }

    public void addData(List<T> dataList, boolean cleanAll) {
        if (cleanAll) {
            this.dataList.clear();
        }
        if (dataList != null) {
            int current = this.dataList.size();
            this.dataList.addAll(dataList);
            if (current == 0) {
                notifyDataSetChanged();
            } else {
                notifyItemRangeChanged(current, this.dataList.size());
            }
        }
    }

    /**
     * 添加一项item
     *
     * @param position 插入的位置
     * @param data     数据
     */
    public void addItemData(int position, T data) {
        getDataList().add(position, data);
        notifyItemInserted(position);
    }

    /**
     * 移除某一项
     *
     * @param position
     */
    public void removeItem(int position) {
        if (getDataList().size() <= position) return;
        getDataList().remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, 1);
    }

    public void cleanData() {
        this.dataList.clear();
        notifyDataSetChanged();
    }

    public List<T> getDataList() {
        return dataList;
    }

    public T getItem(int position) {
        return dataList.get(position);
    }

    public List<View> getHeaderViewList() {
        if (headerViewList != null)
            return headerViewList;
        else return null;
    }

    public List<View> getFooterViewList() {
        if (footerViewList != null)
            return footerViewList;
        else return null;
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public interface OnItemLongClickListener {
        void onLongClick(int position);
    }
}
