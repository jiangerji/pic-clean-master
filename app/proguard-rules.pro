# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Tools\Android-Tools\SDK/tools/proguard/proguard-android.txt
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

#指定代码的压缩级别
-optimizationpasses 5

#包明不混合大小写
-dontusemixedcaseclassnames

#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses

#优化  不优化输入的类文件
-dontoptimize

#预校验
-dontpreverify

#混淆时是否记录日志
-verbose

#混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#保护注解
-keepattributes *Annotation*

#忽略警告
-ignorewarning

# 保留使用的第三库，不进行混淆
# 保留Android Support Library中的类
-dontwarn android.support.**
-keep class android.support.**

# 保留nineoldandroids中的类
-dontwarn com.nineoldandroids.**
-keep class com.nineoldandroids.**

# 不混淆SuperSlim
-dontwarn com.tonicartos.superslim.**
-keep class com.tonicartos.superslim.**

#####################记录生成的日志数据,gradle build时在本项目根目录输出################
#apk 包内所有 class 的内部结构
-dump class_files.txt

#未混淆的类和成员
-printseeds seeds.txt

#列出从 apk 中删除的代码
-printusage unused.txt

#混淆前后的映射
-printmapping mapping.txt
#####################记录生成的日志数据，gradle build时 在本项目根目录输出-end################

# xUtils中使用DBUtils, 使用了@Column注解的实体类不进行混淆
-keep class cn.iam007.pic.clean.master.recycler.RecyclerImageItem { *; }

# LeanCloud的混淆配置
-keepattributes Signature
-dontwarn com.jcraft.jzlib.**
-keep class com.jcraft.jzlib.**  { *;}

-dontwarn sun.misc.**
-keep class sun.misc.** { *;}

-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.** { *;}

-dontwarn sun.security.**
-keep class sun.security.** { *; }

-dontwarn com.google.**
-keep class com.google.** { *;}

-dontwarn com.avos.**
-keep class com.avos.** { *;}

-keep public class android.net.http.SslError
-keep public class android.webkit.WebViewClient

-dontwarn android.webkit.WebView
-dontwarn android.net.http.SslError
-dontwarn android.webkit.WebViewClient

-dontwarn android.support.**

-dontwarn org.apache.**
-keep class org.apache.** { *;}

-dontwarn org.jivesoftware.smack.**
-keep class org.jivesoftware.smack.** { *;}

-dontwarn com.loopj.**
-keep class com.loopj.** { *;}

-dontwarn org.xbill.**
-keep class org.xbill.** { *;}

-keepattributes *Annotation*

