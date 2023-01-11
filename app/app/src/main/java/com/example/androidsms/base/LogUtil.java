package com.example.androidsms.base;

import android.util.Log;

import com.example.androidsms.BuildConfig;

public class LogUtil {
    private static final String TAG = "czl";
    public static boolean isDebug = BuildConfig.DEBUG;

    public static void d(String TAG, String s) {
        if (isDebug)
            LogAll(s);
    }

    public static void d(String s) {
        if (isDebug) {
            LogAll(s);
        }
    }

    public static void e(String s) {
        Log.e(TAG, "-------------->" + s);
    }


    /**
     * 最大一次打印长度
     */
    public final static int MAX_LENGTH = 3000;

    /**
     * 适应最大长度打印
     *
     * @param msg 信息
     */
    public static void LogAll(String msg) {
        if (msg.length() > MAX_LENGTH) {
            int length = MAX_LENGTH + 1;
            String remain = msg;
            int index = 0;
            while (length > MAX_LENGTH) {
                index++;
                Log.d(TAG, "-------------->" + "[" + index + "]" + " \n" + remain.substring(0, MAX_LENGTH));
                remain = remain.substring(MAX_LENGTH);
                length = remain.length();
            }
            if (length <= MAX_LENGTH) {
                index++;
                Log.d(TAG, "-------------->" + "[" + index + "]" + " \n" + remain);
            }
        } else {
            Log.d(TAG, "-------------->" + msg);
        }
    }
}

