package com.juvetic.rssi.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.google.common.collect.EvictingQueue;
import com.juvetic.rssi.R;
import com.juvetic.rssi.util.ToolUtil;
import java.util.Queue;

public class ApDeployActivity extends BaseActivity {

    EditText x1, y1, x2, y2, x3, y3, edtBssidAp1, edtBssidAp2, edtBssidAp3, edtNoiseQ, edtN, edtAlpha;

    RadioButton rdBtnDefault, rdBtnLainnya;

    RadioGroup rgAp;

    Button btnSave, btnResetKf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ap_deploy);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("AP Deploy");

        x1 = findViewById(R.id.edt_x1);
        y1 = findViewById(R.id.edt_y1);
        x2 = findViewById(R.id.edt_x2);
        y2 = findViewById(R.id.edt_y2);
        x3 = findViewById(R.id.edt_x3);
        y3 = findViewById(R.id.edt_y3);
        edtBssidAp1 = findViewById(R.id.edt_ap1);
        edtBssidAp2 = findViewById(R.id.edt_ap2);
        edtBssidAp3 = findViewById(R.id.edt_ap3);
        btnResetKf = findViewById(R.id.btn_reset_kf);
        edtNoiseQ = findViewById(R.id.edt_noise);
        edtN = findViewById(R.id.edt_n);
        edtAlpha = findViewById(R.id.edt_alpha);

        setupRadioGroupAp();

        btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(view -> {
            ToolUtil.Storage.setValueString(this, "x1", x1.getText().toString());
            ToolUtil.Storage.setValueString(this, "y1", y1.getText().toString());
            ToolUtil.Storage.setValueString(this, "x2", x2.getText().toString());
            ToolUtil.Storage.setValueString(this, "y2", y2.getText().toString());
            ToolUtil.Storage.setValueString(this, "x3", x3.getText().toString());
            ToolUtil.Storage.setValueString(this, "y3", y3.getText().toString());
            ToolUtil.Storage.setValueString(this, "noise", edtNoiseQ.getText().toString());
            ToolUtil.Storage.setValueString(this, "n", edtN.getText().toString());
            ToolUtil.Storage.setValueString(this, "alpha", edtAlpha.getText().toString());

            if (String.valueOf(rgAp.getCheckedRadioButtonId()).contains("67")) {
                ToolUtil.Storage.setValueString(this, "Bssid1", "b6:e6:2d:23:84:90");
                ToolUtil.Storage.setValueString(this, "Bssid2", "6a:c6:3a:d6:9c:92");
                ToolUtil.Storage.setValueString(this, "Bssid3", "be:dd:c2:fe:3b:0b");
            } else {
                ToolUtil.Storage.setValueString(this, "Bssid1", edtBssidAp1.getText().toString());
                ToolUtil.Storage.setValueString(this, "Bssid2", edtBssidAp2.getText().toString());
                ToolUtil.Storage.setValueString(this, "Bssid3", edtBssidAp3.getText().toString());
            }

            Toast.makeText(this, "Data saved.", Toast.LENGTH_SHORT).show();
        });

        btnResetKf.setOnClickListener(view -> {
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

            ToolUtil.Storage.setValueString(this, "dist_feedback_ap1",
                    String.valueOf(0));
            ToolUtil.Storage.setValueString(this, "dist_feedback_ap2",
                    String.valueOf(0));
            ToolUtil.Storage.setValueString(this, "dist_feedback_ap3",
                    String.valueOf(0));

            ToolUtil.Storage.setValueString(this, "pre_rssi_ap1",
                    String.valueOf(0));
            ToolUtil.Storage.setValueString(this, "pre_rssi_ap2",
                    String.valueOf(0));
            ToolUtil.Storage.setValueString(this, "pre_rssi_ap3",
                    String.valueOf(0));

            Queue<Double> rssiListAp1 = EvictingQueue.create(10);
            Queue<Double> rssiListAp2 = EvictingQueue.create(10);
            Queue<Double> rssiListAp3 = EvictingQueue.create(10);

            tinydb.putQueueDouble("rssi_kalman_list_ap1", rssiListAp1);
            tinydb.putQueueDouble("rssi_kalman_list_ap2", rssiListAp2);
            tinydb.putQueueDouble("rssi_kalman_list_ap3", rssiListAp3);

            Toast.makeText(this, "KF Calculation has been reset.", Toast.LENGTH_SHORT).show();
        });

        setupTextValue();
    }

    private void setupRadioGroupAp() {
        rdBtnDefault = findViewById(R.id.rdbtn_default);
        rdBtnLainnya = findViewById(R.id.rdbtn_lainnya);
        rgAp = findViewById(R.id.rg_ap);

        edtBssidAp1.setVisibility(View.GONE);
        edtBssidAp2.setVisibility(View.GONE);
        edtBssidAp3.setVisibility(View.GONE);

        rgAp.setOnCheckedChangeListener((group, checkedId) -> {

            if (String.valueOf(checkedId).contains("67")) {
                edtBssidAp1.setVisibility(View.GONE);
                edtBssidAp2.setVisibility(View.GONE);
                edtBssidAp3.setVisibility(View.GONE);
            } else {
                edtBssidAp1.setVisibility(View.VISIBLE);
                edtBssidAp2.setVisibility(View.VISIBLE);
                edtBssidAp3.setVisibility(View.VISIBLE);
            }

        });
    }

    private void setupTextValue() {
        x1.setText(ToolUtil.Storage.getValueString(this, "x1"));
        y1.setText(ToolUtil.Storage.getValueString(this, "y1"));
        x2.setText(ToolUtil.Storage.getValueString(this, "x2"));
        y2.setText(ToolUtil.Storage.getValueString(this, "y2"));
        x3.setText(ToolUtil.Storage.getValueString(this, "x3"));
        y3.setText(ToolUtil.Storage.getValueString(this, "y3"));
        edtNoiseQ.setText(ToolUtil.Storage.getValueString(this, "noise"));
        edtN.setText(ToolUtil.Storage.getValueString(this, "n"));
        edtAlpha.setText(ToolUtil.Storage.getValueString(this, "alpha"));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        setupTextValue();
        super.onResume();
    }
}
