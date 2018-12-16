package com.juvetic.rssi.util;

import android.content.Context;
import android.content.SharedPreferences;

public class ToolUtil {

    public static final class Storage {

        public static final String BASE_PREFS_NAME = "analyzer_storage";

        public static void clear() {
            SharedPreferences settings = PageUtil.getInstance().getApplicationContext().getSharedPreferences(
                    BASE_PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.apply();
        }

        public static void setValueString(Context context, String key, String value) {
            if (null == value) {
                return;
            }
            SharedPreferences settings = context.getApplicationContext().getSharedPreferences(
                    BASE_PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(key, value);
            editor.apply();
        }

        public static void setValueBoolean(String key, Boolean value) {
            if (null == value) {
                return;
            }
            SharedPreferences settings = PageUtil.getInstance().getApplicationContext().getSharedPreferences(
                    BASE_PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(key, value);
            editor.apply();
        }

        public static void setValueLong(String key, long value) {
            if (0 == value) {
                return;
            }
            SharedPreferences settings = PageUtil.getInstance().getApplicationContext().getSharedPreferences(
                    BASE_PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong(key, value);
            editor.apply();
        }

        public static long getValueLong(String key) {
            return getValueLong(key, 0);
        }

        public static long getValueLong(String key, long defaultValue) {
            SharedPreferences settings = PageUtil.getInstance().getApplicationContext().getSharedPreferences(
                    BASE_PREFS_NAME, 0);
            return settings.getLong(key, defaultValue);
        }

        public static void setValueInt(String key, int value) {
            if (0 == value) {
                return;
            }
            SharedPreferences settings = PageUtil.getInstance().getApplicationContext().getSharedPreferences(
                    BASE_PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(key, value);
            editor.apply();
        }

        public static int getValueInt(String key) {
            return getValueInt(key, 0);
        }

        public static int getValueInt(String key, int defaultValue) {
            SharedPreferences settings = PageUtil.getInstance().getApplicationContext().getSharedPreferences(
                    BASE_PREFS_NAME, 0);
            return settings.getInt(key, defaultValue);
        }

        public static String getValueString(Context context, String key) {
            return getValueString(context, key, "");
        }

        public static String getValueString(Context context, String key, String defaultValue) {
            SharedPreferences settings = context.getApplicationContext().getSharedPreferences(
                    BASE_PREFS_NAME, 0);
            return settings.getString(key, defaultValue);
        }

        public static Boolean getValueBoolean(String key) {
            return getValueBoolean(key, false);
        }

        public static Boolean getValueBoolean(String key, Boolean defaultValue) {
            SharedPreferences settings = PageUtil.getInstance().getApplicationContext().getSharedPreferences(
                    BASE_PREFS_NAME, 0);
            return settings.getBoolean(key, defaultValue);
        }

    }


}
