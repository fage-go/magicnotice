# MagicNotice（通知了 / Cicada）

Android 通知管理应用。首页的通知时间旁显示该通知对应的星期。

## 构建

使用 JDK 11、Android SDK Platform 30 和 Build Tools 30.0.2 / 30.0.3。配置好本地 Android SDK 后，在仓库根目录执行：

```sh
bash ./gradlew :app:assembleZhRelease :app:assembleEnRelease
```

不要设置 `MAGICNOTICE_KEYSTORE`、`MAGICNOTICE_STORE_PASSWORD`、`MAGICNOTICE_KEY_ALIAS` 或 `MAGICNOTICE_KEY_PASSWORD`，即可生成未签名的 Release APK。GitHub Actions 会核验 APK 未签名，再将中英文版本分别保存为构建产物，不会自动发布 GitHub Release。未签名 APK 需要自行签名后才能安装。

仓库不含发布密钥或签名密码。

## 规则备份

打开「设置 → 导入/导出数据」，可通过系统文件选择器保存或选取 `.cicada-rules.json` 备份。文件只包含所选规则及其条件和动作，不包含通知历史、命中次数或本机规则 ID。**文件未加密**，可能含关键词、联系人号码或动作参数，请勿公开分享。

导入会先验证完整文件，确认后按备份顺序追加，不覆盖现有规则。所有导入规则默认关闭；请检查条件和动作，再在规则列表手动开启。文件保留导出时的开关状态供核对，不会以它自动启用规则。当前版不导入旧的 `.cicada` 加密文件；若仍需要旧文件，需先用能打开它的旧兼容版本恢复规则，再升级并导出新版备份。

## 许可

本仓库使用[PolyForm Noncommercial 1.0.0](https://polyformproject.org/licenses/noncommercial/1.0.0) 。这是一份非商业使用的源码许可。在它适用的项目中，可按条款为非商业目的使用、修改和分发；

第三方组件保留各自适用的许可和署名，见 [第三方声明](https://github.com/fage-go/magicnotice/blob/main/THIRD_PARTY_NOTICES.md)。
