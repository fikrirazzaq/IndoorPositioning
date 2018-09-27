package com.juvetic.rssi.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.juvetic.rssi.R;

public class DashboardActivity extends AppCompatActivity {

    Button list, map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        list = findViewById(R.id.btn_list);
        map = findViewById(R.id.btn_map);

        list.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        map.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(DashboardActivity.this, LocationOverlayActivity.class);
                startActivity(intent);
            }
        });
    }
}
