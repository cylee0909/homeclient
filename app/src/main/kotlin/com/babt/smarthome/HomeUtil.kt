package com.babt.smarthome

import android.text.TextUtils
import com.babt.smarthome.model.ExecModel
import com.cylee.androidlib.base.BaseActivity
import com.cylee.androidlib.base.Callback
import com.cylee.androidlib.net.Net
import com.cylee.androidlib.net.NetError
import com.cylee.androidlib.util.PreferenceUtils
import com.cylee.lib.widget.dialog.DialogUtil
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by cylee on 16/10/9.
 */
object HomeUtil {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    val timeFormat = SimpleDateFormat("HH:mm")
    val WEEKS = arrayOf("一", "二","三", "四", "五", "六", "日")
    fun getWeek(weekInt : Int) : String{
        var newWeekInt = Math.min(Math.max(weekInt, 0), 6)
        return WEEKS[newWeekInt]
    }

    fun getNextDateByWeek(weekInt: Int, time : String) : String {
        var current = Date()
        var cal = Calendar.getInstance()
        cal.time = current
        var currentWeek = cal.get(Calendar.DAY_OF_WEEK) -2
        if (currentWeek == -1) currentWeek = 6 // 周日
        var minDay = Int.MAX_VALUE
        var minWeek = currentWeek
        for (i in 0 .. WEEKS.size) {
            if (weekInt and (1 shl i) > 0) {
                var diff = i - currentWeek
                if (diff < 0) {
                    diff += 7
                }
                if (diff == 0) { // 是同一天,判断时间
                    var timeDate = timeFormat.parse(time)
                    var currentTime = timeFormat.parse(timeFormat.format(current))
                    if (timeDate.before(currentTime)) {
                        diff += 7
                    }
                }
                if (minDay > diff) {
                    minDay = diff
                    minWeek = i
                }
            }
        }
        cal.add(Calendar.DAY_OF_YEAR, minDay)
        return dateFormat.format(cal.time)
    }

    fun getAllWeek(weekInt: Int) : String{
        var result = ""
        if (weekInt > 0) {
            for (i in 0 .. WEEKS.size) {
                if (weekInt and (1 shl i) > 0) {
                    result += getWeek(i)+","
                }
            }
        }
        if (result.endsWith(",")) {
            result = result.substring(0, result.length - 1)
        }
        return result
    }

    fun getChannelFromId(p :Int):Char {
        if (p <= 9) return '0' + (p - 0)
        return 'a' + (p - 10)
    }

    fun getPmSetLevel(value : Int):Char {
        if (value <= 9) return '0' + (value - 0)
        return 'a' + (value - 10)
    }

    inline fun postCommand(activity:BaseActivity, command:String, runnable: Runnable?) {
        activity.dialogUtil.showWaitingDialog(activity,"正在操作...")
        Net.post(activity, ExecModel.buidInput(command), object : Net.SuccessListener<ExecModel>() {
            override fun onResponse(response: ExecModel?) {
                activity.dialogUtil.dismissWaitingDialog()
                if (response != null && "ok".equals(response.result, false)) {
                    if (runnable != null) {
                        runnable.run()
                    }
                } else{
                    DialogUtil.showToast(activity, "操作失败", false)
                }
            }
        }, object : Net.ErrorListener() {
            override fun onErrorResponse(e: NetError?) {
                activity.dialogUtil.dismissWaitingDialog()
            }
        })
    }

    inline fun postCommand(activity:BaseActivity, command:String, callback: Callback<String>?, quite:Boolean = false) {
        if (!quite)activity.dialogUtil.showWaitingDialog(activity,"正在操作...")
        Net.post(activity, ExecModel.buidInput(command), object : Net.SuccessListener<ExecModel>() {
            override fun onResponse(response: ExecModel?) {
                if (!quite) {
                    activity.dialogUtil.dismissWaitingDialog()
                }
                if (response != null && !TextUtils.isEmpty(response.result)) {
                    if (callback != null) {
                        callback.callback(response.result)
                    }
                } else{
                    if (!quite) {
                        DialogUtil.showToast(activity, "操作失败", false)
                    }
                }
            }
        }, object : Net.ErrorListener() {
            override fun onErrorResponse(e: NetError?) {
                if (!quite){
                    activity.dialogUtil.dismissWaitingDialog()
                    DialogUtil.showToast(activity, "请求失败", false)
                }
            }
        })
    }
}


inline fun Number.toMinus() : Number {
    return this.toLong() / 1000 / 60
}