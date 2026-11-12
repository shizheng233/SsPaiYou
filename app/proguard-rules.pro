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

# Flexmark keys dependency ordering by each factory's runtime Class. R8 must not
# merge Dependent implementations (e.g. reference and table paragraph factories),
# or opening an article fails with "Dependent class ... is duplicated".
# Keep class identity only; unused classes and members may still be removed,
# and class names may still be obfuscated. Do not add allowoptimization here.
-keep,allowshrinking,allowobfuscation class * implements com.vladsch.flexmark.util.dependency.Dependent
# HTML conversion unwraps Dependent wrappers and keys by the renderer factory class.
-keep,allowshrinking,allowobfuscation class * implements com.vladsch.flexmark.html2md.converter.HtmlNodeRendererFactory

# BitFieldSet reflects public enum fields and passes their names to Enum.valueOf.
# Preserve constants for the enums used as its bit flags, including unused flags
# whose ordinals determine the masks. The enum classes may still be obfuscated.
-keepclassmembers enum com.vladsch.flexmark.util.ast.TextContainer$Flags,
                       com.vladsch.flexmark.util.format.TrackedOffset$Flags,
                       com.vladsch.flexmark.util.sequence.BasedOptionsHolder$Options,
                       com.vladsch.flexmark.util.sequence.LineAppendable$Options,
                       com.vladsch.flexmark.util.sequence.LineInfo$Flags,
                       com.vladsch.flexmark.util.sequence.builder.ISegmentBuilder$Options {
    public static final ** *;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn com.alipay.sdk.app.H5PayCallback
-dontwarn com.alipay.sdk.app.PayTask
-dontwarn com.download.library.DownloadImpl
-dontwarn com.download.library.DownloadListenerAdapter
-dontwarn com.download.library.DownloadTask
-dontwarn com.download.library.ResourceRequest
-dontwarn okhttp3.internal.Util
-dontwarn org.commonmark.ext.gfm.strikethrough.Strikethrough
