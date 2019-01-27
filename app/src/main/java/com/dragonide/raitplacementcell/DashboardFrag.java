package com.dragonide.raitplacementcell;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.dragonide.raitplacementcell.providers.FeedContract;

import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import static com.dragonide.raitplacementcell.HomeActivity.ACCOUNT;
import static com.dragonide.raitplacementcell.HomeActivity.AUTHORITY;

/**
 * Created by Ankit on 1/22/2017.
 */

public class DashboardFrag extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    SharedPreferences sharedPreferences;
    Elements nav_elements;

    int batch_val = 2018;
    String[] nav_ele;
    ProgressBar pb;
    ListView lv1;
    Elements row_elementstemp;
    String query = "";
    String DASHURL;
    SwipeRefreshLayout mSwipeRefreshLayout;
    String COOKIE;
    public ArrayList<NotifItem> mArray;
    NotifcationAdapter notifcationAdapter;
    MaterialDialog d;
    AccountManager am;
    Account account;


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Log.d("hey" + s, sharedPreferences.getString("mainquery", ""));
        try {
            query = sharedPreferences.getString("mainquery", "");
            populateListArray();
            if (notifcationAdapter != null) {
                notifcationAdapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestingSync() {


        Bundle b = new Bundle();
        b.putString("batch", am.getUserData(account, "batch"));
        ContentResolver.requestSync(account, FeedContract.CONTENT_AUTHORITY, b);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mArray = new ArrayList<NotifItem>();

        View v = inflater.inflate(R.layout.notifications_home, container, false);

        lv1 = (ListView) v.findViewById(R.id.lvhead);
        pb = (ProgressBar) v.findViewById(R.id.progressBarlv);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        requestingSync();
                        mSwipeRefreshLayout.setRefreshing(isSyncing());
                    }
                }
        );
        populateListArray();


        afterlistPopulate();

        pb.setVisibility(View.GONE);
        return v;
    }

    public boolean isSyncing() {
        return ContentResolver.isSyncActive(account, AUTHORITY);
    }

    public String cleanthepage(String g) {
        Document dirty = Parser.parseBodyFragment(g, "");
        Cleaner cleaner = new Cleaner(Whitelist.relaxed());
        Document clean = cleaner.clean(dirty);
        clean.outputSettings().prettyPrint(false);
        return clean.body().html();
    }

    public void populateListArray() {

        //  long m_id;
        Cursor cursorExternal;
        mArray.clear();
        String filter = query;
        if (filter.length() > 0) {
            cursorExternal = LocalDb.getInstance(getActivity()).searchcursor(filter);
        } else {
            cursorExternal = LocalDb.getInstance(getActivity()).getAllEntries();
        }
        try {
            cursorExternal.moveToFirst();
            do {
                NotifItem item = new NotifItem();
                item.n_title = cursorExternal.getString(cursorExternal.getColumnIndexOrThrow("n_title"));
                item.n_content = cursorExternal.getString(cursorExternal.getColumnIndexOrThrow("n_content"));
                item.n_read = cursorExternal.getString(cursorExternal.getColumnIndexOrThrow("n_read"));
                //  item.mLyrics = cursorExternal.getString(cursorExternal.getColumnIndexOrThrow("lyrics"));
                mArray.add(item);


            } while (cursorExternal.moveToNext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        afterlistPopulate();
    }

    public void afterlistPopulate() {
        try {
            notifcationAdapter = new NotifcationAdapter(getActivity(), mArray);

            lv1.setAdapter(notifcationAdapter);
            //    PrefUtils.setString(getActivity(), "latest_notification", row_elementstemp.first().text());


            lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    d = new MaterialDialog.Builder(getActivity())
                            .title(notifcationAdapter.getItem(i).getN_title() + "")
                            .customView(R.layout.dialog_layout, true)
                            .theme(Theme.LIGHT)
                            .positiveText(R.string.ok)

                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                }
                            }).show();


                    String newString = notifcationAdapter.getItem(i).getN_content();
                    Log.d("testt", newString);
                    //    String new2 = newString.replaceAll("https://.+?(com|net|org|gl)/{0,1}", "<a href=\"$0\">$0</a>");

                    View dview = d.getCustomView();
                    WebView wv = (WebView) dview.findViewById(R.id.dialog_text);

                    //poistion i
                    wv.loadData(newString, "text/html; charset=utf-8", "utf-8");


                    Log.d("clicked", adapterView.getItemAtPosition(i) + "");
                    Log.d("clicked", i + "");
                    Log.d("clicked", l + "");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void shareIntent(String title, String body) {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(shareIntent, "Share Via"));

    }

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(syncFinishedReceiver, new IntentFilter(Constants.FINISH_SYNC));

    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(syncFinishedReceiver);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        DASHURL = "http://rait.placyms.com/home.php"; //getArguments().getString("theurl");
        COOKIE = getArguments().getString("thecookie");


        Log.d("LAYPOUTT", "DASHBOARD FRAG");


        Log.d("loggedin", PrefUtils.getBoolean(getActivity(), "loggedin", false) + "");

        Log.d("email", PrefUtils.getString(getActivity(), "email", "") + "");

        Log.d("pass", PrefUtils.getString(getActivity(), "pass", "") + "");
        Log.d("php_session_id", PrefUtils.getString(getActivity(), "php_session_id", "") + "");
        Log.d("has_php_session_id", PrefUtils.getBoolean(getActivity(), "has_php_session_id", false) + "");

        am = AccountManager.get(getContext());
        account = am.getAccountsByType(ACCOUNT)[0];
        requestingSync();
    }

    private BroadcastReceiver syncFinishedReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("syncFinishedReceiver", "Sync finished, should refresh nao!!");
          //  mSwipeRefreshLayout.setRefreshing(false);
         //   populateListArray();
          /*  if (d != null) {
                if (d.isShowing()) {
                    d.dismiss();
                }
            }*/

        }
    };
}
