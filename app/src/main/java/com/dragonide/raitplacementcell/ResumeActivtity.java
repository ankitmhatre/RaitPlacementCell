package com.dragonide.raitplacementcell;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Ankit on 9/2/2017.
 */

public class ResumeActivtity extends AppCompatActivity {
    DownloadFile downloadFile;
    ProgressDialog progressDialog;
    RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    String[] myDataset;
    List<Item> items;
    String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
    String fileName = "RaitPlacementCell";

    File f = new File(baseDir + File.separator + fileName);

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.resume_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_download_resume:
                DownloadResume();

                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(this, HomeActivity.class));
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        f.mkdir();
        setContentView(R.layout.resume_layout);
        // Toolbar toolbar = (Toolbar) findViewById(R.id.rtrrt);
        //setSupportActionBar(toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        items = new ArrayList<>();
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new ResumeAdapter(items);
        prepareResumeList();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Item movie = items.get(position);
                //   Toast.makeText(getApplicationContext(), movie.getTimeStamp() + " is selected!", Toast.LENGTH_SHORT).show();
                OpenFile(movie.getrUri());

            }

            @Override
            public void onLongClick(View view, int position) {
               /* Item movie = items.get(position);
                Intent share = new Intent();
                share.setAction(Intent.ACTION_SEND);
                share.setType("application/pdf");
                share.putExtra(Intent.EXTRA_STREAM, Uri.parse(movie.getrUri()));


                startActivity(share);*/
            }
        }));

        super.onCreate(savedInstanceState);
    }

    private void prepareResumeList() {
        items.clear();
        FilenameFilter filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.endsWith(".pdf");
            }
        };

        File[] dirFiles = f.listFiles(filenameFilter);
        if (dirFiles != null) {
            for (File strFile : dirFiles) {
                Item item = new Item();
                item.setrUri(strFile.getPath());
                item.setTimeStamp(strFile.getName());

                items.add(item);

            }
        } else {
            Item item = new Item();
            item.setrUri("empty");
            item.setTimeStamp("No Files present");

            items.add(item);

        }

        mAdapter.notifyDataSetChanged();
    }

    public void DownloadResume() {

        progressDialog = new ProgressDialog(ResumeActivtity.this);

        progressDialog.setMessage("Logging In");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            //Gotcha
            String asr = getIntent().getExtras().getString("sessid");
            downloadFile = new DownloadFile();
            downloadFile.execute("http://rait.placyms.com/resume.php", asr);

        } else {

        }
    }

    public void OpenFile(String s) {
        if (s.equals("empty")) {
            return;
        }
        try {
            Uri ur = FileProvider.getUriForFile(ResumeActivtity.this, getApplicationContext().getPackageName() + ".provider", new File(s));

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(ur, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ResumeActivtity.this, "No Activity Found to Handle Such File", Toast.LENGTH_SHORT).show();
        }
    }

    class DownloadFile extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String _path;

            try {
               /* java.net.CookieManager msCookieManager = new java.net.CookieManager();
                msCookieManager.getCookieStore().add(null, HttpCookie.parse(phpsessid).get(0));
*/

                Connection.Response response = Jsoup.connect(strings[0])
                        .cookie("PHPSESSID", strings[1])
                        .ignoreContentType(true)
                        .execute();

                byte[] daas = response.bodyAsBytes();

                File data = new File(f, GetTime() + ".pdf");
                //File file = new File(getFilesDir(), GetTime() + ".pdf");
                OutputStream op = new FileOutputStream(data);
                op.write(daas);
                op.close();
                _path = data.getPath();


            } catch (Exception e) {
                e.printStackTrace();
                _path = "failed";
            }


            return _path;
        }

        public String GetTime() {
            long yourmilliseconds = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
            Date resultdate = new Date(yourmilliseconds);
            return sdf.format(resultdate);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (progressDialog != null)
                progressDialog.dismiss();

            if (s.equals("failed")) {
                Toast.makeText(ResumeActivtity.this, "Failed to Download", Toast.LENGTH_SHORT).show();
            } else {
                prepareResumeList();

             /*   try {
                    Uri ur = FileProvider.getUriForFile(ResumeActivtity.this, getApplicationContext().getPackageName() + ".provider", new File(s));

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(ur, "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ResumeActivtity.this, "No Activity Found to Handle Such File", Toast.LENGTH_SHORT).show();
                }*/
            }

            progressDialog.hide();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Downloading File");
            progressDialog.show();
        }
    }


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ResumeActivtity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ResumeActivtity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }


}
