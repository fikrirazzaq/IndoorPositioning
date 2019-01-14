package com.juvetic.rssi.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.google.common.collect.EvictingQueue;
import com.juvetic.rssi.util.TinyDB;
import com.juvetic.rssi.util.ToolUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class BaseActivity extends AppCompatActivity {

    public static final int MAX_X = 725;

    public static final int MIN_X = -125;

    public static final int MIN_Y = 0;

    public static final int MAX_Y = 1325;

    TinyDB tinydb;

    String x1, y1, x2, y2, x3, y3,
            d1, d2, d3,
            xPos, yPos, xPosKalman1, yPosKalman1, xPosKalman2, yPosKalman2, xPosFeedback, yPosFeedback,
            bssid1, bssid2, bssid3,
            noise, n, alpha;

    double variansiAp1TypeA = 0;

    double variansiAp2TypeA = 0;

    double variansiAp3TypeA = 0;

    double variansiAp1TypeB = 0;

    double variansiAp2TypeB = 0;

    double variansiAp3TypeB = 0;

    double preRssiAp1KFTypeB = 0;

    double preRssiAp2KFTypeB = 0;

    double preRssiAp3KFTypeB = 0;

    int iAp1 = 0;

    int iAp2 = 0;

    int iAp3 = 0;

    Queue<Double> rssiKFQueueAp1 = EvictingQueue.create(10);

    Queue<Double> rssiKFQueueAp2 = EvictingQueue.create(10);

    Queue<Double> rssiKFQueueAp3 = EvictingQueue.create(10);

    ArrayList<Double> rssiListAp1 = new ArrayList<>();

    ArrayList<Double> rssiListAp2 = new ArrayList<>();

    ArrayList<Double> rssiListAp3 = new ArrayList<>();

    ArrayList<Double> rssiKFListAp1 = new ArrayList<>();

    ArrayList<Double> rssiKFListAp2 = new ArrayList<>();

    ArrayList<Double> rssiKFListAp3 = new ArrayList<>();

    ArrayList<Double> rssiKFListAp1v2 = new ArrayList<>();

    ArrayList<Double> rssiKFListAp2v2 = new ArrayList<>();

    ArrayList<Double> rssiKFListAp3v2 = new ArrayList<>();

    ArrayList<Double> rssiFBListAp1 = new ArrayList<>();

    ArrayList<Double> rssiFBListAp2 = new ArrayList<>();

    ArrayList<Double> rssiFBListAp3 = new ArrayList<>();

    ArrayList<Double> kfAlgoAp1TypeA = new ArrayList<>();

    ArrayList<Double> kfAlgoAp2TypeA = new ArrayList<>();

    ArrayList<Double> kfAlgoAp3TypeA = new ArrayList<>();

    ArrayList<Double> kfAlgoAp1TypeB = new ArrayList<>();

    ArrayList<Double> kfAlgoAp2TypeB = new ArrayList<>();

    ArrayList<Double> kfAlgoAp3TypeB = new ArrayList<>();

    ArrayList<Double> fbAlgoAp1 = new ArrayList<>();

    ArrayList<Double> fbAlgoAp2 = new ArrayList<>();

    ArrayList<Double> fbAlgoAp3 = new ArrayList<>();

    ArrayList<Long> xRaw = new ArrayList<>();

    ArrayList<Long> yRaw = new ArrayList<>();

    ArrayList<Long> xKF1 = new ArrayList<>();

    ArrayList<Long> yKF1 = new ArrayList<>();

    ArrayList<Long> xKF2 = new ArrayList<>();

    ArrayList<Long> yKF2 = new ArrayList<>();

    ArrayList<Long> xFB = new ArrayList<>();

    ArrayList<Long> yFB = new ArrayList<>();

    List<Double> xy;

    List<Double> xyKalman1;

    List<Double> xyKalman2;

    List<Double> xyFeedback;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();

        tinydb = new TinyDB(this);

        variansiAp1TypeA = Double.parseDouble(ToolUtil.Storage.getValueString(this, "var_kalman_ap1_type_a", "0"));
        variansiAp2TypeA = Double.parseDouble(ToolUtil.Storage.getValueString(this, "var_kalman_ap2_type_a", "0"));
        variansiAp3TypeA = Double.parseDouble(ToolUtil.Storage.getValueString(this, "var_kalman_ap3_type_a", "0"));

        variansiAp1TypeB = Double.parseDouble(ToolUtil.Storage.getValueString(this, "var_kalman_ap1_type_b", "0"));
        variansiAp1TypeB = Double.parseDouble(ToolUtil.Storage.getValueString(this, "var_kalman_ap2_type_b", "0"));
        variansiAp1TypeB = Double.parseDouble(ToolUtil.Storage.getValueString(this, "var_kalman_ap3_type_b", "0"));

        preRssiAp1KFTypeB = Double.parseDouble(ToolUtil.Storage.getValueString(this, "pre_rssi_ap1", "0"));
        preRssiAp2KFTypeB = Double.parseDouble(ToolUtil.Storage.getValueString(this, "pre_rssi_ap2", "0"));
        preRssiAp3KFTypeB = Double.parseDouble(ToolUtil.Storage.getValueString(this, "pre_rssi_ap3", "0"));

        iAp1 = ToolUtil.Storage.getValueInt(this, "i_kalman_ap1", 0);
        iAp2 = ToolUtil.Storage.getValueInt(this, "i_kalman_ap2", 0);
        iAp3 = ToolUtil.Storage.getValueInt(this, "i_kalman_ap3", 0);

        x1 = ToolUtil.Storage.getValueString(this, "x1", "");
        y1 = ToolUtil.Storage.getValueString(this, "y1", "");
        x2 = ToolUtil.Storage.getValueString(this, "x2", "");
        y2 = ToolUtil.Storage.getValueString(this, "y2", "");
        x3 = ToolUtil.Storage.getValueString(this, "x3", "");
        y3 = ToolUtil.Storage.getValueString(this, "y3", "");
        d1 = ToolUtil.Storage.getValueString(this, "d1", "");
        d2 = ToolUtil.Storage.getValueString(this, "d2", "");
        d3 = ToolUtil.Storage.getValueString(this, "d3", "");
        xPos = ToolUtil.Storage.getValueString(this, "xPos", "");
        yPos = ToolUtil.Storage.getValueString(this, "yPos", "");
        xPosKalman1 = ToolUtil.Storage.getValueString(this, "xPosKalman1", "");
        yPosKalman1 = ToolUtil.Storage.getValueString(this, "yPosKalman1", "");
        xPosKalman2 = ToolUtil.Storage.getValueString(this, "xPosKalman2", "");
        yPosKalman2 = ToolUtil.Storage.getValueString(this, "yPosKalman2", "");
        xPosFeedback = ToolUtil.Storage.getValueString(this, "xPosFeedback", "");
        yPosFeedback = ToolUtil.Storage.getValueString(this, "yPosFeedback", "");
        bssid1 = ToolUtil.Storage.getValueString(this, "Bssid1", "");
        bssid2 = ToolUtil.Storage.getValueString(this, "Bssid2", "");
        bssid3 = ToolUtil.Storage.getValueString(this, "Bssid3", "");

        noise = ToolUtil.Storage.getValueString(this, "noise", "");
        n = ToolUtil.Storage.getValueString(this, "n", "");
        alpha = ToolUtil.Storage.getValueString(this, "alpha", "");
    }

}
