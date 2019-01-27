package com.dragonide.raitplacementcell;

import android.content.Intent;
import android.os.StrictMode;
import android.app.PendingIntent;
import android.app.Service;

import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import android.util.Log;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class MyPortalService extends Service {

    static final int NOTIFICATION_ID = 543;

    public static boolean isServiceRunning = false;
    String sess_ID;
    Element row_elementstemp;

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            AccessHome(PrefUtils.getString(MyPortalService.this, "php_session_id", ""));//  startServiceWithNotification();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (PrefUtils.getBoolean(MyPortalService.this, "loggedin", false)) {

            if (!PrefUtils.getBoolean(MyPortalService.this, "has_php_session_id", false)) {
                try {
                    Log.d("sesssssss", "2");
                    Connection.Response res = Jsoup.connect("http://rait.placyms.com/index.php")
                            .data("username", PrefUtils.getString(MyPortalService.this, "email", ""), "password", PrefUtils.getString(MyPortalService.this, "pass", ""), "batch", PrefUtils.getInt(MyPortalService.this, "batch", -1) + "", "submit", "Login")
                            .method(Connection.Method.POST)
                            .execute();
                    Log.d("sesssssss", "3");

                    String sessionId = res.cookie("PHPSESSID");
                    PrefUtils.setString(MyPortalService.this, "php_session_id", sessionId);
                    PrefUtils.setBoolean(MyPortalService.this, "has_php_session_id", true);
                    AccessHome(sessionId);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    AccessHome(PrefUtils.getString(MyPortalService.this, "php_session_id", ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }


        }
        return START_STICKY;
    }
    public void AccessHome(String sess) throws Exception {



        Log.d("MyPortalService", "runnning in accesshome");
        Document doc = Jsoup.connect("http://rait.placyms.com/home.php").cookie("PHPSESSID", sess).get();
        row_elementstemp = doc.select("div.col-md-12").first();

        String asd = row_elementstemp.text();
        String rough = row_elementstemp.html();
        if (!(asd.equals(PrefUtils.getString(MyPortalService.this, "latest_notification", "")))) {
            Log.d("tserviceheaders", "SendNotifiction\n\n\n\n" + asd);//notify
            PrefUtils.setString(MyPortalService.this, "latest_notification", asd);
            sendNotification(rough, row_elementstemp);

        } else {
            Log.d("tserviceheaders", "Dont Send");//notify
        }
    }

    // In case the service is deleted or crashes some how
    @Override
    public void onDestroy() {
        isServiceRunning = false;
        super.onDestroy();
        PrefUtils.setBoolean(MyPortalService.this, "has_php_session_id", false);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return null;
    }




    public void sendNotification(String gh, Element d) {

       // if (isServiceRunning) return;
        isServiceRunning = true;

        Intent a = new Intent(MyPortalService.this, LoginActivity.class);
        //    Bundle bundle = new Bundle();
        //  bundle.putString("msg", gh);
        //  bundle.putString("title", d.select("b").first().text());
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        a,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.grad)
                        .setContentIntent(resultPendingIntent)
                        .setContentTitle("R.A.I.T. Placement Cell")
                        .setAutoCancel(true)
                        .setContentText(d.select("b").first().text());

        int mNotificationId = 234567;


        startForeground(mNotificationId, mBuilder.build());
    }


    void stopMyService() {
        stopForeground(true);
        stopSelf();
        isServiceRunning = false;
    }
}
