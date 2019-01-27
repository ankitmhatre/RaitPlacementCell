package com.dragonide.raitplacementcell;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.HttpURLConnection;
import java.net.URL;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

/**
 * Created by Ankit on 6/8/2017.
 */

public class ClassX extends Fragment {
    String sessId;
    Spinner xBoard;
    EditText xboardother, xmonth, xyear, xmo, xto;
    TextView xdownload;
    Button uploadxmarksheet;
    Retrive10Details retrive10Details;
    ProgressDialog progressDialog;
    boolean executiondone = false;

    String marksheetUrl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessId = PrefUtils.getString(getActivity(), "php_session_id", "");

        Log.d("acads", sessId);
        Log.d("acadsu", Utilities.getSESSID());
        marksheetUrl = "http://rait.placyms.com/documents/" + PrefUtils.getString(getActivity(), "roll_no", "") + "-xupload.pdf";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        marksheetUrl = "http://rait.placyms.com/documents/" + PrefUtils.getString(getActivity(), "roll_no", "") + "-xupload.pdf";
        View v = inflater.inflate(R.layout.class_x, container, false);
        sessId = PrefUtils.getString(getActivity(), "php_session_id", "");
        Log.d("acads", sessId);
        Log.d("acadsu", Utilities.getSESSID());
        xBoard = (Spinner) v.findViewById(R.id.xboardspinner);
        xboardother = (EditText) v.findViewById(R.id.otherboardetinxboard);
        xmonth = (EditText) v.findViewById(R.id.completionMonthxboard);
        xyear = (EditText) v.findViewById(R.id.completionYearxboard);
        xdownload = (TextView) v.findViewById(R.id.downloadxboardmarksheet);
        xmo = (EditText) v.findViewById(R.id.marksObtainedxboard);
        xto = (EditText) v.findViewById(R.id.totalMarksxBoard);
        uploadxmarksheet = (Button) v.findViewById(R.id.xmarksheetButton);
        progressDialog = new ProgressDialog(getActivity());

        progressDialog.setMessage("Fetching");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        if (!executiondone) {
            retrive10Details = new Retrive10Details();
            retrive10Details.execute(new String[]{sessId});
        } else {
            retrive10Details = null;
            retrive10Details = new Retrive10Details();
            retrive10Details.execute(new String[]{sessId});
        }

        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 98:

                switch (grantResults[0]) {
                    case PackageManager.PERMISSION_GRANTED:
                        downloadFile(marksheetUrl);
                        break;

                }
        }
    }

    private void downloadFile(String url) {
        Log.d("marksheetUrl", url);
        String _path;
       /* try {
            Connection.Response response = Jsoup.connect(url)
                    .cookie("PHPSESSID", sessId)
                    .ignoreContentType(true)
                    .execute();

            byte[] daas = response.bodyAsBytes();


            File data = new File(Environment.getExternalStorageDirectory(), "xmarksheet-" + System.currentTimeMillis() + ".pdf");
            OutputStream op = new FileOutputStream(data);
            op.write(daas);
            op.close();
            _path = data.getPath();*/
        Uri ur = Uri.parse(url);// FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", new File(_path));

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(ur, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
       /* } catch (Exception e) {
            _path = "failed";
            return; // swallow}
        }*/
    }

    class Retrive10Details extends AsyncTask<String, Void, Document> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPostExecute(Document document) {
            super.onPostExecute(document);

            executiondone = true;
        /*    String kalu = null;
            for (Element e : document.getAllElements()) {
                for (Node n : e.childNodes()) {

                    if (n instanceof Comment) {

                        kalu = ((Comment) n).getData();
                    }

                }
  Document d = Jsoup.parse(kalu);

            }*/


            if (document != null) {
                if (xBoard.getSelectedItemPosition() == 5) {
                    xboardother.setEnabled(true);
                    xboardother.setText(document.select("input[name=xboardotr]").first().attr("value"));
                } else {
                    xboardother.setEnabled(false);
                }

                xmonth.setText(document.select("input[name=xmonth]").first().attr("value"));
                xyear.setText(document.select("input[name=xyear]").first().attr("value"));
                xmo.setText(document.select("input[name=xmo]").first().attr("value"));
                xto.setText(document.select("input[name=xtm]").first().attr("value"));

                Element fr = document.select("select").first();
                Elements selected = fr.select("option");
                int rt = 0;
                for (Element element : selected) {

                    if (element.select("option[selected]").attr("value").length() > 2) {
                        Log.d("optiontag", "" + rt);
                        xBoard.setSelection(rt);
                    }
                    rt++;


                }
                if (FileExistsOnInternet(marksheetUrl)) {
                    xdownload.setVisibility(View.VISIBLE);
                    xdownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED) {
                                downloadFile(marksheetUrl);
                            } else {
                                requestPermissions(new String[]{
                                        "android.permission.WRITE_EXTERNAL_STORAGE"
                                }, 98);

                            }
                        }
                    });
                } else {
                    xdownload.setVisibility(View.GONE);
                }

            } else {
                Toast.makeText(getActivity(), "Failed to Retrieve", Toast.LENGTH_SHORT).show();
            }

            progressDialog.hide();
        }

        @Override
        protected Document doInBackground(String... strings) {
            Document document = null;
            try {
                Log.d("acads", strings[0]);

                Connection.Response loginForm = Jsoup.connect("http://rait.placyms.com/academic.php")
                        .method(Connection.Method.GET)
                        .cookie("PHPSESSID", strings[0])
                        .execute();
                document = loginForm.parse();
                // document = Jsoup.connect("http://rait.placyms.com/academic.php").cookie("PHPSESSID", strings[0]).get();


                Log.d("acads", document.html());
                if (!document.html().contains("Edit Academic Information")) {
                    return null;
                }

            } catch (Exception e) {
                e.printStackTrace();

            }
            return document;
        }
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
