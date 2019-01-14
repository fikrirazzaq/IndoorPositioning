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
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.juvetic.rssi.R;
import com.juvetic.rssi.model.AccessPoint;
import com.juvetic.rssi.util.ApComparator;
import com.juvetic.rssi.util.ToolUtil;
import com.juvetic.rssi.util.formulas.Formula;
import com.juvetic.rssi.util.helper.AssetsHelper;
import id.recharge.library.SVGMapView;
import id.recharge.library.SVGMapViewListener;
import id.recharge.library.overlay.SVGMapLocationOverlay;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class MapShowAllFilterActivity extends BaseActivity {

    public static final String EXTRA_FILTER = "extra_filter";

    private static final String TAG = MapShowAllFilterActivity.class.getSimpleName();

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
            case R.id.menu_main_export:
                saveExcelFile(MapShowAllFilterActivity.this, "List RSSI and Position.xls",
                        rssiListAp1, rssiKFListAp1,
                        rssiListAp2, rssiKFListAp2,
                        rssiListAp3, rssiKFListAp3,
                        rssiKFListAp1v2, rssiKFListAp2v2,
                        rssiKFListAp3v2, rssiFBListAp1,
                        rssiFBListAp2, rssiFBListAp3,
                        xRaw, yRaw,
                        xKF1, yKF1,
                        xKF2, yKF2,
                        xFB, yFB);
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
                            rssiKFListAp1v2.add(kfAlgoAp1TypeB.get(3));
                            rssiFBListAp1.add(fbAlgoAp1.get(3));

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
                            rssiKFListAp2v2.add(kfAlgoAp2TypeB.get(3));
                            rssiFBListAp2.add(fbAlgoAp2.get(3));

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
                            rssiKFListAp3v2.add(kfAlgoAp3TypeB.get(3));
                            rssiFBListAp3.add(fbAlgoAp3.get(3));

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

                xRaw.add(Math.round(xy.get(0)));
                yRaw.add(Math.round(xy.get(1)));

                xyKalman1 = Formula.koordinat(
                        Double.valueOf(x1), Double.valueOf(y1), d1Kalman1,
                        Double.valueOf(x2), Double.valueOf(y2), d2Kalman1,
                        Double.valueOf(x3), Double.valueOf(y3), d3Kalman1);

                xKF1.add(Math.round(xyKalman1.get(0)));
                yKF1.add(Math.round(xyKalman1.get(1)));

                xyKalman2 = Formula.koordinat(
                        Double.valueOf(x1), Double.valueOf(y1), d1Kalman2,
                        Double.valueOf(x2), Double.valueOf(y2), d2Kalman2,
                        Double.valueOf(x3), Double.valueOf(y3), d3Kalman2);

                xKF2.add(Math.round(xyKalman2.get(0)));
                yKF2.add(Math.round(xyKalman2.get(1)));

                xyFeedback = Formula.koordinat(
                        Double.valueOf(x1), Double.valueOf(y1), d1Feedback,
                        Double.valueOf(x2), Double.valueOf(y2), d2Feedback,
                        Double.valueOf(x3), Double.valueOf(y3), d3Feedback);

                xFB.add(Math.round(xyFeedback.get(0)));
                yFB.add(Math.round(xyFeedback.get(1)));

                ToolUtil.Storage
                        .setValueString(MapShowAllFilterActivity.this, "xPos",
                                String.valueOf(Math.round(xy.get(0))));
                ToolUtil.Storage
                        .setValueString(MapShowAllFilterActivity.this, "yPos",
                                String.valueOf(Math.round(xy.get(1))));
                ToolUtil.Storage
                        .setValueString(MapShowAllFilterActivity.this, "xPosKalman1",
                                String.valueOf(Math.round(xyKalman1.get(0))));
                ToolUtil.Storage
                        .setValueString(MapShowAllFilterActivity.this, "yPosKalman1",
                                String.valueOf(Math.round(xyKalman1.get(1))));
                ToolUtil.Storage
                        .setValueString(MapShowAllFilterActivity.this, "xPosKalman2",
                                String.valueOf(Math.round(xyKalman2.get(0))));
                ToolUtil.Storage
                        .setValueString(MapShowAllFilterActivity.this, "yPosKalman2",
                                String.valueOf(Math.round(xyKalman2.get(1))));
                ToolUtil.Storage
                        .setValueString(MapShowAllFilterActivity.this, "xPosFeedback",
                                String.valueOf(Math.round(xyFeedback.get(0))));
                ToolUtil.Storage
                        .setValueString(MapShowAllFilterActivity.this, "yPosFeedback",
                                String.valueOf(Math.round(xyFeedback.get(1))));

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

                float x_smooth = Float.valueOf(xPos);
                if (x_smooth < MIN_X) {
                    x_smooth = MIN_X;
                } else if (x_smooth > MAX_X) {
                    x_smooth = MAX_X;
                }

                float y_smooth = Float.valueOf(yPos);
                if (y_smooth < MIN_Y) {
                    y_smooth = MIN_Y;
                } else if (y_smooth > MAX_Y) {
                    y_smooth = MAX_Y;
                }

                float x_smooth_kalman1 = Float.valueOf(xPosKalman1);
                if (x_smooth_kalman1 < MIN_X) {
                    x_smooth_kalman1 = MIN_X;
                } else if (x_smooth_kalman1 > MAX_X) {
                    x_smooth_kalman1 = MAX_X;
                }

                float y_smooth_kalman1 = Float.valueOf(yPosKalman1);
                if (y_smooth_kalman1 < MIN_Y) {
                    y_smooth_kalman1 = MIN_Y;
                } else if (y_smooth > MAX_Y) {
                    y_smooth_kalman1 = MAX_Y;
                }

                float x_smooth_kalman2 = Float.valueOf(xPosKalman2);
                if (x_smooth_kalman2 < MIN_X) {
                    x_smooth_kalman2 = MIN_X;
                } else if (x_smooth_kalman2 > MAX_X) {
                    x_smooth_kalman2 = MAX_X;
                }

                float y_smooth_kalman2 = Float.valueOf(yPosKalman2);
                if (y_smooth_kalman2 < MIN_Y) {
                    y_smooth_kalman2 = MIN_Y;
                } else if (y_smooth_kalman2 > MAX_Y) {
                    y_smooth_kalman2 = MAX_Y;
                }

                float x_smooth_feedback = Float.valueOf(xPosFeedback);
                if (x_smooth_feedback < MIN_X) {
                    x_smooth_feedback = MIN_X;
                } else if (x_smooth_feedback > MAX_X) {
                    x_smooth_feedback = MAX_X;
                }

                float y_smooth_feedback = Float.valueOf(yPosFeedback);
                if (y_smooth_feedback < MIN_Y) {
                    y_smooth_feedback = MIN_Y;
                } else if (y_smooth_feedback > MAX_Y) {
                    y_smooth_feedback = MAX_Y;
                }

                locationOverlayKalman1 = new SVGMapLocationOverlay(mapView, "kalman1");
                locationOverlayKalman1.setPosition(
                        new PointF(x_smooth_kalman1, y_smooth_kalman1));

                locationOverlayKalman2 = new SVGMapLocationOverlay(mapView, "kalman2");
                locationOverlayKalman2.setPosition(
                        new PointF(x_smooth_kalman2, y_smooth_kalman2));

                locationOverlayFeedback = new SVGMapLocationOverlay(mapView, "feedback");
                locationOverlayFeedback.setPosition(
                        new PointF(x_smooth_feedback, y_smooth_feedback));

                locationOverlay = new SVGMapLocationOverlay(mapView, "default");
                locationOverlay.setPosition(
                        new PointF(x_smooth, y_smooth));

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

    private static boolean saveExcelFile(Context context, String fileName,
            ArrayList<Double> rssiListAp1, ArrayList<Double> rssiKFListAp1,
            ArrayList<Double> rssiListAp2, ArrayList<Double> rssiKFListAp2,
            ArrayList<Double> rssiListAp3, ArrayList<Double> rssiKFListAp3,
            ArrayList<Double> rssiKFListAp1v2, ArrayList<Double> rssiKFListAp2v2,
            ArrayList<Double> rssiKFListAp3v2, ArrayList<Double> rssiFBListAp1,
            ArrayList<Double> rssiFBListAp2, ArrayList<Double> rssiFBListAp3,
            ArrayList<Long> xRaw, ArrayList<Long> yRaw,
            ArrayList<Long> xKF1, ArrayList<Long> yKF1,
            ArrayList<Long> xKF2, ArrayList<Long> yKF2,
            ArrayList<Long> xFeedback, ArrayList<Long> yFeedback) {

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
        Sheet sheetXRaw = wb.createSheet("X Raw");
        Sheet sheetYRaw = wb.createSheet("Y Raw");
        Sheet sheetXKF1 = wb.createSheet("X KFv1");
        Sheet sheetYKF1 = wb.createSheet("Y KFv1");
        Sheet sheetXKF2 = wb.createSheet("X KFv2");
        Sheet sheetYKF2 = wb.createSheet("Y KFv2");
        Sheet sheetXFB = wb.createSheet("X Feedback");
        Sheet sheetYFB = wb.createSheet("Y Feedback");

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

        // XY
        for (int i = 0; i < xRaw.size(); i++) {
            sheetXRaw.createRow(i).createCell(0).setCellValue(xRaw.get(i));
            sheetYRaw.createRow(i).createCell(0).setCellValue(yRaw.get(i));

            sheetXKF1.createRow(i).createCell(0).setCellValue(xKF1.get(i));
            sheetYKF1.createRow(i).createCell(0).setCellValue(yKF1.get(i));

            sheetXKF2.createRow(i).createCell(0).setCellValue(xKF2.get(i));
            sheetYKF2.createRow(i).createCell(0).setCellValue(yKF2.get(i));

            sheetXFB.createRow(i).createCell(0).setCellValue(xFeedback.get(i));
            sheetYFB.createRow(i).createCell(0).setCellValue(yFeedback.get(i));
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
