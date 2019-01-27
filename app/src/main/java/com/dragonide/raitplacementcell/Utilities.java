package com.dragonide.raitplacementcell;

import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by Ankit on 1/23/2017.
 */

public class Utilities {

    public static String SESSID;
    public static Map<String, String> listMap;

    public static String GetOnlyValue(String a) {

        String[] parts = a.split("=");
        String part1 = parts[0]; // 004
        String part2 = parts[1];
        String we[] = part2.split(";");
        String we1 = we[0];
        String we2 = we[1];
        Log.d("SeperatedID", we1);
        return we1;
    }

    public static Map<String, String> getMap() {
        return listMap;
    }

    public static void setListMap(Map<String, String> listMap) {
        Utilities.listMap = listMap;
    }

    public static String getSESSID() {
        return SESSID;
    }

    public static void setSESSID(String SESSID) {
        Utilities.SESSID = SESSID;
    }

    public boolean FileExistsOnInternet(String URLName) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            // note : you may also need
            //        HttpURLConnection.setInstanceFollowRedirects(false)
            HttpURLConnection con =
                    (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
