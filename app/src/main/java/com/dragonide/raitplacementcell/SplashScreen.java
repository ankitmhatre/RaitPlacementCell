package com.dragonide.raitplacementcell;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dragonide.raitplacementcell.authenticator.AuthenticatorActivity;

/**
 * Created by Ankit on 1/23/2017.
 */

public class SplashScreen extends AppCompatActivity {
    TextView status_on_splash_screen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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
}
