package com.juvetic.rssi.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.juvetic.rssi.R;
import com.juvetic.rssi.model.AccessPoint;
import com.juvetic.rssi.util.ApComparator;
import com.juvetic.rssi.util.RecyclerTouchListener;
import com.juvetic.rssi.util.ToolUtil;
import com.juvetic.rssi.util.formulas.Formula;
import com.juvetic.rssi.util.formulas.KalmanFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends BaseActivity {

    private List<AccessPoint> accessPointList = new ArrayList<>();

    private ApAdapter mAdapter;

    private RecyclerView recyclerView;

    private ProgressBar progressBar;

    WifiManager wifiManager;

    WifiScanReceiver wifiReceiver;

    ArrayList<Double> rssiListAp1 = new ArrayList<>();

    ArrayList<Double> rssiListAp2 = new ArrayList<>();

    ArrayList<Double> rssiListAp3 = new ArrayList<>();

    ArrayList<Double> kfAlgoAp1 = new ArrayList<>();

    ArrayList<Double> kfAlgoAp2 = new ArrayList<>();

    ArrayList<Double> kfAlgoAp3 = new ArrayList<>();

    AccessPoint accessPoint;

    double variansiAp1 = 0;

    double variansiAp2 = 0;

    double variansiAp3 = 0;

    int iAp1 = 0;

    int iAp2 = 0;

    int iAp3 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("List Wifi Strength");

        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
//        loadData();

        variansiAp1 = Double.parseDouble(ToolUtil.Storage.getValueString(this, "var_kalman_ap1"));
        variansiAp2 = Double.parseDouble(ToolUtil.Storage.getValueString(this, "var_kalman_ap2"));
        variansiAp3 = Double.parseDouble(ToolUtil.Storage.getValueString(this, "var_kalman_ap3"));
        iAp1 = ToolUtil.Storage.getValueInt(this,"i_kalman_ap1");
        iAp2 = ToolUtil.Storage.getValueInt(this,"i_kalman_ap2");
        iAp3 = ToolUtil.Storage.getValueInt(this,"i_kalman_ap3");

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
                progressBar.setVisibility(View.VISIBLE);
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
            progressBar.setVisibility(View.GONE);
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
                            rssiListAp1 = tinydb.getListDouble("rssi_kalman_list_ap1");
                            rssiListAp1.add((double) scanResult.level);
                            tinydb.putListDouble("rssi_kalman_list_ap1", rssiListAp1);

                            if (iAp1 == 0) {
                                kfAlgoAp1 = KalmanFilter.applyKFAlgorithm(rssiListAp1, 1, 0.008);
                                variansiAp1 = kfAlgoAp1.get(4);
                            } else {
                                kfAlgoAp1 = KalmanFilter.applyKFAlgorithm(rssiListAp1, variansiAp1, 0.008);
                                variansiAp1 = kfAlgoAp1.get(4);
                            }
                            iAp1 += 1;

                            ToolUtil.Storage.setValueString(MainActivity.this, "rssi_kalman_ap1",
                                    String.valueOf(kfAlgoAp1.get(3)));
                            ToolUtil.Storage.setValueInt(MainActivity.this,"i_kalman_ap1", iAp1);
                            ToolUtil.Storage.setValueString(MainActivity.this, "var_kalman_ap1",
                                    String.valueOf(variansiAp1));
                            ToolUtil.Storage.setValueString(MainActivity.this, "dist_kalman_ap1",
                                    Formula.distance(kfAlgoAp1.get(3)));

                            accessPoint = new AccessPoint(
                                    scanResult.SSID,
                                    String.valueOf(scanResult.level) + " dBm",
                                    String.valueOf(scanResult.frequency) + " MHz",
                                    scanResult.capabilities,
                                    Formula.distance((double) scanResult.level),
                                    String.valueOf(level),
                                    scanResult.BSSID,
                                    String.valueOf(kfAlgoAp1.get(3)) + " dBm",
                                    Formula.distance(kfAlgoAp1.get(3)));
                            accessPointList.add(accessPoint);
                            break;
                        case "6a:c6:3a:d6:9c:92":
                            rssiListAp2 = tinydb.getListDouble("rssi_kalman_list_ap2");
                            rssiListAp2.add((double) scanResult.level);
                            tinydb.putListDouble("rssi_kalman_list_ap2", rssiListAp2);

                            if (iAp2 == 0) {
                                kfAlgoAp2 = KalmanFilter.applyKFAlgorithm(rssiListAp2, 1, 0.008);
                                variansiAp2 = kfAlgoAp2.get(4);
                            } else {
                                kfAlgoAp2 = KalmanFilter.applyKFAlgorithm(rssiListAp2, variansiAp2, 0.008);
                                variansiAp2 = kfAlgoAp2.get(4);
                            }
                            iAp2 += 1;

                            ToolUtil.Storage.setValueString(MainActivity.this, "rssi_kalman_ap2",
                                    String.valueOf(kfAlgoAp2.get(3)));
                            ToolUtil.Storage.setValueInt(MainActivity.this,"i_kalman_ap2", iAp2);
                            ToolUtil.Storage.setValueString(MainActivity.this, "var_kalman_ap2",
                                    String.valueOf(variansiAp2));
                            ToolUtil.Storage.setValueString(MainActivity.this, "dist_kalman_ap2",
                                    Formula.distance(kfAlgoAp2.get(3)));

                            accessPoint = new AccessPoint(
                                    scanResult.SSID,
                                    String.valueOf(scanResult.level) + " dBm",
                                    String.valueOf(scanResult.frequency) + " MHz",
                                    scanResult.capabilities,
                                    Formula.distance(scanResult.level),
                                    String.valueOf(level),
                                    scanResult.BSSID,
                                    String.valueOf(kfAlgoAp2.get(3)) + " dBm",
                                    Formula.distance(kfAlgoAp2.get(3)));
                            accessPointList.add(accessPoint);
                            break;
                        case "be:dd:c2:fe:3b:0b":
                            rssiListAp3 = tinydb.getListDouble("rssi_kalman_list_ap3");
                            rssiListAp3.add((double) scanResult.level);
                            tinydb.putListDouble("rssi_kalman_list_ap3", rssiListAp3);

                            if (iAp3 == 0) {
                                kfAlgoAp3 = KalmanFilter.applyKFAlgorithm(rssiListAp3, 1, 0.008);
                                variansiAp3 = kfAlgoAp3.get(4);
                            } else {
                                kfAlgoAp3 = KalmanFilter.applyKFAlgorithm(rssiListAp3, variansiAp3, 0.008);
                                variansiAp3 = kfAlgoAp3.get(4);
                            }
                            iAp3 += 1;

                            ToolUtil.Storage.setValueString(MainActivity.this, "rssi_kalman_ap3",
                                    String.valueOf(kfAlgoAp3.get(3)));
                            ToolUtil.Storage.setValueInt(MainActivity.this,"i_kalman_ap3", iAp3);
                            ToolUtil.Storage.setValueString(MainActivity.this, "var_kalman_ap3",
                                    String.valueOf(variansiAp3));
                            ToolUtil.Storage.setValueString(MainActivity.this, "dist_kalman_ap3",
                                    Formula.distance(kfAlgoAp3.get(3)));

                            accessPoint = new AccessPoint(
                                    scanResult.SSID,
                                    String.valueOf(scanResult.level) + " dBm",
                                    String.valueOf(scanResult.frequency) + " MHz",
                                    scanResult.capabilities,
                                    Formula.distance(scanResult.level),
                                    String.valueOf(level),
                                    scanResult.BSSID,
                                    String.valueOf(kfAlgoAp3.get(3)) + " dBm",
                                    Formula.distance(kfAlgoAp3.get(3)));
                            accessPointList.add(accessPoint);
                            break;
                        default:
                            accessPoint = new AccessPoint(
                                    scanResult.SSID,
                                    String.valueOf(scanResult.level) + " dBm",
                                    String.valueOf(scanResult.frequency) + " MHz",
                                    scanResult.capabilities,
                                    Formula.distance(scanResult.level),
                                    String.valueOf(level),
                                    scanResult.BSSID,
                                    "0 dBm", "0");
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
            runLayoutAnimation(recyclerView);
            Toast.makeText(getApplicationContext(), "Jumlah Access Point: " + accessPointList.size(),
                    Toast.LENGTH_SHORT).show();
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

    private void loadData() {
        accessPointList.clear();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> wifiList = null;
        if (wifiManager != null) {
            wifiList = wifiManager.getScanResults();
        }
        if (wifiList != null) {
            for (ScanResult scanResult : wifiList) {
                int level = WifiManager.calculateSignalLevel(scanResult.level, 4);

                AccessPoint accessPoint = new AccessPoint(
                        scanResult.SSID,
                        String.valueOf(scanResult.level) + " dBm",
                        String.valueOf(scanResult.frequency) + " MHz",
                        scanResult.capabilities,
                        Formula.distance(scanResult.level),
                        String.valueOf(level),
                        scanResult.BSSID, "0", "0");
//                if (accessPoint.getBssid().equals("c4:12:f5:b8:7a:99")) {
                accessPointList.add(accessPoint);
//                }
            }
        }

        Collections.sort(accessPointList, new ApComparator());

        mAdapter = new ApAdapter(accessPointList);
        recyclerView.setAdapter(mAdapter);
        Toast.makeText(this, "Jumlah Access Point: " + accessPointList.size(), Toast.LENGTH_SHORT).show();
    }

    private void calculateRssiMean() {

    }
}