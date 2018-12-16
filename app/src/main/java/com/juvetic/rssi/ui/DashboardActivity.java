package com.juvetic.rssi.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.juvetic.rssi.R;
import com.juvetic.rssi.util.PageUtil;

public class DashboardActivity extends AppCompatActivity implements OnClickListener {

    Button list, map, apdeploy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        list = findViewById(R.id.btn_list);
        map = findViewById(R.id.btn_map);
        apdeploy = findViewById(R.id.btn_apdeploy);

        list.setOnClickListener(this);
        map.setOnClickListener(this);
        apdeploy.setOnClickListener(this);

    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.btn_list:
                PageUtil.getInstance().jumpToPage(DashboardActivity.this, MainActivity.class);
                break;
            case R.id.btn_apdeploy:
                PageUtil.getInstance().jumpToPage(DashboardActivity.this, ApDeployActivity.class);
                break;
            case R.id.btn_map:
                PageUtil.getInstance().jumpToPage(DashboardActivity.this, LocationOverlayActivity.class);
                break;
        }
    }
}
