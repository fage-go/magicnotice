package com.zhangshuo.magicnotice.config;

import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;

import java.util.HashMap;
import java.util.Map;

public class CacheMemory {

    public static Map<String, Object> cacheData = new HashMap<>();

    private static Map<String, String> txtJudgeMap = new HashMap<>();

    public static Map<String, String> getTxtJudgeMap() {
        if (txtJudgeMap.isEmpty()) {
            txtJudgeMap.put("A", StringUtil.getString(R.string.judge_cache_a));
            txtJudgeMap.put("T", StringUtil.getString(R.string.judge_cache_t));
            txtJudgeMap.put("C", StringUtil.getString(R.string.judge_cache_c));
            txtJudgeMap.put("*", StringUtil.getString(R.string.judge_cache_d));
            txtJudgeMap.put("!*", StringUtil.getString(R.string.judge_cache_e));
            txtJudgeMap.put("*~", StringUtil.getString(R.string.judge_cache_f));
            txtJudgeMap.put("~*", StringUtil.getString(R.string.judge_cache_g));
        }
        return txtJudgeMap;
    }
}
