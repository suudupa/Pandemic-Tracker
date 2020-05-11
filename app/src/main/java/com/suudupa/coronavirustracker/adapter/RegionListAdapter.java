package com.suudupa.coronavirustracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.suudupa.coronavirustracker.R;
import com.suudupa.coronavirustracker.model.Region;

import java.util.ArrayList;

public class RegionListAdapter extends ArrayAdapter<Region> {

    public RegionListAdapter(Context context, ArrayList<Region> regionItems) {
        super(context, 0, regionItems);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.region_list, parent, false);
        }

        TextView regionName = convertView.findViewById(R.id.regionName);

        Region regionItem = getItem(position);
        if (regionItem != null) {
            regionName.setText(regionItem.getName());
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.region_list, parent, false);
        }

        TextView regionName = convertView.findViewById(R.id.regionName);
        TextView regionCases = convertView.findViewById(R.id.regionCases);

        Region regionItem = getItem(position);
        if (regionItem != null) {
            regionName.setText(regionItem.getName());
            regionCases.setText(regionItem.getCases());
        }

        return convertView;
    }
}