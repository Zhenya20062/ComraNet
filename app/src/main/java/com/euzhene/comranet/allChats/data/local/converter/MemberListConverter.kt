package com.euzhene.comranet.allChats.data.local.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson

@ProvidedTypeConverter
class MemberListConverter {

    @TypeConverter
    fun toList(value:String):List<String> {
       return Gson().fromJson(value, Array<String>::class.java).toList()
    }
    @TypeConverter
    fun fromList(value:List<String>):String {
       return Gson().toJson(value)
    }
}