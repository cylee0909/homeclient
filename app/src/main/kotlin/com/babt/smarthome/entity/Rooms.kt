package com.babt.smarthome.entity

import com.cylee.androidlib.GsonBuilderFactory
import java.io.Serializable

/**
 * Created by cylee on 16/9/26.
 */
class Rooms : Serializable {
    companion object {
        fun fromJson(str:String) : Rooms{
            return GsonBuilderFactory.createBuilder().fromJson(str, Rooms::class.java)
        }
    }

    var mRooms : MutableList<RoomItem>? = null

    class RoomItem : Serializable {
        var name : String? = ""
        var id : Int = -1
        var iconRes : Int = 0
    }
}