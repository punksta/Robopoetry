# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /usr/local/android_sdk/sdk/tools/proguard/proguard-android.txt
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

-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}
-keep class ru.yandex.speechkit.** { *; }
-keep interface java.beans.** { *; }
-keep class java.beans.** { *; }
-keepattributes *Annotation*

-dontwarn java.beans.**
-dontwarn java.awt.**
-dontwarn javax.swing.**


-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-dontwarn com.squareup.okhttp.**
-dontwarn org.apache.**
