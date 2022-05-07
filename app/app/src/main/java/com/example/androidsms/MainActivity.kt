package com.example.androidsms

import android.database.Cursor
import android.database.SQLException
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        refresh_sms.setOnRefreshListener {
            getSmsList()
        }
        refresh_sms.autoRefresh()
    }

    private fun getSmsList() {
        val smsBuilder = StringBuilder()
        try {
            val uri: Uri = Uri.parse("content://sms/")
            val projection = arrayOf(
                "_id", "address", "person",
                "body", "date", "type"
            )
            var cur: Cursor? = contentResolver.query(
                uri, projection, null,
                null, "date desc"
            ) // 获取手机内部短信
            if (cur!!.moveToFirst()) {
                val index_Address = cur.getColumnIndex("address")
                val index_Person = cur.getColumnIndex("person")
                val index_Body = cur.getColumnIndex("body")
                val index_Date = cur.getColumnIndex("date")
                val index_Type = cur.getColumnIndex("type")
                do {
                    val strAddress = cur.getString(index_Address)
                    val intPerson = cur.getInt(index_Person)
                    val strbody = cur.getString(index_Body)
                    val longDate = cur.getLong(index_Date)
                    val intType = cur.getInt(index_Type)
                    val dateFormat = SimpleDateFormat(
                        "yyyy-MM-dd hh:mm:ss"
                    )
                    val d = Date(longDate)
                    val strDate: String = dateFormat.format(d)
                    var strType = ""
                    strType = when (intType) {
                        1 -> {
                            "接收"
                        }
                        2 -> {
                            "发送"
                        }
                        3 -> {
                            "草稿"
                        }
                        4 -> {
                            "发件箱"
                        }
                        5 -> {
                            "发送失败"
                        }
                        6 -> {
                            "待发送列表"
                        }
                        0 -> {
                            "所以短信"
                        }
                        else -> {
                            "null"
                        }
                    }
                    smsBuilder.append("[ ")
                    smsBuilder.append("$strAddress, ")
                    smsBuilder.append("$intPerson, ")
                    smsBuilder.append("$strbody, ")
                    smsBuilder.append("$strDate, ")
                    smsBuilder.append(strType)
                    smsBuilder.append(" ]\n\n")
                } while (cur.moveToNext())
                if (!cur.isClosed) {
                    cur.close()
                    cur = null
                }
            } else {
                smsBuilder.append("no result!")
            }
        } catch (ex: SQLException) {
            Log.d("SMS", ex.toString())
        }
    }
}