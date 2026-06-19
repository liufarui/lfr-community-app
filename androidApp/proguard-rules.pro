-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

# Kotlin Serialization
-keepclassmembers class com.lfr.community.data.model.** {
    *;
}
-keep,includedescriptorclasses class com.lfr.community.**$$serializer { *; }
-keepclassmembers class com.lfr.community.** {
    *** Companion;
}

# Ktor
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
