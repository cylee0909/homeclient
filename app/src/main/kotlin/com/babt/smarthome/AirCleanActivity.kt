package com.babt.smarthome

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import cn.csnbgsh.herbarium.bind
import com.babt.smarthome.model.AirCleanData
import com.babt.smarthome.model.ExecModel
import com.cylee.androidlib.base.BaseActivity
import com.cylee.androidlib.base.Callback
import com.cylee.androidlib.net.Net
import com.cylee.androidlib.net.NetError
import com.cylee.androidlib.util.PreferenceUtils
import com.cylee.lib.widget.dialog.DialogUtil
import org.jetbrains.anko.find

/**
 * Created by cylee on 16/9/22.
 */
class AirCleanActivity :BaseActivity() , View.OnClickListener {
    companion object {
        fun createIntent(context : Context): Intent {
            return Intent(context, AirCleanActivity::class.java);
        }
    }

    var mAutoContainer : LinearLayout? = null
    var mHeatContainer : LinearLayout? = null
    var mSelectContainer : LinearLayout? = null
    var mTmpText : TextView? = null
    var mHdyText : TextView? = null
    var mPmText : TextView ?= null
    var mOutPmText : TextView ?= null
    var paused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_air_clean)
        mAutoContainer = bind(R.id.ac_auto_run_container)
        mHeatContainer = bind(R.id.ac_heat_container)
        mSelectContainer = bind(R.id.ac_select_container)

        mTmpText = bind(R.id.aac_tmp_tip)
        mHdyText = bind(R.id.aac_hdy_tip)
        mPmText = bind(R.id.aac_pm_tip)
        mOutPmText = bind(R.id.out_pm_text)

        mAutoContainer!!.setOnClickListener(this)
        mHeatContainer!!.setOnClickListener(this)
        mSelectContainer!!.setOnClickListener(this)

        find<View>(R.id.aac_exit).setOnClickListener {
            view -> onBackPressed()
        }

        if (PreferenceUtils.getBoolean(HomePreference.AUTO_RUN)) {
            mAutoContainer?.isSelected = true
        } else{
            mAutoContainer?.isSelected = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ac_auto_run_container -> {
                var stat = if (mAutoContainer!!.isSelected) 0 else 1
                HomeUtil.postCommand(this, "autorun"+stat, object : Runnable {
                    override fun run() {
                        mAutoContainer?.isSelected = (!(mAutoContainer?.isSelected ?: false))
                        var autoRun = mAutoContainer?.isSelected ?: false
                        PreferenceUtils.setBoolean(HomePreference.AUTO_RUN, autoRun)
                        DialogUtil.showToast(this@AirCleanActivity,
                                "自动运行"+(if (autoRun) "打开" else "关闭")
                                , false)
                    }
                })
            }
            R.id.ac_heat_container -> {
                var command = if (mHeatContainer?.isSelected ?: false) "Close3" else "Open_3"
                HomeUtil.postCommand(this, command, object : Callback<String>{
                    override fun callback(data: String?) {
                        mHeatContainer?.isSelected = !(mHeatContainer?.isSelected ?: false)
                        DialogUtil.showToast(this@AirCleanActivity,
                                "加热模式已"+(if (mHeatContainer?.isSelected ?: false) "打开" else "关闭")
                                , false)
                    }
                })
            }
            R.id.ac_select_container -> {
                startActivity(RoomSelectActivity.createIntent(this))
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        paused = false
        Net.post(this, ExecModel.buidInput("airclean"), object : Net.SuccessListener<ExecModel>() {
            override fun onResponse(response: ExecModel?) {
                if (response !=null && response.result != null) {
                    var airData = AirCleanData.fromJson(response.result)
                    if (airData != null) {
                        mOutPmText?.text = "室外\n"+airData.opm+"ug/m3"
                        mTmpText?.setText(airData.tmp.toString()+"°C")
                        mHdyText?.setText(airData.hdy.toString()+"%")
                        mPmText?.setText(String.format("PM2.5  %dug/m3", airData.ipm))
                        mHeatContainer?.isSelected = airData.heat
                        mAutoContainer?.isSelected = airData.autoRun
                    }
                }
            }
        }, object : Net.ErrorListener() {
            override fun onErrorResponse(e: NetError?) {

            }
        })
    }

    override fun onPause() {
        super.onPause()
        paused = true
    }
}