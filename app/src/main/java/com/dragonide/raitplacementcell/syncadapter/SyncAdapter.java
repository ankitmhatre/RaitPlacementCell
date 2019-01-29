/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dragonide.raitplacementcell.syncadapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.dragonide.raitplacementcell.Constants;
import com.dragonide.raitplacementcell.HomeActivity;
import com.dragonide.raitplacementcell.LocalDb;
import com.dragonide.raitplacementcell.PrefUtils;
import com.dragonide.raitplacementcell.R;
import com.dragonide.raitplacementcell.providers.FeedContract;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.core.app.NotificationCompat;

/**
 * Define a sync adapter for the app.
 * <p>
 * <p>This class is instantiated in {@link SyncService}, which also binds SyncAdapter to the system.
 * SyncAdapter should only be initialized in SyncService, never anywhere else.
 * <p>
 * <p>The system calls onPerformSync() via an RPC call through the IBinder object supplied by
 * SyncService.
 */
class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String LOG = "SyncAdapteraaaa";
    private static final int NET_CONNECT_TIMEOUT_MILLIS = 15000;  // 15 seconds
    private static final int NET_READ_TIMEOUT_MILLIS = 10000;  // 10 seconds

    private final ContentResolver mContentResolver;
    String first, second;

    public final String AUTHORITY = "com.dragonide.raitplacementcell";

    private static final String[] PROJECTION = new String[]{
            FeedContract.Entry._ID,
            FeedContract.Entry.COLUMN_NAME_ENTRY_ID,
            FeedContract.Entry.COLUMN_NAME_TITLE,
            FeedContract.Entry.COLUMN_NAME_LINK,
            FeedContract.Entry.COLUMN_NAME_PUBLISHED};

    // Constants representing column positions from PROJECTION.
    public static final int COLUMN_ID = 0;
    public static final int COLUMN_ENTRY_ID = 1;
    public static final int COLUMN_TITLE = 2;
    public static final int COLUMN_LINK = 3;
    public static final int COLUMN_PUBLISHED = 4;

    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        mContentResolver = context.getContentResolver();
    }

    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG, "Beginning network synchronization");
        first = LocalDb.getInstance(getContext()).getTopMostItemCOntent();
        Log.d(LOG, "Last title of the item in " + first);
        ContentResolver.setIsSyncable(account, AUTHORITY, 1);


        AccountManager am = AccountManager.get(getContext());
        String uName = account.name;
        String pass = am.getPassword(account);
        String batch = am.getUserData(account, "batch");
        String sessID = null;

        Log.d(LOG, "username" + " - " + uName + "\n" + "password" + " - " + pass + "\n" + "batch" + " - " + batch);


        try {
            Connection.Response res = Jsoup.connect("http://rait.placyms.com/index.php")
                    .data("username", uName, "password", pass, "batch", batch, "submit", "Login")
                    .method(Connection.Method.POST)
                    .execute();

            Document document = res.parse();
            //   Log.d(LOGasdfgh", document.html());
            sessID = res.cookie("PHPSESSID");
            //     Log.d(LOGasdfgh", "asdfgh");

            //SECTION 2
            Document pers = Jsoup.connect("http://rait.placyms.com/personal.php").cookie("PHPSESSID", sessID).get();
            String crn = pers.select("input[name=crn]").first().attr("value");
            String fname = pers.select("input[name=fn]").first().attr("value");

            //  Log.d(LOGasdfgh", crn);
            PrefUtils.setString(getContext(), "roll_no", crn);
            PrefUtils.setString(getContext(), "fname", fname);
            //postNewNotification();
            getFromServer(document);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(LOG, "3");


        Intent i = new Intent(Constants.FINISH_SYNC);
        getContext().sendBroadcast(i);

        getContext().getContentResolver().notifyChange(Constants.BASE_URI, null, true);


    }

    private void postNewNotification() {
        NotificationManager mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationID = 1;
        Intent i = new Intent(getContext(), HomeActivity.class);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        getContext(),
                        0,
                        i,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext(), "dashboard")
                .setSmallIcon(R.drawable.placyms)
                .setContentTitle(getContext().getString(R.string.app_name))
                .setContentText(LocalDb.getInstance(getContext()).getTopMostItemTitle())
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

//
// Issue the notification.
        mNotificationManager.notify(notificationID, mBuilder.build());
        PrefUtils.setString(getContext(), "notif", LocalDb.getInstance(getContext()).getTopMostItemTitle());
    }

    /**
     * Get date from the server and put it into the database everytime system performs sync operation
     */

    @SuppressWarnings("")
    private void getFromServer(Document mdoc) {


        Elements notification_rows;

        notification_rows = mdoc.select("div.col-md-12");

        final String[] notif_array = new String[notification_rows.size()];
        int q = 0;
       /* if(LocalDb.getInstance(getContext()).getTopMostItemCOntent().equals(notif_array[0])){

        }*/

        LocalDb.getInstance(getContext()).deleteALl();
        Log.d(LOG, "deletedAll - Inside get FRom Server");
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
                    //  Log.d(LOGtesttttttt",title_in_html);
                    notif_array[q++] = final_title; //e.select("b").first().text();


                }
                String content_in_html = e.html();
                String content = content_in_html.replaceAll("(https://[^<>[:space:]]+[[:alnum:]/])", "<a href=\"$1\">$1</a>");

                content = content.replaceAll("(http://[^<>[:space:]]+[[:alnum:]/])", "<a href=\"$1\">$1</a>");
                if (final_title.length() > 1 && content.length() > 1) {
                    LocalDb.getInstance(getContext()).addNotifications(final_title, content);
                }


            } catch (Exception exception) {
                exception.printStackTrace();


            }

        }
        second = LocalDb.getInstance(getContext()).getTopMostItemCOntent();
        Log.d(LOG, "New Title in the database = " + second);

        if (!first.equals(second)) {
            Log.d(LOG, "posting a notification");
            postNewNotification();
        } else {
            first = null;
            second = null;
        }


    }


    /**
     * Given a string representation of a URL, sets up a connection and gets an input stream.
     */
    private InputStream downloadUrl(final URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(NET_READ_TIMEOUT_MILLIS /* milliseconds */);
        conn.setConnectTimeout(NET_CONNECT_TIMEOUT_MILLIS /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }
}
