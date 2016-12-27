package com.babt.smarthome

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import cn.csnbgsh.herbarium.bind
import com.babt.smarthome.model.Login
import com.babt.smarthome.util.EncryptUtil
import com.cylee.androidlib.net.Net
import com.cylee.androidlib.net.NetError
import com.cylee.androidlib.util.PreferenceUtils
import com.cylee.lib.widget.dialog.DialogUtil

/**
 * Created by cylee on 16/12/25.
 */
class LoginActivity : AppBaseActivity() {
    companion object {
        fun createIntent(context : Context): Intent {
            return Intent(context, LoginActivity::class.java);
        }
    }

    var loginName : EditText? = null
    var loginPassd : EditText? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginName = bind(R.id.ai_login_name)
        loginPassd = bind(R.id.ai_login_passwd)
        bind<View>(R.id.ai_confirm_text).setOnClickListener {
            if (TextUtils.isEmpty(loginName?.text.toString())) {
                DialogUtil.showToast(this, "登陆用户名未填写", false)
                return@setOnClickListener
            }

            dialogUtil.showWaitingDialog(this, "正在连接...")
            Net.post(this, Login.buidInput(loginName?.text.toString(), EncryptUtil.getVerify(loginPassd?.text.toString())), object : Net.SuccessListener<Login>() {
                override fun onResponse(response: Login?) {
                    dialogUtil.dismissWaitingDialog()
                    if (response != null) {
                        PreferenceUtils.setString(HomePreference.APPID, response.id)
                        DialogUtil.showToast(this@LoginActivity, "登陆成功", false)
                        startActivity(MainActivity.createIntent(this@LoginActivity))
                        this@LoginActivity.finish()
                    } else {
                        DialogUtil.showToast(this@LoginActivity, "登陆失败", false)
                    }
                }
            }, object : Net.ErrorListener() {
                override fun onErrorResponse(e: NetError?) {
                    dialogUtil.dismissWaitingDialog()
                    DialogUtil.showToast(this@LoginActivity, "登陆失败", false)
                }
            })
        }
    }
}