package com.suudupa.coronavirustracker.asyncTask;

import android.os.AsyncTask;

import com.suudupa.coronavirustracker.activity.MainActivity;
import com.suudupa.coronavirustracker.utility.Utils;

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

    private String filename;
    private MainActivity mainActivityContext;
    private String region;

    @Override
    protected Void doInBackground(Object... params) {

        String link = (String) params[0];
        String [] path = link.split("/");
        filename = path[path.length-1];
        mainActivityContext = (MainActivity) params[1];
        region = (String) params[2];

        HttpURLConnection connection = null;
        URL url = null;
        String json = null;

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
            cancel(true);
            return null;
        } finally {
            assert connection != null;
            connection.disconnect();
        }

        try {
            mainActivityContext.jsonResponse = new JSONObject(json);
            Utils.writeObject(mainActivityContext.getApplicationContext(), filename, json);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mainActivityContext.dismissNoConnectionMsg();
        getData(region);
    }

    @Override
    protected void onCancelled() {
        String jsonData = null;
        boolean notFound = false;

        try {
            jsonData = (String) Utils.readObject(mainActivityContext.getApplicationContext(), filename);
        } catch (IOException | ClassNotFoundException e) {
            notFound = true;
        }

        if (notFound || jsonData == null) {
            mainActivityContext.showError();
        }
        else {
            getData(jsonData, region);
            mainActivityContext.showNoConnectionMsg();
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

    private void getData(String region) {
        mainActivityContext.buildRegionList(region);
        try {
            mainActivityContext.loadData(region);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getData(String json, String region) {
        try {
            mainActivityContext.jsonResponse = new JSONObject(json);
            mainActivityContext.buildRegionList(region);
            mainActivityContext.loadData(region);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}