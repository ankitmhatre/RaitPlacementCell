package com.dragonide.raitplacementcell;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dragonide.raitplacementcell.authenticator.AuthenticatorActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Ankit on 1/23/2017.
 */

public class SplashScreen extends AppCompatActivity {
    TextView status_on_splash_screen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createNotificationChannel();
        setContentView(R.layout.splash);
        ProgressBar spinner = (ProgressBar) findViewById(R.id.asadadsad);
        spinner.getIndeterminateDrawable().setColorFilter(0xFFFFFFFF, android.graphics.PorterDuff.Mode.MULTIPLY);

        status_on_splash_screen = (TextView) findViewById(R.id.status_on_splash_screen);
        status_on_splash_screen.setText("Signing In");




        /*This is to check weather at the start of the Application
         * there is an account on this device or not
         * If it doesn't navigate the user to LoginActivity.java
         * else navigate the user to Dashboard  */
        AccountManager am = AccountManager.get(this);
        final Account[] account = am.getAccountsByType(HomeActivity.ACCOUNT);


        if (account.length > 1) {
            //More than one account
            status_on_splash_screen.setText("Conflicting two Accounts");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SplashScreen.this, "Delete multiple accounts", Toast.LENGTH_SHORT).show();
                    startActivityForResult(new Intent(Settings.ACTION_SYNC_SETTINGS).putExtra("account", account[0]), 0);


                }
            }, 1500);
        } else if (account.length < 1) {
            //No Account
            status_on_splash_screen.setText("Getting Started");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivityForResult(new Intent(SplashScreen.this, AuthenticatorActivity.class), 0);

                }
            }, 1500);
        } else {
            //Only One Account
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivityForResult(new Intent(SplashScreen.this, HomeActivity.class), 0);

                }
            }, 1500);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

            finish();
        }
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("dashboard", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.d("chnnal", "NotificationCHannel registered");
        }
    }

}
