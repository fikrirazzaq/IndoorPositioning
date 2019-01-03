package com.juvetic.rssi.ui;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.juvetic.rssi.R;
import com.juvetic.rssi.util.PageUtil;
import com.juvetic.rssi.util.ToolUtil;

public class DashboardActivity extends BaseActivity implements OnClickListener {

    Button list, mapKalman, apdeploy, mapNonKalman, mapKalmanB, mapFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        String[] PERMS_INITIAL = {
                Manifest.permission.ACCESS_FINE_LOCATION,
        };
        ActivityCompat.requestPermissions(this, PERMS_INITIAL, 127);

        list = findViewById(R.id.btn_list);
        mapKalman = findViewById(R.id.btn_map_kalman);
        mapNonKalman = findViewById(R.id.btn_map_non_kalman);
        apdeploy = findViewById(R.id.btn_apdeploy);
        mapKalmanB = findViewById(R.id.btn_map_kalman_type_b);
        mapFeedback = findViewById(R.id.btn_map_feedback);

        list.setOnClickListener(this);
        mapKalman.setOnClickListener(this);
        mapNonKalman.setOnClickListener(this);
        apdeploy.setOnClickListener(this);
        mapKalmanB.setOnClickListener(this);
        mapFeedback.setOnClickListener(this);

        initVal();
    }

    private void initVal() {
        ToolUtil.Storage.setValueString(this, "rssi_kalman_api1_type_a",
                String.valueOf(0));
        ToolUtil.Storage.setValueString(this, "rssi_kalman_api2_type_a",
                String.valueOf(0));
        ToolUtil.Storage.setValueString(this, "rssi_kalman_api3_type_a",
                String.valueOf(0));
        ToolUtil.Storage.setValueInt(this, "i_kalman_ap1", 0);
        ToolUtil.Storage.setValueInt(this, "i_kalman_ap2", 0);
        ToolUtil.Storage.setValueInt(this, "i_kalman_ap3", 0);
        ToolUtil.Storage.setValueString(this, "var_kalman_ap1_type_a",
                String.valueOf(0));
        ToolUtil.Storage.setValueString(this, "var_kalman_ap2_type_a",
                String.valueOf(0));
        ToolUtil.Storage.setValueString(this, "var_kalman_ap3_type_a",
                String.valueOf(0));
        ToolUtil.Storage.setValueString(this, "dist_kalman_ap1_type_a",
                String.valueOf(0));
        ToolUtil.Storage.setValueString(this, "dist_kalman_ap2_type_a",
                String.valueOf(0));
        ToolUtil.Storage.setValueString(this, "dist_kalman_ap3_type_a",
                String.valueOf(0));
        ToolUtil.Storage.setValueString(this, "d1",
                String.valueOf(0));
        ToolUtil.Storage.setValueString(this, "d2",
                String.valueOf(0));
        ToolUtil.Storage.setValueString(this, "d3",
                String.valueOf(0));

        ToolUtil.Storage.setValueString(this, "rssi_kalman_api1_type_b",
                String.valueOf(0));
        ToolUtil.Storage.setValueString(this, "rssi_kalman_api2_type_b",
                String.valueOf(0));
        ToolUtil.Storage.setValueString(this, "rssi_kalman_api3_type_b",
                String.valueOf(0));
        ToolUtil.Storage.setValueString(this, "var_kalman_ap1_type_b",
                String.valueOf(0));
        ToolUtil.Storage.setValueString(this, "var_kalman_ap2_type_b",
                String.valueOf(0));
        ToolUtil.Storage.setValueString(this, "var_kalman_ap3_type_b",
                String.valueOf(0));
        ToolUtil.Storage.setValueString(this, "dist_kalman_ap1_type_b",
                String.valueOf(0));
        ToolUtil.Storage.setValueString(this, "dist_kalman_ap2_type_b",
                String.valueOf(0));
        ToolUtil.Storage.setValueString(this, "dist_kalman_ap3_type_b",
                String.valueOf(0));

        ToolUtil.Storage.setValueString(this, "pre_rssi_ap1",
                String.valueOf(0));
        ToolUtil.Storage.setValueString(this, "pre_rssi_ap2",
                String.valueOf(0));
        ToolUtil.Storage.setValueString(this, "pre_rssi_ap3",
                String.valueOf(0));
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.btn_list:
                if (noise.equals("") || n.equals("") || alpha.equals("")) {
                    Toast.makeText(this, "Set Filter Input in AP Deploy", Toast.LENGTH_SHORT).show();
                } else {
                    PageUtil.getInstance().jumpToPage(DashboardActivity.this, MainActivity.class);
                }
                break;
            case R.id.btn_apdeploy:
                PageUtil.getInstance().jumpToPage(DashboardActivity.this, ApDeployActivity.class);
                break;
            case R.id.btn_map_kalman:
                if (x1.equals("") || y1.equals("") || x2.equals("") || y2.equals("")
                        || x3.equals("") || y3.equals("")
                        || noise.equals("") || n.equals("") || alpha.equals("")) {
                    Toast.makeText(this, "Please fill AP Deploy", Toast.LENGTH_SHORT).show();
                } else {
                    PageUtil.getInstance().jumpToMap(DashboardActivity.this, MapFilterActivity.class,
                            "kalman1");
                }
                break;
            case R.id.btn_map_non_kalman:
                if (x1.equals("") || y1.equals("") || x2.equals("") || y2.equals("")
                        || x3.equals("") || y3.equals("")
                        || noise.equals("") || n.equals("") || alpha.equals("")) {
                    Toast.makeText(this, "Please fill AP Deploy", Toast.LENGTH_SHORT).show();
                } else {
                    PageUtil.getInstance().jumpToPage(DashboardActivity.this, MapActivity.class);
                }
                break;

            case R.id.btn_map_kalman_type_b:
                if (x1.equals("") || y1.equals("") || x2.equals("") || y2.equals("")
                        || x3.equals("") || y3.equals("")
                        || noise.equals("") || n.equals("") || alpha.equals("")) {
                    Toast.makeText(this, "Please fill AP Deploy", Toast.LENGTH_SHORT).show();
                } else {
                    PageUtil.getInstance().jumpToMap(DashboardActivity.this, MapFilterActivity.class,
                            "kalman2");
                }
                break;

            case R.id.btn_map_feedback:
                if (x1.equals("") || y1.equals("") || x2.equals("") || y2.equals("")
                        || x3.equals("") || y3.equals("")
                        || noise.equals("") || n.equals("") || alpha.equals("")) {
                    Toast.makeText(this, "Please fill AP Deploy", Toast.LENGTH_SHORT).show();
                } else {
                    PageUtil.getInstance().jumpToMap(DashboardActivity.this, MapFilterActivity.class,
                            "feedback");
                }
                break;
        }
    }
}
