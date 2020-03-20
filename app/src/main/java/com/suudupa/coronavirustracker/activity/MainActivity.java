package com.suudupa.coronavirustracker.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.suudupa.coronavirustracker.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static final String HOMEPAGE_URL = "https://www.worldometers.info/coronavirus/";
    public static final String OUTBREAK_DATA = ".maincounter-number";
    public static final String REGION_URL = "https://www.worldometers.info/coronavirus/#countries";

    public static final int CASES_COL = 1;
    public static final int DEATHS_COL = 3;
    public static final int RECOVERED_COL = 5;

    public String numCases = "0";
    public String numDeaths = "0";
    public String numRecovered = "0";

    private TextView casesTextView;
    private TextView deathsTextView;
    private TextView recoveredTextView;

    public Spinner regionList;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        casesTextView = findViewById(R.id.casesTextView);
        deathsTextView = findViewById(R.id.deathsTextView);
        recoveredTextView = findViewById(R.id.recoveredTextView);

        new RetrieveGlobalDataTask().execute(HOMEPAGE_URL, OUTBREAK_DATA);

        regionList = findViewById(R.id.regionListSpinner);
        regionList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRegion = parent.getItemAtPosition(position).toString();
                switch (selectedRegion) {
                    case "Global":
                        new RetrieveGlobalDataTask().execute(HOMEPAGE_URL, OUTBREAK_DATA);
                        break;

                    default:
                        new RetrieveRegionDataTask().execute(REGION_URL, selectedRegion);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData(regionList.getSelectedItem().toString());
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    private class RetrieveGlobalDataTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... urls) {

            String url = urls[0];
            String id = urls[1];

            try {
                Document homepage = Jsoup.connect(url).get();
                Elements results = homepage.select(id);

                if (results != null && results.size() > 0) {
                    numCases = results.get(0).text();
                    numDeaths = results.get(1).text();
                    numRecovered = results.get(2).text();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            casesTextView.setText(numCases);
            deathsTextView.setText(numDeaths);
            recoveredTextView.setText(numRecovered);
            super.onPostExecute(aVoid);
        }
    }

    private class RetrieveRegionDataTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... urls) {

            String url = urls[0];
            String rgnName = urls[1];

            try {
                Document rgnPage = Jsoup.connect(url).get();
                Element rgn = rgnPage.select("tr:contains(" + rgnName + ")").get(0);

                if (rgn != null) {
                    Elements data = rgn.select("td");

                    if (data != null && data.size() > 0) {
                        numCases = data.get(CASES_COL).text();
                        numDeaths = data.get(DEATHS_COL).text();
                        numRecovered = data.get(RECOVERED_COL).text();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            casesTextView.setText(numCases);
            deathsTextView.setText(numDeaths);
            recoveredTextView.setText(numRecovered);
            super.onPostExecute(aVoid);
        }
    }

    private void refreshData(String region) {
        switch (region) {
            case "Global":
                new RetrieveGlobalDataTask().execute(HOMEPAGE_URL, OUTBREAK_DATA);
                break;

            default:
                new RetrieveRegionDataTask().execute(REGION_URL, region);
                break;
        }
    }
}


