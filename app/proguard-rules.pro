# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keep public class com.euzhene.comranet.allChats.data.model.*
-keep public class com.euzhene.comranet.allChats.data.local.model.*
-keep public class com.euzhene.comranet.chatRoom.data.local.model.*
-keep public class com.euzhene.comranet.chatRoom.data.remote.dto.*

-keepclassmembers class com.google.firebase.database.GenericTypeIndicator { *; }
-keep class * extends com.google.firebase.database.GenericTypeIndicator { *; }

#-keepclassmembers class * extends java.lang.Enum {
#    <fields>;
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
-keep public enum com.euzhene.comranet.chatRoom.domain.entity.ChatDataType$** {
    **[] $VALUES;
    public *;
}

#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile