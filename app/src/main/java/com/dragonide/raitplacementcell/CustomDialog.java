package com.dragonide.raitplacementcell;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by Ankit on 7/5/2017.
 */

public class CustomDialog extends DialogFragment {
    private TextBackListener textBackListenerGlobal;
    public static CustomDialog newInstance(String title, String contentText){
        CustomDialog customDialog = new CustomDialog();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("contentText", contentText);



        customDialog.setArguments(bundle);

        return customDialog;

    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
}
