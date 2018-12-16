package com.juvetic.rssi.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.juvetic.rssi.util.formulas.Formula;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<AccessPoint> accessPointList = new ArrayList<>();

    private ApAdapter mAdapter;

    private RecyclerView recyclerView;

    private ProgressBar progressBar;

    WifiManager wifiManager;

    WifiScanReceiver wifiReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("List Wifi Strength");

        String[] PERMS_INITIAL = {
                Manifest.permission.ACCESS_FINE_LOCATION,
        };
        ActivityCompat.requestPermissions(this, PERMS_INITIAL, 127);

        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
//        loadData();

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

        int countAp1 = 0;

        int countAp2 = 0;

        int countAp3 = 0;

        int sumAp1 = 0;

        int sumAp2 = 0;

        int sumAp3 = 0;

        double rataAp1 = 0;

        double rataAp2 = 0;

        double rataAp3 = 0;

        int rata2 = 0;

        @Override
        public void onReceive(final Context context, final Intent intent) {
            progressBar.setVisibility(View.GONE);
            accessPointList.clear();

            List<ScanResult> scanResultList = wifiManager.getScanResults();
            if (scanResultList != null) {
                for (ScanResult scanResult : scanResultList) {
                    int level = WifiManager.calculateSignalLevel(scanResult.level, 4);

                    switch (scanResult.BSSID) {
                        case "b6:e6:2d:23:84:90":
                            countAp1 += 1;
                            sumAp1 += scanResult.level;
                            rataAp1 = sumAp1 / countAp1;
                            rata2 = (int) rataAp1;
                            break;
                        case "6a:c6:3a:d6:9c:92":
                            countAp2 += 1;
                            sumAp2 += scanResult.level;
                            rataAp2 = sumAp2 / countAp2;
                            rata2 = (int) rataAp2;
                            break;
                        case "be:dd:c2:fe:3b:0b":
                            countAp3 += 1;
                            sumAp3 += scanResult.level;
                            rataAp3 = sumAp3 / countAp3;
                            rata2 = (int) rataAp3;
                            break;
                        default:
                            rata2 = 0;
                            break;
                    }

                    AccessPoint accessPoint = new AccessPoint(
                            scanResult.SSID,
                            String.valueOf(scanResult.level) + " dBm",
                            String.valueOf(scanResult.frequency) + " MHz",
                            scanResult.capabilities,
                            Formula.distance(scanResult.level),
                            String.valueOf(level),
                            scanResult.BSSID,
                            String.valueOf(rata2));
                    accessPointList.add(accessPoint);
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
                        scanResult.BSSID, null);
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