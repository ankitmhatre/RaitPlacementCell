/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.dragonide.raitplacementcell.client;

import android.content.Context;
import android.util.Log;

import com.dragonide.raitplacementcell.LocalDb;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Provides utility methods for communicating with the server.
 */
final public class NetworkUtilities {

    /**
     * Timeout (in ms) we specify for each http request
     */
    public static final int HTTP_REQUEST_TIMEOUT_MS = 30 * 1000;

    /**
     * Base URL for the v2 Sample Sync Service
     */


    private NetworkUtilities() {
    }


    /**
     * Connects to the SampleSync test server, authenticates the provided
     * username and password.
     *
     * @param username The server account username
     * @param password The server account password
     * @return String The authentication token returned by the server (or null)
     */
    public static String authenticate(String username, String password, String batch) {
        Document doc = null;
        Connection.Response res;


        try {
            res = Jsoup.connect("http://rait.placyms.com/index.php")
                    .data("username", username, "password", password, "batch", batch, "submit", "Login")
                    .method(Connection.Method.POST)
                    .ignoreHttpErrors(true)
                    .execute();
        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }
        Log.d("sesssssss", "3");
        Log.d("sess123", res.cookie("PHPSESSID"));

        // Log.d("sesssssss", res.body());
        try {
            doc = res.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (doc != null) {
            //setContentView(R.layout.activity_login);
            //   Log.d("thedoc", doc.html());


            if (!doc.hasText()) {

                //"Cannot connect, Check your connection"
                return null;
            } else {
                //done
                Log.d("reach", "1");
                Document d = Jsoup.parse(doc.html());
                Element script = d.select("span").first();
                Log.d("reach", "2");
                if (script != null && script.text().equals("Invalid Username and/or Password")) {

                    //Wrong Combination
                    //freshLogin();
                    return null;
                } else {


                    //  startBasicStuff();
                   /* try {
                        //start TheService
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                    return res.cookie("PHPSESSID");
                }


            }

        } else {
            return null;
            //  freshLogin();
        }


    }

    public static void getFromServer(Document mdoc, Context c) {


        Elements notification_rows;

        notification_rows = mdoc.select("div.col-md-12");

        final String[] notif_array = new String[notification_rows.size()];
        int q = 0;
        for (Element e : notification_rows) {
            String final_title;
            try {
                if (e.select("b").first() != null) {
                    final_title = e.select("b").first().text();
                    notif_array[q++] = final_title;
                } else {
                    String title_in_html = e.html();
                    String[] split_title = title_in_html.split("<br>");
                    final_title = split_title[0];
                    //  Log.d("testtttttt",title_in_html);
                    notif_array[q++] = final_title; //e.select("b").first().text();


                }
                String content_in_html = e.html();
                String content = content_in_html.replaceAll("(https://[^<>[:space:]]+[[:alnum:]/])", "<a href=\"$1\">$1</a>");

                content = content.replaceAll("(http://[^<>[:space:]]+[[:alnum:]/])", "<a href=\"$1\">$1</a>");
                LocalDb.getInstance(c).addNotifications(final_title, content);
            } catch (Exception exception) {
                exception.printStackTrace();


            }

        }


    }


}
