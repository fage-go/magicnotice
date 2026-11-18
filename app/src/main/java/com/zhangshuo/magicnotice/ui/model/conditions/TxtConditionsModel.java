package com.zhangshuo.magicnotice.ui.model.conditions;

import android.text.TextUtils;

import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.CacheMemory;
import com.zhangshuo.magicnotice.notice.model.NotificationInfo;
import com.zhangshuo.magicnotice.ui.model.ConditionsModel;

import java.util.ArrayList;
import java.util.List;

public class TxtConditionsModel extends ConditionsModel {
    //A-*:
    private List<String> txtList = new ArrayList<>();

    public TxtConditionsModel() {
        super(ConditionsType.CONDITIONS_TXT);
    }

    public List<String> getTxtList() {
        return txtList;
    }

    public void setTxtList(List<String> txtList) {
        this.txtList = txtList;
    }

    @Override
    public boolean isEmpty() {
        return txtList.isEmpty();
    }

    @Override
    public boolean isFilter(NotificationInfo notificationInfo) {
        String title = notificationInfo.getTitle();
        String content = notificationInfo.getContent();
        boolean result = false;
        if (txtList.size() > 0) {
            for (String s : txtList) {
                if (isJudge(s)) {
                    //A-*:
                    String text = "None";
                    if (s.startsWith("A")) {
                        text = title + "\n" + content;
                    } else if (s.startsWith("T")) {
                        text = title;
                    } else if (s.startsWith("C")) {
                        text = content;
                    } else {
                        continue;
                    }
                    String judgeCode = s.substring(s.indexOf("-") + 1, s.indexOf(":"));
                    String judgeContent = s.substring(s.indexOf(":") + 1);
                    if ("*".equals(judgeCode)) {
                        result = text.contains(judgeContent);
                    } else if ("!*".equals(judgeCode)) {
                        result = !text.contains(judgeContent);
                    } else if ("*~".equals(judgeCode)) {
                        result = text.startsWith(judgeContent);
                    } else if ("~*".equals(judgeCode)) {
                        result = text.endsWith(judgeContent);
                    }
                } else {
                    result = (title + "\n" + content).contains(s);
                }
                if (result) {
                    break;
                }
            }
        }
        LogUtils.i("TxtConditions> filterType: " + isFilterType()
                + " title: " + title
                + " content: " + content
                + " result: " + result);
        return isFilterType() == result;
    }

    public static String getJudgeTip(String code) {
        if (!isJudge(code)) return StringUtil.getString(R.string.txt_conditions_judge_all_tip) + code;
        String tmpCode = code.substring(0, code.indexOf(":"));
        String[] codeArray = tmpCode.split("-");
        return CacheMemory.getTxtJudgeMap().get(codeArray[0]) + "-"
                + CacheMemory.getTxtJudgeMap().get(codeArray[1]) + code.substring(code.indexOf(":"));
    }

    public static boolean isJudge(String code) {
        return !TextUtils.isEmpty(code) &&
                code.contains("-") &&
                code.contains("*") &&
                code.contains(":");
    }

    @Override
    public String getFilterTip(int maxLength) {
        String filterTip = "";
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < txtList.size(); i++) {
            stringBuilder.append(TxtConditionsModel.getJudgeTip(txtList.get(i)) + "、");
        }
        if (stringBuilder.length() > 0) {
            filterTip = stringBuilder.substring(0, stringBuilder.length() - 1);
        }
        return filterTip;
    }
}
