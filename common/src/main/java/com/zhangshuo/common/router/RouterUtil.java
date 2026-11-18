package com.zhangshuo.common.router;

import android.app.Activity;
import android.app.Application;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.android.arouter.facade.callback.NavigationCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.zhangshuo.common.util.DebugUtil;
import com.zhangshuo.common.util.GsonUtils;
import com.zhangshuo.common.util.LogUtils;

import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: zhangshuo
 * Date:2020/12/24
 * @doc: 使用ARouter，目前只支持基本跳转，后续需要在逐步添加
 */
public class RouterUtil {

    private static final String TAG = "RouterUtil";

    private boolean showLog = false;
    private String baseH5Router = "/app/activity/h5";
    private List<RouterMapping> routerMappingList = new ArrayList<>();

    /**
     * 获取fragment
     */
    public Object getObject(String router) {
        return getObject(router, null);
    }

    public Object getObject(String router, Bundle bundle) {
        return ARouter.getInstance().build(router).with(bundle).navigation();
    }

    /**
     * 处理路由跳转
     *
     * @param router 普通的无参数路由，或者跨段传递过来的路由,支持H5，支持携带 ?key=data&key=xxx这种参数
     */
    public void navigation(String router) {
        navigation(router, null);
    }

    public void navigation(String router, Bundle bundle) {
        navigation(null, router, bundle, null);
    }

    public void navigation(Activity activity, String router, int requestCode) {
        navigation(activity, router, null, requestCode, null);
    }

    public void navigation(Activity activity, String router, Bundle bundle, NavigationCallback callback) {
        navigation(activity, router, bundle, -1, callback);
    }

    public void navigation(Activity activity,
                           String router,
                           Bundle bundle,
                           int requestCode,
                           NavigationCallback callback) {
        if (showLog) {
            Log.d(TAG, "start: activity: " + activity + " router: " + router + " bundle: " + bundle + " requestCode: " + requestCode);
        }
        if (bundle == null) {
            bundle = new Bundle();
        }
        router = checkMappingRouter(router, bundle);
        if (showLog) {
            Log.d(TAG, "afterMapping: activity: " + activity + " router: " + router + " bundle: " + bundle.toString() + " requestCode: " + requestCode);
        }
        if (TextUtils.isEmpty(router)) {
            // 如果路由为空，则不跳转
            Log.d(TAG, "router is empty...");
            return;
        }
        if (!router.startsWith("/")) {
            // 如果到这里害不是/开头的
            Log.d(TAG, "router is begin is not / ...");
            return;
        }
        try {
            ARouter.getInstance().build(router).with(bundle).navigation(activity, requestCode, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检测映射路由地址
     */
    private String checkMappingRouter(String router, Bundle bundle) {
        // 判断路由是否包含参数
        String domainRouter = router;
        if (TextUtils.isEmpty(domainRouter)) {
            return null;
        }

        boolean isH5 = isMatch(domainRouter, "^http.*");
        if (router.contains("?")) {
            // 如果是包含问号
            try {
                setParamInUrl(domainRouter, bundle);
                if (!isH5) {
                    //如果不是H5
                    domainRouter = router.substring(0, router.indexOf("?"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (TextUtils.isEmpty(domainRouter)) {
            return null;
        }
        // 判断是否匹配为H5
        if (isH5) {
            // 如果匹配为h5
            bundle.putString("url", domainRouter);
            domainRouter = baseH5Router;
        } else {
            for (RouterMapping routerMapping : routerMappingList) {
                // 判断是否为相等的
                if (domainRouter.equals(routerMapping.getJumpUrl())) {
                    domainRouter = routerMapping.getRouter();
                    isShowFloatView(bundle, routerMapping);
                    break;
                }
            }
        }
        return domainRouter;
    }

    private void isShowFloatView(Bundle bundle, RouterMapping routerMapping) {
    }


    private void setParamInUrl(String url, Bundle bundle) {
        try {
            Uri uri = Uri.parse(url);
            Set<String> queryParameterNames = uri.getQueryParameterNames();
            if (queryParameterNames.size() > 0) {
                for (String str : queryParameterNames) {
                    bundle.putString(str, uri.getQueryParameter(str));
                }
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * 增加路由映射的能力，实际能力可以后后端下发
     */
    public void addRouterMappingList(List<RouterMapping> routerMappingList) {
        if (routerMappingList == null || routerMappingList.size() == 0) return;
        this.routerMappingList.addAll(routerMappingList);
    }

    /**
     * 恢复原生的路由映射能力
     */
    public void restoreRouterMapping() {
        if (this.routerMappingList != null && this.routerMappingList.size() > 0) {
            for (int i = 0; i < this.routerMappingList.size(); i++) {
                if (this.routerMappingList.get(i).getType() != RouterMapping.TYPE_INIT) {
                    // 不是初始化的路由映射
                    this.routerMappingList.remove(i);
                    i--;
                }
            }
        }
    }

    public void inject(Object object) {
        if (showLog) {
            LogUtils.i("Router inject : " + object.getClass().getName());
        }
        ARouter.getInstance().inject(object);
    }

    /**
     * 路由初始化
     *
     * @param clazz 路由地址定义的clazz
     */
    public void init(Application application, Class clazz) {
        if (DebugUtil.isApkInDebug(application)) {
            ARouter.openDebug();
            ARouter.openLog();
        }
        ARouter.init(application);
        scanAllRouterMapping(clazz);
    }

    public void bindBaseH5Router(String routerUrl) {
        this.baseH5Router = routerUrl;
    }

    /**
     * 扫描所有的class
     */
    private void scanAllRouterMapping(Class clazz) {
        long scanStartTime = System.currentTimeMillis();
        routerMappingList.clear();
        Field[] fieldArray = clazz.getFields();
        for (Field field : fieldArray) {
            try {
                field.setAccessible(true);
                Scheme scheme = field.getAnnotation(Scheme.class);
                if (scheme == null) continue;
                String[] jumpUrlArray = scheme.value();
                if (jumpUrlArray.length == 0) continue;
                for (String jumpUrl : jumpUrlArray) {
                    RouterMapping routerMapping = new RouterMapping(RouterMapping.TYPE_INIT, jumpUrl, (String) field.get(null));
                    routerMappingList.add(routerMapping);
                }
            } catch (Exception ignored) {

            }
        }
        if (showLog)
            LogUtils.i("scanAllRouterMapping loss time : " + (System.currentTimeMillis() - scanStartTime) + " data:" + GsonUtils.toString(routerMappingList));
    }

    /**
     * url decode 编码
     */
    public String decodeStr(String str) {
        try {
            str = URLDecoder.decode(str, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * url encode 编码
     */
    public String encodeStr(String str) {
        try {
            str = URLEncoder.encode(str, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 匹配正则表达式
     */
    private boolean isMatch(String str, String reg) {
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 是否显示日志
     *
     * @param show
     */
    public void showLog(boolean show) {
        this.showLog = show;
    }

    //单例
    public static RouterUtil get() {
        return Holder.instance;
    }

    private RouterUtil() {

    }

    private static class Holder {
        private static RouterUtil instance = new RouterUtil();
    }
}
