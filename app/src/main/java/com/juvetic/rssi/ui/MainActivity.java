package com.juvetic.rssi.ui;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;
import com.juvetic.rssi.R;
import com.juvetic.rssi.model.AccessPoint;
import com.juvetic.rssi.util.ApComparator;
import com.juvetic.rssi.util.Formula;
import com.juvetic.rssi.util.RecyclerTouchListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;

    private List<AccessPoint> accessPointList = new ArrayList<>();

    private ApAdapter mAdapter;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("List Wifi Strength");

        recyclerView = findViewById(R.id.recycler_view);
        loadData();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView,
                new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        AccessPoint ap = accessPointList.get(position);
                        Toast.makeText(getApplicationContext(), ap.getName() + " is selected!", Toast.LENGTH_SHORT)
                                .show();
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));

        loadData();

        swipeRefreshLayout = findViewById(R.id.swp_refresh_ap);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                loadData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

// Level of current connection
//        int rssi = wifiManager.getConnectionInfo().getRssi();
//        int level = WifiManager.calculateSignalLevel(rssi, 5);
//        Log.i("INJEKSI BOS", "Level is " + level + " out of 5");
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
                loadData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void loadData() {
        accessPointList.clear();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> wifiList = wifiManager.getScanResults();
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

        Collections.sort(accessPointList, new ApComparator());

        mAdapter = new ApAdapter(accessPointList);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        Toast.makeText(this, "Jumlah Access Point: " + accessPointList.size(), Toast.LENGTH_SHORT).show();
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();

        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_bottom);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
}