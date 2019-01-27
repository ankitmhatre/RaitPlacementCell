package com.dragonide.raitplacementcell;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by Ankit on 1/27/2017.
 */

public class ShowDialog extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MaterialDialog d = new MaterialDialog.Builder(ShowDialog.this)
                .title(getIntent().getStringExtra("title"))
                .customView(R.layout.dialog_layout, true)
                .positiveText(R.string.ok)

                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();


        View dview = d.getCustomView();
        WebView wv = (WebView) dview.findViewById(R.id.dialog_text);

        //poistion i
        wv.loadData(getIntent().getStringExtra("msg"), "text/html; charset=utf-8", "utf-8");


    }
}
