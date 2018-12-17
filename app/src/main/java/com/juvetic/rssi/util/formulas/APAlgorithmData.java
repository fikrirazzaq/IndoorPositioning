package com.juvetic.rssi.util.formulas;

import android.graphics.Point;

/**
 * Algorithm data needed to face Least Square algorithm stage.
 */
public class APAlgorithmData {
    public String bssid;
    public double distance;
    public int RSS;
    public Point coordinatesAP;

    public APAlgorithmData(final int RSS) {
        this.RSS = RSS;
    }

    public APAlgorithmData(String bssid, double distance, int RSS, Point coordinatesAP){
        this.bssid = bssid;
        this.distance = distance;
        this.RSS = RSS;
        this.coordinatesAP = coordinatesAP;
    }

}
