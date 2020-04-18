package com.suudupa.coronavirustracker.asyncTask;

import android.os.AsyncTask;

import com.suudupa.coronavirustracker.activity.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class JsonResponse extends AsyncTask<Object, Void, Void> {

    private MainActivity mainActivityContext;
    private String region;

    @Override
    protected Void doInBackground(Object... params) {

        String link = (String) params[0];
        mainActivityContext = (MainActivity) params[1];
        region = (String) params[2];

        HttpURLConnection connection = null;
        URL url = null;
        String json = "";

        try {
            url = new URL(link);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            assert url != null;
            connection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"), 8);
            json = getJson(reader);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            assert connection != null;
            connection.disconnect();
        }

        try {
            mainActivityContext.jsonResponse = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mainActivityContext.buildRegionList();
        try {
            mainActivityContext.loadData(region);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getJson(BufferedReader reader) throws IOException {
        StringBuilder sBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sBuilder.append(line + "\n");
        }
        return sBuilder.toString();
    }
}