package com.babt.smarthome

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import cn.csnbgsh.herbarium.bind
import com.cylee.androidlib.base.Callback
import com.cylee.androidlib.util.DateUtils
import com.cylee.androidlib.util.PreferenceUtils
import com.cylee.androidlib.util.SimpleLunarCalendar
import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppBaseActivity() {
    companion object {
        val DATE_FORMAT = SimpleDateFormat("yyyy年MM月dd日")
        fun createIntent(context : Context):Intent {
            return Intent(context, MainActivity::class.java);
        }
    }

    var mDateText : TextView? = null
    var mDateCnText : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        find<LinearLayout>(R.id.air_clean_container).setOnClickListener {
           v -> startActivity(AirCleanActivity.createIntent(this))
        }
        find<View>(R.id.am_exit).setOnClickListener {
            view -> onBackPressed()
        }

        mDateText = bind(R.id.am_date)
        mDateCnText = bind(R.id.am_date_cn)
        var date = Date()
        mDateCnText?.setText("农历"+SimpleLunarCalendar(date).noYearDate+"    "+DateUtils.getWeekOfDate(date))
        mDateText?.setText(DATE_FORMAT.format(date))
    }

    override fun onResume() {
        super.onResume()
        HomeUtil.postCommand(this, "filterTime", object : Callback<String> {
            override fun callback(data: String?) {
                try {
                    var value = data?.toLong() ?: 0L
                    PreferenceUtils.setLong(HomePreference.CHANGE_FILTER_USED_TIME, value)
                    PreferenceUtils.setLong(HomePreference.CHANGE_FILTER_TIP_TIME, System.currentTimeMillis())
                } catch (e:Exception){
                }
            }
        }, true)
    }
}
