package com.juvetic.rssi.util.formulas;

import android.util.Log;
import com.juvetic.rssi.ui.LocationOverlayActivity;
import java.util.ArrayList;
import java.util.Queue;

public class KalmanFilter {

    /**
     * @param inputValues List of RSSI
     * @param variance    variansi atau 1
     * @param noise       0.008
     */
    public static ArrayList<Double> applyKFAlgorithm(Queue<Double> inputValues, double variance, double noise) {
        Double kalmanGain, mean;
        mean = Utils.mean(inputValues);
        double inputVar = variance;
        variance = variance + noise;

        Double measurementNoise = Utils.variance(inputValues);
        kalmanGain = variance / (variance + measurementNoise);
        variance = (1 - kalmanGain) * variance;

        ArrayList<Double> fromQueue = new ArrayList(inputValues);
        Log.d(LocationOverlayActivity.class.getSimpleName(), "onReceive: Last Value " + fromQueue.get(0));
        Log.d(LocationOverlayActivity.class.getSimpleName(), "onReceive: First Value " + fromQueue.get(fromQueue.size()-1));

        ArrayList<Double> returnList = new ArrayList<>();
        returnList.add(mean);
        returnList.add(kalmanGain);
        returnList.add(fromQueue.get(0) - mean);
        returnList.add(mean + kalmanGain * (fromQueue.get(0) - mean));
        returnList.add(variance);
        returnList.add(inputVar);

        return returnList;
    }
}

