package com.juvetic.rssi.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.common.collect.EvictingQueue;
import com.juvetic.rssi.R;
import com.juvetic.rssi.model.AccessPoint;
import com.juvetic.rssi.util.ApComparator;
import com.juvetic.rssi.util.RecyclerTouchListener;
import com.juvetic.rssi.util.ToolUtil;
import com.juvetic.rssi.util.formulas.Formula;
import com.juvetic.rssi.util.formulas.KalmanFilter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    Parcelable recyclerViewState;

    private List<AccessPoint> accessPointList = new ArrayList<>();

    private ApAdapter mAdapter;

    private RecyclerView recyclerView;

    private ProgressBar progressBar, progressBarTop;

    WifiManager wifiManager;

    WifiScanReceiver wifiReceiver;

    Queue<Double> rssiKFQueueAp1 = EvictingQueue.create(10);

    Queue<Double> rssiKFQueueAp2 = EvictingQueue.create(10);

    Queue<Double> rssiKFQueueAp3 = EvictingQueue.create(10);

    ArrayList<Double> rssiListAp1 = new ArrayList<>();

    ArrayList<Double> rssiListAp2 = new ArrayList<>();

    ArrayList<Double> rssiListAp3 = new ArrayList<>();

    ArrayList<Double> rssiKFListAp1 = new ArrayList<>();

    ArrayList<Double> rssiKFListAp2 = new ArrayList<>();

    ArrayList<Double> rssiKFListAp3 = new ArrayList<>();

    ArrayList<Double> kfAlgoAp1 = new ArrayList<>();

    ArrayList<Double> kfAlgoAp2 = new ArrayList<>();

    ArrayList<Double> kfAlgoAp3 = new ArrayList<>();

    ArrayList<Double> elseList = new ArrayList<>();

    AccessPoint accessPoint;

    double variansiAp1 = 0;

    double variansiAp2 = 0;

    double variansiAp3 = 0;

    int iAp1 = 0;

    int iAp2 = 0;

    int iAp3 = 0;

    int refreshCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("List Wifi Strength");

        recyclerView = findViewById(R.id.recycler_view);
        progressBarTop = findViewById(R.id.progress_bar_top);
        progressBarTop.setVisibility(View.VISIBLE);
//        loadData();

        variansiAp1 = Double.parseDouble(ToolUtil.Storage.getValueString(this, "var_kalman_ap1"));
        variansiAp2 = Double.parseDouble(ToolUtil.Storage.getValueString(this, "var_kalman_ap2"));
        variansiAp3 = Double.parseDouble(ToolUtil.Storage.getValueString(this, "var_kalman_ap3"));
        iAp1 = ToolUtil.Storage.getValueInt(this, "i_kalman_ap1");
        iAp2 = ToolUtil.Storage.getValueInt(this, "i_kalman_ap2");
        iAp3 = ToolUtil.Storage.getValueInt(this, "i_kalman_ap3");

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiScanReceiver();
        wifiManager.startScan();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView,
                new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));
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
//                loadData();
                wifiManager.startScan();
                progressBarTop.setVisibility(View.VISIBLE);
                return true;
            case R.id.menu_main_export:
                saveExcelFile(MainActivity.this, "List RSSI.xls",
                        rssiListAp1, rssiKFListAp1,
                        rssiListAp2, rssiKFListAp2,
                        rssiListAp3, rssiKFListAp3);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onPause() {
        unregisterReceiver(wifiReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        registerReceiver(
                wifiReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        );
        super.onResume();
    }

    class WifiScanReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(final Context context, final Intent intent) {
            progressBarTop.setVisibility(View.INVISIBLE);
            accessPointList.clear();

            List<ScanResult> scanResultList = wifiManager.getScanResults();
            if (scanResultList != null) {
                for (ScanResult scanResult : scanResultList) {
                    int level = WifiManager.calculateSignalLevel(scanResult.level, 4);

                    switch (scanResult.BSSID) {
                        //b6:e6:2d:23:84:90
                        //60:de:f3:03:60:30 SBK Group
                        //78:8a:20:d4:ac:28 Cocowork
                        case "b6:e6:2d:23:84:90": //AP1
                            rssiKFQueueAp1 = tinydb.getQueueDouble("rssi_kalman_list_ap1");
                            rssiKFQueueAp1.add((double) scanResult.level);
                            tinydb.putQueueDouble("rssi_kalman_list_ap1", rssiKFQueueAp1);

                            if (iAp1 == 0) {
                                kfAlgoAp1 = KalmanFilter.applyKFAlgorithm(rssiKFQueueAp1, 1, 0.008);
                                variansiAp1 = kfAlgoAp1.get(4);
                            } else {
                                kfAlgoAp1 = KalmanFilter.applyKFAlgorithm(rssiKFQueueAp1, variansiAp1, 0.008);
                                variansiAp1 = kfAlgoAp1.get(4);
                            }
                            iAp1 += 1;

                            rssiListAp1.add((double) scanResult.level);
                            rssiKFListAp1.add(kfAlgoAp1.get(3));

                            ToolUtil.Storage.setValueString(MainActivity.this, "rssi_kalman_ap1",
                                    String.valueOf(kfAlgoAp1.get(3)));
                            ToolUtil.Storage.setValueInt(MainActivity.this, "i_kalman_ap1",
                                    iAp1);
                            ToolUtil.Storage.setValueString(MainActivity.this, "var_kalman_ap1",
                                    String.valueOf(variansiAp1));
                            ToolUtil.Storage.setValueString(MainActivity.this, "dist_kalman_ap1",
                                    Formula.distance(kfAlgoAp1.get(3), Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MainActivity.this, "n"))));

                            accessPoint = new AccessPoint(
                                    scanResult.SSID,
                                    String.valueOf(scanResult.level) + " dBm",
                                    String.valueOf(scanResult.frequency) + " MHz",
                                    scanResult.capabilities,
                                    Formula.distance((double) scanResult.level, Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MainActivity.this, "n"))),
                                    String.valueOf(level),
                                    scanResult.BSSID,
                                    String.valueOf(kfAlgoAp1.get(3)) + " dBm",
                                    Formula.distance(kfAlgoAp1.get(3), Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MainActivity.this, "n"))));
                            accessPointList.add(accessPoint);
                            break;
                        case "6a:c6:3a:d6:9c:92": //6a:c6:3a:d6:9c:92
                            rssiKFQueueAp2 = tinydb.getQueueDouble("rssi_kalman_list_ap2");
                            rssiKFQueueAp2.add((double) scanResult.level);
                            tinydb.putQueueDouble("rssi_kalman_list_ap2", rssiKFQueueAp2);

                            if (iAp2 == 0) {
                                kfAlgoAp2 = KalmanFilter.applyKFAlgorithm(rssiKFQueueAp2, 1, 0.008);
                                variansiAp2 = kfAlgoAp2.get(4);
                            } else {
                                kfAlgoAp2 = KalmanFilter.applyKFAlgorithm(rssiKFQueueAp2, variansiAp2, 0.008);
                                variansiAp2 = kfAlgoAp2.get(4);
                            }
                            iAp2 += 1;

                            rssiListAp2.add((double) scanResult.level);
                            rssiKFListAp2.add(kfAlgoAp2.get(3));

                            ToolUtil.Storage.setValueString(MainActivity.this, "rssi_kalman_ap2",
                                    String.valueOf(kfAlgoAp2.get(3)));
                            ToolUtil.Storage.setValueInt(MainActivity.this, "i_kalman_ap2", iAp2);
                            ToolUtil.Storage.setValueString(MainActivity.this, "var_kalman_ap2",
                                    String.valueOf(variansiAp2));
                            ToolUtil.Storage.setValueString(MainActivity.this, "dist_kalman_ap2",
                                    Formula.distance(kfAlgoAp2.get(3), Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MainActivity.this, "n"))));

                            accessPoint = new AccessPoint(
                                    scanResult.SSID,
                                    String.valueOf(scanResult.level) + " dBm",
                                    String.valueOf(scanResult.frequency) + " MHz",
                                    scanResult.capabilities,
                                    Formula.distance(scanResult.level, Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MainActivity.this, "n"))),
                                    String.valueOf(level),
                                    scanResult.BSSID,
                                    String.valueOf(kfAlgoAp2.get(3)) + " dBm",
                                    Formula.distance(kfAlgoAp2.get(3), Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MainActivity.this, "n"))));
                            accessPointList.add(accessPoint);
                            break;
                        case "be:dd:c2:fe:3b:0b":
                            rssiKFQueueAp3 = tinydb.getQueueDouble("rssi_kalman_list_ap3");
                            rssiKFQueueAp3.add((double) scanResult.level);
                            tinydb.putQueueDouble("rssi_kalman_list_ap3", rssiKFQueueAp3);

                            if (iAp3 == 0) {
                                kfAlgoAp3 = KalmanFilter.applyKFAlgorithm(rssiKFQueueAp3, 1, 0.008);
                                variansiAp3 = kfAlgoAp3.get(4);
                            } else {
                                kfAlgoAp3 = KalmanFilter.applyKFAlgorithm(rssiKFQueueAp3, variansiAp3, 0.008);
                                variansiAp3 = kfAlgoAp3.get(4);
                            }
                            iAp3 += 1;

                            rssiListAp3.add((double) scanResult.level);
                            rssiKFListAp3.add(kfAlgoAp3.get(3));

                            ToolUtil.Storage.setValueString(MainActivity.this, "rssi_kalman_ap3",
                                    String.valueOf(kfAlgoAp3.get(3)));
                            ToolUtil.Storage.setValueInt(MainActivity.this, "i_kalman_ap3", iAp3);
                            ToolUtil.Storage.setValueString(MainActivity.this, "var_kalman_ap3",
                                    String.valueOf(variansiAp3));
                            ToolUtil.Storage.setValueString(MainActivity.this, "dist_kalman_ap3",
                                    Formula.distance(kfAlgoAp3.get(3), Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MainActivity.this, "n"))));

                            accessPoint = new AccessPoint(
                                    scanResult.SSID,
                                    String.valueOf(scanResult.level) + " dBm",
                                    String.valueOf(scanResult.frequency) + " MHz",
                                    scanResult.capabilities,
                                    Formula.distance(scanResult.level, Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MainActivity.this, "n"))),
                                    String.valueOf(level),
                                    scanResult.BSSID,
                                    String.valueOf(kfAlgoAp3.get(3)) + " dBm",
                                    Formula.distance(kfAlgoAp3.get(3), Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MainActivity.this, "n"))));
                            accessPointList.add(accessPoint);
                            break;
                        default:
                            accessPoint = new AccessPoint(
                                    scanResult.SSID,
                                    String.valueOf(scanResult.level) + " dBm",
                                    String.valueOf(scanResult.frequency) + " MHz",
                                    scanResult.capabilities,
                                    Formula.distance(scanResult.level, Double.parseDouble(ToolUtil.Storage
                                            .getValueString(MainActivity.this, "n"))),
                                    String.valueOf(level),
                                    scanResult.BSSID,
                                    "0 dBm", "0");
                            accessPointList.add(accessPoint);

                            elseList.add((double) scanResult.level);
                            break;
                    }
                }
            }

            Collections.sort(accessPointList, new ApComparator());

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setHasFixedSize(true);

            mAdapter = new ApAdapter(accessPointList);
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            // Save state
            recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();

            // Restore state
            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
//            runLayoutAnimation(recyclerView);
            Toast.makeText(getApplicationContext(), "Refresh count: " + refreshCount,
                    Toast.LENGTH_SHORT).show();
            refreshCount++;

            // Refresh after 1 seconds
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                wifiManager.startScan();
                progressBarTop.setVisibility(View.VISIBLE);
            }, 1000);
        }
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();

        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_bottom);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private static boolean saveExcelFile(Context context, String fileName,
            ArrayList<Double> rssiListAp1, ArrayList<Double> rssiKFListAp1,
            ArrayList<Double> rssiListAp2, ArrayList<Double> rssiKFListAp2,
            ArrayList<Double> rssiListAp3, ArrayList<Double> rssiKFListAp3) {

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e(TAG, "Storage not available or read only");
            return false;
        }

        boolean success = false;

        //New Workbook
        Workbook wb = new HSSFWorkbook();

        Cell c = null;

        //Cell style for header row
//        CellStyle cs = wb.createCellStyle();
//        cs.setFillForegroundColor(HSSFColor.LIME.index);
//        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        //New Sheet
        Sheet sheetRssiAp1 = null;
        Sheet sheetRssiAp1KF = null;
        Sheet sheetRssiAp2 = null;
        Sheet sheetRssiAp2KF = null;
        Sheet sheetRssiAp3 = null;
        Sheet sheetRssiAp3KF = null;
        sheetRssiAp1 = wb.createSheet("AP1");
        sheetRssiAp1KF = wb.createSheet("AP1 KF");
        sheetRssiAp2 = wb.createSheet("AP2");
        sheetRssiAp2KF = wb.createSheet("AP2 KF");
        sheetRssiAp3 = wb.createSheet("AP3");
        sheetRssiAp3KF = wb.createSheet("AP3 KF");

        List<String> list = new ArrayList<>();
        list.add("Atep");
        list.add("Ajun");
        list.add("Ronggo");
        list.add("Asu");
        list.add("Jjjj");
        list.add("asdlfkj");
        list.add("Fu");

        // Generate column headings
        Row row = sheetRssiAp1KF.createRow(0);
//
//        c = row.createCell(0);
//        c.setCellValue("AP1 - RSSI");
//        c = row.createCell(1);
//        c.setCellValue("AP1 - RSSI KF");
//        c = row.createCell(2);
//        c.setCellValue("AP2 - RSSI");
//        c = row.createCell(3);
//        c.setCellValue("AP2 - RSSI KF");
//        c = row.createCell(4);
//        c.setCellValue("AP3 - RSSI");
//        c = row.createCell(5);
//        c.setCellValue("AP3 - RSSI KF");

        // AP1 RSSI
        for (int i = 0; i < rssiListAp1.size(); i++) {
            sheetRssiAp1.createRow(i).createCell(0).setCellValue(rssiListAp1.get(i));
        }

        // AP1 RSSI KF
        for (int i = 0; i < list.size(); i++) {
            sheetRssiAp1KF.createRow(i).createCell(0).setCellValue(list.get(i));
        }

        // AP2 RSSI
        for (int i = 0; i < rssiListAp2.size(); i++) {
            sheetRssiAp2.createRow(i).createCell(0).setCellValue(rssiListAp2.get(i));
        }

        // AP2 RSSI KF
        for (int i = 0; i < rssiKFListAp2.size(); i++) {
            sheetRssiAp2KF.createRow(i).createCell(0).setCellValue(rssiKFListAp2.get(i));
        }

        // AP3 RSSI
        for (int i = 0; i < rssiListAp3.size(); i++) {
            sheetRssiAp3.createRow(i).createCell(0).setCellValue(rssiListAp3.get(i));
        }

        // AP3 RSSI KF
        for (int i = 0; i < rssiKFListAp3.size(); i++) {
            sheetRssiAp3KF.createRow(i).createCell(0).setCellValue(rssiKFListAp3.get(i));
        }

        // Create a path where we will place our List of objects on external storage
        File file = new File(context.getExternalFilesDir(null), fileName);

        try (FileOutputStream os = new FileOutputStream(file)) {
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            Toast.makeText(context, "Exported to " + file, Toast.LENGTH_SHORT).show();
            success = true;
        } catch (IOException e) {
            Toast.makeText(context, "Error writing " + e, Toast.LENGTH_SHORT).show();
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Toast.makeText(context, "Error " + e, Toast.LENGTH_SHORT).show();
            Log.w("FileUtils", "Failed to save file", e);
        }
        return success;
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState);
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(extStorageState);
    }
}