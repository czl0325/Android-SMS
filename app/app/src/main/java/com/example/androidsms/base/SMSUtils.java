package com.example.androidsms.base;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class SMSUtils {
    public interface OnPhoneNumber {
        void onSuccess(String number);
    }
    public static void getNumber(Context context, OnPhoneNumber callback) {
        XXPermissions.with(context).permission(Permission.READ_SMS, Permission.READ_PHONE_NUMBERS, Permission.READ_PHONE_STATE).request((permissions, all) -> {
            if (all) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                String phoneNumber1 = tm.getLine1Number();
                if (callback != null) {
                    callback.onSuccess(phoneNumber1);
                }
            }
        });
    }

    public static boolean isDoubleTelephone(Context context)
    {
        boolean isDouble = true;
        Method method = null;
        Object result_0 = null;
        Object result_1 = null;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try
        {
            // 只要在反射getSimStateGemini 这个函数时报了错就是单卡手机（这是我自己的经验，不一定全正确）
            method = TelephonyManager.class.getMethod("getSimStateGemini", new Class[]
                    { int.class });
            // 获取SIM卡1
            result_0 = method.invoke(tm, new Object[]
                    { new Integer(0) });
            // 获取SIM卡2
            result_1 = method.invoke(tm, new Object[]
                    { new Integer(1) });
        } catch (SecurityException e)
        {
            isDouble = false;
            e.printStackTrace();
        } catch (NoSuchMethodException e)
        {
            isDouble = false;
            e.printStackTrace();
        } catch (IllegalArgumentException e)
        {
            isDouble = false;
            e.printStackTrace();
        } catch (IllegalAccessException e)
        {
            isDouble = false;
            e.printStackTrace();
        } catch (InvocationTargetException e)
        {
            isDouble = false;
            e.printStackTrace();
        } catch (Exception e)
        {
            isDouble = false;
            e.printStackTrace();
        }
        return isDouble;
    }
}
