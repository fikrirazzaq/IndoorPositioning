package com.juvetic.rssi.util.formulas;

import java.util.ArrayList;
import java.util.List;

public class KalmanFilter {

    /**
     *
     * @param inputValues List of RSSI
     * @param variance Nilai P[] = {10}
     * @param noise 0.008
     * @return
     */
    public static ArrayList<Double> applyKFAlgorithm(List<Double> inputValues, double variance, double noise) {
        Double kalmanGain, mean;
        mean = Utils.mean(inputValues);
        double inputVar = variance;
        variance = variance + noise;

        Double measurementNoise = Utils.variance(inputValues);
        kalmanGain = variance / (variance + measurementNoise);
        variance = (1 - kalmanGain) * variance;

        ArrayList<Double> returnList = new ArrayList<>();
        returnList.add(mean);
        returnList.add(kalmanGain);
        returnList.add(inputValues.get(0) - mean);
        returnList.add(mean + kalmanGain * (inputValues.get(0) - mean));
        returnList.add(variance);
        returnList.add(inputVar);

        return returnList;
    }
}

