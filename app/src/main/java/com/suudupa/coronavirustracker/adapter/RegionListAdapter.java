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

    private LayoutInflater layoutInflater;

    public RegionListAdapter(Context context, ArrayList<Region> regionItems) {
        super(context, 0, regionItems);
        this.layoutInflater  = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.region_list, null);
            holder.regionName = convertView.findViewById(R.id.regionName);
            convertView.setTag(R.layout.region_list, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.layout.region_list);
        }

        Region regionItem = getItem(position);
        if (regionItem != null) {
            holder.regionName.setText(regionItem.getName());
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        DropDownViewHolder holder;

        if (convertView == null) {
            holder = new DropDownViewHolder();
            convertView = layoutInflater.inflate(R.layout.region_list, null);
            holder.regionName = convertView.findViewById(R.id.regionName);
            holder.regionCases = convertView.findViewById(R.id.regionCases);
            convertView.setTag(R.layout.region_list, holder);
        } else {
            holder = (DropDownViewHolder) convertView.getTag(R.layout.region_list);
        }

        Region regionItem = getItem(position);
        if (regionItem != null) {
            holder.regionName.setText(regionItem.getName());
            holder.regionCases.setText(regionItem.getCases());
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView regionName;
    }

    private static class DropDownViewHolder {
        TextView regionName;
        TextView regionCases;
    }
}