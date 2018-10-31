package com.juvetic.rssi.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import com.juvetic.rssi.R;
import com.juvetic.rssi.model.AccessPoint;
import com.juvetic.rssi.util.ApComparator;
import com.juvetic.rssi.util.Formula;
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

    String x1, y1, x2, y2, x3, y3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_location);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Indoor Map");

        Intent intent = getIntent();
        x1 = intent.getStringExtra("x1");
        y1 = intent.getStringExtra("y1");
        x2 = intent.getStringExtra("x2");
        y2 = intent.getStringExtra("y2");
        x3 = intent.getStringExtra("x3");
        y3 = intent.getStringExtra("y3");

        loadData();

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

                double d1 = 0, d2 = 0, d3 = 0;
                for (AccessPoint a : accessPointList) {
                    switch (a.getBssid()) {
                        case "b6:e6:2d:23:84:90":
                            d1 = Double.parseDouble(a.getDistance().substring(0, a.getDistance().length()-2));
                            Log.d("=======d1 ", "onMapLoadComplete: " + d1);
                            break;
                        case "6a:c6:3a:d6:9c:92":
                            d2 = Double.parseDouble(a.getDistance().substring(0, a.getDistance().length()-2));
                            Log.d("=======d2 ", "onMapLoadComplete: " + d2);
                            break;
                        case "be:dd:c2:fe:3b:0b":
                            d3 = Double.parseDouble(a.getDistance().substring(0, a.getDistance().length()-2));
                            Log.d("=======d3 ", "onMapLoadComplete: " + d3);
                            break;
                    }
                }

                List<Double> xy = new ArrayList<>();
                xy = Formula.koordinat(
                        Double.valueOf(x1), Double.valueOf(x1), d1,
                        Double.valueOf(x2), Double.valueOf(y2), d2,
                        Double.valueOf(x3), Double.valueOf(y3), d3);
                Log.d("============", "onMapLoadComplete: " + xy);
                locationOverlay.setPosition(new PointF(xy.get(0).floatValue(), xy.get(1).floatValue()));
                locationOverlay.setIndicatorCircleRotateDegree(90);
                locationOverlay.setMode(SVGMapLocationOverlay.MODE_COMPASS);
                locationOverlay.setIndicatorArrowRotateDegree(-45);
                mapView.getOverLays().add(locationOverlay);
                mapView.refresh();
            }

            @Override
            public void onMapLoadError() {
            }
        });
        mapView.loadMap(AssetsHelper.getContent(this, "denah_gedung_e.svg"));

        mapView.getController().sparkAtPoint(new PointF(600, 660), 75, Color.BLACK, 1000);
        mapView.getController().sparkAtPoint(new PointF(1350, 660), 75, Color.GREEN, 1000);
        mapView.getController().sparkAtPoint(new PointF(2300, 660), 75, Color.BLUE, 1000);
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
                        scanResult.BSSID);
                accessPointList.add(accessPoint);

            }
        }

        Collections.sort(accessPointList, new ApComparator());

        Toast.makeText(this, "Jumlah Access Point Terdekat: " + accessPointList.size(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
