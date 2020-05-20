package com.suudupa.coronavirustracker.utility;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static com.suudupa.coronavirustracker.utility.Resources.API_KEYS;

public class Utils {

    private static ColorDrawable[] vibrantLightColorList =
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

    public static String getRandomApiKey() {
        int index = new Random().nextInt(API_KEYS.length);
        return API_KEYS[index];
    }

    private static String getCountry() {
        Locale locale = Locale.getDefault();
        return locale.getCountry().toLowerCase();
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
        PrettyTime p = new PrettyTime(new Locale(getCountry()));
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
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
        DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT, Locale.getDefault());
        return dateFormatter.format(date);
    }

    public static String formatNumber(String value) {
        int number = Integer.parseInt(value);
        return String.format("%,d", number);
    }

    public static String formatNumber(String value, String op) {
        int number = Integer.parseInt(value);
        if (number == 0) { return ""; }
        else { return op + String.format("%,d", number); }
    }

    public static void sortAlphabetical (List<String> regionsList) {
        Collections.sort(regionsList);
    }

    public static void writeObject(Context context, String fileName, Object object) throws IOException {
        FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.flush();
        oos.close();
        fos.close();
    }

    public static Object readObject(Context context, String fileName) throws IOException, ClassNotFoundException {
        FileInputStream fis = context.openFileInput(fileName);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object object = ois.readObject();
        fis.close();
        return object;
    }
}