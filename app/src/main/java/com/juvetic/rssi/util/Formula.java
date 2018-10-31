package com.juvetic.rssi.util;

import java.util.ArrayList;
import java.util.List;

public class Formula {

    public static String distance(int rssi) {
        int d0 = 1;
        int p = -40;
        int n = 2;
        return String.format("%.2f", d0 * ( Math.pow(10, (p - rssi) / (float) (n * 10)) ));
    }

    public static List<Double> koordinat(
            double x1, double y1, double d1,
            double x2, double y2, double d2,
            double x3, double y3, double d3) {
        double A = Math.pow(x1,2) + Math.pow(y1,2) - Math.pow(d1,2);
        double B = Math.pow(x2,2) + Math.pow(y2,2) - Math.pow(d2,2);
        double C = Math.pow(x3,2) + Math.pow(y3,2) - Math.pow(d3,2);
        double X32 = x3 - x2;
        double X13 = x1 - x3;
        double X21 = x2 - x1;
        double Y32 = y3 - y2;
        double Y13 = y1 - y3;
        double Y21 = y2 - y1;
        double x = (A*Y32 + B*Y13 + C*Y21) / (2*(x1*Y32 + x2*Y13 + x3*Y21));
        double y = (A*X32 + B*X13 + C*X21) / (2*(y1*X32 + y2*X13 + y3*X21));

        List<Double> koordinat = new ArrayList<>();
        koordinat.add(x);
        koordinat.add(y);

        return koordinat;
    }

}
