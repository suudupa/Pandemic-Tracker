package com.suudupa.coronavirustracker.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RegionList implements java.io.Serializable {

    @SerializedName("regions")
    private List<Region> regions;

    @SerializedName("regionCount")
    private int regionCount;

    @SerializedName("regionNames")
    private List<String> regionNames;

    public RegionList(List<Region> regions) {
        this.regions = regions;
        this.regionCount = regions.size();
    }

    public List<Region> getRegions() {
        return regions;
    }

    public int getRegionCount() {
        return regionCount;
    }

    public List<String> getRegionNames() {
        regionNames.clear();
        for (Region region : regions) {
            String regionName = region.getName();
            regionNames.add(regionName);
        }
        return regionNames;
    }
}