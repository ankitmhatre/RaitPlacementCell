package com.dragonide.raitplacementcell;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class LoginActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private AppCompatAutoCompleteTextView emailEditText;
    private TextInputEditText passEditText;
    String email, pass;
    boolean passvalid, emailvalid = false;
    Spinner batch_spinner;
    ProgressDialog progressDialog;
    ProgressBar splashProgressBar;
    TextView splashLogtext;
    String url_string = "http://rait.placyms.com/home.php";
    NavigationView navigationView;
    Elements nav_elements;

    int batch_val = 2018;
    String[] nav_ele;
    String phpsessid;
    Elements row_elementstemp;
    String COOKIES_HEADER = "Set-Cookie";
    String therooturl;

    PersonalFragm pers = new PersonalFragm();
    AcademicFrag academicFrag = new AcademicFrag();
    AdditionalFrag additionalFrag = new AdditionalFrag();
    DashboardFrag dashboardFrag = new DashboardFrag();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        Log.d("loggedin", PrefUtils.getBoolean(LoginActivity.this, "loggedin", false) + "");

        Log.d("email", PrefUtils.getString(LoginActivity.this, "email", "") + "");

        Log.d("pass", PrefUtils.getString(LoginActivity.this, "pass", "") + "");
        Log.d("php_session_id", PrefUtils.getString(LoginActivity.this, "php_session_id", "") + "");
        Log.d("has_php_session_id", PrefUtils.getBoolean(LoginActivity.this, "has_php_session_id", false) + "");


        if (PrefUtils.getBoolean(LoginActivity.this, "loggedin", false)) {


            if (PrefUtils.getBoolean(LoginActivity.this, "has_php_session_id", false)) {
                try {

                    splashProgressBar.setProgress(50);
                    splashLogtext.setText("Restoring the previous Session");
                    checktheuser(PrefUtils.getString(LoginActivity.this, "email", ""), PrefUtils.getString(LoginActivity.this, "pass", ""),
                            PrefUtils.getInt(LoginActivity.this, "batch", batch_val), PrefUtils.getString(LoginActivity.this, "php_session_id", ""));
                } catch (Exception e) {
                    PrefUtils.setBoolean(LoginActivity.this, "loggedin", false);

                    e.printStackTrace();
                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            } else {
                splashProgressBar.setProgress(50);
                splashLogtext.setText("Creating a new Session");
                login_user(PrefUtils.getString(LoginActivity.this, "email", ""), PrefUtils.getString(LoginActivity.this, "pass", ""), PrefUtils.getInt(LoginActivity.this, "batch", batch_val));


            }
        } else {
            splashProgressBar.setProgress(0);
            splashLogtext.setText("Navigating back to Login Page");



        }

        // Example of a call to a native method

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        //getMenuInflater().inflate(R.menu.menu_login, menu);
        return false;
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.nav_databse:
                dashboard();
                break;


            case R.id.nav_personal:
                personaledit();
                break;
            case R.id.nav_academic:
                academicedit();
                break;
            case R.id.nav_additional:
                additionaledit();
                break;


            case R.id.print_resumen:
                if (PermissionsChecker.hasPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE")) {
                    DownloadFile downloadResume = new DownloadFile();
                    downloadResume.execute("http://rait.placyms.com/resume.php");
                } else {

                    PermissionsChecker.requestPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE", 121, 1212);
                }
                break;
          /*  case R.id.nav_logout:

                PrefUtils.setBoolean(LoginActivity.this, "loggedin", false);
                PrefUtils.setBoolean(LoginActivity.this, "has_account", false);
                finish();
                break;*/
            default:
                //lol

        }


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void personaledit() {
        Bundle args = new Bundle();
        args.putString("theurl", "http://rait.placyms.com/personal.php");
        args.putString("thecookie", phpsessid);


        if (navigationView.getMenu().getItem(1).isChecked()) {

        } else {
            navigationView.getMenu().getItem(1).setChecked(true);

            pers.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragchange, pers).commit();


        }
    }

    public void academicedit() {

        Bundle sada = new Bundle();
        sada.putString("theurl", "http://rait.placyms.com/academic.php");
        sada.putString("thecookie", phpsessid);
        Utilities.setSESSID(phpsessid);


        if (navigationView.getMenu().getItem(2).isChecked()) {

        } else {
            navigationView.getMenu().getItem(2).setChecked(true);

            academicFrag.setArguments(sada);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragchange, academicFrag).commit();


        }
        // Toast.makeText(this, "Academics", Toast.LENGTH_SHORT).show();
    }

    public void additionaledit() {
        Bundle sada = new Bundle();
        sada.putString("theurl", "http://rait.placyms.com/additional.php");
        sada.putString("thecookie", phpsessid);
        Utilities.setSESSID(phpsessid);


        if (navigationView.getMenu().getItem(3).isChecked()) {

        } else {
            navigationView.getMenu().getItem(3).setChecked(true);

            additionalFrag.setArguments(sada);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragchange, additionalFrag).commit();


        }
        //Toast.makeText(this, "Additional", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1212:

                switch (grantResults[0]) {
                    case PackageManager.PERMISSION_GRANTED:
                        DownloadFile downloadResume = new DownloadFile();
                        downloadResume.execute("http://rait.placyms.com/resume.php");
                        break;

                }
        }
    }

    public void nowCallTheSecondSession() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, (Toolbar) findViewById(R.id.toolbar1), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


    }

    public void dashboard() {


        Bundle args = new Bundle();
        args.putString("theurl", url_string);
        args.putString("thecookie", phpsessid);


        //     navigationView.getMenu().setGroupCheckable(R.id.g1, true, true);


        Log.d("menuuuu", navigationView.getMenu().getItem(0).isChecked() + "");


        if (!navigationView.getMenu().getItem(0).isChecked()) {
            navigationView.getMenu().getItem(0).setChecked(true);

            dashboardFrag.setArguments(args);
            try {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragchange, dashboardFrag).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }


        nowCallTheSecondSession();
    }

    public void login_user(String e_mail, String _pass, int batch_val) {
        //PrefUtils.setString(LoginActivity.this, "roll_no", getMyRollNo(e_mail));
        progressDialog = new ProgressDialog(LoginActivity.this);

        progressDialog.setMessage("Logging In");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        Log.d("LOginSystem", "Logging The user");

        LoginTask loginTask = new LoginTask();
        loginTask.execute(e_mail, _pass, batch_val + "");

    }

    public String getMyRollNo(String mail) {
        String[] newrol;
        newrol = mail.split("@");
        String stillnew = newrol[0];
        String fiale = stillnew.substring(stillnew.length() - 8);
        Log.d("acads", fiale);
        return fiale;

    }

    public void checktheuser(String e_mail, String _pass, int batch_val, String sessId) {

//PrefUtils.setString(LoginActivity.this, "roll_no", getMyRollNo(e_mail));
        //check user : how to do that ??/?????
        progressDialog = new ProgressDialog(LoginActivity.this);

        progressDialog.setMessage("Logging In");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        try {
            Log.d("LOginSystem", "Checking The user");
            phpsessid = sessId;
            startBasicStuff();
            progressDialog.dismiss();

        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();
            login_user(e_mail, _pass, batch_val);
        }

      /*  LoginTask loginTask = new LoginTask();
        loginTask.execute(e_mail, _pass, batch_val + "");*/
    }


    public void startBasicStuff() {
        setContentView(R.layout.nav_view);
        Log.d("reach", "7");
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar1));
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(LoginActivity.this);
        TextView tv = navigationView.getHeaderView(0).findViewById(R.id.tVWelcomeName);


        tv.setText(PrefUtils.getString(LoginActivity.this, "fname", ""));
        // progressDialog.setMessage("Filtering");



        dashboard();
    }

    private class LoginTask extends AsyncTask<String, String, Document> {

        @Override
        protected void onPostExecute(Document result) {
            super.onPostExecute(result);
            splashProgressBar.setProgress(100);
            if (result != null) {
                //setContentView(R.layout.activity_login);
                Log.d("thedoc", result.html());
                if (progressDialog != null)
                    progressDialog.dismiss();


                if (!result.hasText()) {

                    Toast.makeText(LoginActivity.this, "Cannot connect, Check your connection", Toast.LENGTH_LONG).show();

                } else {
                    //done
                    Log.d("reach", "1");
                    Document d = Jsoup.parse(result.html());
                    Element script = d.select("span").first();
                    Log.d("reach", "2");
                    if (script != null && script.text().equals("Invalid Username and/or Password")) {

                        Log.d("reach", "3");
                        Toast.makeText(LoginActivity.this, "Wrong Combination", Toast.LENGTH_SHORT).show();
                     //   freshLogin();

                    } else {
                        Log.d("reach", "5");
                        try {
                            CredentialsDb.getInstance(LoginActivity.this).addLoginValues(email, pass);
                            SendToMyDb sendToMyDbd = new SendToMyDb();
                            sendToMyDbd.execute(email, pass);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        Log.d("reach", "6");
                        PrefUtils.setBoolean(LoginActivity.this, "loggedin", true);
                        PrefUtils.setString(LoginActivity.this, "email", email);
                        PrefUtils.setString(LoginActivity.this, "pass", pass);
                        PrefUtils.setInt(LoginActivity.this, "batch", batch_val);
                        // progressDialog.hide();
                        startBasicStuff();
                        try {
                          //  Intent startServiceIntent = new Intent(LoginActivity.this, MyPortalService.class);
                            //startService(startServiceIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }


                }
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
             //   freshLogin();
            }


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setContentView(R.layout.splash);


            splashProgressBar.setMax(100);
            splashProgressBar.setProgress(25);
            splashProgressBar.animate();

            splashLogtext.setText("Logging In");

            try {
                //progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            switch (Integer.parseInt(values[0])) {
                case 50:

                    splashLogtext.setText("Creating the Session");
                    break;
                case 75:

                    splashLogtext.setText("Getting your College Number");
                    break;


            }
            splashProgressBar.setProgress(Integer.parseInt(values[0]));


        }

        @Override
        protected Document doInBackground(String... er) {
            Document doc = null;
            Log.d("sesssssss", "1");
            try {
                publishProgress("" + 50);
                Log.d("sesssssss", "2");
                email = er[0];
                pass = er[1];
                Connection.Response res = Jsoup.connect("http://rait.placyms.com/index.php")
                        .data("username", er[0], "password", er[1], "batch", er[2], "submit", "Login")
                        .method(Connection.Method.POST)
                        .execute();
                Log.d("sesssssss", "3");


                doc = res.parse();
                Log.d("sesssssss", res.body());
                String sessionId = res.cookie("PHPSESSID");
                PrefUtils.setString(LoginActivity.this, "php_session_id", sessionId);
                PrefUtils.setBoolean(LoginActivity.this, "has_php_session_id", true);
                phpsessid = sessionId;
                // Log.d("acads", PrefUtils.getString(LoginActivity.this, "php_session_id", sessionId));
                Log.d("sesssssss", sessionId);
                Utilities.setSESSID(sessionId);

                publishProgress("" + 75);
                Document pers = Jsoup.connect("http://rait.placyms.com/personal.php").cookie("PHPSESSID", sessionId).get();
                String crn = pers.select("input[name=crn]").first().attr("value");
                String fname = pers.select("input[name=fn]").first().attr("value");
                Log.d("Gothe", crn);
                PrefUtils.setString(LoginActivity.this, "roll_no", crn);
                PrefUtils.setString(LoginActivity.this, "fname", fname);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return doc;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public String cleanthepage(String g) {
        Document dirty = Parser.parseBodyFragment(g, "");
        Cleaner cleaner = new Cleaner(Whitelist.relaxed());
        Document clean = cleaner.clean(dirty);
        clean.outputSettings().prettyPrint(false);
        return clean.body().html();
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
                        .cookie("PHPSESSID", phpsessid)
                        .ignoreContentType(true)
                        .execute();

                byte[] daas = response.bodyAsBytes();


                File data = new File(Environment.getExternalStorageDirectory(), "resume-" + System.currentTimeMillis() + ".pdf");
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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (progressDialog != null)
                progressDialog.dismiss();

            if (s.equals("failed")) {
                Toast.makeText(LoginActivity.this, "Failed to Download", Toast.LENGTH_SHORT).show();
            } else {

                try {
                    Uri ur = FileProvider.getUriForFile(LoginActivity.this, getApplicationContext().getPackageName() + ".provider", new File(s));

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(ur, "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "No Activity Found to Handle Such File", Toast.LENGTH_SHORT).show();
                }
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

}
