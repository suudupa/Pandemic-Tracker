package com.suudupa.coronavirustracker.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.suudupa.coronavirustracker.R;
import com.suudupa.coronavirustracker.asyncTask.RetrieveGlobalData;
import com.suudupa.coronavirustracker.asyncTask.RetrieveRegionData;

import static com.suudupa.coronavirustracker.utility.Resources.GLOBAL;
import static com.suudupa.coronavirustracker.utility.Resources.HOMEPAGE_URL;
import static com.suudupa.coronavirustracker.utility.Resources.OUTBREAK_DATA;
import static com.suudupa.coronavirustracker.utility.Resources.REGION_URL;

public class MainActivity extends AppCompatActivity {

    public String numCases = "0";
    public String numDeaths = "0";
    public String numRecovered = "0";

    public TextView casesTextView;
    public TextView deathsTextView;
    public TextView recoveredTextView;

    private Spinner regionList;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeView();

        new RetrieveGlobalData().execute(HOMEPAGE_URL, OUTBREAK_DATA, this);

        regionList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRegion = parent.getItemAtPosition(position).toString();
                retrieveData(selectedRegion);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { return; }
        });

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                String selectedRegion = regionList.getSelectedItem().toString();
                retrieveData(selectedRegion);
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void initializeView() {
        casesTextView = findViewById(R.id.casesTextView);
        deathsTextView = findViewById(R.id.deathsTextView);
        recoveredTextView = findViewById(R.id.recoveredTextView);
        regionList = findViewById(R.id.regionListSpinner);
        swipeRefresh = findViewById(R.id.swipeRefresh);
    }

    private void retrieveData(String region) {
        switch (region) {
            case GLOBAL:
                new RetrieveGlobalData().execute(HOMEPAGE_URL, OUTBREAK_DATA, this);
                break;
            default:
                new RetrieveRegionData().execute(REGION_URL, region, this);
                break;
        }
    }
}


