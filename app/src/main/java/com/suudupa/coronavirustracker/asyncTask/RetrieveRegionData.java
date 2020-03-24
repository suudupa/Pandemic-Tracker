package com.suudupa.coronavirustracker.asyncTask;

import android.os.AsyncTask;

import com.suudupa.coronavirustracker.activity.MainActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import static com.suudupa.coronavirustracker.utility.Resources.CASES_COL;
import static com.suudupa.coronavirustracker.utility.Resources.DEATHS_COL;
import static com.suudupa.coronavirustracker.utility.Resources.RECOVERED_COL;

public class RetrieveRegionData extends AsyncTask<Object, Void, Void> {

    MainActivity mainActivityContext;

    @Override
    protected Void doInBackground(Object...params) {

        String url = (String) params[0];
        String rgnName = (String) params[1];
        mainActivityContext = (MainActivity) params[2];

        try {
            Document rgnPage = Jsoup.connect(url).get();
            Element rgn = rgnPage.select("tr:contains(" + rgnName + ")").get(0);

            if (rgn != null) {
                Elements rgnData = rgn.select("td");

                if (rgnData != null && rgnData.size() > 0) {
                    mainActivityContext.numCases = rgnData.get(CASES_COL).text();
                    mainActivityContext.numDeaths = rgnData.get(DEATHS_COL).text();
                    mainActivityContext.numRecovered = rgnData.get(RECOVERED_COL).text();
                    mainActivityContext.setToZeroIfEmpty();
                }
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
