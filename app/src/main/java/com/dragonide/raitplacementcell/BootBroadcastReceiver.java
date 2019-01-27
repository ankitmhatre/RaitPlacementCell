package com.dragonide.raitplacementcell;

import android.content.Context;
import android.content.Intent;
import androidx.legacy.content.WakefulBroadcastReceiver;

/**
 * Created by Ankit on 1/27/2017.
 */

public class BootBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Launch the specified service when this message is received
      //  Intent startServiceIntent = new Intent(context, MyPortalService.class);
       // startWakefulService(context, startServiceIntent);
    }
}