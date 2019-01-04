package com.juvetic.rssi.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.common.collect.EvictingQueue;
import com.juvetic.rssi.R;
import com.juvetic.rssi.model.AccessPoint;
import com.juvetic.rssi.util.ApComparator;
import com.juvetic.rssi.util.RecyclerTouchListener;
import com.juvetic.rssi.util.ToolUtil;
import com.juvetic.rssi.util.formulas.Formula;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    Parcelable recyclerViewState;

    private List<AccessPoint> accessPointList = new ArrayList<>();

    private ApAdapter mAdapter;

    private RecyclerView recyclerView;

    private ProgressBar progressBar, progressBarTop;

    WifiManager wifiManager;

    WifiScanReceiver wifiReceiver;

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

    ArrayList<Double> elseList = new ArrayList<>();

    AccessPoint accessPoint;

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

    int refreshCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("List Wifi Strength");

        recyclerView = findViewById(R.id.recycler_view);
        progressBarTop = findViewById(R.id.progress_bar_top);
        progressBarTop.setVisibility(View.VISIBLE);
//        loadData();

        variansiAp1TypeA = Double.parseDouble(ToolUtil.Storage.getValueString(this, "var_kalman_ap1_type_a"));
        variansiAp2TypeA = Double.parseDouble(ToolUtil.Storage.getValueString(this, "var_kalman_ap2_type_a"));
        variansiAp3TypeA = Double.parseDouble(ToolUtil.Storage.getValueString(this, "var_kalman_ap3_type_a"));

        variansiAp1TypeB = Double.parseDouble(ToolUtil.Storage.getValueString(this, "var_kalman_ap1_type_b"));
        variansiAp2TypeB = Double.parseDouble(ToolUtil.Storage.getValueString(this, "var_kalman_ap2_type_b"));
        variansiAp3TypeB = Double.parseDouble(ToolUtil.Storage.getValueString(this, "var_kalman_ap3_type_b"));

        preRssiAp1KFTypeB = Double.parseDouble(ToolUtil.Storage.getValueString(this, "pre_rssi_ap1"));
        preRssiAp2KFTypeB = Double.parseDouble(ToolUtil.Storage.getValueString(this, "pre_rssi_ap2"));
        preRssiAp3KFTypeB = Double.parseDouble(ToolUtil.Storage.getValueString(this, "pre_rssi_ap3"));

        iAp1 = ToolUtil.Storage.getValueInt(this, "i_kalman_ap1");
        iAp2 = ToolUtil.Storage.getValueInt(this, "i_kalman_ap2");
        iAp3 = ToolUtil.Storage.getValueInt(this, "i_kalman_ap3");

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiScanReceiver();
        wifiManager.startScan();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView,
                new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_main_setting:
//                loadData();
                wifiManager.startScan();
                progressBarTop.setVisibility(View.VISIBLE);
                return true;
            case R.id.menu_main_export:
                saveExcelFile(MainActivity.this, "List RSSI.xls",
                        rssiListAp1, rssiKFListAp1,
                        rssiListAp2, rssiKFListAp2,
                        rssiListAp3, rssiKFListAp3,
                        rssiKFListAp1v2, rssiKFListAp2v2,
                        rssiKFListAp3v2, rssiFBListAp1,
                        rssiFBListAp2, rssiFBListAp3);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onPause() {
        unregisterReceiver(wifiReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        registerReceiver(
                wifiReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        );
        super.onResume();
    }

    class WifiScanReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(final Context context, final Intent intent) {
            progressBarTop.setVisibility(View.INVISIBLE);
            accessPointList.clear();

            List<ScanResult> scanResultList = wifiManager.getScanResults();
            if (scanResultList != null) {
                for (ScanResult scanResult : scanResultList) {
                    int level = WifiManager.calculateSignalLevel(scanResult.level, 4);

                    switch (scanResult.BSSID) {
                        //b6:e6:2d:23:84:90
                        //60:de:f3:03:60:30 SBK Group
                        //78:8a:20:d4:ac:28 Cocowork
                        case "b6:e6:2d:23:84:90": //AP1
                            // KF Type A
                            rssiKFQueueAp1 = tinydb.getQueueDouble("rssi_kalman_list_ap1");
                            rssiKFQueueAp1.add((double) scanResult.level);
                            tinydb.putQueueDouble("rssi_kalman_list_ap1", rssiKFQueueAp1);

                            if (iAp1 == 0) {
                                // KF Type A
                                kfAlgoAp1TypeA = Formula.applyKFAlgorithmTypeA(rssiKFQueueAp1, 1, Double.parseDouble
                                        (ToolUtil.Storage.getValueString(MainActivity.this, "noise")));
                                variansiAp1TypeA = kfAlgoAp1TypeA.get(4);

                                // KF Type B
                                kfAlgoAp1TypeB = Formula.applyKFAlgorithmTypeB(rssiKFQueueAp1, preRssiAp1KFTypeB, 1,
                                        Double.parseDouble(
                                                ToolUtil.Storage.getValueString(MainActivity.this, "noise")));
                                preRssiAp1KFTypeB = kfAlgoAp1TypeB.get(3);
                                variansiAp1TypeB = kfAlgoAp1TypeB.get(4);

                                // Feedback
                                fbAlgoAp1 = Formula.applyFeedbackFilterAlgorithm(0, (double) scanResult.level,
                                        Double.parseDouble(ToolUtil.Storage
                                                .getValueString(MainActivity.this, "alpha")));
                            } else {
                                // KF Type A
                                kfAlgoAp1TypeA = Formula.applyKFAlgorithmTypeA(rssiKFQueueAp1, variansiAp1TypeA,
                                        Double.parseDouble(
                                                ToolUtil.Storage.getValueString(MainActivity.this, "noise")));
                                variansiAp1TypeA = kfAlgoAp1TypeA.get(4);

                                // KF Type B
                                kfAlgoAp1TypeB = Formula
                                        .applyKFAlgorithmTypeB(rssiKFQueueAp1, preRssiAp1KFTypeB, variansiAp1TypeB,
                                                Double.parseDouble(
                                                        ToolUtil.Storage.getValueString(MainActivity.this, "noise")));
                                preRssiAp1KFTypeB = kfAlgoAp1TypeB.get(3);
                                variansiAp1TypeB = kfAlgoAp1TypeB.get(4);

                                // Feedback
                                fbAlgoAp1 = Formula.applyFeedbackFilterAlgorithm(kfAlgoAp1TypeA.get(0),
                                        (double) scanResult.level,
                                        Double.parseDouble(ToolUtil.Storage
                                                .getValueString(MainActivity.this, "alpha")));
                            }
                            iAp1 += 1;

                            rssiListAp1.add((double) scanResult.level);
                            rssiKFListAp1.add(kfAlgoAp1TypeA.get(3));
                            rssiKFListAp1v2.add(kfAlgoAp1TypeB.get(3));
                            rssiFBListAp1.add(fbAlgoAp1.get(3));

                            ToolUtil.Storage.setValueInt(MainActivity.this, "i_kalman_ap1",
                                    iAp1);
                            ToolUtil.Storage.setValueString(MainActivity.this, "pre_rssi_ap1",
                                    String.valueOf(preRssiAp1KFTypeB));

                            ToolUtil.Storage.setValueString(MainActivity.this, "rssi_kalman_ap1_type_a",
                                    String.valueOf(kfAlgoAp1TypeA.get(3)));
                            ToolUtil.Storage.setValueString(MainActivity.this, "var_kalman_ap1_type_a",
                                    String.valueOf(variansiAp1TypeA));
                            ToolUtil.Storage.setValueString(MainActivity.this, "dist_kalman_ap1_type_a",
                                    Formula.distance(kfAlgoAp1TypeA.get(3), Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MainActivity.this, "n"))));

                            ToolUtil.Storage.setValueString(MainActivity.this, "rssi_kalman_ap1_type_b",
                                    String.valueOf(kfAlgoAp1TypeB.get(3)));
                            ToolUtil.Storage.setValueString(MainActivity.this, "var_kalman_ap1_type_b",
                                    String.valueOf(variansiAp1TypeB));
                            ToolUtil.Storage.setValueString(MainActivity.this, "dist_kalman_ap1_type_b",
                                    Formula.distance(kfAlgoAp1TypeB.get(3), Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MainActivity.this, "n"))));

                            ToolUtil.Storage.setValueString(MainActivity.this, "dist_feedback_ap1",
                                    Formula.distance(fbAlgoAp1.get(3), Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MainActivity.this, "n"))));

                            accessPoint = new AccessPoint(
                                    scanResult.SSID,
                                    String.valueOf(scanResult.level) + " dBm",
                                    String.valueOf(scanResult.frequency) + " MHz",
                                    scanResult.capabilities,
                                    Formula.distance((double) scanResult.level, Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MainActivity.this, "n"))),
                                    String.valueOf(level),
                                    scanResult.BSSID,
                                    String.valueOf(kfAlgoAp1TypeA.get(3)) + " dBm",
                                    String.valueOf(kfAlgoAp1TypeB.get(3)) + " dBm",
                                    String.valueOf(fbAlgoAp1.get(3)) + " dBm",
                                    Formula.distance
                                            (kfAlgoAp1TypeA.get(3), Double.parseDouble(ToolUtil.Storage
                                                    .getValueString(MainActivity.this, "n"))),
                                    Formula.distance
                                            (kfAlgoAp1TypeB.get(3), Double.parseDouble(ToolUtil.Storage
                                                    .getValueString(MainActivity.this, "n"))),
                                    Formula.distance(
                                            fbAlgoAp1.get(3), Double.parseDouble(ToolUtil.Storage
                                                    .getValueString(MainActivity.this, "n")))
                            );
                            accessPointList.add(accessPoint);
                            break;
                        //78:8a:20:d4:a4:d8
                        //6a:c6:3a:d6:9c:92
                        case "6a:c6:3a:d6:9c:92": //2
                            rssiKFQueueAp2 = tinydb.getQueueDouble("rssi_kalman_list_ap2");
                            rssiKFQueueAp2.add((double) scanResult.level);
                            tinydb.putQueueDouble("rssi_kalman_list_ap2", rssiKFQueueAp2);

                            if (iAp2 == 0) {
                                // KF Type A
                                kfAlgoAp2TypeA = Formula.applyKFAlgorithmTypeA(rssiKFQueueAp2, 1,
                                        Double.parseDouble(
                                                ToolUtil.Storage.getValueString(MainActivity.this, "noise")));
                                variansiAp2TypeA = kfAlgoAp2TypeA.get(4);

                                // KF Type B
                                kfAlgoAp2TypeB = Formula.applyKFAlgorithmTypeB(rssiKFQueueAp2, preRssiAp2KFTypeB, 1,
                                        Double.parseDouble(
                                                ToolUtil.Storage.getValueString(MainActivity.this, "noise")));
                                preRssiAp2KFTypeB = kfAlgoAp2TypeB.get(3);
                                variansiAp2TypeB = kfAlgoAp2TypeB.get(4);

                                // Feedback
                                fbAlgoAp2 = Formula.applyFeedbackFilterAlgorithm(0, (double) scanResult.level,
                                        Double.parseDouble(ToolUtil.Storage
                                                .getValueString(MainActivity.this, "alpha")));
                            } else {
                                // KF Type A
                                kfAlgoAp2TypeA = Formula.applyKFAlgorithmTypeA(rssiKFQueueAp2, variansiAp2TypeA,
                                        Double.parseDouble(
                                                ToolUtil.Storage.getValueString(MainActivity.this, "noise")));
                                variansiAp2TypeA = kfAlgoAp2TypeA.get(4);

                                // KF Type B
                                kfAlgoAp2TypeB = Formula
                                        .applyKFAlgorithmTypeB(rssiKFQueueAp2, preRssiAp2KFTypeB, variansiAp2TypeB,
                                                Double.parseDouble(
                                                        ToolUtil.Storage.getValueString(MainActivity.this, "noise")));
                                preRssiAp2KFTypeB = kfAlgoAp2TypeB.get(3);
                                variansiAp2TypeB = kfAlgoAp2TypeB.get(4);

                                // Feedback
                                fbAlgoAp2 = Formula.applyFeedbackFilterAlgorithm(kfAlgoAp2TypeA.get(0),
                                        (double) scanResult.level,
                                        Double.parseDouble(ToolUtil.Storage
                                                .getValueString(MainActivity.this, "alpha")));
                            }
                            iAp2 += 1;

                            rssiListAp2.add((double) scanResult.level);
                            rssiKFListAp2.add(kfAlgoAp2TypeA.get(3));
                            rssiKFListAp2v2.add(kfAlgoAp2TypeB.get(3));
                            rssiFBListAp2.add(fbAlgoAp2.get(3));

                            ToolUtil.Storage.setValueInt(MainActivity.this, "i_kalman_ap2", iAp2);
                            ToolUtil.Storage.setValueString(MainActivity.this, "pre_rssi_ap2",
                                    String.valueOf(preRssiAp2KFTypeB));

                            ToolUtil.Storage.setValueString(MainActivity.this, "rssi_kalman_ap2_type_b",
                                    String.valueOf(kfAlgoAp2TypeA.get(3)));
                            ToolUtil.Storage.setValueString(MainActivity.this, "var_kalman_ap2_type_b",
                                    String.valueOf(variansiAp2TypeA));
                            ToolUtil.Storage.setValueString(MainActivity.this, "dist_kalman_ap2_type_b",
                                    Formula.distance(kfAlgoAp2TypeA.get(3), Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MainActivity.this, "n"))));

                            ToolUtil.Storage.setValueString(MainActivity.this, "rssi_kalman_ap2_type_b",
                                    String.valueOf(kfAlgoAp2TypeB.get(3)));
                            ToolUtil.Storage.setValueString(MainActivity.this, "var_kalman_ap2_type_b",
                                    String.valueOf(variansiAp2TypeB));
                            ToolUtil.Storage.setValueString(MainActivity.this, "dist_kalman_ap2_type_b",
                                    Formula.distance(kfAlgoAp2TypeB.get(3), Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MainActivity.this, "n"))));

                            ToolUtil.Storage.setValueString(MainActivity.this, "dist_feedback_ap2",
                                    Formula.distance(fbAlgoAp2.get(3), Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MainActivity.this, "n"))));

                            accessPoint = new AccessPoint(
                                    scanResult.SSID,
                                    String.valueOf(scanResult.level) + " dBm",
                                    String.valueOf(scanResult.frequency) + " MHz",
                                    scanResult.capabilities,
                                    Formula.distance((double) scanResult.level, Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MainActivity.this, "n"))),
                                    String.valueOf(level),
                                    scanResult.BSSID,
                                    String.valueOf(kfAlgoAp2TypeA.get(3)) + " dBm",
                                    String.valueOf(kfAlgoAp2TypeB.get(3)) + " dBm",
                                    String.valueOf(fbAlgoAp2.get(3)) + " dBm",
                                    Formula.distance
                                            (kfAlgoAp2TypeA.get(3), Double.parseDouble(ToolUtil.Storage
                                                    .getValueString(MainActivity.this, "n"))),
                                    Formula.distance
                                            (kfAlgoAp2TypeB.get(3), Double.parseDouble(ToolUtil.Storage
                                                    .getValueString(MainActivity.this, "n"))),
                                    Formula.distance(
                                            fbAlgoAp2.get(3), Double.parseDouble(ToolUtil.Storage
                                                    .getValueString(MainActivity.this, "n")))
                            );
                            accessPointList.add(accessPoint);
                            break;
                        // 78:8a:20:d4:a9:74
                        // be:dd:c2:fe:3b:0b
                        case "be:dd:c2:fe:3b:0b": //AP3
                            rssiKFQueueAp3 = tinydb.getQueueDouble("rssi_kalman_list_ap3");
                            rssiKFQueueAp3.add((double) scanResult.level);
                            tinydb.putQueueDouble("rssi_kalman_list_ap3", rssiKFQueueAp3);

                            if (iAp3 == 0) {
                                // KF Type A
                                kfAlgoAp3TypeA = Formula.applyKFAlgorithmTypeA(rssiKFQueueAp3, 1,
                                        Double.parseDouble(
                                                ToolUtil.Storage.getValueString(MainActivity.this, "noise")));
                                variansiAp3TypeA = kfAlgoAp3TypeA.get(4);

                                // KF Type B
                                kfAlgoAp3TypeB = Formula.applyKFAlgorithmTypeB(rssiKFQueueAp3, preRssiAp3KFTypeB, 1,
                                        Double.parseDouble(
                                                ToolUtil.Storage.getValueString(MainActivity.this, "noise")));
                                preRssiAp3KFTypeB = kfAlgoAp3TypeB.get(3);
                                variansiAp3TypeB = kfAlgoAp3TypeB.get(4);

                                // Feedback
                                fbAlgoAp3 = Formula.applyFeedbackFilterAlgorithm(0, (double) scanResult.level,
                                        Double.parseDouble(ToolUtil.Storage
                                                .getValueString(MainActivity.this, "alpha")));
                            } else {
                                // KF Type A
                                kfAlgoAp3TypeA = Formula.applyKFAlgorithmTypeA(rssiKFQueueAp3, variansiAp3TypeA,
                                        Double.parseDouble(
                                                ToolUtil.Storage.getValueString(MainActivity.this, "noise")));
                                variansiAp3TypeA = kfAlgoAp3TypeA.get(4);

                                // KF Type B
                                kfAlgoAp3TypeB = Formula
                                        .applyKFAlgorithmTypeB(rssiKFQueueAp3, preRssiAp3KFTypeB, variansiAp3TypeB,
                                                Double.parseDouble(
                                                        ToolUtil.Storage.getValueString(MainActivity.this, "noise")));
                                preRssiAp3KFTypeB = kfAlgoAp3TypeB.get(3);
                                variansiAp3TypeB = kfAlgoAp3TypeB.get(4);

                                // Feedback
                                fbAlgoAp3 = Formula.applyFeedbackFilterAlgorithm(kfAlgoAp3TypeA.get(0),
                                        (double) scanResult.level,
                                        Double.parseDouble(ToolUtil.Storage
                                                .getValueString(MainActivity.this, "alpha")));
                            }
                            iAp3 += 1;

                            rssiListAp3.add((double) scanResult.level);
                            rssiKFListAp3.add(kfAlgoAp3TypeA.get(3));
                            rssiKFListAp3v2.add(kfAlgoAp3TypeB.get(3));
                            rssiFBListAp3.add(fbAlgoAp3.get(3));

                            ToolUtil.Storage.setValueInt(MainActivity.this, "i_kalman_ap3", iAp3);
                            ToolUtil.Storage.setValueString(MainActivity.this, "pre_rssi_ap3",
                                    String.valueOf(preRssiAp3KFTypeB));

                            ToolUtil.Storage.setValueString(MainActivity.this, "rssi_kalman_ap3_type_a",
                                    String.valueOf(kfAlgoAp3TypeA.get(3)));
                            ToolUtil.Storage.setValueString(MainActivity.this, "var_kalman_ap3_type_a",
                                    String.valueOf(variansiAp3TypeA));
                            ToolUtil.Storage.setValueString(MainActivity.this, "dist_kalman_ap3_type_a",
                                    Formula.distance(kfAlgoAp3TypeA.get(3), Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MainActivity.this, "n"))));

                            ToolUtil.Storage.setValueString(MainActivity.this, "rssi_kalman_ap3_type_b",
                                    String.valueOf(kfAlgoAp3TypeB.get(3)));
                            ToolUtil.Storage.setValueString(MainActivity.this, "var_kalman_ap3_type_b",
                                    String.valueOf(variansiAp3TypeB));
                            ToolUtil.Storage.setValueString(MainActivity.this, "dist_kalman_ap3_type_b",
                                    Formula.distance(kfAlgoAp3TypeB.get(3), Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MainActivity.this, "n"))));

                            ToolUtil.Storage.setValueString(MainActivity.this, "dist_feedback_ap3",
                                    Formula.distance(fbAlgoAp3.get(3), Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MainActivity.this, "n"))));

                            accessPoint = new AccessPoint(
                                    scanResult.SSID,
                                    String.valueOf(scanResult.level) + " dBm",
                                    String.valueOf(scanResult.frequency) + " MHz",
                                    scanResult.capabilities,
                                    Formula.distance((double) scanResult.level, Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MainActivity.this, "n"))),
                                    String.valueOf(level),
                                    scanResult.BSSID,
                                    String.valueOf(kfAlgoAp3TypeA.get(3)) + " dBm",
                                    String.valueOf(kfAlgoAp3TypeB.get(3)) + " dBm",
                                    String.valueOf(fbAlgoAp3.get(3)) + " dBm",
                                    Formula.distance
                                            (kfAlgoAp3TypeA.get(3), Double.parseDouble(ToolUtil.Storage
                                                    .getValueString(MainActivity.this, "n"))),
                                    Formula.distance
                                            (kfAlgoAp3TypeB.get(3), Double.parseDouble(ToolUtil.Storage
                                                    .getValueString(MainActivity.this, "n"))),
                                    Formula.distance(
                                            fbAlgoAp3.get(3), Double.parseDouble(ToolUtil.Storage
                                                    .getValueString(MainActivity.this, "n")))
                            );
                            accessPointList.add(accessPoint);
                            break;
                        default:
                            accessPoint = new AccessPoint(
                                    scanResult.SSID,
                                    String.valueOf(scanResult.level) + " dBm",
                                    String.valueOf(scanResult.frequency) + " MHz",
                                    scanResult.capabilities,
                                    Formula.distance((double) scanResult.level, Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MainActivity.this, "n"))),
                                    String.valueOf(level),
                                    scanResult.BSSID,
                                    "0 dBm",
                                    "0 dBm",
                                    "0 dBm",
                                    "0",
                                    "0",
                                    "0"
                            );
                            accessPointList.add(accessPoint);
                            break;
                    }
                }
            }

            Collections.sort(accessPointList, new ApComparator());

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setHasFixedSize(true);

            mAdapter = new ApAdapter(accessPointList);
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            // Save state
            recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();

            // Restore state
            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
//            runLayoutAnimation(recyclerView);
            Toast.makeText(getApplicationContext(), "Refresh count: " + refreshCount,
                    Toast.LENGTH_SHORT).show();
            refreshCount++;

            // Refresh after 1 seconds
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                wifiManager.startScan();
                progressBarTop.setVisibility(View.VISIBLE);
            }, 1000);
        }
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();

        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_bottom);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private static boolean saveExcelFile(Context context, String fileName,
            ArrayList<Double> rssiListAp1, ArrayList<Double> rssiKFListAp1,
            ArrayList<Double> rssiListAp2, ArrayList<Double> rssiKFListAp2,
            ArrayList<Double> rssiListAp3, ArrayList<Double> rssiKFListAp3,
            ArrayList<Double> rssiKFListAp1v2, ArrayList<Double> rssiKFListAp2v2,
            ArrayList<Double> rssiKFListAp3v2, ArrayList<Double> rssiFBListAp1,
            ArrayList<Double> rssiFBListAp2, ArrayList<Double> rssiFBListAp3) {

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e(TAG, "Storage not available or read only");
            return false;
        }

        boolean success = false;

        //New Workbook
        Workbook wb = new HSSFWorkbook();

        //New Sheet
        Sheet sheetRssiAp1 = wb.createSheet("AP1");
        Sheet sheetRssiAp1KF = wb.createSheet("AP1 KFv1");
        Sheet sheetRssiAp1KF2 = wb.createSheet("AP1 KFv2");
        Sheet sheetRssiAp1FB = wb.createSheet("AP1 Feedback");
        Sheet sheetRssiAp2 = wb.createSheet("AP2");
        Sheet sheetRssiAp2KF = wb.createSheet("AP2 KFv1");
        Sheet sheetRssiAp2KF2 = wb.createSheet("AP2 KFv2");
        Sheet sheetRssiAp2FB = wb.createSheet("AP2 Feedback");
        Sheet sheetRssiAp3 = wb.createSheet("AP3");
        Sheet sheetRssiAp3KF = wb.createSheet("AP3 KFv1");
        Sheet sheetRssiAp3KF2 = wb.createSheet("AP3 KFv2");
        Sheet sheetRssiAp3FB = wb.createSheet("AP3 Feedback");

        // AP1 RSSI
        for (int i = 0; i < rssiListAp1.size(); i++) {
            sheetRssiAp1.createRow(i).createCell(0).setCellValue(rssiListAp1.get(i));
            sheetRssiAp1KF.createRow(i).createCell(0).setCellValue(rssiKFListAp1.get(i));
            sheetRssiAp1KF2.createRow(i).createCell(0).setCellValue(rssiKFListAp1v2.get(i));
            sheetRssiAp1FB.createRow(i).createCell(0).setCellValue(rssiFBListAp1.get(i));
        }

        // AP2 RSSI
        for (int i = 0; i < rssiListAp2.size(); i++) {
            sheetRssiAp2.createRow(i).createCell(0).setCellValue(rssiListAp2.get(i));
            sheetRssiAp2KF.createRow(i).createCell(0).setCellValue(rssiKFListAp2.get(i));
            sheetRssiAp2KF2.createRow(i).createCell(0).setCellValue(rssiKFListAp2v2.get(i));
            sheetRssiAp2FB.createRow(i).createCell(0).setCellValue(rssiFBListAp2.get(i));
        }

        // AP3 RSSI
        for (int i = 0; i < rssiListAp3.size(); i++) {
            sheetRssiAp3.createRow(i).createCell(0).setCellValue(rssiListAp3.get(i));
            sheetRssiAp3KF.createRow(i).createCell(0).setCellValue(rssiKFListAp3.get(i));
            sheetRssiAp3KF2.createRow(i).createCell(0).setCellValue(rssiKFListAp3v2.get(i));
            sheetRssiAp3FB.createRow(i).createCell(0).setCellValue(rssiFBListAp3.get(i));
        }

        // Create a path where we will place our List of objects on external storage
        File file = new File(context.getExternalFilesDir(null), fileName);

        try (FileOutputStream os = new FileOutputStream(file)) {
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            Toast.makeText(context, "Exported to " + file, Toast.LENGTH_SHORT).show();
            success = true;
        } catch (IOException e) {
            Toast.makeText(context, "Error writing " + e, Toast.LENGTH_SHORT).show();
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Toast.makeText(context, "Error " + e, Toast.LENGTH_SHORT).show();
            Log.w("FileUtils", "Failed to save file", e);
        }
        return success;
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState);
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(extStorageState);
    }
}