package com.juvetic.rssi.model;

public class AccessPoint {

    private String bssid;

    private String name;

    private String level;

    private String freq;

    private String cap;

    private String ch;

    private String venue;

    private String distance;

    private String rssiKalmanTypeA;

    private String rssiKalmanTypeB;

    private String rssiFeedback;

    private String distanceKalmanTypeA;

    private String distanceKalmanTypeB;

    private String distanceFeedback;

    public AccessPoint(String name, String level, String freq, String cap,
            String distance, String ch, String bssid, String rssiKalmanTypeA, final String rssiKalmanTypeB,
            final String rssiFeedback, String distanceKalmanTypeA, final String distanceKalmanTypeB,
            final String distanceFeedback) {
        this.name = name;
        this.level = level;
        this.freq = freq;
        this.cap = cap;
        this.distance = distance;
        this.ch = ch;
        this.bssid = bssid;
        this.rssiKalmanTypeA = rssiKalmanTypeA;
        this.rssiKalmanTypeB = rssiKalmanTypeB;
        this.rssiFeedback = rssiFeedback;
        this.distanceKalmanTypeA = distanceKalmanTypeA;
        this.distanceKalmanTypeB = distanceKalmanTypeB;
        this.distanceFeedback = distanceFeedback;
    }

    public AccessPoint(String name, String level, String freq, String cap,
            String distance, String ch, String bssid, String rssiKalmanTypeA, String distanceKalmanTypeA) {
        this.name = name;
        this.level = level;
        this.freq = freq;
        this.cap = cap;
        this.distance = distance;
        this.ch = ch;
        this.bssid = bssid;
        this.rssiKalmanTypeA = rssiKalmanTypeA;
        this.distanceKalmanTypeA = distanceKalmanTypeA;
    }

    public String getBssid() {
        return bssid;
    }

    public String getDistanceFeedback() {
        return distanceFeedback;
    }

    public String getDistanceKalmanTypeA() {
        return distanceKalmanTypeA;
    }

    public String getDistanceKalmanTypeB() {
        return distanceKalmanTypeB;
    }

    public String getRssiFeedback() {
        return rssiFeedback;
    }

    public String getRssiKalmanTypeA() {
        return rssiKalmanTypeA;
    }

    public String getRssiKalmanTypeB() {
        return rssiKalmanTypeB;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getFreq() {
        return freq;
    }

    public void setFreq(String freq) {
        this.freq = freq;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public String getCh() {
        return ch;
    }

    public void setCh(String ch) {
        this.ch = ch;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
