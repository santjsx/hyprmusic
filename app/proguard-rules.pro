# ProGuard & R8 Optimization Rules for HyprMusic

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep class kotlinx.serialization.** { *; }

# App Domain Data Models
-keep class com.example.hyprmusic.core.model.** { *; }
-keep class com.example.hyprmusic.core.data.CustomPlaylist { *; }
-keep class com.example.hyprmusic.core.cloud.telegram.TelegramServerSettings { *; }
-keep class com.example.hyprmusic.core.cloud.telegram.TelegramServerHealth { *; }
-keep class com.example.hyprmusic.core.cloud.telegram.CloudTrackMeta { *; }
-keep class com.example.hyprmusic.core.cloud.telegram.TelegramSyncPayload { *; }

# AndroidX Media3 / ExoPlayer
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# Coil Image Loader
-keep class coil.** { *; }
-dontwarn coil.**

# OkHttp & Okio
-dontwarn okhttp3.**
-dontwarn okio.**

# Compose Runtime Markers
-keep class androidx.compose.runtime.Immutable
