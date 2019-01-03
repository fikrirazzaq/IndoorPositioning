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
import android.util.Log;
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

public class MapActivity extends BaseActivity {

    private SVGMapView mapView;

    private List<AccessPoint> accessPointList = new ArrayList<>();

    WifiManager wifiManager;

    WifiScanReceiver wifiReceiver;

    AccessPoint accessPoint;

    SVGMapLocationOverlay locationOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Map - No Filter");

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new MapActivity.WifiScanReceiver();
        wifiManager.startScan();

        mapView = findViewById(R.id.location_mapview);

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
                InformationDialogMap bottomSheetDialog = InformationDialogMap.getInstance();
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

        mapView.getController().sparkAtPoint(new PointF(Float.valueOf(x1), Float.valueOf(y1)), 20, Color.RED, 1000);
        mapView.getController().sparkAtPoint(new PointF(Float.valueOf(x2), Float.valueOf(y2)), 20, Color.GREEN, 1000);
        mapView.getController().sparkAtPoint(new PointF(Float.valueOf(x3), Float.valueOf(y3)), 20, Color.BLUE, 1000);
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

                            accessPoint = new AccessPoint(
                                    scanResult.SSID,
                                    String.valueOf(scanResult.level) + " dBm",
                                    String.valueOf(scanResult.frequency) + " MHz",
                                    scanResult.capabilities,
                                    Formula.distance((double) scanResult.level, Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MapActivity.this, "n"))),
                                    String.valueOf(level),
                                    scanResult.BSSID,
                                    "0", "0");
                            accessPointList.add(accessPoint);

                            d1 = accessPoint.getDistance();
                            Log.d("=======d1 ", "onMapLoadComplete: " + d1);
                            ToolUtil.Storage.setValueString(MapActivity.this, "d1", String.valueOf(d1));
                            break;
                        case "6a:c6:3a:d6:9c:92": //2
                            accessPoint = new AccessPoint(
                                    scanResult.SSID,
                                    String.valueOf(scanResult.level) + " dBm",
                                    String.valueOf(scanResult.frequency) + " MHz",
                                    scanResult.capabilities,
                                    Formula.distance(scanResult.level, Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MapActivity.this, "n"))),
                                    String.valueOf(level),
                                    scanResult.BSSID,
                                    "0", "0");
                            d2 = accessPoint.getDistance();
                            Log.d("=======d2 ", "onMapLoadComplete: " + d2);
                            ToolUtil.Storage.setValueString(MapActivity.this, "d2", String.valueOf(d2));
                            break;
                        case "be:dd:c2:fe:3b:0b": //AP3
                            accessPoint = new AccessPoint(
                                    scanResult.SSID,
                                    String.valueOf(scanResult.level) + " dBm",
                                    String.valueOf(scanResult.frequency) + " MHz",
                                    scanResult.capabilities,
                                    Formula.distance(scanResult.level, Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MapActivity.this, "n"))),
                                    String.valueOf(level),
                                    scanResult.BSSID,
                                    "0", "0");
                            accessPointList.add(accessPoint);
                            d3 = accessPoint.getDistance();
                            Log.d("=======d3 ", "onMapLoadComplete: " + d3);
                            ToolUtil.Storage.setValueString(MapActivity.this, "d3", String.valueOf(d3));
                            break;
                        default:
                            accessPoint = new AccessPoint(
                                    scanResult.SSID,
                                    String.valueOf(scanResult.level) + " dBm",
                                    String.valueOf(scanResult.frequency) + " MHz",
                                    scanResult.capabilities,
                                    "0",
                                    String.valueOf(level),
                                    scanResult.BSSID,
                                    "0", "0");
                            accessPointList.add(accessPoint);
                            break;
                    }
                }

                xy = Formula.koordinat(
                        Double.valueOf(x1), Double.valueOf(y1), Double.parseDouble(ToolUtil.Storage.getValueString
                                (MapActivity.this, "d1")),
                        Double.valueOf(x2), Double.valueOf(y2), Double.parseDouble(ToolUtil.Storage.getValueString
                                (MapActivity.this, "d2")),
                        Double.valueOf(x3), Double.valueOf(y3), Double.parseDouble(ToolUtil.Storage.getValueString
                                (MapActivity.this, "d3")));
                ToolUtil.Storage
                        .setValueString(MapActivity.this, "xPos", String.valueOf(xy.get(0).floatValue()));
                ToolUtil.Storage
                        .setValueString(MapActivity.this, "yPos", String.valueOf(xy.get(1).floatValue()));

                Log.d(MapFilterActivity.class.getSimpleName(),
                        "onReceive: X " + String.valueOf(xy.get(0).floatValue()));
                Log.d(MapFilterActivity.class.getSimpleName(),
                        "onReceive: Y " + String.valueOf(xy.get(1).floatValue()));

                xPos = ToolUtil.Storage.getValueString(MapActivity.this, "xPos");
                yPos = ToolUtil.Storage.getValueString(MapActivity.this, "yPos");

                mapView.getOverLays().remove(locationOverlay);

                locationOverlay = new SVGMapLocationOverlay(mapView);
                locationOverlay.setPosition(new PointF(Float.valueOf(xPos), Float.valueOf(yPos)));

                mapView.getOverLays().add(locationOverlay);
                mapView.refresh();
            }

            Collections.sort(accessPointList, new ApComparator());

            wifiManager.startScan();
        }
    }
}
