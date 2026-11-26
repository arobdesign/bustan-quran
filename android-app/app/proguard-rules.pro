# R8 / ProGuard rules for Bustan Qur'an (REL-02)
-keepattributes SourceFile,LineNumberTable
-keep class com.bustanquran.core.data.database.model.** { *; }
-keep class com.bustanquran.core.data.preferences.** { *; }
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-dontwarn com.android.billingclient.**
-keep class com.android.billingclient.api.** { *; }
