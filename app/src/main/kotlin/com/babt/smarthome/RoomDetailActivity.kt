package com.babt.smarthome

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cn.csnbgsh.herbarium.bind
import com.babt.smarthome.entity.Rooms
import com.babt.smarthome.model.AirCleanData
import com.babt.smarthome.model.ExecModel
import com.babt.smarthome.model.RoomDetailData
import com.cylee.androidlib.base.Callback
import com.cylee.androidlib.net.Net
import com.cylee.androidlib.net.NetError
import com.cylee.lib.widget.dialog.DialogUtil
import org.jetbrains.anko.find

/**
 * Created by cylee on 16/9/27.
 */
class RoomDetailActivity : AppBaseActivity() , View.OnClickListener{
    companion object {
        val INPUT_ROOM_ITEM = "INPUT_ROOM_ITEM"
        fun createIntent(context : Context, roomItem: Rooms.RoomItem): Intent {
            var intent = Intent(context, RoomDetailActivity::class.java)
            intent.putExtra(INPUT_ROOM_ITEM, roomItem)
            return intent
        }
    }
    var mPmTipText : TextView? = null
    var mTimeTip : TextView? = null
    var mTmpText :TextView? = null
    var mHdyText :TextView? = null
    var mFanIcon :ImageView? = null
    var mHighIcon :ImageView? = null
    var mMediumIcon :ImageView? = null
    var mLowIcon:ImageView? = null
    var mStartIcon :ImageView? = null
    var mTitleText : TextView? = null
    var mStarted = false
    var mCurrentId = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_detail)
        mPmTipText = bind(R.id.ard_pm_tip_text)
        mTimeTip = bind(R.id.ard_time_tip_text)
        mTmpText = bind(R.id.ard_tmp_text)
        mHdyText = bind(R.id.ard_hdy_text)
        mFanIcon = bind(R.id.ard_fan_icon)
        mHighIcon = bind(R.id.ard_high_icon)
        mMediumIcon = bind(R.id.ard_medium_icon)
        mLowIcon = bind(R.id.ard_low_icon)
        mStartIcon = bind(R.id.ard_start_icon)
        mTitleText = bind(R.id.ard_title)

        mStartIcon?.setOnClickListener (this )
        mLowIcon?.setOnClickListener (this)
        mMediumIcon?.setOnClickListener(this)
        mHighIcon?.setOnClickListener(this)

        if (intent != null) {
            var item = intent.getSerializableExtra(INPUT_ROOM_ITEM) as Rooms.RoomItem
            if (item != null) {
                mTitleText?.setText(item.name)
                mCurrentId = item.id
            }
        }

        find<View>(R.id.ard_exit).setOnClickListener {
            view -> onBackPressed()
        }
    }

    fun refreshStartState() {
        if (mStarted) {
            mStartIcon?.setImageResource(R.drawable.icon_stop)
        } else{
            mStartIcon?.setImageResource(R.drawable.icon_start)
            mLowIcon?.isEnabled = true
            mMediumIcon?.isEnabled = true
            mHighIcon?.isEnabled = true
        }
    }

    fun getChannelFromPosition(p :Int):Char {
        if (p <= 9) return '0' + (p - 0)
        return 'a' + (p - 10)
    }

    override fun onStart() {
        super.onStart()
        Net.post(this, ExecModel.buidInput("roomDetail"+mCurrentId), object : Net.SuccessListener<ExecModel>() {
            override fun onResponse(response: ExecModel?) {
                if (response !=null && response.result != null) {
                    var roomData = RoomDetailData.fromJson(response.result)
                    if (roomData != null) {
                        mPmTipText?.setText(String.format("PM2.5  %dug/m3", roomData.ipm))
                        mTmpText?.setText(roomData.tmp.toString()+"°C")
                        mHdyText?.setText(roomData.hdy.toString()+"%")
                        mStarted = roomData.mode != 0
                        refreshStartState()
                        refreshLevel(roomData.mode)
                    }
                }
            }
        }, object : Net.ErrorListener() {
            override fun onErrorResponse(e: NetError?) {
                DialogUtil.showToast(this@RoomDetailActivity, "获取数据失败,请稍后重试", false)
            }
        })
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.ard_start_icon -> {
                var command = if (mStarted)  "DESMB" else "SETMB"
                HomeUtil.postCommand(this, command + getChannelFromPosition(mCurrentId), Callback<String> {
                    result ->
                    if ("ok".equals(result, true)) {
                        mStarted = !mStarted
                        refreshStartState()
                    }
                })
            }
            R.id.ard_low_icon -> {
                if (mStarted) {
                    HomeUtil.postCommand(this, "SETMB" + getChannelFromPosition(mCurrentId) + "L", Callback {
                        result ->
                        if ("ok".equals(result, true)) {
                            refreshLevel(3)
                        }
                    })
                } else {
                    DialogUtil.showToast(this, "还未启动,请先启动", false)
                }
            }
            R.id.ard_medium_icon -> {
                if (mStarted) {
                    HomeUtil.postCommand(this, "SETMB" + getChannelFromPosition(mCurrentId) + "M", Callback {
                        result ->
                        if ("ok".equals(result, true)) {
                            refreshLevel(6)
                        }
                    })
                } else {
                    DialogUtil.showToast(this, "还未启动,请先启动", false)
                }
            }
            R.id.ard_high_icon -> {
                if (mStarted) {
                    HomeUtil.postCommand(this, "SETMB" + getChannelFromPosition(mCurrentId) + "H", Callback {
                        result ->
                        if ("ok".equals(result, true)) {
                            refreshLevel(9)
                        }
                    })
                } else {
                    DialogUtil.showToast(this, "还未启动,请先启动", false)
                }
            }
        }
    }

    fun refreshLevel(level : Int) {
        when(level) {
            0 -> {
                mStarted = false
                refreshStartState()
                mLowIcon?.isEnabled = true
                mMediumIcon?.isEnabled = true
                mHighIcon?.isEnabled = true
            }
            3 -> {
                mStarted = true
                refreshStartState()
                mLowIcon?.isEnabled = false
                mMediumIcon?.isEnabled = true
                mHighIcon?.isEnabled = true
            }
            6 -> {
                mStarted = true
                refreshStartState()
                mLowIcon?.isEnabled = true
                mMediumIcon?.isEnabled = false
                mHighIcon?.isEnabled = true
            }
            9 -> {
                mStarted = true
                refreshStartState()
                mLowIcon?.isEnabled = true
                mMediumIcon?.isEnabled = true
                mHighIcon?.isEnabled = false
            }
        }
    }
}
