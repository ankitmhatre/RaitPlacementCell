package com.dragonide.raitplacementcell;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.dragonide.raitplacementcell.authenticator.AuthenticatorActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignupActivity extends AppCompatActivity {

    EditText fname, mname, lname, dob, mob, pass, addr, religion, caste, email, collegeroll;
    String fn, mn, ln, dobst, mobint, passt, adddrst, relstr, casr, emailtxt, crn, img_path;
    Button signUp;
    Spinner gender_spinner, dept_spinner;
    public Document global;
    ArrayList<String> genderText = new ArrayList<>();
    ArrayList<String> genderValues = new ArrayList<>();
    ArrayList<String> departmentText = new ArrayList<>();
    ArrayList<String> departmentValues = new ArrayList<>();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);

        dob = (EditText) findViewById(R.id.bdate);
        fname = (EditText) findViewById(R.id.fname);
        mname = (EditText) findViewById(R.id.mname);
        lname = (EditText) findViewById(R.id.lname);
        mob = (EditText) findViewById(R.id.mobileno);
        pass = (EditText) findViewById(R.id.passw);
        collegeroll = (EditText) findViewById(R.id.college_rno);
        signUp = (Button) findViewById(R.id.signUp_button_);

        addr = (EditText) findViewById(R.id.address);
        religion = (EditText) findViewById(R.id.religion);
        caste = (EditText) findViewById(R.id.caste);

        email = (EditText) findViewById(R.id.email_id);

        gender_spinner = (Spinner) findViewById(R.id.gender_spinner);
        dept_spinner = (Spinner) findViewById(R.id.department_spinner);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("douche", "updateclicked");

                if (validate()) {
                    new RegisterWithPost().execute(
                            email.getText().toString(),
                            pass.getText().toString(),
                            fname.getText().toString(),
                            lname.getText().toString(),
                            mname.getText().toString(),
                            dob.getText().toString(),
                            collegeroll.getText().toString(),
                            mob.getText().toString(),
                            addr.getText().toString(),
                            religion.getText().toString(),
                            caste.getText().toString(),
                            genderValues.get(gender_spinner.getSelectedItemPosition()),
                            departmentValues.get(dept_spinner.getSelectedItemPosition()),
                            "Register"
                    );
                    //execute the Async Post here
                }
            }
        });
        if (getIntent().getExtras() != null) {
            email.setText(getIntent().getExtras().getString("email"));
            pass.setText(getIntent().getExtras().getString("password"));
        }
        progressDialog = new ProgressDialog(this);

        progressDialog.setMessage("Creating");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }

    @Override
    protected void onStart() {
        new ConnectToPage().execute();
        super.onStart();
    }

    private boolean validate() {

        boolean good = true;
        if (fname.length() < 1) {
            fname.setError("Please fill your first name");
            good = false;
        }
        if (lname.length() < 1) {
            good = false;
            lname.setError("Please fill your last name");
        }
        if (dob.length() < 1) {


            dob.setError("Please fill the correct date");
            good = false;
        } else {
            String dobPattern = "^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$";
            Pattern pat = Pattern.compile(dobPattern);
            if (!pat.matcher(dob.getText().toString()).matches()) {
                good = false;
            }
        }
        if (mob.length() != 10) {
            good = false;
            mob.setError("Please fill your 10 digit Mobile no.");
        }
        if (collegeroll.length() != 8) {
            collegeroll.setError("Please fill your correct college roll no.");
            good = false;
        }
        if (email.length() < 1) {
            good = false;

            email.setError("Please fill your Email address");
        } else {
            if (!AuthenticatorActivity.isValidEmail(email.getText().toString()))
                good = false;
        }
        if (pass.length() < 4) {
            pass.setError("Please fill the password");
            good = false;
        }
        if (addr.length() < 1) {
            good = false;
            addr.setError("Please fill your address");
        }
        if (religion.length() < 1) {
            religion.setError("Please fill your religion or put a NA");
            good = false;
        }
        if (caste.length() < 1) {
            good = false;

            caste.setError("Please fill your caste or put NA");
        }
        if (gender_spinner.getSelectedItem().equals("- Select Gender -")) {
            Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show();
            good = false;
        }
        if (dept_spinner.getSelectedItem().equals("- Select Department -")) {
            Toast.makeText(this, "Please select a Department", Toast.LENGTH_SHORT).show();
            good = false;
        }
        Log.d("newaccresp", good + "");
        return good;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            finish();
        }
    }

    class ConnectToPage extends AsyncTask<Void, Void, Document> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Document document) {
            if (document == null)
                return;

            super.onPostExecute(document);
            global = document;
/*
This is for populating the gender spinner on runtime

--STARTS HERE--
 */
            Element genderSuperElement = document.select("select[name=gender]").first();
            Elements genderOptionElements = genderSuperElement.select("option");

            for (Element eachGenderOption : genderOptionElements) {
                Log.d("spinnerd", eachGenderOption.toString());
                genderText.add(eachGenderOption.text());
                if (eachGenderOption.attr("value").length() > 0) {
                    genderValues.add(eachGenderOption.attr("value"));
                } else {
                    genderValues.add("- Select Gender -");
                }

            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_spinner_item, genderText);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            gender_spinner.setAdapter(adapter);
            gender_spinner.setSelection(0);
            /*
            Gender population ends here
            --ENDS HERE--
             */




            /*
            Department Spinner work starts here
             */

            Element deptSuperElement = document.select("select[name=branch]").first();
            Log.d("spinnerdd", deptSuperElement.toString());
            Elements deptOptionElements = deptSuperElement.select("option");

            for (Element eachdeptOption : deptOptionElements) {

                departmentText.add(eachdeptOption.text());
                if (eachdeptOption.attr("value").length() > 0) {
                    departmentValues.add(eachdeptOption.attr("value"));
                } else {
                    departmentValues.add("- Select Department -");
                }

            }
            ArrayAdapter<String> deptAdapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_spinner_item, departmentText);
            deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dept_spinner.setAdapter(deptAdapter);
            dept_spinner.setSelection(0);
        }


        @Override
        protected Document doInBackground(Void... voids) {
            Document d = null;
            try {
                d = Jsoup.connect("http://rait.placyms.com/registration.php").get();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return d;
        }
    }

    class RegisterWithPost extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            progressDialog.show();


            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);

            if (aVoid)
                Toast.makeText(SignupActivity.this, "Created an Account successfully now login manually", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(SignupActivity.this, "Failed to create an account, Create from http://rait.placyms.com", Toast.LENGTH_LONG).show();
            progressDialog.hide();
            startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class), 111);
        }

        @Override
        protected Boolean doInBackground(String... string) {
            Document d = null;
            /*
*  email.getText().toString(),
                            pass.getText().toString(),
                            fname.getText().toString(),
                            lname.getText().toString(),
                            mname.getText().toString(),
                            dob.getText().toString(),
                            collegeroll.getText().toString(),
                            mob.getText().toString(),
                            addr.getText().toString(),
                            religion.getText().toString(),
                            caste.getText().toString(),
                             genderValues.get(gender_spinner.getSelectedItemPosition()),
                             departmentValues.get(dept_spinner.getSelectedItemPosition()),
                            "Register"*/
            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody varbody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("email", string[0])
                    .addFormDataPart("pass", string[1])
                    .addFormDataPart("fn", string[2])
                    .addFormDataPart("ln", string[3])
                    .addFormDataPart("mn", string[4])
                    .addFormDataPart("dob", string[5])
                    .addFormDataPart("crn", string[6])
                    .addFormDataPart("cno", string[7])
                    .addFormDataPart("address", string[8])
                    .addFormDataPart("religion", string[9])
                    .addFormDataPart("caste", string[10])
                    .addFormDataPart("gender", string[11])
                    .addFormDataPart("branch", string[12])
                    .addFormDataPart("submit", "Register")

                    .build();


            Request request = new Request.Builder()
                    .url("http://rait.placyms.com/registration.php")
                    .post(varbody)
                    .build();

            try {
                Response response = okHttpClient.newCall(request).execute();
                Log.d("newaccresp", response.body().string());
                d = Jsoup.parse(response.body().string());
                Log.d("newaccresp", response.headers().toString());
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Timeout, please try later", Toast.LENGTH_LONG).show();
                e.printStackTrace();

            }
            String html;

            if (d != null && d.select("h2").text().equals("Data registered")) {

                return true;

            } else {
                return false;
            }


        }
    }
}
