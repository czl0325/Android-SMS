package com.example.androidsms

import android.database.Cursor
import android.database.DataSetObserver
import android.database.SQLException
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.TimeUtils
import com.example.androidsms.models.SMSInfo
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_sms.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private val smsList = ArrayList<SMSInfo>()
    private val adapterSMS by lazy { SMSAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler_sms.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recycler_sms.addItemDecoration(dividerItemDecoration)
        recycler_sms.adapter = adapterSMS

        refresh_sms.setOnRefreshListener {
            getSmsList()
        }
        refresh_sms.autoRefresh()
    }

    private fun getSmsList() {
        XXPermissions.with(this).permission(Permission.READ_SMS).request { permissions, all ->
            if (all) {
                val smsBuilder = StringBuilder()
                try {
                    val uri: Uri = Uri.parse("content://sms/")
                    val projection = arrayOf(
                        "_id", "address", "person",
                        "body", "date", "type"
                    )
                    var cur: Cursor? = contentResolver.query(uri, projection, null, null, "date desc") // 获取手机内部短信
                    if (cur!!.moveToFirst()) {
                        val address = cur.getColumnIndex("address")
                        val person = cur.getColumnIndex("person")
                        val body = cur.getColumnIndex("body")
                        val date = cur.getColumnIndex("date")
                        val type = cur.getColumnIndex("type")
                        smsList.clear()
                        do {
                            var smsInfo: SMSInfo = SMSInfo()
                            smsInfo.sender = cur.getString(address)
                            smsInfo.content = cur.getString(body)
                            smsInfo.date = TimeUtils.millis2Date(cur.getLong(date))
                            smsInfo.isRead = cur.getInt(person) == 1
                            smsList.add(smsInfo)
                        } while (cur.moveToNext())
                        adapterSMS.notifyDataSetChanged()
                        if (!cur.isClosed) {
                            cur.close()
                        }
                    }
                } catch (ex: SQLException) {
                    Log.d("SMS", ex.toString())
                }
            }
            refresh_sms.finishRefresh()
        }
    }

    inner class SMSAdapter : RecyclerView.Adapter<SMSAdapter.SMSViewHolder>() {
        inner class SMSViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SMSViewHolder {
            return SMSViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_sms, parent, false))
        }

        override fun onBindViewHolder(holder: SMSViewHolder, position: Int) {
            val info = smsList.get(position)
            holder.itemView.tv_sender.text = info.sender
            holder.itemView.tv_date.text = TimeUtils.date2String(info.date)
            holder.itemView.tv_content.text = info.content
        }

        override fun getItemCount(): Int = smsList.size
    }
}