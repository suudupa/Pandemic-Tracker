package com.suudupa.coronavirustracker.utility;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static com.suudupa.coronavirustracker.utility.Resources.GLOBAL;

public class Utils {

    public static ColorDrawable[] vibrantLightColorList =
            {
                    new ColorDrawable(Color.parseColor("#ffeead")),
                    new ColorDrawable(Color.parseColor("#93cfb3")),
                    new ColorDrawable(Color.parseColor("#fd7a7a")),
                    new ColorDrawable(Color.parseColor("#faca5f")),
                    new ColorDrawable(Color.parseColor("#1ba798")),
                    new ColorDrawable(Color.parseColor("#6aa9ae")),
                    new ColorDrawable(Color.parseColor("#ffbf27")),
                    new ColorDrawable(Color.parseColor("#d93947"))
            };

    public static ColorDrawable getRandomColorDrawable() {
        int index = new Random().nextInt(vibrantLightColorList.length);
        return vibrantLightColorList[index];
    }

    public static String getCountry() {
        Locale locale = Locale.getDefault();
        return locale.getCountry().toLowerCase();
    }

    public static String[] getCountryList() {
        String[] countryCodes = Locale.getISOCountries();
        List<String> countryNames = new ArrayList<>();
        countryNames.add(GLOBAL);
        for (String countryCode : countryCodes) {
            Locale locale = new Locale("", countryCode);
            countryNames.add(locale.getDisplayCountry());
        }
        return countryNames.toArray(new String[countryNames.size()]);
    }

    public static String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    public static String formatDate (String dateString) {
        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault());
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(dateString);
            assert date != null;
            return dateFormatter.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String formatDateTime (String dateString) {
        PrettyTime p = new PrettyTime();
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", new Locale(getCountry()));
            Date date = simpleDateFormat.parse(dateString);
            return p.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String convertUnixTimestamp (String timestamp) {
        long time = Long.parseLong(timestamp)*1000L;
        java.util.Date date = new java.util.Date(time);
        DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.getDefault());
        return dateFormatter.format(date);
    }

    public static void sortAlphabetical (List<String> regionsList) {
        Collections.sort(regionsList);
    }
}