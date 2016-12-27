package com.babt.smarthome

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import cn.csnbgsh.herbarium.bind
import com.babt.smarthome.entity.Rooms
import com.cylee.androidlib.base.Callback
import com.cylee.androidlib.net.Net
import com.cylee.androidlib.util.PreferenceUtils
import com.cylee.lib.widget.dialog.DialogUtil
import org.jetbrains.anko.find

/**
 * Created by cylee on 16/9/22.
 */
class RoomSelectActivity :AppBaseActivity() {
    companion object {
        fun createIntent(context : Context): Intent {
            return Intent(context, RoomSelectActivity::class.java);
        }
    }
    var mList : ListView? = null
    var mAdapter:InnerAdapter?=null
    var mRooms: Rooms? = null
    var mNeedStoreData : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_select)
        mList = bind(R.id.ars_list);
        mAdapter = InnerAdapter()
        mList!!.adapter = mAdapter
        mList!!.onItemClickListener = AdapterView.OnItemClickListener {
            adapterView, view, i, l ->
            var item = mRooms?.mRooms?.get(i)
            if (item != null) {
                startActivity(RoomDetailActivity.createIntent(this, item))
            }
        }

        find<View>(R.id.ars_exit).setOnClickListener {
            view -> onBackPressed()
        }

        find<View>(R.id.ars_people).setOnLongClickListener {
            view ->
            dialogUtil.showDialog(this, "", "取消","确定",
                    object : DialogUtil.ButtonClickListener {
                        override fun OnLeftButtonClick() {
                        }

                        override fun OnRightButtonClick() {
                            dialogUtil.showWaitingDialog(this@RoomSelectActivity, "搜索中...")
                            searRooms(true)
                        }
                    },"重新搜索房间?")
            return@setOnLongClickListener true
        }
    }

    override fun onResume() {
        super.onResume()
        searRooms(false)
    }

    fun searRooms(research:Boolean) {
        var status = if (research) 1 else 0
        HomeUtil.postCommand(this, "rooms"+status, object : Callback<String> {
            override fun callback(data: String?) {
                var rooms = Rooms.fromJson(data!!)
                mRooms = rooms
                mAdapter?.notifyDataSetChanged()
            }
        })
//        SocketManager.sendString("ASKCH1", object : TimeCheckSocket.AbsTimeSocketListener() {
//            override fun onError(errorCode: Int) {
//                onUiThread {
//                    dialogUtil.dismissWaitingDialog()
//                    DialogUtil.showToast(this@RoomSelectActivity, "刷新房间数据失败,请退出后重试!", false)
//                }
//            }
//            override fun onSuccess(data: String?) {
//                onUiThread {
//                    dialogUtil.dismissWaitingDialog()
//                    if (data != null && data.matches(Regex("C1=\\w+"))) {
//                        onUiThread {
//                            var rooms = Integer.parseInt(data.subSequence(3, data.length).toString(), 16)
//                            mRooms = Rooms()
//                            mRooms?.mRooms = mutableListOf()
//                            for (i in 1..15) {
//                                if ((rooms and ROOM_MASKS[i]) > 0) {
//                                    var item = Rooms.RoomItem()
//                                    item.id = i
//                                    item.name = "房间"+ NUM_CHS[i]
//                                    mRooms?.mRooms?.add(item)
//                                }
//                            }
//                            mNeedStoreData = true
//                            mAdapter?.notifyDataSetChanged()
//                        }
//                    } else {
//                        DialogUtil.showToast(this@RoomSelectActivity, "控制器异常,请退出后重试!", false)
//                    }
//                }
//            }
//        })
    }

    override fun onStop() {
        super.onStop()
        if (mNeedStoreData) {
            PreferenceUtils.setObject(HomePreference.ROOMS, mRooms)
            mNeedStoreData = false
        }
    }


    inner class InnerAdapter : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            var holder : ViewHolder?;
            var oldView = convertView;
            if (oldView == null) {
                oldView = View.inflate(this@RoomSelectActivity, R.layout.room_select_item, null);
                holder = ViewHolder();
                holder.titleText = oldView!!.bind(R.id.rsi_name)
                holder.selText = oldView!!.bind(R.id.rsi_select_text);
                holder.icon = oldView!!.bind(R.id.rsi_icon);
                oldView.tag = holder;
            } else{
                holder = oldView.tag as ViewHolder;
            }
            var roomItem = mRooms?.mRooms?.get(position) ?: null
            if (roomItem != null) {
                var iconRes = roomItem.iconRes
                holder.icon?.setImageResource(if (iconRes > 0) iconRes else R.drawable.room_1)
                holder.titleText?.setText(roomItem.name)
            }
            return oldView;
        }

        override fun getItem(position: Int): Any? {
            return mRooms?.mRooms?.get(position)
        }

        override fun getItemId(position: Int): Long {
            return  position.toLong();
        }

        override fun getCount(): Int {
            return mRooms?.mRooms?.size ?: 0
        }
    }

    class ViewHolder {
        var selText : TextView? = null
        var titleText : TextView? = null
        var icon : ImageView? = null
    }
}