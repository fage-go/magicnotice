# MagicNotice（通知了 / Cicada）

Android 通知管理应用。首页的通知时间旁显示该通知对应的星期。

## 构建

使用 JDK 11、Android SDK Platform 30 和 Build Tools 30.0.2 / 30.0.3。配置好本地 Android SDK 后，在仓库根目录执行：

```sh
bash ./gradlew :app:assembleZhRelease :app:assembleEnRelease
```

不要设置 `MAGICNOTICE_KEYSTORE`、`MAGICNOTICE_STORE_PASSWORD`、`MAGICNOTICE_KEY_ALIAS` 或 `MAGICNOTICE_KEY_PASSWORD`，即可生成未签名的 Release APK。GitHub Actions 会核验 APK 未签名，再将中英文版本分别保存为构建产物，不会自动发布 GitHub Release。未签名 APK 需要自行签名后才能安装。

公开构建默认不读取或生成旧版加密规则文件。仅对受信任的私有构建，维护者可以同时提供与旧版对应的 `MAGICNOTICE_LEGACY_EXPORT_KEY` 和 `MAGICNOTICE_LEGACY_EXPORT_IV` 以兼容旧版规则文件。不要把这些值写入仓库、公开 CI 或公开分发含有这些值的构建。旧版 DES 仅为兼容原有文件，不应用于保护新数据。仓库不含发布密钥或签名密码。

## 许可

本仓库使用[PolyForm Noncommercial 1.0.0](https://polyformproject.org/licenses/noncommercial/1.0.0) 。这是一份非商业使用的源码许可。在它适用的项目中，可按条款为非商业目的使用、修改和分发；

第三方组件保留各自适用的许可和署名，见 [第三方声明](https://github.com/fage-go/magicnotice/blob/main/THIRD_PARTY_NOTICES.md)。
