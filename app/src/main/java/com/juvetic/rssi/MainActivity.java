package com.juvetic.rssi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<AccessPoint> accessPointList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ApAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);

        mAdapter = new ApAdapter(accessPointList);

        recyclerView.setHasFixedSize(true);

        // vertical RecyclerView
        // keep movie_list_row.xml width to `match_parent`
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        // adding inbuilt divider line
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // adding custom divider line with padding 16dp
        // recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.HORIZONTAL, 16));
//        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);

        runLayoutAnimation(recyclerView);

        // row click listener
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                AccessPoint movie = accessPointList.get(position);
                Toast.makeText(getApplicationContext(), movie.getName() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        // Level of a Scan Result
        List<ScanResult> wifiList = wifiManager.getScanResults();
        for (ScanResult scanResult : wifiList) {
            int level = WifiManager.calculateSignalLevel(scanResult.level, 4);

            AccessPoint accessPoint = new AccessPoint(
                    scanResult.SSID,
                    String.valueOf(scanResult.level),
                    String.valueOf(scanResult.frequency),
                    scanResult.capabilities,
                    String.valueOf(distance(scanResult.level)),
                    String.valueOf(level),
                    scanResult.BSSID);
            accessPointList.add(accessPoint);

        }

// Level of current connection
//        int rssi = wifiManager.getConnectionInfo().getRssi();
//        int level = WifiManager.calculateSignalLevel(rssi, 5);
//        Log.i("INJEKSI BOS", "Level is " + level + " out of 5");
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();

        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_bottom);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private float distance(int rssi) {
        int d0 = 1;
        int p = -40;
        int n = 2;
        return d0*(10^((p - rssi)/(n*10)));
    }
}