# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Android\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

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
-keep class com.google.**
-dontwarn com.google.**
-dontwarn com.android.**
-dontwarn **HoneycombMR2
-dontwarn **CompatICS
-dontwarn **Honeycomb
-dontwarn **CompatIcs*
-dontwarn **CompatFroyo
-dontwarn **CompatGingerbread
-dontwarn com.tencent.bugly.**
-keep public class baza.android.bzw.R$*{
public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keepclasseswithmembernames class * {       # 保持 native 方法不被混淆
    native <methods>;
}

-keepclasseswithmembers class * {            # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {            # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity { #保持类成员
   public void *(android.view.View);
}
#不混淆第三方jar
-keep class com.bm.**{*;}
-keep class com.umeng.**{*;}
-keep class com.baidu.**{*;}
#-keep class com.android.volley.**{*;}
#-keep class com.nostra13.**{*;}
-keep class com.nineoldandroids.**{*;}
#-dontwarn org.apache.http.**
#-keep class org.apache.http.** { *;}
-keep class com.alibaba.fastjson.**{*;}
-keep class com.handmark.**{*;}
#自己写的model，里面字段对应的fastjson解析的字段。因此我们要解析json数据，肯定是不希望它被混淆的
-keep class com.baza.android.bzw.bean.**{*;}
#-keep class com.baza.track.**{*;}

#log包
-keep class com.baza.android.bzw.log.**{*;}
-dontwarn com.bm.**
-keepattributes Signature
-keepattributes InnerClasses
#growingIO

-keep class com.growingio.android.sdk.** {
    *;
}
-dontwarn com.growingio.android.sdk.**
-dontwarn android.support.**
-keep class android.support.**{
    *;
}
-keep class com.growingio.android.sdk.collection.GrowingIOInstrumentation {
    public *;
    static <fields>;
}

-keepnames class * extends android.view.View

-keep class * extends android.app.Fragment {
    public void setUserVisibleHint(boolean);
    public void onHiddenChanged(boolean);
    public void onResume();
    public void onPause();
}
-keep class android.support.v4.app.Fragment {
    public void setUserVisibleHint(boolean);
    public void onHiddenChanged(boolean);
    public void onResume();
    public void onPause();
}
-keep class * extends android.support.v4.app.Fragment {
    public void setUserVisibleHint(boolean);
    public void onHiddenChanged(boolean);
    public void onResume();
    public void onPause();
    }
#GIF
-keep public class pl.droidsonroids.gif.GifIOException{<init>(int);}
-keep class pl.droidsonroids.gif.GifInfoHandle{<init>(long,int,int,int);}
#QQ
-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}
#WeChat
-keep class com.tencent.mm.opensdk.** {
   *;
}

-dontwarn com.netease.**
-dontwarn io.netty.**
-keep class com.netease.** {*;}
#如果 netty 使用的官方版本，它中间用到了反射，因此需要 keep。如果使用的是我们提供的版本，则不需要 keep
-keep class io.netty.** {*;}

#如果你使用全文检索插件，需要加入
-dontwarn org.apache.lucene.**
-keep class org.apache.lucene.** {*;}

-dontoptimize
-dontpreverify

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }
#OKHTTP AND GILDE
-dontwarn  okhttp3.**
-keep class okhttp3.**{*;}
-dontwarn okio.**
-keep class okio.** { *; }
-keep class com.bumptech.glide.integration.okhttp3.OkHttpLibraryGlideModule
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule
-keep class social.data.**{*;}
-keep class  com.bznet.android.rcbox.R$*{
   public static final int *;
 }

 -keep class com.taobao.wireless.security.**{*;}
 -keep class com.ut.secbody.**{*;}
 -keep class com.taobao.dp.**{*;}
 -keep class com.alibaba.wireless.security.**{*;}
 -keep class com.alibaba.verificationsdk.**{*;}
 -keep interface com.alibaba.verificationsdk.ui.IActivityCallback

