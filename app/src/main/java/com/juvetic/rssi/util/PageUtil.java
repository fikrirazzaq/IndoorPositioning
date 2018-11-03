package com.juvetic.rssi.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.juvetic.rssi.ui.DashboardActivity;

public class PageUtil {

    public static final String kPageJumpIntentDataKey = "Datas";

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

    /**
     * 页面跳转并弹出栈其他对象
     *
     * @param srcClass
     * @param cls
     */
    public void jumpToPageAndPop(Activity srcClass, Class<?> cls) {
        Intent intent = new Intent(srcClass, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        srcClass.startActivity(intent);
        srcClass.finish();
    }

    /**
     * 页面跳转
     *
     * @param srcClass 原界面
     * @param cls      跳转界面
     */
    public void jumpToPage(Context srcClass, Class<?> cls) {
        Intent intent = new Intent(srcClass, cls);
        srcClass.startActivity(intent);
    }

    /**
     * @param srcClass
     * @param cls
     * @param flags    配置参数
     */
    public void jumpToPage(Context srcClass, Class<?> cls, int flags) {
        Intent intent = new Intent(srcClass, cls);
        intent.setFlags(flags);
        srcClass.startActivity(intent);
    }

    public void invalidTokenJump(Context srcClass, Class<?> cls, int flags) {
        Intent intent = new Intent(srcClass, cls);
        intent.setFlags(flags);
        intent.putExtra("invalidtoken", "true");
        srcClass.startActivity(intent);
    }

    /**
     * 带返回的页面跳转
     *
     * @param srcClass
     * @param cls
     * @param requestCode
     */
    public void jumpToPageForResult(Activity srcClass, Class<?> cls,
                                    int requestCode) {
        Intent intent = new Intent(srcClass, cls);
        srcClass.startActivityForResult(intent, requestCode);
    }

    /**
     * 无论前台或后台，都会清除全部页面后切换到前台
     */
    public void jumpToMain() {
        Intent intent = new Intent(applicationContext, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        applicationContext.startActivity(intent);
    }

    /**
     * 跳转到webview
     *
     * @param activity
     * @param cls
     * @param title
     * @param url
     */
    public void jumpToWebView(Activity activity, Class<?> cls, String title, String url) {
        Intent intent = new Intent(activity, cls);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        activity.startActivity(intent);
    }

    public void jumpToTcashWebView(Activity activity, Class<?> cls, String title, String url, String token) {
        Intent intent = new Intent(activity, cls);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        intent.putExtra("token", token);
        activity.startActivity(intent);
    }

    public void jumpToWelcome(Activity activity) {
        Intent intent = new Intent(activity, DashboardActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

}
