package com.dragonide.raitplacementcell;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Ankit on 1/22/2017.
 */

public class PersonalFragm extends Fragment {
    String DASHURL, COOKIE;
    int PICK_IMAGE_REQUEST = 243;
    String base4;
    FloatingActionButton floatingActionButton;
    EditText fname, mname, lname, dob, mob, pass, addr, religion, caste, email;
    String fn, mn, ln, dobst, mobint, passt, adddrst, relstr, casr, emailtxt, crn, img_path;
    Button upbtn, imageUpload;
    ProgressDialog progressDialog;
    Bitmap bitmap;
    boolean isImageUploaded = false;
    String imagePath;
    File Img;
    String finaleName;
    Uri gUri;
    ArrayList<String> genderDisplay = new ArrayList<>();
    ArrayList<String> genderValues = new ArrayList<>();
    ArrayList<String> departmentDisplay = new ArrayList<>();
    ArrayList<String> departmentValues = new ArrayList<>();
    Button update, reset;
    Spinner gender, department;
    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };


    public void saveclicked() {
        SaveDetails saveDetails = new SaveDetails();
        Log.d("douche", "inside save clicked");
        if (isImageUploaded) {
            saveDetails.execute(fname.getText().toString(),
                    mname.getText().toString(),
                    lname.getText().toString(),
                    dob.getText().toString(),
                    genderValues.get(gender.getSelectedItemPosition()),
                    departmentValues.get(department.getSelectedItemPosition()),
                    mob.getText().toString(),
                    pass.getText().toString(),
                    addr.getText().toString(),
                    religion.getText().toString(),
                    caste.getText().toString(),
                    crn, img_path

            );
        } else {
            saveDetails.execute(fname.getText().toString(),
                    mname.getText().toString(),
                    lname.getText().toString(),
                    dob.getText().toString(),
                    genderValues.get(gender.getSelectedItemPosition()),
                    departmentValues.get(department.getSelectedItemPosition()),
                    mob.getText().toString(),
                    pass.getText().toString(),
                    addr.getText().toString(),
                    religion.getText().toString(),
                    caste.getText().toString(),
                    crn

            );
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        RetrievePersonalData retrievePersonalData = new RetrievePersonalData();
        retrievePersonalData.execute();

    }

    public String getMimeExtension(String pathy) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(pathy);
        String g = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        Log.d("douche", pathy);
        return g;
    }


    class SaveDetails extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... string) {

            String mResponseBody = "off";
            PrefUtils.setString(getContext(), "fname", string[0]);


               /* java.net.CookieManager msCookieManager = new java.net.CookieManager();
                msCookieManager.getCookieStore().add(null, HttpCookie.parse(COOKIE).get(0));

                URL url = new URL("http://rait.placyms.com/personal.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                if (msCookieManager.getCookieStore().getCookies().size() > 0) {
                    // While joining the Cookies, use ',' or ';' as needed. Most of the servers are using ';'
                    httpURLConnection.setRequestProperty("Cookie",
                            TextUtils.join(";", msCookieManager.getCookieStore().getCookies()));
                }

                httpURLConnection.setRequestProperty("PHPSESSID", COOKIE);
                httpURLConnection.setDoInput(true);
               *//* httpURLConnection.setRequestProperty("Content-Type",
                        "multipart/form-data;");
               *//*
                httpURLConnection.setDoOutput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
*/
            String data;

            if (isImageUploaded) {
                Log.d("douche", "insideImageUpload");
                /*    String fPath = Img.getAbsolutePath();
                    String contentType = getMimeExtension(Img.getPath());*/
                File file = new File(getPath(gUri));

                String hu = getPath(gUri);
                Log.d("douche", hu);
                finaleName = hu.substring(hu.lastIndexOf("/") + 1);

                Log.d("douche", finaleName);
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody fileBody = RequestBody.create(MediaType.parse(finaleName), file);
                RequestBody varbody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("fn", string[0])
                        .addFormDataPart("mn", string[1])
                        .addFormDataPart("ln", string[2])
                        .addFormDataPart("dob", string[3])
                        .addFormDataPart("gender", string[4])
                        .addFormDataPart("branch", string[5])
                        .addFormDataPart("cno", string[6])
                        .addFormDataPart("pass", string[7])
                        .addFormDataPart("address", string[8])
                        .addFormDataPart("religion", string[9])
                        .addFormDataPart("caste", string[10])
                        .addFormDataPart("crn", string[11])
                        .addFormDataPart("submit", "Update Personal Info")
                        .addFormDataPart("photo", finaleName, fileBody)
                        .build();


                Request request = new Request.Builder()
                        .url("http://rait.placyms.com/personal.php")
                        .post(varbody)
                        .addHeader("Cookie", "PHPSESSID=" + getArguments().getString("thecookie"))
                        .build();

                try {
                    Response response = okHttpClient.newCall(request).execute();
                    Log.d("responseexec", response.body().string());
                } catch (IOException e) {
                    Toast.makeText(getActivity(), "Timeout, please try later", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();

                }

                 /*   data = URLEncoder.encode("fn", "UTF-8") + "=" + URLEncoder.encode(string[0], "UTF-8") + "&"
                            + URLEncoder.encode("mn", "UTF-8") + "=" + URLEncoder.encode(string[1], "UTF-8") + "&"
                            + URLEncoder.encode("ln", "UTF-8") + "=" + URLEncoder.encode(string[2], "UTF-8") + "&"
                            + URLEncoder.encode("dob", "UTF-8") + "=" + URLEncoder.encode(string[3], "UTF-8") + "&"
                            + URLEncoder.encode("gender", "UTF-8") + "=" + URLEncoder.encode(string[4], "UTF-8") + "&"
                            + URLEncoder.encode("branch", "UTF-8") + "=" + URLEncoder.encode(string[5], "UTF-8") + "&"
                            + URLEncoder.encode("cno", "UTF-8") + "=" + URLEncoder.encode(string[6], "UTF-8") + "&"
                            + URLEncoder.encode("pass", "UTF-8") + "=" + URLEncoder.encode(string[7], "UTF-8") + "&"
                            + URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(string[8], "UTF-8") + "&"
                            + URLEncoder.encode("photo", "UTF-8") + "=" + URLEncoder.encode(string[12], "UTF-8") + "&"
                            + URLEncoder.encode("religion", "UTF-8") + "=" + URLEncoder.encode(string[9], "UTF-8") + "&"
                            + URLEncoder.encode("caste", "UTF-8") + "=" + URLEncoder.encode(string[10], "UTF-8") + "&"
                            + URLEncoder.encode("crn", "UTF-8") + "=" + URLEncoder.encode(string[11], "UTF-8") + "&"
                            + URLEncoder.encode("submit", "UTF-8") + "=" + URLEncoder.encode("Update Personal Info", "UTF-8");//+ "&"*/
                //+ URLEncoder.encode("photo", "UTF-8") + "=" + URLEncoder.encode(base4,"UTF-8" );
            } else {

                OkHttpClient okHttpClient = new OkHttpClient();

                RequestBody varbody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("fn", string[0])
                        .addFormDataPart("mn", string[1])
                        .addFormDataPart("ln", string[2])
                        .addFormDataPart("dob", string[3])
                        .addFormDataPart("gender", string[4])
                        .addFormDataPart("branch", string[5])
                        .addFormDataPart("cno", string[6])
                        .addFormDataPart("pass", string[7])
                        .addFormDataPart("address", string[8])
                        .addFormDataPart("religion", string[9])
                        .addFormDataPart("caste", string[10])
                        .addFormDataPart("crn", string[11])
                        .addFormDataPart("submit", "Update Personal Info")

                        .build();
                Request request = new Request.Builder()
                        .url("http://rait.placyms.com/personal.php")
                        .post(varbody)
                        .addHeader("Cookie", "PHPSESSID=" + getArguments().getString("thecookie"))
                        .build();

                try {
                    Response response = okHttpClient.newCall(request).execute();
                    mResponseBody = response.body().string();
                    Log.d("responseexec", mResponseBody);
                } catch (IOException e) {
                    e.printStackTrace();

                }

                  /*  data = URLEncoder.encode("fn", "UTF-8") + "=" + URLEncoder.encode(string[0], "UTF-8") + "&"
                            + URLEncoder.encode("mn", "UTF-8") + "=" + URLEncoder.encode(string[1], "UTF-8") + "&"
                            + URLEncoder.encode("ln", "UTF-8") + "=" + URLEncoder.encode(string[2], "UTF-8") + "&"
                            + URLEncoder.encode("dob", "UTF-8") + "=" + URLEncoder.encode(string[3], "UTF-8") + "&"
                            + URLEncoder.encode("gender", "UTF-8") + "=" + URLEncoder.encode(string[4], "UTF-8") + "&"
                            + URLEncoder.encode("branch", "UTF-8") + "=" + URLEncoder.encode(string[5], "UTF-8") + "&"
                            + URLEncoder.encode("cno", "UTF-8") + "=" + URLEncoder.encode(string[6], "UTF-8") + "&"
                            + URLEncoder.encode("pass", "UTF-8") + "=" + URLEncoder.encode(string[7], "UTF-8") + "&"
                            + URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(string[8], "UTF-8") + "&"

                            + URLEncoder.encode("religion", "UTF-8") + "=" + URLEncoder.encode(string[9], "UTF-8") + "&"
                            + URLEncoder.encode("caste", "UTF-8") + "=" + URLEncoder.encode(string[10], "UTF-8") + "&"
                            + URLEncoder.encode("crn", "UTF-8") + "=" + URLEncoder.encode(string[11], "UTF-8") + "&"
                            + URLEncoder.encode("submit", "UTF-8") + "=" + URLEncoder.encode("Update Personal Info", "UTF-8");//+ "&"*/
                //+ URLEncoder.encode("photo", "UTF-8") + "=" + URLEncoder.encode(base4,"UTF-8" );
                //bufferedWriter.write(data);
            }


              /*  bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String resultt = "";
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    resultt = resultt + "\n" + line;
                }
                mResponseBody = resultt;
                Log.d("respppp", mResponseBody);
*/

            return mResponseBody;
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                Document d = Jsoup.parse(s);
                Toast.makeText(getContext(), d.select("h3").first().text(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();

            }


            super.onPostExecute(s);
            progressDialog.hide();
            Log.d("douche", "onpostExcexute");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Saving");
            progressDialog.show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.personal_edit_layout, container, false);

        dob = (EditText) v.findViewById(R.id.bdate);
        fname = (EditText) v.findViewById(R.id.fname);
        mname = (EditText) v.findViewById(R.id.mname);
        lname = (EditText) v.findViewById(R.id.lname);
        mob = (EditText) v.findViewById(R.id.mobileno);
        pass = (EditText) v.findViewById(R.id.passw);
        update = (Button) v.findViewById(R.id.updateButton);
        reset = (Button) v.findViewById(R.id.resetutton);
        addr = (EditText) v.findViewById(R.id.address);
        religion = (EditText) v.findViewById(R.id.religion);
        caste = (EditText) v.findViewById(R.id.caste);
        imageUpload = (Button) v.findViewById(R.id.imageUpload);
        email = (EditText) v.findViewById(R.id.email_id);

        gender = (Spinner) v.findViewById(R.id.gender_spinner);
        department = (Spinner) v.findViewById(R.id.department_spinner);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("douche", "updateclicked");
                saveclicked();
            }
        });
       /* upbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });*/
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED) {

                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/jpg");
                    startActivityForResult(photoPickerIntent, 1);
                } else {
                    requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 98);
                }
            }
        });
        progressDialog = new ProgressDialog(getActivity());

        progressDialog.setMessage("Fetching");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);


        return v;
    }

    private final int GALLERY_ACTIVITY_CODE = 200;
    private final int RESULT_CROP = 400;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 98:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/jpg");
                startActivityForResult(photoPickerIntent, 1);
                break;
        }
    }

    private boolean performCrop(String picUri) {
        boolean yeah = false;
        try {
            //Start Crop Activity(


            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            File f = new File(picUri);
            Uri contentUri = Uri.fromFile(f);

            cropIntent.setDataAndType(contentUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
          /*  cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
           */ // indicate output X and Y
            cropIntent.putExtra("outputX", 250);
            cropIntent.putExtra("outputY", 400);

            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, RESULT_CROP);
            yeah = true;
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
            yeah = false;
        }
        return yeah;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == AppCompatActivity.RESULT_OK) {
                Uri selectedImage = data.getData();

                gUri = selectedImage;
                String gh = getPath(gUri);
                String nhmam = gh.substring(gh.lastIndexOf("/") + 1);
                imageUpload.setText(nhmam);
                String filePath = getPath(selectedImage);
                String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);
                //image_name_tv.setText(filePath);

                try {
                    if (file_extn.equals("jpg") || file_extn.equals("jpeg")) {
                        //FINE
                        boolean isSuccesfulCrop = performCrop(filePath);
                        if (isSuccesfulCrop) {

                            imagePath = filePath;
                            //  imageUpload.setText(filePath.lastIndexOf("/") + 1);
                            isImageUploaded = true;

                            Img = new File(selectedImage.toString());
                            Log.d("isSuccessful", "true");


                        } else {
                            isImageUploaded = false;
                        }


                    } else {
                        //NOT IN REQUIRED FORMAT
                        Toast.makeText(getContext(), "Not Supported Format", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = null;
        int column_index = 0;
        try {
            cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
            column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.moveToFirst();
        imagePath = cursor.getString(column_index);

        return cursor.getString(column_index);
    }

    class RetrievePersonalData extends AsyncTask<String, Void, Document> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPostExecute(Document document) {
            super.onPostExecute(document);
            if (document == null) {
                return;
            }
            String kalu = null;
            for (Element e : document.getAllElements()) {
                for (Node n : e.childNodes()) {

                    if (n instanceof Comment) {

                        kalu = ((Comment) n).getData();
                    }

                }


            }
            Document d = Jsoup.parse(kalu);

            try {
                fname.setText(document.select("input[name=fn]").first().attr("value"));
                mname.setText(document.select("input[name=mn]").first().attr("value"));
                lname.setText(document.select("input[name=ln]").first().attr("value"));
                dob.setText(document.select("input[name=dob]").first().attr("value"));
                mob.setText(document.select("input[name=cno]").first().attr("value"));
                pass.setText(document.select("input[name=pass]").first().attr("value"));
                addr.setText(document.select("textarea[name=address]").first().text());
                religion.setText(document.select("input[name=religion]").first().attr("value"));
                caste.setText(document.select("input[name=caste]").first().attr("value"));
                email.setText(d.select("input[name=email]").first().attr("value"));
                crn = document.select("input[name=crn]").first().attr("value");
                Log.d("multpart", crn);

                spinnerLogic(document, "gender", genderDisplay, genderValues, gender);
                spinnerLogic(document, "branch", departmentDisplay, departmentValues, department);

               /* Element ty = document.select("select").get(1);
                Elements qw = ty.select("option");
                int e = 0;
                for (Element qqq : qw) {

                    if (qqq.select("option[selected]").attr("value").length() > 2) {
                        Log.d("optiontag", "" + e);
                        department.setSelection(e);
                    }
                    e++;


                }*/


            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Failed to retrieve", Toast.LENGTH_SHORT).show();
            } finally {
                progressDialog.hide();
            }
        }

        private void spinnerLogic(Document document, String whichName, ArrayList<String> displaylist, ArrayList<String> valueList, Spinner sp) {

            Element superElement = document.select("select[name=" + whichName + "]").first();
            Elements optionElements = superElement.select("option");
            int selectionCounter = 0;
            int selectionPosition = 0;
            displaylist.clear();
            valueList.clear();
            for (Element element : optionElements) {

                if (element.select("option[selected]").attr("value").length() > 0) {


                    //  Log.d("persotest", "selected =  " + element.select("option[selected]").attr("value"));
//This is selected items
                    displaylist.add(element.select("option[selected]").text());
                    if (element.select("option[selected]").attr("value").length() > 0) {
                        valueList.add(element.select("option[selected]").attr("value"));
                    } else {
                        valueList.add(element.select("option[selected]").text());
                    }
                    selectionPosition = selectionCounter;
                } else if (element.select("option").attr("value").length() > 0) {
                    //Normal options with values

                    //    Log.d("persotest", element.select("option").attr("value"));
                    displaylist.add(element.select("option").text());
                    valueList.add(element.select("option").attr("value"));
                } else {
                    //Normal options without values
                    //Log.d("persotest", element.select("option").text());
//No Value, i.e. the default options
                    displaylist.add(element.select("option").text());
                    valueList.add(element.select("option").text());
                }
                selectionCounter++;

            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_spinner_item, displaylist);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp.setAdapter(adapter);
            sp.setSelection(selectionPosition);


        }

        @Override
        protected Document doInBackground(String... strings) {
            Document document = null;
            try {
                document = Jsoup.connect(DASHURL).cookie("PHPSESSID", getArguments().getString("thecookie")).get();


            } catch (Exception e) {
                e.printStackTrace();

            }
            return document;
        }
    }

    private void updateLabel() {

        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dob.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DASHURL = "http://rait.placyms.com/personal.php";// getArguments().getString("theurl");
        COOKIE = getArguments().getString("thecookie");
    }


    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/jpeg");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();

        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }


}
