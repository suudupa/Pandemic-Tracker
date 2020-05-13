package com.suudupa.coronavirustracker.model;

import com.google.gson.annotations.SerializedName;

public class Region implements java.io.Serializable {

    @SerializedName("name")
    private String name;

    @SerializedName("cases")
    private String cases;

    public Region(String name, String cases) {
        this.name = name;
        this.cases = cases;
    }

    public String getName() {
        return name;
    }

    public String getCases() {
        return cases;
    }

    @Override
    public String toString() {
        return name;
    }
}