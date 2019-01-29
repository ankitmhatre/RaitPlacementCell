/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.dragonide.raitplacementcell.authenticator;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.dragonide.raitplacementcell.Constants;
import com.dragonide.raitplacementcell.HomeActivity;
import com.dragonide.raitplacementcell.PrefUtils;
import com.dragonide.raitplacementcell.R;
import com.dragonide.raitplacementcell.SignupActivity;
import com.dragonide.raitplacementcell.client.NetworkUtilities;
import com.google.android.material.textfield.TextInputEditText;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.regex.Pattern;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;


/**
 * Activity which displays login screen to the user.
 */
public class AuthenticatorActivity extends AccountAuthenticatorActivity {
    /**
     * The Intent flag to confirm credentials.
     */
    public static final String PARAM_CONFIRM_CREDENTIALS = "confirmCredentials";

    /**
     * The Intent extra to store password.
     */
    public static final String PARAM_PASSWORD = "password";

    /**
     * The Intent extra to store username.
     */
    public static final String PARAM_USERNAME = "username";

    /**
     * The Intent extra to store username.
     */
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";

    /**
     * The tag used to log to adb console.
     */
    private static final String TAG = "AuthenticatorActivity";
    private AccountManager mAccountManager;

    /**
     * Keep track of the login task so can cancel it if requested
     */
    private UserLoginTask mAuthTask = null;


    /**
     * If set we are just checking that the user knows their credentials; this
     * doesn't cause the user's password or authToken to be changed on the
     * device.
     */
    private Boolean mConfirmCredentials = false;

    /**
     * for posting authentication attempts back to UI thread
     */


    private String mPassword;

    private AppCompatAutoCompleteTextView mUsernameEdit;
    private TextInputEditText mPasswordEdit;


    private String mUsername;


    private Spinner batch_spinner;

    public boolean isOnline = false;
    Document fullpage = null;

    ArrayList<String> displayvalues = new ArrayList<>();
    ArrayList<String> passingvalues = new ArrayList<>();

    ProgressDialog progressDialog;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle icicle) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Data");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        new CheckInternetTask().execute();
        new ConnectToRaitPlacyMs().execute();
        super.onCreate(icicle);
        mAccountManager = AccountManager.get(this);
        Log.i(TAG, "loading data from Intent");
        final Intent intent = getIntent();
        mUsername = intent.getStringExtra(PARAM_USERNAME);

        mConfirmCredentials = intent.getBooleanExtra(PARAM_CONFIRM_CREDENTIALS, false);

        setContentView(R.layout.activity_login);


        mUsernameEdit = (AppCompatAutoCompleteTextView) findViewById(R.id.input_layout_email);
        mPasswordEdit = (TextInputEditText) findViewById(R.id.input_layout_password);
        if (!TextUtils.isEmpty(mUsername)) mUsernameEdit.setText(mUsername);
        //mMessage.setText(getMessage());

        batch_spinner = (Spinner) findViewById(R.id.batch_spinner);


        batch_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });


    }

    @Override
    protected void onStart() {

        if (!isOnline) {
            Toast.makeText(this, "No Internet Connection, Connect and try again later ", Toast.LENGTH_SHORT).show();
            progressDialog.hide();
        }
        super.onStart();

    }


    /**
     * Handles onClick event on the Submit button. Sends username/password to
     * the server for authentication. The button is configured to call
     * handleLogin() in the layout XML.
     *
     * @paramview The Submit button for which this method is invoked
     */
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }


    public void handleLogin(View view) {
        mUsername = mUsernameEdit.getText().toString();
        mPassword = mPasswordEdit.getText().toString();

        switch (view.getId()) {
            case R.id.b_login:
                boolean correct = true;
                if (!isValidEmail(mUsernameEdit.getText().toString())) {
                    mUsernameEdit.setError("Enter the correct email address");
                    correct = false;
                }
                if (TextUtils.isEmpty(mPasswordEdit.getText().toString())) {
                    mPasswordEdit.setError("Enter the password");
                    correct = false;
                }
                if (correct) {
                    mAuthTask = new UserLoginTask();
                    mAuthTask.execute((new String[]{mUsernameEdit.getText().toString(), mPasswordEdit.getText().toString(), passingvalues.get(batch_spinner.getSelectedItemPosition())}));
                }


                // mAuthTask = new UserLoginTask();
                // mAuthTask.execute((new String[]{mUsernameEdit.getText().toString(), mPasswordEdit.getText().toString(), batch_val + ""}));


                // Show a progress dialog, and kick off a background task to perform
                // the user login attempt.


                break;
            case R.id.signUp_inLogin_button:
                mUsername = mUsernameEdit.getText().toString();
                mPassword = mPasswordEdit.getText().toString();
                Bundle b = new Bundle();
                b.putString("email", mUsername);
                b.putString("password", mPassword);

                Intent i = new Intent(this, SignupActivity.class);
                i.putExtras(b);
                startActivity(i);


                break;

        }


    }

    /**
     * Called when response is received from the server for confirm credentials
     * request. See onAuthenticationResult(). Sets the
     * AccountAuthenticatorResult which is sent back to the caller.
     *
     * @param result the confirmCredentials result.
     */
    private void finishConfirmCredentials(boolean result) {
        Log.i(TAG, "finishConfirmCredentials()");
        final Account account = new Account(mUsername, Constants.ACCOUNT_TYPE);
        mAccountManager.setPassword(account, mPassword);
        mAccountManager.setUserData(account, "batch", passingvalues.get(batch_spinner.getSelectedItemPosition()));
        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_BOOLEAN_RESULT, result);
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        //finish(result);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 212) {
            finish();

        }
    }

    /**
     * Called when response is received from the server for authentication
     * request. See onAuthenticationResult(). Sets the
     * AccountAuthenticatorResult which is sent back to the caller. We store the
     * authToken that's returned from the server as the 'password' for this
     * account - so we're never storing the user's actual password locally.
     *
     * @paramresult the confirmCredentials result.
     */


    private void finishLogin(String authToken) {

        Log.i(TAG, "finishLogin()");
        final Account account = new Account(mUsername, Constants.ACCOUNT_TYPE);

        Bundle da = new Bundle();
        da.putString("batch", passingvalues.get(batch_spinner.getSelectedItemPosition()));
        mAccountManager.addAccountExplicitly(account, mPassword, da);
        mAccountManager.setPassword(account, mPassword);
        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, mUsername);
        intent.putExtra(AccountManager.KEY_PASSWORD, mPassword);
        intent.putExtra("batch", passingvalues.get(batch_spinner.getSelectedItemPosition()));

        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);

        startActivityForResult(new Intent(AuthenticatorActivity.this, HomeActivity.class), 212);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    /**
     * Called when the authentication process completes (see attemptLogin()).
     *
     * @param authToken the authentication token returned by the server, or NULL if
     *                  authentication failed.
     */

    public void onAuthenticationResult(String authToken) {

        boolean success = ((authToken != null) && (authToken.length() > 0));
        Log.i(TAG, "onAuthenticationResult(" + success + ")");

        // Our task is complete, so clear it out
        mAuthTask = null;

        // Hide the progress dialog


        if (success) {


            //setPriodic syncing

            if (!mConfirmCredentials) {
                finishLogin(authToken);
            } else {
                finishConfirmCredentials(success);
            }
        } else {

            // if (mRequestNewAccount) {
            // "Please enter a valid username/password.
            Toast.makeText(this, "Please enter a valid username/password/batch", Toast.LENGTH_LONG).show();
            // } else {
            // "Please enter a valid password." (Used when the
            // account is already in the database but the password
            // doesn't work.)
            //  mMessage.setText(getText(R.string.login_fail));
            // Toast.makeText(this, "Please enter the correct password", Toast.LENGTH_LONG).show();
            //  }
        }

    }

    public void onAuthenticationCancel() {
        Log.i(TAG, "onAuthenticationCancel()");

        // Our task is complete, so clear it out
        mAuthTask = null;

        // Hide the progress dialog

    }


    /**
     * Returns the message to be displayed at the top of the login dialog box.
     */
    private CharSequence getMessage() {
        getString(R.string.label);
        if (TextUtils.isEmpty(mUsername)) {
            // If no username, then we ask the user to log in using an
            // appropriate service.
            final CharSequence msg = getText(R.string.login_activity_newaccount_text);
            return msg;
        }
        if (TextUtils.isEmpty(mPassword)) {
            // We have an account but no password
            return getText(R.string.login_activity_loginfail_text_pwmissing);
        }
        return null;
    }

    /**
     * Represents an asynchronous task used to authenticate a user against the
     * SampleSync Service
     */
    public class UserLoginTask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            // We do the actual work of authenticating the user
            // in the NetworkUtilities class.
            try {

                String sess = NetworkUtilities.authenticate(params[0], params[1], params[2]);
                try {
                    Document pers = Jsoup.connect("http://rait.placyms.com/personal.php").cookie("PHPSESSID", sess).get();
                    String crn = pers.select("input[name=crn]").first().attr("value");
                    String fname = pers.select("input[name=fn]").first().attr("value");
                    Log.d("Gothe", crn);

                    PrefUtils.setString(AuthenticatorActivity.this, "roll_no", crn);
                    PrefUtils.setString(AuthenticatorActivity.this, "fname", fname);

                    Document doc = Jsoup.connect("http://rait.placyms.com/home.php").cookie("PHPSESSID", sess).get();

                    NetworkUtilities.getFromServer(doc, AuthenticatorActivity.this);


                } catch (IOException e) {
                    e.printStackTrace();
                }
                return sess;
            } catch (Exception ex) {
                Log.e(TAG, "UserLoginTask.doInBackground: failed to authenticate");
                Log.i(TAG, ex.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String authToken) {
            // On a successful authentication, call back into the Activity to
            // communicate the authToken (or null for an error).
            onAuthenticationResult(authToken);
            progressDialog.dismiss();
            if (progressDialog != null) {
                progressDialog = null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(AuthenticatorActivity.this);
            progressDialog.setMessage("Authenticating");
            progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                   /* if (mAuthTask != null) {
                        mAuthTask.cancel(true);
                    }*/
                }
            });
            progressDialog.setIndeterminate(true);
            progressDialog.show();

        }

        @Override
        protected void onCancelled() {


            // If the action was canceled (by the user clicking the cancel
            // button in the progress dialog), then call back into the
            // activity to let it know.
            onAuthenticationCancel();
        }
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

    public class CheckInternetTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            isOnline = isOnline();
            return null;
        }
    }

    public class ConnectToRaitPlacyMs extends AsyncTask<Void, Void, Document> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Document response) {
            if (response == null)
                return;

            fullpage = response;
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                    R.layout.spinner_item, displayvalues);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            batch_spinner.setAdapter(adapter);
            super.onPostExecute(response);
            progressDialog.hide();
        }

        @Override
        protected Document doInBackground(Void... voids) {

            Document fr = null;
            try {

                fr = Jsoup.connect("http://rait.placyms.com/").get();
                fr.select("select[name=batch]").first();
                Elements selected = fr.select("option");

                for (Element element : selected) {
                    displayvalues.add(element.select("option").text());
                    passingvalues.add(element.select("option").attr("value"));
                }

            } catch (IOException e) {
                e.printStackTrace();

            }


            return fr;
        }
    }


}
