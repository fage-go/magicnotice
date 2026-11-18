package com.zhangshuo.magicnotice.ui.view;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.VibrateUtils;

public class DefaultItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private OnItemTouchCallbackListener l;
    //当前item可拖拽状态
    private boolean currentPositionLongPressEnabled = true;

    //整个列表的可操作状态 (总开关)
    private boolean isLongPressEnabled;
    private boolean isItemSwipeEnabled;


    public DefaultItemTouchHelperCallback(OnItemTouchCallbackListener onItemTouchCallbackListener) {
        if (onItemTouchCallbackListener == null) {
            throw new NullPointerException("OnItemTouchCallbackListener is null");
        }
        this.l = onItemTouchCallbackListener;
        isLongPressEnabled = l.isLongPressDragEnabled();
        isItemSwipeEnabled = l.isItemViewSwipeEnabled();
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
        if (isLongPressEnabled)
            currentPositionLongPressEnabled = l.currentPositionLongPressEnabled(position);

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {// GridLayoutManager
            // flag如果值是0，相当于这个功能被关闭
            int dragFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlag = 0;
            // create make
            return makeMovementFlags(dragFlag, swipeFlag);
        } else if (layoutManager instanceof LinearLayoutManager) {// linearLayoutManager
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int orientation = linearLayoutManager.getOrientation();

            int dragFlag = 0;
            int swipeFlag = 0;
            // 为了方便理解，相当于分为横着的ListView和竖着的ListView
            if (orientation == LinearLayoutManager.HORIZONTAL) {// 如果是横向的布局
                swipeFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                dragFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            } else if (orientation == LinearLayoutManager.VERTICAL) {// 如果是竖向的布局，相当于ListView
                dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                swipeFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            }
            return makeMovementFlags(dragFlag, swipeFlag);
        }
        return 0;
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            VibrateUtils.vibrate(50);
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();
        int targetPosition = target.getAdapterPosition();
        //如果fromPosition or targetPosition不可操作则直接返回false 不进行数据交换回调
        return l.currentPositionLongPressEnabled(fromPosition)
                && l.currentPositionLongPressEnabled(targetPosition)
                && l.onMove(fromPosition, targetPosition);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        l.onSwiped(viewHolder.getAdapterPosition());
    }

    @Override
    public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
        l.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return isLongPressEnabled && currentPositionLongPressEnabled;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return isItemSwipeEnabled;
    }

    public interface OnItemTouchCallbackListener {
        /**
         * @param position 当前位置是否可长按拖拽
         * @return 默认true 可拖拽
         */
        default boolean currentPositionLongPressEnabled(int position) {
            return true;
        }

        /**
         * 所有item是否可拖拽
         *
         * @return 默认true 可拖拽
         */
        default boolean isLongPressDragEnabled() {
            return true;
        }

        /**
         * 所有item是否可滑动删除
         *
         * @return 默认false 不可删除
         */
        default boolean isItemViewSwipeEnabled() {
            return false;
        }

        /**
         * @param position 滑动删除后位置
         */
        default void onSwiped(int position) {
        }

        /**
         * @param fromPosition   开始位置
         * @param targetPosition 结束位置
         * @return 是否处理
         */
        boolean onMove(int fromPosition, int targetPosition);


        default void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {

        }
    }
}

