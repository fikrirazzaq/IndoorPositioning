package com.juvetic.rssi;

public class AccessPoint {

    private String name;
    private String level;
    private String freq;
    private String cap;
    private String ch;
    private String venue;

    public AccessPoint(String name, String level, String freq, String cap) {
        this.name = name;
        this.level = level;
        this.freq = freq;
        this.cap = cap;
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
