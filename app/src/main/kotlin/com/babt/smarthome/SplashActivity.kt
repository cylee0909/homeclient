package com.babt.smarthome

import android.os.Bundle
import android.text.TextUtils
import com.cylee.androidlib.base.BaseActivity
import com.cylee.androidlib.thread.Worker
import com.cylee.androidlib.util.PreferenceUtils
import com.cylee.androidlib.util.TaskUtils

/**
 * Created by cylee on 16/9/20.
 */
class SplashActivity : BaseActivity() {
    var startWork = object: Worker() {
        override fun work() {
            if (!TextUtils.isEmpty(PreferenceUtils.getString(HomePreference.APPID))) {
                startActivity(MainActivity.createIntent(this@SplashActivity))
            } else {
                startActivity(LoginActivity.createIntent(this@SplashActivity))
            }
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        TaskUtils.postOnMain(startWork, 2000)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        TaskUtils.removePostedWork(startWork)
    }
}