package com.dragonide.raitplacementcell;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Ankit on 9/2/2017.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        int status = NetworkUtil.getConnectivityStatusString(context);

        if (!"android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            if(status==NetworkUtil.NETWORK_STATUS_NOT_CONNECTED){
               //not COnnected



            }else{
             //yes connected
            }

        }
    }
}