package com.suudupa.coronavirustracker.model;

public class Region {

    private String name;
    private String newCases;

    public Region(String name) {
        this.name = name;
        this.newCases = "";
    }

    public Region(String name, String newCases) {
        this.name = name;
        this.newCases = "+" + newCases;
    }

    public String getName() {
        return name;
    }

    public String getNewCases() {
        return newCases;
    }
}