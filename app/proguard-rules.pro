# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-verbose
# 不做预校验，Android不需要预校验，能够加快混淆速度
-dontpreverify
-ignorewarnings
# 代码混淆压缩比，在0~7之间
-optimizationpasses 5
# 混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames
# 指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses
-dontoptimize
-dontskipnonpubliclibraryclassmembers
-dontwarn dalvik.**

# 避免混淆泛型
-keepattributes Signature
# 保留Annotation不混淆
-keepattributes Exceptions
# 避免混淆Annotation、内部类、泛型、匿名类
-keepattributes *Annotation*,InnerClasses,Signature,EnclosingMethod
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

# google推荐算法
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*

# 重命名抛出异常时的文件名称
-renamesourcefileattribute SourceFile

# 保持native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保持Parcelable不被混淆
-keep class * implements Android.os.Parcelable {
    public static final Android.os.Parcelable$Creator *;
}

-keepnames class * implements java.io.Serializable
-keep class * implements com.alibaba.android.arouter.facade.template.IInterceptor{*;}

-keepclassmembers class * implements java.io.Serializable {
   static final long serialVersionUID;
   private static final java.io.ObjectStreamField[]   serialPersistentFields;
   !static !transient <fields>;
   !private <fields>;
   !private <methods>;
   private void writeObject(java.io.ObjectOutputStream);
   private void readObject(java.io.ObjectInputStream);
   java.lang.Object writeReplace();
   java.lang.Object readResolve();
}
# 友盟
-keep class com.umeng.** {*;}

# model
-keep class **.model.** {*;}
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#======以下为项目本身相关类的混淆规则===========#

# 保留R下面的资源
-keep class **.R$* {*;}
-keep public class com.zhangshuo.magicnotice.R$*{
public static final int *;
}

-keep class android.view.** {*;}
-keep class android.app.** {*;}
-keep class androidx.viewbinding.** {*;}
-keep class com.android.ddmlib.** {*;}
-keep class com.android.internal.** {*;}
-keep class com.android.tools.** {*;}
# AndroidX 防止混淆
-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**
-dontwarn android.databinding.**
-keep class android.databinding.** {*;}
-keep class **.databinding.** {*;}

-keep @androidx.annotation.Keep class *
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}

# Understand the @Keep support annotation.
-keep class android.support.annotation.Keep

-keep @android.support.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
 }

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
 }

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
 }

# 处理support包
-dontnote android.support.**
-dontwarn android.support.**
# 保留继承的
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**

-keep public class * extends android.app.Application {
     <init>();
     void attachBaseContext(android.content.Context);
}

# webview相关
-dontwarn android.webkit.WebView
-keep public class android.webkit.**
-keep class com.zhangshuo.magicnotice.ui.h5.**{*;}
# 这个类中使用了反射
-keep class **.view.**{*;}
-keep public class com.zhangshuo.common.mvp.**{*;}

# 新Dialog基类防混淆
-keep public class * extends com.zhangshuo.common.dialog.BaseDialog{*;}

# ======以下为第三方库的混淆规则========= #

# gson工具不需要混淆
-dontwarn com.google.gson**
-keep class com.google.gson.**{*;}

# mainactivity BottomNavigationMenuView
-keepclassmembers class android.support.design.internal.BottomNavigationMenuView {
    boolean mShiftingMode;
}

# glide配置信息
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

# arouter路由混淆
-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep public class com.alibaba.android.arouter.facade.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}

# If you use the byType method to obtain Service, add the following rules to protect the interface:
-keep interface * implements com.alibaba.android.arouter.facade.template.IProvider

# If single-type injection is used, that is, no interface is defined to implement IProvider, the following rules need to be added to protect the implementation
# -keep class * implements com.alibaba.android.arouter.facade.template.IProvider

# rxjava
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
 long producerIndex;
 long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# rxandroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# EventBus
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
# okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#---------------------------------------------------------------------------

#------------------  下方是android平台自带的排除项，这里不要动         ----------------

-keep public class * extends android.app.Activity{
	public <fields>;
	public <methods>;
}
-keep public class * extends android.app.Application {
	public <fields>;
	public <methods>;
}
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclasseswithmembers class * {
	public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
	public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepattributes *Annotation*

-keepclasseswithmembernames class *{
	native <methods>;
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#------------------  下方是共性的排除项目         ----------------
# 方法名中含有“JNI”字符的，认定是Java Native Interface方法，自动排除
# 方法名中含有“JRI”字符的，认定是Java Reflection Interface方法，自动排除

-keepclasseswithmembers class * {
    ... *JNI*(...);
}

-keepclasseswithmembernames class * {
	... *JRI*(...);
}

-keep class **JNI* {*;}
-dontwarn dalvik.**
-dontwarn com.tencent.smtt.**

-keep class com.tencent.smtt.** {
    *;
}

-keep class com.tencent.tbs.** {
    *;
}

-keep class **.**MonthView {*;}
-keep class **.**WeekBar {*;}
-keep class **.**WeekView {*;}
-keep class **.**YearView {*;}

-keep class com.zhangshuo.magicnotice.BaseIniter {*;}
-keep class com.zhangshuo.magicnotice.widget.**{*;}
