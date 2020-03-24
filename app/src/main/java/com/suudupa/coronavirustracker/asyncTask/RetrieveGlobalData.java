package com.suudupa.coronavirustracker.asyncTask;

import android.os.AsyncTask;

import com.suudupa.coronavirustracker.activity.MainActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class RetrieveGlobalData extends AsyncTask<Object, Void, Void> {

    MainActivity mainActivityContext;

    @Override
    protected Void doInBackground(Object...params) {

        String url = (String) params[0];
        String id = (String) params[1];
        mainActivityContext = (MainActivity) params[2];

        try {
            Document homepage = Jsoup.connect(url).get();
            Elements results = homepage.select(id);

            if (results != null && results.size() > 0) {
                mainActivityContext.numCases = results.get(0).text();
                mainActivityContext.numDeaths = results.get(1).text();
                mainActivityContext.numRecovered = results.get(2).text();
                mainActivityContext.setToZeroIfEmpty();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mainActivityContext.casesTextView.setText(mainActivityContext.numCases);
        mainActivityContext.deathsTextView.setText(mainActivityContext.numDeaths);
        mainActivityContext.recoveredTextView.setText(mainActivityContext.numRecovered);
        super.onPostExecute(aVoid);
    }
}
