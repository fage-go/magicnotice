package com.zhangshuo.magicnotice.ui.view.remind;

import android.content.Context;
import android.view.View;

import com.zhangshuo.magicnotice.scheduler.NextDateResult;
import com.zhangshuo.magicnotice.ui.model.RemindTimeInfo;

/**
 * - 不重复
 * - 指定日期 默认为当前日期 / 可选
 * - 按日
 * - 日期设置 每日 / 可选范围 （YYMMDD 至 YYMMDD)
 * - 按周
 * - 周期设置 周一、周二、周三、周四、周五、周六、周日（多选）
 * - 按日期
 * - 年份设置 每年 / 近五年 多选
 * - 月份设置 每月 / 月份 多选
 * - 日期设置 每日 / 日期（多选）
 */
public interface IRemindRepeatFactory {

    /**
     * 返回配置的信息
     */
    String getRepeatData();

    /**
     * 设置配置的数据
     *
     * @param remindData
     */
    void setRepeatData(String remindData);

    /**
     * 设置开始时间和结束时间
     */
    void setTimeData(String startTime, String endTime);

    /**
     * 构造基础的View
     */
    View buildRepeatView(Context context);

    /**
     * 设置View的监听事件
     */
    void setViewCallback(IDataRemindCallback dataRemindCallback);

    /**
     * 与上述方法无交互
     * 处理要展示的DateTip信息
     */
    CharSequence handlerDateTip(RemindTimeInfo remindTimeInfo);

    /**
     * 与上述方法无交互
     * 处理要展示的重复信息
     */
    CharSequence handlerRepeatTip(String repeatDate);

    /**
     * 与上述方法无交互
     * 获取下一次执行的时间
     */
    NextDateResult handlerNextDate(RemindTimeInfo remindTimeInfo, int currentCount);
}
