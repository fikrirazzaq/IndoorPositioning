package com.juvetic.rssi.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.juvetic.rssi.R;
import com.juvetic.rssi.util.ToolUtil;

public class ApDeployActivity extends AppCompatActivity {

    EditText x1, y1, x2, y2, x3, y3;
    Button btnSave;

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

        btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(view -> {
            ToolUtil.Storage.setValueString(this, "x1", x1.getText().toString());
            ToolUtil.Storage.setValueString(this, "y1", y1.getText().toString());
            ToolUtil.Storage.setValueString(this, "x2", x2.getText().toString());
            ToolUtil.Storage.setValueString(this, "y2", y2.getText().toString());
            ToolUtil.Storage.setValueString(this, "x3", x3.getText().toString());
            ToolUtil.Storage.setValueString(this, "y3", y3.getText().toString());

            Toast.makeText(this, "Data saved.", Toast.LENGTH_SHORT).show();
        });

        x1.setText(ToolUtil.Storage.getValueString(this, "x1"));
        y1.setText(ToolUtil.Storage.getValueString(this, "y1"));
        x2.setText(ToolUtil.Storage.getValueString(this, "x2"));
        y2.setText(ToolUtil.Storage.getValueString(this, "y2"));
        x3.setText(ToolUtil.Storage.getValueString(this, "x3"));
        y3.setText(ToolUtil.Storage.getValueString(this, "y3"));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
