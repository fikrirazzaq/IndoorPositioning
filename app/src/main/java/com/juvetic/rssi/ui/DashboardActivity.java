package com.juvetic.rssi.ui;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.juvetic.rssi.R;
import com.juvetic.rssi.util.PageUtil;
import com.juvetic.rssi.util.ToolUtil;

public class DashboardActivity extends BaseActivity implements OnClickListener {

    Button list, map, apdeploy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        String[] PERMS_INITIAL = {
                Manifest.permission.ACCESS_FINE_LOCATION,
        };
        ActivityCompat.requestPermissions(this, PERMS_INITIAL, 127);

        list = findViewById(R.id.btn_list);
        map = findViewById(R.id.btn_map);
        apdeploy = findViewById(R.id.btn_apdeploy);

        list.setOnClickListener(this);
        map.setOnClickListener(this);
        apdeploy.setOnClickListener(this);

        ToolUtil.Storage.setValueString(this, "rssi_kalman_api1",
                String.valueOf(0));
        ToolUtil.Storage.setValueString(this, "rssi_kalman_api2",
                String.valueOf(0));
        ToolUtil.Storage.setValueString(this, "rssi_kalman_api3",
                String.valueOf(0));
        ToolUtil.Storage.setValueInt(this,"i_kalman_ap1", 0);
        ToolUtil.Storage.setValueInt(this,"i_kalman_ap2", 0);
        ToolUtil.Storage.setValueInt(this,"i_kalman_ap3", 0);
        ToolUtil.Storage.setValueString(this, "var_kalman_ap1",
                String.valueOf(0));
        ToolUtil.Storage.setValueString(this, "var_kalman_ap2",
                String.valueOf(0));
        ToolUtil.Storage.setValueString(this, "var_kalman_ap3",
                String.valueOf(0));
        ToolUtil.Storage.setValueString(this, "dist_kalman_ap1",
                String.valueOf(0));
        ToolUtil.Storage.setValueString(this, "dist_kalman_ap2",
                String.valueOf(0));
        ToolUtil.Storage.setValueString(this, "dist_kalman_ap3",
                String.valueOf(0));
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
