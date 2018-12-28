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

    TinyDB tinydb;

    String x1, y1, x2, y2, x3, y3, d1, d2, d3, xPos, yPos, bssid1, bssid2, bssid3, noise, n;

    double variansiAp1 = 0;

    double variansiAp2 = 0;

    double variansiAp3 = 0;

    int iAp1 = 0;

    int iAp2 = 0;

    int iAp3 = 0;

    Queue<Double> rssiListAp1 = EvictingQueue.create(10);

    Queue<Double> rssiListAp2 = EvictingQueue.create(10);

    Queue<Double> rssiListAp3 = EvictingQueue.create(10);

    ArrayList<Double> kfAlgoAp1 = new ArrayList<>();

    ArrayList<Double> kfAlgoAp2 = new ArrayList<>();

    ArrayList<Double> kfAlgoAp3 = new ArrayList<>();

    List<Double> xy;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();

        tinydb = new TinyDB(this);

        variansiAp1 = Double.parseDouble(ToolUtil.Storage.getValueString(this, "var_kalman_ap1", "0"));
        variansiAp2 = Double.parseDouble(ToolUtil.Storage.getValueString(this, "var_kalman_ap2", "0"));
        variansiAp3 = Double.parseDouble(ToolUtil.Storage.getValueString(this, "var_kalman_ap3", "0"));
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
        bssid1 = ToolUtil.Storage.getValueString(this, "Bssid1", "");
        bssid2 = ToolUtil.Storage.getValueString(this, "Bssid2", "");
        bssid3 = ToolUtil.Storage.getValueString(this, "Bssid3", "");

        noise = ToolUtil.Storage.getValueString(this, "noise", "");
        n = ToolUtil.Storage.getValueString(this, "n", "");
    }

}
