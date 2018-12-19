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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
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
import java.util.Collections;
import java.util.List;


public class LocationOverlayActivity extends AppCompatActivity {

    private SVGMapView mapView;

    private List<AccessPoint> accessPointList = new ArrayList<>();

    String x1, y1, x2, y2, x3, y3, d1, d2, d3, xPos, yPos, bssid1, bssid2, bssid3;

    WifiManager wifiManager;

    WifiScanReceiver wifiReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_location);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Indoor Map");

        Intent intent = getIntent();
        x1 = ToolUtil.Storage.getValueString(this, "x1");
        y1 = ToolUtil.Storage.getValueString(this, "y1");
        x2 = ToolUtil.Storage.getValueString(this, "x2");
        y2 = ToolUtil.Storage.getValueString(this, "y2");
        x3 = ToolUtil.Storage.getValueString(this, "x3");
        y3 = ToolUtil.Storage.getValueString(this, "y3");
        d1 = ToolUtil.Storage.getValueString(this, "d1");
        d2 = ToolUtil.Storage.getValueString(this, "d2");
        d3 = ToolUtil.Storage.getValueString(this, "d3");
        xPos = ToolUtil.Storage.getValueString(this, "xPos");
        yPos = ToolUtil.Storage.getValueString(this, "yPos");
        bssid1 = ToolUtil.Storage.getValueString(this, "Bssid1");
        bssid2 = ToolUtil.Storage.getValueString(this, "Bssid2");
        bssid3 = ToolUtil.Storage.getValueString(this, "Bssid3");

        if (x1.equals("") && y1.equals("") && x2.equals("") && y2.equals("")
                && x3.equals("") && y3.equals("")
                && d1.equals("") && d2.equals("") && d3.equals("")
                && xPos.equals("") && yPos.equals("")) {
            ToolUtil.Storage.setValueString(LocationOverlayActivity.this, "x1", "0");
            ToolUtil.Storage.setValueString(LocationOverlayActivity.this, "y1", "0");
            ToolUtil.Storage.setValueString(LocationOverlayActivity.this, "x2", "0");
            ToolUtil.Storage.setValueString(LocationOverlayActivity.this, "y2", "0");
            ToolUtil.Storage.setValueString(LocationOverlayActivity.this, "x3", "0");
            ToolUtil.Storage.setValueString(LocationOverlayActivity.this, "y3", "0");
            ToolUtil.Storage.setValueString(LocationOverlayActivity.this, "d1", "0");
            ToolUtil.Storage.setValueString(LocationOverlayActivity.this, "d2", "0");
            ToolUtil.Storage.setValueString(LocationOverlayActivity.this, "d3", "0");
            ToolUtil.Storage.setValueString(LocationOverlayActivity.this, "xPos", "0");
            ToolUtil.Storage.setValueString(LocationOverlayActivity.this, "yPos", "0");
        }

//        loadData();

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new LocationOverlayActivity.WifiScanReceiver();
        wifiManager.startScan();

        mapView = findViewById(R.id.location_mapview);

        mapView.registerMapViewListener(new SVGMapViewListener() {
            @Override
            public void onGetCurrentMap(Bitmap bitmap) {
            }

            @Override
            public void onMapLoadComplete() {
                SVGMapLocationOverlay locationOverlay = new SVGMapLocationOverlay(mapView);
                locationOverlay.setIndicatorArrowBitmap(
                        BitmapFactory.decodeResource(getResources(), R.mipmap.indicator_arrow));

//                locationOverlay.setPosition(new PointF(xy.get(0).floatValue(), xy.get(1).floatValue()));
//                locationOverlay.setIndicatorCircleRotateDegree(90);
//                locationOverlay.setMode(SVGMapLocationOverlay.MODE_COMPASS);
//                locationOverlay.setIndicatorArrowRotateDegree(-45);
//                mapView.getOverLays().add(locationOverlay);
                mapView.refresh();
            }

            @Override
            public void onMapLoadError() {
            }
        });
        mapView.loadMap(AssetsHelper.getContent(this, "gedung_e_v5.svg"));
//        Toast.makeText(this, x1 + " " + x2 + " " + x3 + " " + y1 + " " + y2 + " " + y3, Toast.LENGTH_SHORT).show();

        mapView.getController().sparkAtPoint(new PointF(Float.valueOf(x1), Float.valueOf(y1)), 75, Color.BLACK, 1000);
        mapView.getController().sparkAtPoint(new PointF(Float.valueOf(x2), Float.valueOf(y2)), 75, Color.GREEN, 1000);
        mapView.getController().sparkAtPoint(new PointF(Float.valueOf(x3), Float.valueOf(y3)), 75, Color.BLUE, 1000);
        mapView.getController().setScrollGestureEnabled(false);
        mapView.getController().setZoomGestureEnabled(false);
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
        unregisterReceiver(wifiReceiver);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(
                wifiReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        );
        super.onResume();
    }

    class WifiScanReceiver extends BroadcastReceiver {

        List<Double> rssiListAp1, rssiListAp2, rssiListAp3 = new ArrayList<>();

        List<Double> kfAlgoAp1, kfAlgoAp2, kfAlgoAp3 = new ArrayList<>();

        AccessPoint accessPoint;

        double variansiAp1, variansiAp2, variansiAp3 = 0;

        int iAp1, iAp2, iAp3 = 0;

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
                        case "60:de:f3:03:60:30": //AP1
                            rssiListAp1.add((double) scanResult.level);
                            if (iAp1 == 0) {
                                kfAlgoAp1 = KalmanFilter.applyKFAlgorithm(rssiListAp1, 1, 0.008);
                                variansiAp1 = kfAlgoAp1.get(4);
                            } else {
                                kfAlgoAp1 = KalmanFilter.applyKFAlgorithm(rssiListAp1, variansiAp1, 0.008);
                                variansiAp1 = kfAlgoAp1.get(4);
                            }
                            iAp1 += 1;

                            accessPoint = new AccessPoint(
                                    scanResult.SSID,
                                    String.valueOf(scanResult.level) + " dBm",
                                    String.valueOf(scanResult.frequency) + " MHz",
                                    scanResult.capabilities,
                                    Formula.distance((double) scanResult.level),
                                    String.valueOf(level),
                                    scanResult.BSSID,
                                    String.valueOf(kfAlgoAp1.get(3)),
                                    Formula.distance(kfAlgoAp1.get(3)));
                            accessPointList.add(accessPoint);
                            break;
                        case "6a:c6:3a:d6:9c:92":
                            rssiListAp2.add((double) scanResult.level);
                            if (iAp2 == 0) {
                                kfAlgoAp2 = KalmanFilter.applyKFAlgorithm(rssiListAp2, 1, 0.008);
                                variansiAp2 = kfAlgoAp2.get(4);
                            } else {
                                kfAlgoAp2 = KalmanFilter.applyKFAlgorithm(rssiListAp2, variansiAp2, 0.008);
                                variansiAp2 = kfAlgoAp2.get(4);
                            }
                            iAp2 += 1;

                            accessPoint = new AccessPoint(
                                    scanResult.SSID,
                                    String.valueOf(scanResult.level) + " dBm",
                                    String.valueOf(scanResult.frequency) + " MHz",
                                    scanResult.capabilities,
                                    Formula.distance(scanResult.level),
                                    String.valueOf(level),
                                    scanResult.BSSID,
                                    String.valueOf(kfAlgoAp2.get(3)),
                                    Formula.distance(kfAlgoAp2.get(3)));
                            accessPointList.add(accessPoint);
                            break;
                        case "be:dd:c2:fe:3b:0b":
                            rssiListAp3.add((double) scanResult.level);
                            if (iAp3 == 0) {
                                kfAlgoAp3 = KalmanFilter.applyKFAlgorithm(rssiListAp3, 1, 0.008);
                                variansiAp3 = kfAlgoAp3.get(4);
                            } else {
                                kfAlgoAp3 = KalmanFilter.applyKFAlgorithm(rssiListAp3, variansiAp3, 0.008);
                                variansiAp3 = kfAlgoAp3.get(4);
                            }
                            iAp3 += 1;

                            accessPoint = new AccessPoint(
                                    scanResult.SSID,
                                    String.valueOf(scanResult.level) + " dBm",
                                    String.valueOf(scanResult.frequency) + " MHz",
                                    scanResult.capabilities,
                                    Formula.distance(scanResult.level),
                                    String.valueOf(level),
                                    scanResult.BSSID,
                                    String.valueOf(kfAlgoAp3.get(3)),
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
                                    "0", "0");
                            accessPointList.add(accessPoint);
                            break;
                    }
                }

                double d1 = 0, d2 = 0, d3 = 0;
//                for (AccessPoint a : accessPointList) {
//                    if (a.getBssid().contains(bssid1)) {
//                        d1 = Double.parseDouble(a.getDistance().substring(0, a.getDistance().length() - 2));
//                        Log.d("=======d1 ", "onMapLoadComplete: " + d1);
//                        ToolUtil.Storage.setValueString(LocationOverlayActivity.this, "d1", String.valueOf(d1));
//                    } else if (a.getBssid().contains(bssid2)) {
//                        d2 = Double.parseDouble(a.getDistance().substring(0, a.getDistance().length() - 2));
//                        Log.d("=======d2 ", "onMapLoadComplete: " + d2);
//                        ToolUtil.Storage.setValueString(LocationOverlayActivity.this, "d2", String.valueOf(d2));
//                    } else if (a.getBssid().contains(bssid3)) {
//                        d3 = Double.parseDouble(a.getDistance().substring(0, a.getDistance().length() - 2));
//                        Log.d("=======d3 ", "onMapLoadComplete: " + d3);
//                        ToolUtil.Storage.setValueString(LocationOverlayActivity.this, "d3", String.valueOf(d3));
//                    }
//                }

                for (AccessPoint a : accessPointList) {
                    switch (a.getBssid()) {
                        case "b6:e6:2d:23:84:90":
                            d1 = Double.parseDouble(a.getDistanceKalman());
                            Log.d("=======d1 ", "onMapLoadComplete: " + d1);
                            ToolUtil.Storage.setValueString(LocationOverlayActivity.this, "d1", String.valueOf(d1));
                            break;
                        case "6a:c6:3a:d6:9c:92":
                            d2 = Double.parseDouble(a.getDistanceKalman());
                            Log.d("=======d2 ", "onMapLoadComplete: " + d2);
                            ToolUtil.Storage.setValueString(LocationOverlayActivity.this, "d2", String.valueOf(d2));
                            break;
                        case "be:dd:c2:fe:3b:0b":
                            d3 = Double.parseDouble(a.getDistanceKalman());
                            Log.d("=======d3 ", "onMapLoadComplete: " + d3);
                            ToolUtil.Storage.setValueString(LocationOverlayActivity.this, "d3", String.valueOf(d3));
                            break;
                    }
                }

                List<Double> xy;
                xy = Formula.koordinat(
                        Double.valueOf(x1), Double.valueOf(y1), d1,
                        Double.valueOf(x2), Double.valueOf(y2), d2,
                        Double.valueOf(x3), Double.valueOf(y3), d3);
                Log.d("============", "onMapLoadComplete: " + xy);
                ToolUtil.Storage
                        .setValueString(LocationOverlayActivity.this, "xPos", String.valueOf(xy.get(0).floatValue()));
                ToolUtil.Storage
                        .setValueString(LocationOverlayActivity.this, "yPos", String.valueOf(xy.get(1).floatValue()));

                wifiManager.startScan();

                String x = ToolUtil.Storage.getValueString(LocationOverlayActivity.this, "xPos");
                String y = ToolUtil.Storage.getValueString(LocationOverlayActivity.this, "yPos");

                mapView.getController()
                        .sparkAtPoint(new PointF(Float.valueOf(x), Float.valueOf(y)), 15, Color.RED, 1000);
                mapView.refresh();
            }

            Collections.sort(accessPointList, new ApComparator());

        }
    }

    private void calculateRssiMean() {

    }

    private void loadData() {
        accessPointList.clear();

        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
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
                        Formula.distance(scanResult.level) + " m",
                        String.valueOf(level),
                        scanResult.BSSID, "0", "0");
                accessPointList.add(accessPoint);

            }
        }

        Collections.sort(accessPointList, new ApComparator());

        Toast.makeText(this, "Jumlah Access Point Terdekat: " + accessPointList.size(), Toast.LENGTH_SHORT).show();
    }
}
