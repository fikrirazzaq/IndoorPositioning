package com.juvetic.rssi.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.juvetic.rssi.R;

public class DashboardActivity extends AppCompatActivity {

    Button list, map;
    EditText x1, y1, x2, y2, x3, y3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        list = findViewById(R.id.btn_list);
        map = findViewById(R.id.btn_map);
        x1 = findViewById(R.id.tx_x1);
        y1 = findViewById(R.id.tx_y1);
        x2 = findViewById(R.id.tx_x2);
        y2 = findViewById(R.id.tx_y2);
        x3 = findViewById(R.id.tx_x3);
        y3 = findViewById(R.id.tx_y3);

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
                if (x1.getText().toString().equals("") || y1.getText().toString().equals("")
                        || x2.getText().toString().equals("") || y2.getText().toString().equals("")
                        || x3.getText().toString().equals("") || y3.getText().toString().equals("")) {
                    Toast.makeText(DashboardActivity.this, "Harus diisi", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(DashboardActivity.this, LocationOverlayActivity.class);
                    intent.putExtra("x1", x1.getText().toString());
                    intent.putExtra("y1", y1.getText().toString());
                    intent.putExtra("x2", x2.getText().toString());
                    intent.putExtra("y2", y2.getText().toString());
                    intent.putExtra("x3", x3.getText().toString());
                    intent.putExtra("y3", y3.getText().toString());
                    startActivity(intent);
                }

            }
        });
    }
}
