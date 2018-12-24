package com.juvetic.rssi.util.formulas;

import java.util.Queue;

final class Utils {

    /* Statistical support methods */

    static Double variance(Queue<Double> values) {
        Double sum = 0.0;
        Double mean = mean(values);
        for (double num : values) {
            sum += Math.pow(num - mean, 2);
        }
        return sum / (values.size());
    }

    static Double mean(Queue<Double> values) {
        return sum(values) / values.size();
    }

    private static Double sum(Queue<Double> values) {
        Double sum = 0.0;
        for (Double num : values) {
            sum += num;
        }
        return sum;
    }
}

