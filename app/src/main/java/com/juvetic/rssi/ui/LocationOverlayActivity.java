package com.juvetic.rssi.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import com.juvetic.rssi.R;
import com.juvetic.rssi.util.helper.AssetsHelper;
import id.recharge.library.SVGMapView;
import id.recharge.library.SVGMapViewListener;
import id.recharge.library.overlay.SVGMapLocationOverlay;


public class LocationOverlayActivity extends AppCompatActivity {

    private SVGMapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_location);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Indoor Map");

        mapView = (SVGMapView) findViewById(R.id.location_mapview);

        mapView.registerMapViewListener(new SVGMapViewListener() {
            @Override
            public void onGetCurrentMap(Bitmap bitmap) {
            }

            @Override
            public void onMapLoadComplete() {
                SVGMapLocationOverlay locationOverlay = new SVGMapLocationOverlay(mapView);
                locationOverlay.setIndicatorArrowBitmap(
                        BitmapFactory.decodeResource(getResources(), R.mipmap.indicator_arrow));
                locationOverlay.setPosition(new PointF(400, 500));
//                locationOverlay.setIndicatorCircleRotateDegree(90);
//                locationOverlay.setMode(SVGMapLocationOverlay.MODE_COMPASS);
//                locationOverlay.setIndicatorArrowRotateDegree(-45);
                mapView.getOverLays().add(locationOverlay);
                mapView.refresh();
            }

            @Override
            public void onMapLoadError() {
            }
        });
        mapView.loadMap(AssetsHelper.getContent(this, "sample2.svg"));

        mapView.getController().sparkAtPoint(new PointF(600, 660), 75, Color.BLACK, 1000);
        mapView.getController().sparkAtPoint(new PointF(1350, 660), 75, Color.GREEN, 1000);
        mapView.getController().sparkAtPoint(new PointF(2300, 660), 75, Color.BLUE, 1000);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
