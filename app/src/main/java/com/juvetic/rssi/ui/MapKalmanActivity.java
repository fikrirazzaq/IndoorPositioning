package com.juvetic.rssi.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.juvetic.rssi.R;
import com.juvetic.rssi.model.AccessPoint;
import com.juvetic.rssi.util.ApComparator;
import com.juvetic.rssi.util.ToolUtil;
import com.juvetic.rssi.util.formulas.Formula;
import com.juvetic.rssi.util.formulas.KalmanFilter;
import com.juvetic.rssi.util.helper.AssetsHelper;
import id.recharge.library.SVGMapView;
import id.recharge.library.SVGMapViewListener;
import id.recharge.library.overlay.SVGMapLocationOverlay;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class MapKalmanActivity extends BaseActivity {

    private SVGMapView mapView;

    private List<AccessPoint> accessPointList = new ArrayList<>();

    WifiManager wifiManager;

    WifiScanReceiver wifiReceiver;

    AccessPoint accessPoint;

    SVGMapLocationOverlay locationOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_location);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Map with KF");

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new MapKalmanActivity.WifiScanReceiver();
        wifiManager.startScan();

        mapView = findViewById(R.id.location_mapview);
    }

    //78:8a:20:d4:a4:d8
    //78:8a:20:d4:a9:74
    //78:8a:20:d4:ac:28
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
                InformationDialog bottomSheetDialog = InformationDialog.getInstance();
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
        mapView.loadMap(AssetsHelper.getContent(this, "gedung_e_v4.svg"));

        mapView.getController().sparkAtPoint(new PointF(Float.valueOf(x1), Float.valueOf(y1)), 10, Color.RED, 1000);
        mapView.getController().sparkAtPoint(new PointF(Float.valueOf(x2), Float.valueOf(y2)), 10, Color.GREEN, 1000);
        mapView.getController().sparkAtPoint(new PointF(Float.valueOf(x3), Float.valueOf(y3)), 10, Color.BLUE, 1000);
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
                            rssiListAp1 = tinydb.getQueueDouble("rssi_kalman_list_ap1");
                            rssiListAp1.add((double) scanResult.level);
                            tinydb.putQueueDouble("rssi_kalman_list_ap1", rssiListAp1);

                            if (iAp1 == 0) {
                                kfAlgoAp1 = KalmanFilter.applyKFAlgorithm(rssiListAp1, 1, Double.parseDouble
                                        (ToolUtil.Storage.getValueString(MapKalmanActivity.this, "noise")));
                                variansiAp1 = kfAlgoAp1.get(4);
                            } else {
                                kfAlgoAp1 = KalmanFilter.applyKFAlgorithm(rssiListAp1, variansiAp1, Double.parseDouble
                                        (ToolUtil.Storage.getValueString(MapKalmanActivity.this, "noise")));
                                variansiAp1 = kfAlgoAp1.get(4);
                            }
                            iAp1 += 1;

                            ToolUtil.Storage.setValueString(MapKalmanActivity.this, "rssi_kalman_ap1",
                                    String.valueOf(kfAlgoAp1.get(3)));
                            ToolUtil.Storage.setValueInt(MapKalmanActivity.this, "i_kalman_ap1",
                                    iAp1);
                            ToolUtil.Storage.setValueString(MapKalmanActivity.this, "var_kalman_ap1",
                                    String.valueOf(variansiAp1));
                            ToolUtil.Storage.setValueString(MapKalmanActivity.this, "dist_kalman_ap1",
                                    Formula.distance(kfAlgoAp1.get(3), Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MapKalmanActivity.this, "n"))));

                            accessPoint = new AccessPoint(
                                    scanResult.SSID,
                                    String.valueOf(scanResult.level) + " dBm",
                                    String.valueOf(scanResult.frequency) + " MHz",
                                    scanResult.capabilities,
                                    Formula.distance((double) scanResult.level, Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MapKalmanActivity.this, "n"))),
                                    String.valueOf(level),
                                    scanResult.BSSID,
                                    String.valueOf(kfAlgoAp1.get(3)),
                                    Formula.distance(kfAlgoAp1.get(3), Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MapKalmanActivity.this, "n"))));
                            accessPointList.add(accessPoint);
                            break;
                        case "6a:c6:3a:d6:9c:92": //2
                            rssiListAp2 = tinydb.getQueueDouble("rssi_kalman_list_ap2");
                            rssiListAp2.add((double) scanResult.level);
                            tinydb.putQueueDouble("rssi_kalman_list_ap2", rssiListAp2);

                            if (iAp2 == 0) {
                                kfAlgoAp2 = KalmanFilter.applyKFAlgorithm(rssiListAp2, 1, 0.008);
                                variansiAp2 = kfAlgoAp2.get(4);
                            } else {
                                kfAlgoAp2 = KalmanFilter.applyKFAlgorithm(rssiListAp2, variansiAp2, 0.008);
                                variansiAp2 = kfAlgoAp2.get(4);
                            }
                            iAp2 += 1;

                            ToolUtil.Storage.setValueString(MapKalmanActivity.this, "rssi_kalman_ap2",
                                    String.valueOf(kfAlgoAp2.get(3)));
                            ToolUtil.Storage.setValueInt(MapKalmanActivity.this, "i_kalman_ap2",
                                    iAp2);
                            ToolUtil.Storage.setValueString(MapKalmanActivity.this, "var_kalman_ap2",
                                    String.valueOf(variansiAp2));
                            ToolUtil.Storage.setValueString(MapKalmanActivity.this, "dist_kalman_ap2",
                                    Formula.distance(kfAlgoAp2.get(3), Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MapKalmanActivity.this, "n"))));

                            accessPoint = new AccessPoint(
                                    scanResult.SSID,
                                    String.valueOf(scanResult.level) + " dBm",
                                    String.valueOf(scanResult.frequency) + " MHz",
                                    scanResult.capabilities,
                                    Formula.distance(scanResult.level, Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MapKalmanActivity.this, "n"))),
                                    String.valueOf(level),
                                    scanResult.BSSID,
                                    String.valueOf(kfAlgoAp2.get(3)),
                                    Formula.distance(kfAlgoAp2.get(3), Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MapKalmanActivity.this, "n"))));
                            accessPointList.add(accessPoint);
                            break;
                        case "be:dd:c2:fe:3b:0b": //AP3
                            rssiListAp3 = tinydb.getQueueDouble("rssi_kalman_list_ap3");
                            rssiListAp3.add((double) scanResult.level);
                            tinydb.putQueueDouble("rssi_kalman_list_ap3", rssiListAp3);

                            if (iAp3 == 0) {
                                kfAlgoAp3 = KalmanFilter.applyKFAlgorithm(rssiListAp3, 1, 0.008);
                                variansiAp3 = kfAlgoAp3.get(4);
                            } else {
                                kfAlgoAp3 = KalmanFilter.applyKFAlgorithm(rssiListAp3, variansiAp3, 0.008);
                                variansiAp3 = kfAlgoAp3.get(4);
                            }
                            iAp3 += 1;

                            ToolUtil.Storage.setValueString(MapKalmanActivity.this, "rssi_kalman_ap3",
                                    String.valueOf(kfAlgoAp3.get(3)));
                            ToolUtil.Storage.setValueInt(MapKalmanActivity.this, "i_kalman_ap3",
                                    iAp3);
                            ToolUtil.Storage.setValueString(MapKalmanActivity.this, "var_kalman_ap3",
                                    String.valueOf(variansiAp3));
                            ToolUtil.Storage.setValueString(MapKalmanActivity.this, "dist_kalman_ap3",
                                    Formula.distance(kfAlgoAp3.get(3), Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MapKalmanActivity.this, "n"))));

                            accessPoint = new AccessPoint(
                                    scanResult.SSID,
                                    String.valueOf(scanResult.level) + " dBm",
                                    String.valueOf(scanResult.frequency) + " MHz",
                                    scanResult.capabilities,
                                    Formula.distance(scanResult.level, Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MapKalmanActivity.this, "n"))),
                                    String.valueOf(level),
                                    scanResult.BSSID,
                                    String.valueOf(kfAlgoAp3.get(3)),
                                    Formula.distance(kfAlgoAp3.get(3), Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MapKalmanActivity.this, "n"))));
                            accessPointList.add(accessPoint);
                            break;
                        default:
                            accessPoint = new AccessPoint(
                                    scanResult.SSID,
                                    String.valueOf(scanResult.level) + " dBm",
                                    String.valueOf(scanResult.frequency) + " MHz",
                                    scanResult.capabilities,
                                    Formula.distance(scanResult.level, Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MapKalmanActivity.this, "n"))),
                                    String.valueOf(level),
                                    scanResult.BSSID,
                                    "0", "0");
                            accessPointList.add(accessPoint);
                            break;
                    }
                }

                Log.d(MapKalmanActivity.class.getSimpleName(), "onReceive: RSSI_1 " + Arrays.toString
                        (rssiListAp1.toArray()));
                Log.d(MapKalmanActivity.class.getSimpleName(), "onReceive: RSSI_2 " + Arrays.toString
                        (rssiListAp2.toArray()));
                Log.d(MapKalmanActivity.class.getSimpleName(), "onReceive: RSSI_3 " + Arrays.toString
                        (rssiListAp3.toArray()));

                Log.d(MapKalmanActivity.class.getSimpleName(),
                        "onReceive: D1 " + ToolUtil.Storage.getValueString
                                (MapKalmanActivity.this, "dist_kalman_ap1"));
                Log.d(MapKalmanActivity.class.getSimpleName(), "onReceive: D2 " + ToolUtil.Storage
                        .getValueString
                                (MapKalmanActivity.this, "dist_kalman_ap2"));
                Log.d(MapKalmanActivity.class.getSimpleName(), "onReceive: D3 " + ToolUtil.Storage
                        .getValueString
                                (MapKalmanActivity.this, "dist_kalman_ap3"));

                xy = Formula.koordinat(
                        Double.valueOf(x1), Double.valueOf(y1), Double.parseDouble(ToolUtil.Storage.getValueString
                                (MapKalmanActivity.this, "dist_kalman_ap1")),
                        Double.valueOf(x2), Double.valueOf(y2), Double.parseDouble(ToolUtil.Storage.getValueString
                                (MapKalmanActivity.this, "dist_kalman_ap2")),
                        Double.valueOf(x3), Double.valueOf(y3), Double.parseDouble(ToolUtil.Storage.getValueString
                                (MapKalmanActivity.this, "dist_kalman_ap3")));
                ToolUtil.Storage
                        .setValueString(MapKalmanActivity.this, "xPos", String.valueOf(xy.get(0).floatValue()));
                ToolUtil.Storage
                        .setValueString(MapKalmanActivity.this, "yPos", String.valueOf(xy.get(1).floatValue()));

                Log.d(MapKalmanActivity.class.getSimpleName(),
                        "onReceive: X " + String.valueOf(xy.get(0).floatValue()));
                Log.d(MapKalmanActivity.class.getSimpleName(),
                        "onReceive: Y " + String.valueOf(xy.get(1).floatValue()));

                xPos = ToolUtil.Storage.getValueString(MapKalmanActivity.this, "xPos");
                yPos = ToolUtil.Storage.getValueString(MapKalmanActivity.this, "yPos");

                mapView.getOverLays().remove(locationOverlay);
                locationOverlay = new SVGMapLocationOverlay(mapView);
                locationOverlay.setIndicatorArrowBitmap(
                        BitmapFactory.decodeResource(getResources(), R.mipmap.indicator_arrow));
                locationOverlay.setPosition(new PointF(Float.valueOf(xPos), Float.valueOf(yPos)));
                locationOverlay.setIndicatorCircleRotateDegree(90);
                locationOverlay.setMode(SVGMapLocationOverlay.MODE_NORMAL);
                mapView.getOverLays().add(locationOverlay);
//                mapView.getController()
//                        .sparkAtPoint(new PointF(Float.valueOf(xPos), Float.valueOf(yPos)), 15, Color.RED, 1000);
                mapView.refresh();
            }

            Collections.sort(accessPointList, new ApComparator());

            wifiManager.startScan();
        }
    }
}
