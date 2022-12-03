-keepclassmembers class com.google.firebase.database.GenericTypeIndicator { *; }
-keep class * extends com.google.firebase.database.GenericTypeIndicator { *; }

#-keep public enum com.euzhene.comranet.chatRoom.domain.entity.**{
#    *;
#}

#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}