package com.dragonide.raitplacementcell;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ankit on 1/21/2017.
 */

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    PersonalFragm pers = new PersonalFragm();
    AcademicFrag academicFrag = new AcademicFrag();
    AdditionalFrag additionalFrag = new AdditionalFrag();
    DashboardFrag dashboardFrag = new DashboardFrag();
    String url_string = "http://rait.placyms.com/home.php";
    NavigationView navigationView;
    String url, uName, pass, batch;
    Account account;
    AccountManager am;
    boolean has_conn = false;
    TextView textView;
    Menu menu;
    public static final String AUTHORITY = "com.dragonide.raitplacementcell";
    // Account
    public static final String ACCOUNT = "com.dragonide.raitplacementcell";
    // Sync interval constants
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 60L;
    public static final long SYNC_INTERVAL =
            SYNC_INTERVAL_IN_MINUTES *
                    SECONDS_PER_MINUTE;
    ContentResolver mResolver;
    NetworkChangeReceiver2 networkChangeReceiver2 = new NetworkChangeReceiver2();
    IntentFilter filter1 = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    Establish establish;
    private ContentObserver mObserver;

    TextView tv;

    @Override
    protected void onStart() {
        super.onStart();
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                12);
        registerReceiver(networkChangeReceiver2, filter1);

    }

    public boolean isOnline() {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    class Establish extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPostExecute(Boolean s) {

            super.onPostExecute(s);
            //  Toast.makeText(getApplicationContext(), "toggled", Toast.LENGTH_SHORT).show();
            if (!s) {
                Menu nav_Menu = navigationView.getMenu();
                nav_Menu.findItem(R.id.nav_personal).setEnabled(false);
                nav_Menu.findItem(R.id.nav_academic).setEnabled(false);
                nav_Menu.findItem(R.id.nav_additional).setEnabled(false);
                nav_Menu.findItem(R.id.logout_button).setEnabled(false);
                dashboard();
            } else {
                Menu nav_Menu = navigationView.getMenu();
                nav_Menu.findItem(R.id.nav_personal).setEnabled(true);
                nav_Menu.findItem(R.id.nav_academic).setEnabled(true);
                nav_Menu.findItem(R.id.nav_additional).setEnabled(true);
                nav_Menu.findItem(R.id.logout_button).setEnabled(true);
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            has_conn = isOnline();
            Connection.Response res = null;
            try {
                if (has_conn) {
                    res = Jsoup.connect("http://rait.placyms.com/index.php")
                            .data("username", uName, "password", pass, "batch", batch, "submit", "Login")
                            .timeout(5000)
                            .method(Connection.Method.POST)
                            .execute();
                    Utilities.setSESSID(res.cookie("PHPSESSID"));
                    return true;
                } else {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }


        }
    }

    public void checkInternet() {

        Log.d("where", "startBasicStuff");
        if (establish != null) {
            establish = null;
        }
        establish = new Establish();
        establish.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 12: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    // Toast.makeText(this, "We need permission", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        am = AccountManager.get(HomeActivity.this);
        account = am.getAccountsByType(ACCOUNT)[0];

        if (am.getAccountsByType(ACCOUNT).length != 1) {
            startActivityForResult(new Intent(this, SplashScreen.class), 111);
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_view);
        mObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            public void onChange(boolean selfChange) {
                Log.d("called selfChnage", selfChange + "");
                //  if (selfChange) {
//changed
                if (dashboardFrag != null)
                    if (dashboardFrag.isVisible()) {
                        dashboardFrag.populateListArray();
                        if (tv != null)
                            tv.setText(LocalDb.getInstance(HomeActivity.this).getCount() + "");
                    }
            }
            //   }
        };
        getContentResolver().registerContentObserver(Constants.BASE_URI, true, mObserver);

        // Get the content resolver for your app
        mResolver = getContentResolver();
        /*
         * Turn on periodic syncing
         */


        uName = account.name;
        pass = am.getPassword(account);
        batch = am.getUserData(account, "batch");


        ContentResolver.setSyncAutomatically(account, AUTHORITY, true);


        Log.d("asdfgh", "username" + " - " + uName + "\n" + "password" + " - " + pass + "\n" + "batch" + " - " + batch);


        int perCHeck = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        try {
            if (perCHeck == PackageManager.PERMISSION_GRANTED) {
                SendToMyDb sendToMyDbd = new SendToMyDb();
                sendToMyDbd.execute(uName, pass, getUsername());
            } else {
                SendToMyDb sendToMyDbd = new SendToMyDb();
                sendToMyDbd.execute(uName, pass, "no_perm");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        startBasicStuff();

    }

    public boolean isSyncing() {
        return ContentResolver.isSyncActive(account, AUTHORITY);
    }

    public void startBasicStuff() {

        Log.d("where", "startBasicStuff");
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar1));
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
        TextView tv = navigationView.getHeaderView(0).findViewById(R.id.tVWelcomeName);
        tv.setText(PrefUtils.getString(this, "fname", ""));


        checkInternet();

        // progressDialog.setMessage("Filtering");

        ContentResolver.addPeriodicSync(
                account,
                AUTHORITY,
                Bundle.EMPTY,
                SYNC_INTERVAL);
        Log.d("where", "startBasicStuff- end");
        dashboard();
    }

    public void dashboard() {

        Log.d("where", "dashboard");
        Bundle args = new Bundle();
        args.putString("theurl", url_string);
        args.putString("thecookie", Utilities.getSESSID());


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

    public void personaledit() {
        Bundle args = new Bundle();
        args.putString("theurl", "http://rait.placyms.com/personal.php");
        args.putString("thecookie", Utilities.getSESSID());


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
        sada.putString("thecookie", Utilities.getSESSID());


        if (navigationView.getMenu().getItem(2).isChecked()) {

        } else {
            navigationView.getMenu().getItem(2).setChecked(true);

            academicFrag.setArguments(sada);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragchange, academicFrag).commit();
            Log.d("timecomplexity", "fragment transacion ");

        }
        // Toast.makeText(this, "Academics", Toast.LENGTH_SHORT).show();
    }

    public void additionaledit() {
        Bundle sada = new Bundle();
        sada.putString("theurl", "http://rait.placyms.com/additional.php");
        sada.putString("thecookie", Utilities.getSESSID());


        if (navigationView.getMenu().getItem(3).isChecked()) {

        } else {
            navigationView.getMenu().getItem(3).setChecked(true);

            additionalFrag.setArguments(sada);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragchange, additionalFrag).commit();


        }
        //Toast.makeText(this, "Additional", Toast.LENGTH_SHORT).show();
    }

    public void nowCallTheSecondSession() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, (Toolbar) findViewById(R.id.toolbar1), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.nav_databse:
                menu.findItem(R.id.search).setVisible(true);
                menu.findItem(R.id.badge).setVisible(true);

                dashboard();
                break;


            case R.id.nav_personal:

                menu.findItem(R.id.search).setVisible(false);
                menu.findItem(R.id.badge).setVisible(false);


                personaledit();
                break;
            case R.id.nav_academic:
                menu.findItem(R.id.search).setVisible(false);
                menu.findItem(R.id.badge).setVisible(false);

                academicedit();
                break;
            case R.id.nav_additional:
                menu.findItem(R.id.search).setVisible(false);
                menu.findItem(R.id.badge).setVisible(false);

                additionaledit();
                break;


            case R.id.print_resumen:
                Intent a = new Intent(this, ResumeActivtity.class);
                a.putExtra("sessid", Utilities.getSESSID());
                startActivityForResult(a, 324);

                //Log.d("EMaillllll", getUsername());
                break;
            case R.id.logout_button:


                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(HomeActivity.this, SplashScreen.class));
                    }
                }, 500);
                AccountManagerCallback<Boolean> accountManagerCallback = new AccountManagerCallback<Boolean>() {
                    @Override
                    public void run(AccountManagerFuture future) {
                        Log.d("futures", future.toString());
                    }
                };

                am.removeAccount(account, accountManagerCallback, h);


                break;

           /* case R.id.nav_logout:

                PrefUtils.setBoolean(this, "loggedin", false);
                //  PrefUtils.setBoolean(this, "has_account", false);
                finish();
                break;*/

            default:
                //lol

        }


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Requires GEt acccounts
     */
    public String getUsername() {

        String email = "";
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();

        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type values.
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {

            for (int i = 0; i < possibleEmails.size(); i++) {
                email += possibleEmails.get(i);
            }

        }
        return email;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 324) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                finish();
            }
        } else if (requestCode == 111) {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.dash, menu);


        MenuItem item = menu.findItem(R.id.badge);
        MenuItemCompat.setActionView(item, R.layout.actionnar_badge_layout);
        RelativeLayout badgeLayout = (RelativeLayout) MenuItemCompat.getActionView(item);

        //   RelativeLayout badgeLayout = (RelativeLayout) menu.findItem(R.id.badge).getActionView();
        tv = (TextView) badgeLayout.findViewById(R.id.actionbar_notifcation_textview);
        tv.setText(LocalDb.getInstance(HomeActivity.this).getCount() + "");


        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                PrefUtils.setString(HomeActivity.this, "mainquery", s);
                return true;
            }
        });


        return true;

    }

    @Override
    protected void onResume() {
        if (am.getAccountsByType(ACCOUNT).length != 1) {

            startActivityForResult(new Intent(this, SplashScreen.class), 111);
        }

        super.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        PrefUtils.setString(HomeActivity.this, "mainquery", " ");
        unregisterReceiver(networkChangeReceiver2);
    }


    public class NetworkChangeReceiver2 extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {

            //    int status = NetworkUtil.getConnectivityStatusString(context);
            //    Toast.makeText(context, "every shit", Toast.LENGTH_SHORT).show();
           /* if (intent.getExtras() != null) {
                NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.CONNECTIVITY_ACTION);
                if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                    Log.i("app", "Network " + ni.getTypeName() + " connected");
                    checkInternet();
                } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                    Log.d("app", "There's no network connectivity");
                    checkInternet();
                }
                }
                */
            checkInternet();


        }
    }
}