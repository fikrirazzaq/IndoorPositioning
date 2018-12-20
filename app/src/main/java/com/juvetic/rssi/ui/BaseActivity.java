package com.juvetic.rssi.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.juvetic.rssi.util.TinyDB;
import com.juvetic.rssi.util.ToolUtil;

public class BaseActivity extends AppCompatActivity {

    TinyDB tinydb;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tinydb = new TinyDB(this);
    }
}
