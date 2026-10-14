# ============================================================
# ROYALTREE PROGUARD RULES
# ============================================================

# ============ GENERAL ============
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# ============ FIREBASE ============
-keep class com.google.firebase.auth.** { *; }
-keep class com.google.firebase.auth.internal.** { *; }
-keep class com.google.firebase.firestore.** { *; }
-keep class com.google.firebase.firestore.model.** { *; }
-dontwarn com.google.firebase.firestore.**
-keep class com.google.firebase.messaging.** { *; }
-keep class com.google.firebase.iid.** { *; }
-dontwarn com.google.firebase.messaging.**
-keep class com.google.firebase.crashlytics.** { *; }
-dontwarn com.google.firebase.crashlytics.**
-keep class com.google.firebase.appcheck.** { *; }

# ============ GOOGLE PLAY SERVICES ============
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.android.gms.ads.identifier.** { *; }
-dontwarn com.google.android.gms.ads.**
-keep class com.google.android.gms.location.** { *; }

# ============ RETROFIT + OKHTTP ============
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keep,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn javax.annotation.**

# ============ MOSHI ============
-keep class com.squareup.moshi.** { *; }
-keep class com.squareup.moshi.kotlin.reflect.** { *; }
-keep @com.squareup.moshi.JsonQualifier @interface *
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
}
-keep @com.squareup.moshi.JsonClass class * { *; }
-keepclassmembers @com.squareup.moshi.JsonClass class * { *; }
-keepattributes Signature

# ============ KOTLINX COROUTINES ============
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }
-dontwarn kotlinx.coroutines.**

# ============ KOTLIN ============
-dontwarn kotlin.**
-keep class kotlin.Metadata { *; }

# ============ ANDROIDX ============
-keep class androidx.work.** { *; }
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.ListenableWorker { *; }
-dontwarn androidx.work.**
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}
-keep class androidx.datastore.** { *; }

# ============ PROJECT MODELS ============
-keep class com.inkside.digital.data.model.** { *; }
-keep class com.inkside.digital.data.network.model.** { *; }

# ============ KOTLIN REFLECTION ============
-keep class kotlin.reflect.** { *; }
-keep class kotlin.jvm.internal.** { *; }

# ============ ENUM ============
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ============ NATIVE METHODS ============
-keepclasseswithmembernames class * {
    native <methods>;
}

# ============ PARCELABLE ============
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# ============ SERIALIZABLE ============
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ============ SUPPRESS WARNINGS ============
-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**
-dontwarn java.lang.invoke.**
-dontwarn sun.misc.**
