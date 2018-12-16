package com.juvetic.rssi.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.juvetic.rssi.ui.DashboardActivity;

public class PageUtil {

    private static PageUtil instance;

    public static PageUtil getInstance() {
        if (instance == null) {
            synchronized (PageUtil.class) {
                if (instance == null) {
                    instance = new PageUtil();
                }
            }
        }
        return instance;
    }

    public Context getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    private Context applicationContext = null;

    public void jumpToPage(Context srcClass, Class<?> cls) {
        Intent intent = new Intent(srcClass, cls);
        srcClass.startActivity(intent);
    }
}
