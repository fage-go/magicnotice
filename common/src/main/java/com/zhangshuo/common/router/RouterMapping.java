package com.zhangshuo.common.router;


public class RouterMapping {

    public static final int TYPE_INIT = 100;//初始化状态下的路由映射能力，其他type均为下发的能力

    private int type;//路由映射的配置
    private String jumpUrl; //提供给第三方的jumpUrl
    private String router; //本身自带的路由地址

    private RouterMapping() {

    }

    public RouterMapping(String jumpUrl, String router) {
        this.jumpUrl = jumpUrl;
        this.router = router;
    }

    public RouterMapping(int type, String jumpUrl, String router) {
        this.jumpUrl = jumpUrl;
        this.router = router;
    }

    public int getType() {
        return type;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public String getRouter() {
        return router;
    }
}
