package com.juvetic.rssi.util;

import com.juvetic.rssi.model.AccessPoint;
import java.util.Comparator;

public class ApComparator implements Comparator<AccessPoint> {
    @Override
    public int compare(AccessPoint ap1, AccessPoint ap2) {
        return ap1.getLevel().compareTo(ap2.getLevel());
    }
}