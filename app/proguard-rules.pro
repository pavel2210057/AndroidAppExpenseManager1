# support-design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }
-ignorewarnings

# support v4
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }


# support-v7-appcompat
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }
-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

# nineoldandroids
-keep class com.your.package.ClassThatUsesObjectAnimator { *; }

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# butter knife
-keep public class * implements butterknife.Unbinder { public <init>(**, android.view.View); }
-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }

# OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# Okio
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**


# google ads
-keep class com.google.android.gms.internal.** { *; }


#custom views
-keep public class * extends android.view.View {
      public <init>(android.content.Context);
      public <init>(android.content.Context, android.util.AttributeSet);
      public <init>(android.content.Context, android.util.AttributeSet, int);
      public void set*(...);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.content.Context {
    public void *(android.view.View);
    public void *(android.view.MenuItem);
}

# enum
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}



-dontnote java.nio.file.Files, java.nio.file.Path
-dontnote **.ILicensingService
-dontnote sun.misc.Unsafe
-dontwarn sun.misc.Unsafe


-keep class com.pierfrancescosoffritti.androidyoutubeplayer.core.** { *; }

-dontwarn android.databinding.**
-keep class android.databinding.** { *; }


# ProGuard Configuration file
#
# See http://proguard.sourceforge.net/index.html#manual/usage.html

# Needed to keep generic types and @Key annotations accessed via reflection

-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault

-keepclassmembers class * {
  @com.google.api.client.util.Key <fields>;
}

# Needed by google-http-client-android when linking against an older platform version

-dontwarn com.google.api.client.extensions.android.**

# Needed by google-api-client-android when linking against an older platform version

-dontwarn com.google.api.client.googleapis.extensions.android.**

# Needed by google-play-services when linking against an older platform version

-dontwarn com.google.android.gms.**

# com.google.client.util.IOUtils references java.nio.file.Files when on Java 7+
-dontnote java.nio.file.Files, java.nio.file.Path

# Suppress notes on LicensingServices
-dontnote **.ILicensingService

# Suppress warnings on sun.misc.Unsafe
-dontnote sun.misc.Unsafe
-dontwarn sun.misc.Unsafe





-keepclassmembers class com.google.android.gms.dynamite.DynamiteModule {
    ** MODULE_ID;
    ** MODULE_VERSION;
    ** sClassLoader;
}
-keepclassmembers class com.google.android.gms.internal.in {
    ** mOrigin;
    ** mCreationTimestamp;
    ** mName;
    ** mValue;
    ** mTriggerEventName;
    ** mTimedOutEventName;
    ** mTimedOutEventParams;
    ** mTriggerTimeout;
    ** mTriggeredEventName;
    ** mTriggeredEventParams;
    ** mTimeToLive;
    ** mExpiredEventName;
    ** mExpiredEventParams;
}
-keepclassmembers class com.google.devtools.build.android.desugar.runtime.ThrowableExtension {
    ** SDK_INT;
}
-keep class com.google.android.gms.dynamic.IObjectWrapper
-keep class com.google.android.gms.tasks.Task
-keep class com.google.android.gms.tasks.TaskCompletionSource
-keep class com.google.android.gms.tasks.OnSuccessListener
-keep class com.google.android.gms.tasks.OnFailureListener
-keep class com.google.android.gms.tasks.OnCompleteListener
-keep class com.google.android.gms.tasks.Continuation
-keep class com.google.android.gms.measurement.AppMeasurement$EventInterceptor
-keep class com.google.android.gms.measurement.AppMeasurement$OnEventListener
-keep class com.google.android.gms.measurement.AppMeasurement$zza
-keep class com.google.android.gms.internal.zzcgl
-keep class com.google.android.gms.internal.zzbhh
-keep class com.google.android.gms.internal.aad
-keep class com.google.android.gms.internal.aae
-keep class com.google.android.gms.internal.iq
-keep class com.google.android.gms.internal.ly
-keep class com.google.android.gms.internal.kx
-keep class com.google.android.gms.internal.xf
-keep class com.google.android.gms.internal.qu
-keep class com.google.android.gms.internal.qr
-keep class com.google.android.gms.internal.xm
-keep class com.google.android.gms.internal.aaj
-keep class com.google.android.gms.internal.aat
-keep class com.google.android.gms.internal.aah
-keep class com.google.android.gms.internal.rx
-keep class com.google.android.gms.internal.qg
-keep class com.google.android.gms.internal.sh
-keep class com.google.android.gms.internal.qu
-keep class com.google.android.gms.internal.vq
-keep class com.google.android.gms.internal.qi
-keep class com.google.android.gms.internal.oh
-keep class com.google.android.gms.internal.oo
-keep class com.google.android.gms.internal.oc
-keep class com.google.android.gms.internal.oi
-keep class com.google.android.gms.internal.ol
-keep class com.google.android.gms.internal.wn
-keep class com.google.android.gms.internal.oj
-keep class com.google.android.gms.internal.om
-keep class com.google.android.gms.internal.pf
-keep class com.google.android.gms.internal.za
-keep class com.google.android.gms.internal.pz
-keep class com.google.android.gms.internal.zn
-keep class com.google.android.gms.internal.zi
-keep class com.google.android.gms.internal.aen
-keep class com.google.android.gms.internal.aas
-keep class com.google.android.gms.internal.aav
-keep class com.google.android.gms.internal.aag
-keep class com.google.android.gms.internal.abh
-keep class com.google.android.gms.internal.abk
-keep class com.google.android.gms.internal.abq
-keep class com.google.android.gms.internal.abl
-keep class com.google.android.gms.internal.acf
-keep class com.google.android.gms.common.api.Result
-keep class com.google.android.gms.common.zza

-dontnote com.google.android.gms.internal.ql
-dontnote com.google.android.gms.internal.zzcem
-dontnote com.google.android.gms.internal.zzchl

# Firebase notes
-dontnote com.google.firebase.messaging.zza

# Protobuf notes
-dontnote com.google.protobuf.zzc
-dontnote com.google.protobuf.zzd
-dontnote com.google.protobuf.zze

-keep class com.startapp.** {
      *;
}

-keep class com.truenet.** {
      *;
}

-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile, LineNumberTable, *Annotation*, EnclosingMethod
-dontwarn android.webkit.JavascriptInterface
-dontwarn com.startapp.**

-dontwarn org.jetbrains.annotations.**




-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

-dontwarn com.appsflyer.AFKeystoreWrapper
-keep class com.appsflyer.** { *; }

-keep class com.google.android.gms.common.ConnectionResult {
    int SUCCESS;
}

-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {
    com.google.android.gms.ads.identifier.AdvertisingIdClient$Info getAdvertisingIdInfo(android.content.Context);
}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info {
    java.lang.String getId();
    boolean isLimitAdTrackingEnabled();
}
-keep public class com.android.installreferrer.** { *; }

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames class * implements android.os.Parcelable
-keepclassmembers class * implements android.os.Parcelable {
  public static final *** CREATOR;
}

-keep @interface android.support.annotation.Keep
-keep @android.support.annotation.Keep class *


-keep @interface com.google.android.gms.common.annotation.KeepName
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
  @com.google.android.gms.common.annotation.KeepName *;
}

-dontwarn android.security.NetworkSecurityPolicy

#-keep class androidx.appcompat.widget.** { *; }

-dontwarn com.handmark.pulltorefresh.library.*

-dontwarn io.branch.referral.InstallListener$ReferrerClientWrapper
-dontwarn io.branch.indexing.*
-dontwarn io.branch.referral.*
-dontwarn io.branch.indexing.AppIndexingHelper
-dontwarn im.delight.android.webview.*

-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**

-keep class com.google.android.gms.ads.identifier.** { *; }
#-dontwarn com.google.android.gms.**

-keep class com.facebook.applinks.** { *; }
-keepclassmembers class com.facebook.applinks.** { *; }
-keep class com.facebook.FacebookSdk { *; }

# Okio
-dontwarn okio.**

# OK HTTP
-keepattributes Signature
-keepattributes Annotation
-keep class okhttp3.* { *; }
-keep interface okhttp3.* { *; }
-dontwarn okhttp3.**

-keepattributes Signature, InnerClasses, EnclosingMethod

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.-KotlinExtensions

# Google Play Services
-keep class * extends java.util.ListResourceBundle {
    protected java.lang.Object[][] getContents();
}
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
-dontwarn org.apache.http.**
-dontwarn android.security.NetworkSecurityPolicy

# OrmLite uses reflection
-keep class com.j256.**
-keepclassmembers class com.j256.** { *; }
-keep enum com.j256.**
-keepclassmembers enum com.j256.** { *; }
-keep interface com.j256.**
-keepclassmembers interface com.j256.** { *; }

# Keep the helper class and its constructor
-keep class * extends com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
-keepclassmembers class * extends com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper {
  public <init>(android.content.Context);
}

# Keep the annotations
-keepattributes *Annotation*

# Keep all model classes that are used by OrmLite
# Also keep their field names and the constructor
-keep @com.j256.ormlite.table.DatabaseTable class * {
    @com.j256.ormlite.field.DatabaseField <fields>;
    @com.j256.ormlite.field.ForeignCollectionField <fields>;
    # Add the ormlite field annotations that your model uses here
    <init>();
}

-dontwarn com.j256.ormlite.android.**
-dontwarn com.j256.ormlite.logger.**
-dontwarn com.j256.ormlite.misc.**

-keep class com.yandex.metrica. { *; }
-dontwarn com.yandex.metrica.

-dontwarn com.google.errorprone.annotations.**

-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**


-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}