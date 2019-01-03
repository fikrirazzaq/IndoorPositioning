package com.juvetic.rssi.util.formulas;

import android.util.Log;
import com.juvetic.rssi.ui.MapKalmanActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Formula {

    public static String distance(double rssi, double n) {
        int d0 = 1;
        int p = -40;
        return String.format("%.2f", d0 * (Math.pow(10, (p - rssi) / (float) (n * 10))));
    }

    public static List<Double> koordinat(
            double x1, double y1, double d1,
            double x2, double y2, double d2,
            double x3, double y3, double d3) {

        d1 *= 134;
        d2 *= 134;
        d3 *= 134;

        double A = Math.pow(x1, 2) + Math.pow(y1, 2) - Math.pow(d1, 2);
        double B = Math.pow(x2, 2) + Math.pow(y2, 2) - Math.pow(d2, 2);
        double C = Math.pow(x3, 2) + Math.pow(y3, 2) - Math.pow(d3, 2);
        double X32 = x3 - x2;
        double X13 = x1 - x3;
        double X21 = x2 - x1;
        double Y32 = y3 - y2;
        double Y13 = y1 - y3;
        double Y21 = y2 - y1;
        double x = (A * Y32 + B * Y13 + C * Y21) / (2 * (x1 * Y32 + x2 * Y13 + x3 * Y21));
        double y = (A * X32 + B * X13 + C * X21) / (2 * (y1 * X32 + y2 * X13 + y3 * X21));

        List<Double> koordinat = new ArrayList<>();
        koordinat.add(x);
        koordinat.add(y);

        return koordinat;
    }


    /**
     * Kalman Filter 1
     *
     * @param inputValues List of RSSI
     * @param variance    variansi, default=1
     * @param noise       0.008
     */
    public static ArrayList<Double> applyKFAlgorithmTypeA(
            Queue<Double> inputValues,
            double variance,
            double noise) {

        Double kalmanGain, mean;
        mean = Utils.mean(inputValues);
        double inputVar = variance;
        variance = variance + noise;

        Double measurementNoise = Utils.variance(inputValues);
        kalmanGain = variance / (variance + measurementNoise);
        variance = (1 - kalmanGain) * variance;

        ArrayList<Double> fromQueue = new ArrayList(inputValues);
        Log.d(MapKalmanActivity.class.getSimpleName(), "onReceive: Last Value " + fromQueue.get(0));
        Log.d(MapKalmanActivity.class.getSimpleName(),
                "onReceive: First Value " + fromQueue.get(fromQueue.size() - 1));

        ArrayList<Double> returnList = new ArrayList<>();
        returnList.add(mean);
        returnList.add(kalmanGain);
        returnList.add(fromQueue.get(0) - mean);
        returnList.add(mean + kalmanGain * (fromQueue.get(0) - mean));
        returnList.add(variance);
        returnList.add(inputVar);

        return returnList;
    }

    /**
     * Kalman Filter 2
     *
     * @param inputValues List of RSSI
     * @param preRSSI     RSSI result of previous iteration, default=0
     * @param variance    variansi, default=1
     * @param noise       0.008, inputan
     */
    public static ArrayList<Double> applyKFAlgorithmTypeB(
            Queue<Double> inputValues,
            double preRSSI,
            double variance,
            double noise) {

        Double kalmanGain;
        double inputVar = variance;
        variance = variance + noise;

        Double measurementNoise = Utils.variance(inputValues);
        kalmanGain = variance / (variance + measurementNoise);
        variance = (1 - kalmanGain) * variance;

        ArrayList<Double> fromQueue = new ArrayList(inputValues);
        Log.d(MapKalmanActivity.class.getSimpleName(), "onReceive: Last Value " + fromQueue.get(0));
        Log.d(MapKalmanActivity.class.getSimpleName(),
                "onReceive: First Value " + fromQueue.get(fromQueue.size() - 1));

        ArrayList<Double> returnList = new ArrayList<>();
        returnList.add(preRSSI);
        returnList.add(kalmanGain);
        returnList.add(fromQueue.get(0) - preRSSI);
        returnList.add(preRSSI + kalmanGain * (fromQueue.get(0) - preRSSI)); // RSSI Output
        returnList.add(variance);
        returnList.add(inputVar);

        return returnList;
    }

    /**
     * Feedback Filter
     *
     * @param meanRSSI Previous mean of RSSI; Init = 0
     * @param curRSSI  Current RSSI reading
     * @param alpha    Weighted value (please refer to paper on Botak), inputan
     */
    public static ArrayList<Double> applyFeedbackFilterAlgorithm(
            double meanRSSI,
            double curRSSI,
            double alpha) {
        double RSSIsmooth = alpha * curRSSI + (1 - alpha) * meanRSSI;

        ArrayList<Double> returnList = new ArrayList<>();
        returnList.add(meanRSSI);
        returnList.add(alpha);
        returnList.add(curRSSI);
        returnList.add(RSSIsmooth);

        return returnList;
    }
}
