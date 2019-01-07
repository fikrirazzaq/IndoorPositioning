package com.juvetic.rssi.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.juvetic.rssi.R;
import com.juvetic.rssi.model.AccessPoint;
import com.juvetic.rssi.util.ApComparator;
import com.juvetic.rssi.util.ToolUtil;
import com.juvetic.rssi.util.formulas.Formula;
import com.juvetic.rssi.util.helper.AssetsHelper;
import id.recharge.library.SVGMapView;
import id.recharge.library.SVGMapViewListener;
import id.recharge.library.overlay.SVGMapLocationOverlay;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapShowAllFilterActivity extends BaseActivity {

    public static final String EXTRA_FILTER = "extra_filter";

    private SVGMapView mapView;

    private List<AccessPoint> accessPointList = new ArrayList<>();

    WifiManager wifiManager;

    WifiScanReceiver wifiReceiver;

    AccessPoint accessPoint;

    SVGMapLocationOverlay locationOverlay, locationOverlayKalman1, locationOverlayKalman2, locationOverlayFeedback;

    double d1 = 0;

    double d2 = 0;

    double d3 = 0;

    double d1Kalman1 = 0;

    double d2Kalman1 = 0;

    double d3Kalman1 = 0;

    double d1Kalman2 = 0;

    double d2Kalman2 = 0;

    double d3Kalman2 = 0;

    double d1Feedback = 0;

    double d2Feedback = 0;

    double d3Feedback = 0;

    String filter = "default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_show_all_filter);

        setContentView(R.layout.activity_location);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new MapShowAllFilterActivity.WifiScanReceiver();
        wifiManager.startScan();

        mapView = findViewById(R.id.location_mapview);

        Intent intent = getIntent();
        filter = intent.getStringExtra(EXTRA_FILTER);

        setTitle("Map - All Filter");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.map_menu_deploy:
                InformationDialogMapAll bottomSheetDialog = InformationDialogMapAll.getInstance();
                bottomSheetDialog.show(getSupportFragmentManager(), "Custom Bottom Sheet");
                return true;
            case R.id.map_menu_reload:
                wifiManager.startScan();
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

    protected void onPause() {
        super.onPause();

        unregisterReceiver(wifiReceiver);
    }

    protected void onResume() {
        super.onResume();

        mapView.registerMapViewListener(new SVGMapViewListener() {
            @Override
            public void onGetCurrentMap(Bitmap bitmap) {
            }

            @Override
            public void onMapLoadComplete() {
                mapView.refresh();
            }

            @Override
            public void onMapLoadError() {
            }
        });
        mapView.loadMap(AssetsHelper.getContent(this, "hes_lab_v2.svg"));

        mapView.getController()
                .sparkAtPoint(new PointF(Float.valueOf(x1), Float.valueOf(y1)), 20, Color.YELLOW, 10000);
        mapView.getController()
                .sparkAtPoint(new PointF(Float.valueOf(x2), Float.valueOf(y2)), 20, Color.GREEN, 10000);
        mapView.getController().sparkAtPoint(new PointF(Float.valueOf(x3), Float.valueOf(y3)), 20, Color.BLUE, 10000);
        mapView.getController().setScrollGestureEnabled(false);
        mapView.getController().setZoomGestureEnabled(false);

        registerReceiver(
                wifiReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        );
    }

    class WifiScanReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
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
                                        (ToolUtil.Storage.getValueString(MapShowAllFilterActivity.this, "noise")));
                                variansiAp1TypeA = kfAlgoAp1TypeA.get(4);

                                // KF Type B
                                kfAlgoAp1TypeB = Formula.applyKFAlgorithmTypeB(rssiKFQueueAp1, preRssiAp1KFTypeB, 1,
                                        Double.parseDouble(
                                                ToolUtil.Storage
                                                        .getValueString(MapShowAllFilterActivity.this, "noise")));
                                preRssiAp1KFTypeB = kfAlgoAp1TypeB.get(3);
                                variansiAp1TypeB = kfAlgoAp1TypeB.get(4);

                                // Feedback
                                fbAlgoAp1 = Formula.applyFeedbackFilterAlgorithm(0, (double) scanResult.level,
                                        Double.parseDouble(ToolUtil.Storage
                                                .getValueString(MapShowAllFilterActivity.this, "alpha")));
                            } else {
                                // KF Type A
                                kfAlgoAp1TypeA = Formula.applyKFAlgorithmTypeA(rssiKFQueueAp1, variansiAp1TypeA,
                                        Double.parseDouble(
                                                ToolUtil.Storage
                                                        .getValueString(MapShowAllFilterActivity.this, "noise")));
                                variansiAp1TypeA = kfAlgoAp1TypeA.get(4);

                                // KF Type B
                                kfAlgoAp1TypeB = Formula
                                        .applyKFAlgorithmTypeB(rssiKFQueueAp1, preRssiAp1KFTypeB, variansiAp1TypeB,
                                                Double.parseDouble(
                                                        ToolUtil.Storage
                                                                .getValueString(MapShowAllFilterActivity.this,
                                                                        "noise")));
                                preRssiAp1KFTypeB = kfAlgoAp1TypeB.get(3);
                                variansiAp1TypeB = kfAlgoAp1TypeB.get(4);

                                // Feedback
                                fbAlgoAp1 = Formula.applyFeedbackFilterAlgorithm(kfAlgoAp1TypeA.get(0),
                                        (double) scanResult.level,
                                        Double.parseDouble(ToolUtil.Storage
                                                .getValueString(MapShowAllFilterActivity.this, "alpha")));
                            }
                            iAp1 += 1;

                            rssiListAp1.add((double) scanResult.level);
                            rssiKFListAp1.add(kfAlgoAp1TypeA.get(3));

                            ToolUtil.Storage.setValueInt(MapShowAllFilterActivity.this, "i_kalman_ap1",
                                    iAp1);
                            ToolUtil.Storage.setValueString(MapShowAllFilterActivity.this, "pre_rssi_ap1",
                                    String.valueOf(preRssiAp1KFTypeB));

                            ToolUtil.Storage.setValueString(MapShowAllFilterActivity.this, "rssi_kalman_ap1_type_a",
                                    String.valueOf(kfAlgoAp1TypeA.get(3)));
                            ToolUtil.Storage.setValueString(MapShowAllFilterActivity.this, "var_kalman_ap1_type_a",
                                    String.valueOf(variansiAp1TypeA));

                            ToolUtil.Storage.setValueString(MapShowAllFilterActivity.this, "rssi_kalman_ap1_type_b",
                                    String.valueOf(kfAlgoAp1TypeB.get(3)));
                            ToolUtil.Storage.setValueString(MapShowAllFilterActivity.this, "var_kalman_ap1_type_b",
                                    String.valueOf(variansiAp1TypeB));

                            accessPoint = new AccessPoint(
                                    scanResult.SSID,
                                    String.valueOf(scanResult.level) + " dBm",
                                    String.valueOf(scanResult.frequency) + " MHz",
                                    scanResult.capabilities,
                                    Formula.distance((double) scanResult.level, Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MapShowAllFilterActivity.this, "n"))),
                                    String.valueOf(level),
                                    scanResult.BSSID,
                                    String.valueOf(kfAlgoAp1TypeA.get(3)) + " dBm",
                                    String.valueOf(kfAlgoAp1TypeB.get(3)) + " dBm",
                                    String.valueOf(fbAlgoAp1.get(3)) + " dBm",
                                    Formula.distance
                                            (kfAlgoAp1TypeA.get(3), Double.parseDouble(ToolUtil.Storage
                                                    .getValueString(MapShowAllFilterActivity.this, "n"))),
                                    Formula.distance
                                            (kfAlgoAp1TypeB.get(3), Double.parseDouble(ToolUtil.Storage
                                                    .getValueString(MapShowAllFilterActivity.this, "n"))),
                                    Formula.distance(
                                            fbAlgoAp1.get(3), Double.parseDouble(ToolUtil.Storage
                                                    .getValueString(MapShowAllFilterActivity.this, "n")))
                            );
                            ToolUtil.Storage.setValueString(MapShowAllFilterActivity.this, "d1",
                                    accessPoint.getDistance());
                            ToolUtil.Storage.setValueString(MapShowAllFilterActivity.this, "dist_kalman_ap1_type_a",
                                    accessPoint.getDistanceKalmanTypeA());
                            ToolUtil.Storage.setValueString(MapShowAllFilterActivity.this, "dist_kalman_ap1_type_b",
                                    accessPoint.getDistanceKalmanTypeB());
                            ToolUtil.Storage.setValueString(MapShowAllFilterActivity.this, "dist_feedback_ap1",
                                    accessPoint.getDistanceFeedback());
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
                                                ToolUtil.Storage
                                                        .getValueString(MapShowAllFilterActivity.this, "noise")));
                                variansiAp2TypeA = kfAlgoAp2TypeA.get(4);

                                // KF Type B
                                kfAlgoAp2TypeB = Formula.applyKFAlgorithmTypeB(rssiKFQueueAp2, preRssiAp2KFTypeB, 1,
                                        Double.parseDouble(
                                                ToolUtil.Storage
                                                        .getValueString(MapShowAllFilterActivity.this, "noise")));
                                preRssiAp2KFTypeB = kfAlgoAp2TypeB.get(3);
                                variansiAp2TypeB = kfAlgoAp2TypeB.get(4);

                                // Feedback
                                fbAlgoAp2 = Formula.applyFeedbackFilterAlgorithm(0, (double) scanResult.level,
                                        Double.parseDouble(ToolUtil.Storage
                                                .getValueString(MapShowAllFilterActivity.this, "alpha")));
                            } else {
                                // KF Type A
                                kfAlgoAp2TypeA = Formula.applyKFAlgorithmTypeA(rssiKFQueueAp2, variansiAp2TypeA,
                                        Double.parseDouble(
                                                ToolUtil.Storage
                                                        .getValueString(MapShowAllFilterActivity.this, "noise")));
                                variansiAp2TypeA = kfAlgoAp2TypeA.get(4);

                                // KF Type B
                                kfAlgoAp2TypeB = Formula
                                        .applyKFAlgorithmTypeB(rssiKFQueueAp2, preRssiAp2KFTypeB, variansiAp2TypeB,
                                                Double.parseDouble(
                                                        ToolUtil.Storage
                                                                .getValueString(MapShowAllFilterActivity.this,
                                                                        "noise")));
                                preRssiAp2KFTypeB = kfAlgoAp2TypeB.get(3);
                                variansiAp2TypeB = kfAlgoAp2TypeB.get(4);

                                // Feedback
                                fbAlgoAp2 = Formula.applyFeedbackFilterAlgorithm(kfAlgoAp2TypeA.get(0),
                                        (double) scanResult.level,
                                        Double.parseDouble(ToolUtil.Storage
                                                .getValueString(MapShowAllFilterActivity.this, "alpha")));
                            }
                            iAp2 += 1;

                            rssiListAp2.add((double) scanResult.level);
                            rssiKFListAp2.add(kfAlgoAp2TypeA.get(3));

                            ToolUtil.Storage.setValueInt(MapShowAllFilterActivity.this, "i_kalman_ap2", iAp2);
                            ToolUtil.Storage.setValueString(MapShowAllFilterActivity.this, "pre_rssi_ap2",
                                    String.valueOf(preRssiAp2KFTypeB));

                            ToolUtil.Storage.setValueString(MapShowAllFilterActivity.this, "rssi_kalman_ap2_type_a",
                                    String.valueOf(kfAlgoAp2TypeA.get(3)));
                            ToolUtil.Storage.setValueString(MapShowAllFilterActivity.this, "var_kalman_ap2_type_a",
                                    String.valueOf(variansiAp2TypeA));

                            ToolUtil.Storage.setValueString(MapShowAllFilterActivity.this, "rssi_kalman_ap2_type_b",
                                    String.valueOf(kfAlgoAp2TypeB.get(3)));
                            ToolUtil.Storage.setValueString(MapShowAllFilterActivity.this, "var_kalman_ap2_type_b",
                                    String.valueOf(variansiAp2TypeB));

                            accessPoint = new AccessPoint(
                                    scanResult.SSID,
                                    String.valueOf(scanResult.level) + " dBm",
                                    String.valueOf(scanResult.frequency) + " MHz",
                                    scanResult.capabilities,
                                    Formula.distance((double) scanResult.level, Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MapShowAllFilterActivity.this, "n"))),
                                    String.valueOf(level),
                                    scanResult.BSSID,
                                    String.valueOf(kfAlgoAp2TypeA.get(3)) + " dBm",
                                    String.valueOf(kfAlgoAp2TypeB.get(3)) + " dBm",
                                    String.valueOf(fbAlgoAp2.get(3)) + " dBm",
                                    Formula.distance
                                            (kfAlgoAp2TypeA.get(3), Double.parseDouble(ToolUtil.Storage
                                                    .getValueString(MapShowAllFilterActivity.this, "n"))),
                                    Formula.distance
                                            (kfAlgoAp2TypeB.get(3), Double.parseDouble(ToolUtil.Storage
                                                    .getValueString(MapShowAllFilterActivity.this, "n"))),
                                    Formula.distance(
                                            fbAlgoAp2.get(3), Double.parseDouble(ToolUtil.Storage
                                                    .getValueString(MapShowAllFilterActivity.this, "n")))
                            );

                            ToolUtil.Storage.setValueString(MapShowAllFilterActivity.this, "d2",
                                    accessPoint.getDistance());
                            ToolUtil.Storage.setValueString(MapShowAllFilterActivity.this, "dist_kalman_ap2_type_a",
                                    accessPoint.getDistanceKalmanTypeA());
                            ToolUtil.Storage.setValueString(MapShowAllFilterActivity.this, "dist_kalman_ap2_type_b",
                                    accessPoint.getDistanceKalmanTypeB());
                            ToolUtil.Storage.setValueString(MapShowAllFilterActivity.this, "dist_feedback_ap2",
                                    accessPoint.getDistanceFeedback());

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
                                                ToolUtil.Storage
                                                        .getValueString(MapShowAllFilterActivity.this, "noise")));
                                variansiAp3TypeA = kfAlgoAp3TypeA.get(4);

                                // KF Type B
                                kfAlgoAp3TypeB = Formula.applyKFAlgorithmTypeB(rssiKFQueueAp3, preRssiAp3KFTypeB, 1,
                                        Double.parseDouble(
                                                ToolUtil.Storage
                                                        .getValueString(MapShowAllFilterActivity.this, "noise")));
                                preRssiAp3KFTypeB = kfAlgoAp3TypeB.get(3);
                                variansiAp3TypeB = kfAlgoAp3TypeB.get(4);

                                // Feedback
                                fbAlgoAp3 = Formula.applyFeedbackFilterAlgorithm(0, (double) scanResult.level,
                                        Double.parseDouble(ToolUtil.Storage
                                                .getValueString(MapShowAllFilterActivity.this, "alpha")));
                            } else {
                                // KF Type A
                                kfAlgoAp3TypeA = Formula.applyKFAlgorithmTypeA(rssiKFQueueAp3, variansiAp3TypeA,
                                        Double.parseDouble(
                                                ToolUtil.Storage
                                                        .getValueString(MapShowAllFilterActivity.this, "noise")));
                                variansiAp3TypeA = kfAlgoAp3TypeA.get(4);

                                // KF Type B
                                kfAlgoAp3TypeB = Formula
                                        .applyKFAlgorithmTypeB(rssiKFQueueAp3, preRssiAp3KFTypeB, variansiAp3TypeB,
                                                Double.parseDouble(
                                                        ToolUtil.Storage
                                                                .getValueString(MapShowAllFilterActivity.this,
                                                                        "noise")));
                                preRssiAp3KFTypeB = kfAlgoAp3TypeB.get(3);
                                variansiAp3TypeB = kfAlgoAp3TypeB.get(4);

                                // Feedback
                                fbAlgoAp3 = Formula.applyFeedbackFilterAlgorithm(kfAlgoAp3TypeA.get(0),
                                        (double) scanResult.level,
                                        Double.parseDouble(ToolUtil.Storage
                                                .getValueString(MapShowAllFilterActivity.this, "alpha")));
                            }
                            iAp3 += 1;

                            rssiListAp3.add((double) scanResult.level);
                            rssiKFListAp3.add(kfAlgoAp3TypeA.get(3));

                            ToolUtil.Storage.setValueInt(MapShowAllFilterActivity.this, "i_kalman_ap3", iAp3);
                            ToolUtil.Storage.setValueString(MapShowAllFilterActivity.this, "pre_rssi_ap3",
                                    String.valueOf(preRssiAp3KFTypeB));

                            ToolUtil.Storage.setValueString(MapShowAllFilterActivity.this, "rssi_kalman_ap3_type_a",
                                    String.valueOf(kfAlgoAp3TypeA.get(3)));
                            ToolUtil.Storage.setValueString(MapShowAllFilterActivity.this, "var_kalman_ap3_type_a",
                                    String.valueOf(variansiAp3TypeA));

                            ToolUtil.Storage.setValueString(MapShowAllFilterActivity.this, "rssi_kalman_ap3_type_b",
                                    String.valueOf(kfAlgoAp3TypeB.get(3)));
                            ToolUtil.Storage.setValueString(MapShowAllFilterActivity.this, "var_kalman_ap3_type_b",
                                    String.valueOf(variansiAp3TypeB));

                            accessPoint = new AccessPoint(
                                    scanResult.SSID,
                                    String.valueOf(scanResult.level) + " dBm",
                                    String.valueOf(scanResult.frequency) + " MHz",
                                    scanResult.capabilities,
                                    Formula.distance((double) scanResult.level, Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MapShowAllFilterActivity.this, "n"))),
                                    String.valueOf(level),
                                    scanResult.BSSID,
                                    String.valueOf(kfAlgoAp3TypeA.get(3)) + " dBm",
                                    String.valueOf(kfAlgoAp3TypeB.get(3)) + " dBm",
                                    String.valueOf(fbAlgoAp3.get(3)) + " dBm",
                                    Formula.distance
                                            (kfAlgoAp3TypeA.get(3), Double.parseDouble(ToolUtil.Storage
                                                    .getValueString(MapShowAllFilterActivity.this, "n"))),
                                    Formula.distance
                                            (kfAlgoAp3TypeB.get(3), Double.parseDouble(ToolUtil.Storage
                                                    .getValueString(MapShowAllFilterActivity.this, "n"))),
                                    Formula.distance(
                                            fbAlgoAp3.get(3), Double.parseDouble(ToolUtil.Storage
                                                    .getValueString(MapShowAllFilterActivity.this, "n")))
                            );

                            ToolUtil.Storage.setValueString(MapShowAllFilterActivity.this, "d3",
                                    accessPoint.getDistance());
                            ToolUtil.Storage.setValueString(MapShowAllFilterActivity.this, "dist_kalman_ap3_type_a",
                                    accessPoint.getDistanceKalmanTypeA());
                            ToolUtil.Storage.setValueString(MapShowAllFilterActivity.this, "dist_kalman_ap3_type_b",
                                    accessPoint.getDistanceKalmanTypeB());
                            ToolUtil.Storage.setValueString(MapShowAllFilterActivity.this, "dist_feedback_ap3",
                                    accessPoint.getDistanceFeedback());

                            accessPointList.add(accessPoint);
                            break;
                        default:
                            accessPoint = new AccessPoint(
                                    scanResult.SSID,
                                    String.valueOf(scanResult.level) + " dBm",
                                    String.valueOf(scanResult.frequency) + " MHz",
                                    scanResult.capabilities,
                                    Formula.distance((double) scanResult.level, Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MapShowAllFilterActivity.this, "n"))),
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

                d1 = Double.parseDouble(ToolUtil.Storage.getValueString
                        (MapShowAllFilterActivity.this, "d1"));
                d2 = Double.parseDouble(ToolUtil.Storage.getValueString
                        (MapShowAllFilterActivity.this, "d2"));
                d3 = Double.parseDouble(ToolUtil.Storage.getValueString
                        (MapShowAllFilterActivity.this, "d3"));

                d1Kalman1 = Double.parseDouble(ToolUtil.Storage.getValueString
                        (MapShowAllFilterActivity.this, "dist_kalman_ap1_type_a"));
                d2Kalman1 = Double.parseDouble(ToolUtil.Storage.getValueString
                        (MapShowAllFilterActivity.this, "dist_kalman_ap2_type_a"));
                d3Kalman1 = Double.parseDouble(ToolUtil.Storage.getValueString
                        (MapShowAllFilterActivity.this, "dist_kalman_ap3_type_a"));

                d1Kalman2 = Double.parseDouble(ToolUtil.Storage.getValueString
                        (MapShowAllFilterActivity.this, "dist_kalman_ap1_type_b"));
                d2Kalman2 = Double.parseDouble(ToolUtil.Storage.getValueString
                        (MapShowAllFilterActivity.this, "dist_kalman_ap2_type_b"));
                d3Kalman2 = Double.parseDouble(ToolUtil.Storage.getValueString
                        (MapShowAllFilterActivity.this, "dist_kalman_ap3_type_b"));

                d1Feedback = Double.parseDouble(ToolUtil.Storage.getValueString
                        (MapShowAllFilterActivity.this, "dist_feedback_ap1"));
                d2Feedback = Double.parseDouble(ToolUtil.Storage.getValueString
                        (MapShowAllFilterActivity.this, "dist_feedback_ap2"));
                d3Feedback = Double.parseDouble(ToolUtil.Storage.getValueString
                        (MapShowAllFilterActivity.this, "dist_feedback_ap3"));

                xy = Formula.koordinat(
                        Double.valueOf(x1), Double.valueOf(y1), d1,
                        Double.valueOf(x2), Double.valueOf(y2), d2,
                        Double.valueOf(x3), Double.valueOf(y3), d3);

                xyKalman1 = Formula.koordinat(
                        Double.valueOf(x1), Double.valueOf(y1), d1Kalman1,
                        Double.valueOf(x2), Double.valueOf(y2), d2Kalman1,
                        Double.valueOf(x3), Double.valueOf(y3), d3Kalman1);

                xyKalman2 = Formula.koordinat(
                        Double.valueOf(x1), Double.valueOf(y1), d1Kalman2,
                        Double.valueOf(x2), Double.valueOf(y2), d2Kalman2,
                        Double.valueOf(x3), Double.valueOf(y3), d3Kalman2);

                xyFeedback = Formula.koordinat(
                        Double.valueOf(x1), Double.valueOf(y1), d1Feedback,
                        Double.valueOf(x2), Double.valueOf(y2), d2Feedback,
                        Double.valueOf(x3), Double.valueOf(y3), d3Feedback);

                ToolUtil.Storage
                        .setValueString(MapShowAllFilterActivity.this, "xPos",
                                String.valueOf(xy.get(0).floatValue()));
                ToolUtil.Storage
                        .setValueString(MapShowAllFilterActivity.this, "yPos",
                                String.valueOf(xy.get(1).floatValue()));
                ToolUtil.Storage
                        .setValueString(MapShowAllFilterActivity.this, "xPosKalman1",
                                String.valueOf(xyKalman1.get(0).floatValue
                                        ()));
                ToolUtil.Storage
                        .setValueString(MapShowAllFilterActivity.this, "yPosKalman1",
                                String.valueOf(xyKalman1.get(1).floatValue
                                        ()));
                ToolUtil.Storage
                        .setValueString(MapShowAllFilterActivity.this, "xPosKalman2",
                                String.valueOf(xyKalman2.get(0).floatValue
                                        ()));
                ToolUtil.Storage
                        .setValueString(MapShowAllFilterActivity.this, "yPosKalman2",
                                String.valueOf(xyKalman2.get(1).floatValue
                                        ()));
                ToolUtil.Storage
                        .setValueString(MapShowAllFilterActivity.this, "xPosFeedback",
                                String.valueOf(xyFeedback.get(0).floatValue
                                        ()));
                ToolUtil.Storage
                        .setValueString(MapShowAllFilterActivity.this, "yPosFeedback",
                                String.valueOf(xyFeedback.get(1).floatValue
                                        ()));

                xPos = ToolUtil.Storage.getValueString(MapShowAllFilterActivity.this, "xPos");
                yPos = ToolUtil.Storage.getValueString(MapShowAllFilterActivity.this, "yPos");
                xPosKalman1 = ToolUtil.Storage.getValueString(MapShowAllFilterActivity.this, "xPosKalman1");
                yPosKalman1 = ToolUtil.Storage.getValueString(MapShowAllFilterActivity.this, "yPosKalman1");
                xPosKalman2 = ToolUtil.Storage.getValueString(MapShowAllFilterActivity.this, "xPosKalman2");
                yPosKalman2 = ToolUtil.Storage.getValueString(MapShowAllFilterActivity.this, "yPosKalman2");
                xPosFeedback = ToolUtil.Storage.getValueString(MapShowAllFilterActivity.this, "xPosFeedback");
                yPosFeedback = ToolUtil.Storage.getValueString(MapShowAllFilterActivity.this, "yPosFeedback");

                mapView.getOverLays().remove(locationOverlay);
                mapView.getOverLays().remove(locationOverlayKalman1);
                mapView.getOverLays().remove(locationOverlayKalman2);
                mapView.getOverLays().remove(locationOverlayFeedback);

                locationOverlayKalman1 = new SVGMapLocationOverlay(mapView, "kalman1");
                locationOverlayKalman1.setPosition(
                        new PointF(Float.valueOf(xPosKalman1), Float.valueOf(yPosKalman1)));

                locationOverlayKalman2 = new SVGMapLocationOverlay(mapView, "kalman2");
                locationOverlayKalman2.setPosition(
                        new PointF(Float.valueOf(xPosKalman2), Float.valueOf(yPosKalman2)));

                locationOverlayFeedback = new SVGMapLocationOverlay(mapView, "feedback");
                locationOverlayFeedback.setPosition(
                        new PointF(Float.valueOf(xPosFeedback), Float.valueOf(yPosFeedback)));

                locationOverlay = new SVGMapLocationOverlay(mapView, "default");
                locationOverlay.setPosition(
                        new PointF(Float.valueOf(xPos), Float.valueOf(yPos)));

                mapView.getOverLays().add(locationOverlay);
                mapView.getOverLays().add(locationOverlayKalman1);
                mapView.getOverLays().add(locationOverlayKalman2);
                mapView.getOverLays().add(locationOverlayFeedback);
                mapView.refresh();
            }

            Collections.sort(accessPointList, new ApComparator());

            wifiManager.startScan();
        }
    }

}
