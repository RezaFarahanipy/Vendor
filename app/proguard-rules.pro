# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Room Database
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>();
}
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Keep Data Models
-keep class com.example.data.model.** { *; }

# Keep Utilities
-keep class com.example.util.** { *; }

# Keep AndroidX Biometric & Security
-keep class androidx.biometric.** { *; }
