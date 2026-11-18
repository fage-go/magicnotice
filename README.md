# MagicNotice（通知了 / Cicada）

Android 通知管理应用。首页的通知时间旁显示该通知对应的星期；此源码版本不再集成 Google Play Billing。

## 构建

使用 JDK 11、Android SDK Platform 30 及 Build Tools 30.0.2 / 30.0.3。配置好本地 Android SDK 后，在仓库根目录运行：

```sh
bash ./gradlew :app:assembleZhDebug :app:assembleEnDebug
```

两个 Debug APK 分别位于 `app/build/outputs/apk/zh/debug/` 和 `app/build/outputs/apk/en/debug/`。GitHub Actions 使用 Android 标准调试签名，不使用发布签名。

公开构建默认不启用友盟初始化，也不读取或生成旧版加密规则文件。仅对受信任的私有构建，维护者可以在本地环境提供 `MAGICNOTICE_UMENG_APP_ID`，或者同时提供与旧版对应的 `MAGICNOTICE_LEGACY_EXPORT_KEY` 和 `MAGICNOTICE_LEGACY_EXPORT_IV` 以使用旧版规则格式。不要把这些值写入仓库、公开 CI 或公开分发含有这些值的构建。旧版 DES 仅为兼容原有文件，不应用于保护新数据。仓库不含发布密钥或签名密码。

## 许可

本项目没有通用开源许可证（No License）；公开可见不等于授予通用的使用、修改或分发许可。第三方组件保留各自适用的许可和署名，见 [第三方声明](THIRD_PARTY_NOTICES.md)。
