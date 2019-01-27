package com.dragonide.raitplacementcell;


import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Ankit on 6/8/2017.
 */


public class AcademicFrag extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    Spinner beunivspinner, becollegespinner, besem1applicablespinner, besem2applicablespinner, meapplicablespinner;
    EditText otheruniv, othercolg;
    Spinner xBoard;
    EditText xboardother, xmonth, xyear, xmo, xto;
    TextView xdownload;
    Button uploadxmarksheet;

    Spinner xiiBoard;
    EditText xiiboardother, xiimonth, xiiyear, xiimo, xiito;
    TextView xiidownload;
    Button uploadxiimarksheet;

    Spinner dBoard;
    EditText dboardother, dmonth, dyear, dmo, dto;
    TextView ddownload;

    String ROLL_NO;
    boolean executiondone = false;
    String sessId;
    Button uploaddmarksheet;

    RequestBody varbody;

    ProgressDialog progressDialog;
    RetrieveAcademicDetails retriveDetails;
    String xmarksheetUrl, xiimarksheetUrl, dmarksheetUrl, be1marksheetUrl, be2marksheetUrl, be3marksheetUrl,
            be4marksheetUrl, be5marksheetUrl, be6marksheetUrl, be7marksheetUrl, be8marksheetUrl, me1marksheetUrl, me2marksheetUrl, me3marksheetUrl, me4marksheetUrl;
    int xboardpos, xiiboardpo, dboardpo, univpos, colgpos, besem1pos, besem2pos, mepos;
    TextView b1, b2, b3, b4, b5, b6, b7, b8, m1, m2, m3, m4;
    Button buttonb1, buttonb2, buttonb3, buttonb4, buttonb5, buttonb6, buttonb7, buttonb8, buttonm1, buttonm2, buttonm3, buttonm4;
    EditText b1cm, b1cy, b1mo, b1tm, b1cgpa;
    EditText b2cm, b2cy, b2mo, b2tm, b2cgpa;
    EditText b3cm, b3cy, b3mo, b3tm, b3cgpa;
    EditText b4cm, b4cy, b4mo, b4tm, b4cgpa;
    EditText b5cm, b5cy, b5mo, b5tm, b5cgpa;
    EditText b6cm, b6cy, b6mo, b6tm, b6cgpa;
    EditText b7cm, b7cy, b7mo, b7tm, b7cgpa;
    EditText b8cm, b8cy, b8mo, b8tm, b8cgpa;
    EditText m1cm, m1cy, m1mo, m1tm, m1cgpa;
    EditText m2cm, m2cy, m2mo, m2tm, m2cgpa;
    EditText m3cm, m3cy, m3mo, m3tm, m3cgpa;
    EditText m4cm, m4cy, m4mo, m4tm, m4cgpa;
    EditText lkt, dkt;
    int STD10 = 647, STD12 = 648, DIPLOMA = 649, BE1 = 650, BE2 = 651, BE3 = 652, BE4 = 653, BE5 = 654, BE6 = 655, BE7 = 656, BE8 = 657, ME1 = 658, ME2 = 659, ME3 = 660, ME4 = 661;
    Context c;
    View globalView;
    boolean std10up = false, std12up = false, diplomaup = false, b1up = false, b2up = false, b3up = false, b4up = false, b5up = false, b6up = false,
            b7up = false, b8up = false, m1up = false, m2up = false, m3up = false, m4up = false;


    String std10path, std12path, diplomapath, b1path, b2path, b3path, b4path, b5path, b6path, b7path, b8path, m1path, m2path, m3path, m4path;
    Intent std10uri, std12uri, diplomauri, b1uri, b2uri, b3uri, b4uri, b5uri, b6uri, b7uri, b8uri, m1uri, m2uri, m3uri, m4uri;

    Uri gUri;

    Button resetuttonacads, updateButtonaacads;

    ArrayList<String> xboardDisplay = new ArrayList<>();
    ArrayList<String> xBoardValues = new ArrayList<>();


    ArrayList<String> xiiboardDisplay = new ArrayList<>();
    ArrayList<String> xiiBoardValues = new ArrayList<>();


    ArrayList<String> dboardDisplay = new ArrayList<>();
    ArrayList<String> dBoardValues = new ArrayList<>();


    ArrayList<String> univDisplay = new ArrayList<>();
    ArrayList<String> univValues = new ArrayList<>();

    ArrayList<String> eboardDisplay = new ArrayList<>();
    ArrayList<String> eboardValues = new ArrayList<>();

    ArrayList<String> iAppDisplay = new ArrayList<>();
    ArrayList<String> iAppValues = new ArrayList<>();

    ArrayList<String> iiAppDisplay = new ArrayList<>();
    ArrayList<String> iiAppValues = new ArrayList<>();

    ArrayList<String> mAppDisplay = new ArrayList<>();
    ArrayList<String> mAppValues = new ArrayList<>();


    @Override
    public void onStart() {
        super.onStart();
        xmarksheetUrl = "http://rait.placyms.com/documents/" + ROLL_NO + "-xupload.pdf";
        xiimarksheetUrl = "http://rait.placyms.com/documents/" + ROLL_NO + "-xiiupload.pdf";
        dmarksheetUrl = "http://rait.placyms.com/documents/" + ROLL_NO + "-dupload.pdf";
        be1marksheetUrl = "http://rait.placyms.com/documents/" + ROLL_NO + "-iupload.pdf";
        be2marksheetUrl = "http://rait.placyms.com/documents/" + ROLL_NO + "-iiupload.pdf";
        be3marksheetUrl = "http://rait.placyms.com/documents/" + ROLL_NO + "-iiiupload.pdf";
        be4marksheetUrl = "http://rait.placyms.com/documents/" + ROLL_NO + "-ivupload.pdf";
        be5marksheetUrl = "http://rait.placyms.com/documents/" + ROLL_NO + "-vupload.pdf";
        be6marksheetUrl = "http://rait.placyms.com/documents/" + ROLL_NO + "-viupload.pdf";
        be7marksheetUrl = "http://rait.placyms.com/documents/" + ROLL_NO + "-viiupload.pdf";
        be8marksheetUrl = "http://rait.placyms.com/documents/" + ROLL_NO + "-viiiupload.pdf";
        me1marksheetUrl = "http://rait.placyms.com/documents/" + ROLL_NO + "-imupload.pdf";
        me2marksheetUrl = "http://rait.placyms.com/documents/" + ROLL_NO + "-iimupload.pdf";
        me3marksheetUrl = "http://rait.placyms.com/documents/" + ROLL_NO + "-iiimupload.pdf";
        me4marksheetUrl = "http://rait.placyms.com/documents/" + ROLL_NO + "-ivmupload.pdf";


        settingUpIds(globalView);


        Log.d("timecomplexity", "onStart");
        if (!executiondone) {
            retriveDetails = new RetrieveAcademicDetails();
            retriveDetails.execute(sessId);
        } else {
            retriveDetails = null;
            retriveDetails = new RetrieveAcademicDetails();
            retriveDetails.execute(sessId);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.xboardspinner:

                xboardpos = position;
                if (position == 5) {
                    xboardother.setEnabled(true);
                    xboardother.setVisibility(View.VISIBLE);
                } else {
                    xboardother.setVisibility(View.GONE);
                    xboardother.setEnabled(false);
                }

                break;
            case R.id.xiiboardspinner:
                xiiboardpo = position;
                if (position == 5) {
                    xiiboardother.setEnabled(true);
                    xiiboardother.setVisibility(View.VISIBLE);
                } else {
                    xiiboardother.setVisibility(View.GONE);
                }
                break;
            case R.id.dboardspinner:
                dboardpo = position;
                if (position == 3) {
                    dboardother.setEnabled(true);
                    dboardother.setVisibility(View.VISIBLE);
                } else {
                    dboardother.setVisibility(View.GONE);
                }
                break;
            case R.id.beunivspinner:
                univpos = position;
                if (position == 1) {
                    otheruniv.setEnabled(true);
                    otheruniv.setVisibility(View.VISIBLE);
                } else if (position == 2) {
                    univpos = 1;
                    otheruniv.setEnabled(true);
                    otheruniv.setVisibility(View.VISIBLE);

                } else {
                    otheruniv.setVisibility(View.GONE);
                }
                break;
            case R.id.becollegeSpinner:
                colgpos = position;
                if (position == 1) {
                    othercolg.setEnabled(true);
                    othercolg.setVisibility(View.VISIBLE);
                } else {
                    othercolg.setVisibility(View.GONE);
                }
                break;
            case R.id.besem1applicable:
                besem1pos = position;

                break;
            case R.id.besem2applicable:
                besem2pos = position;

                break;
            case R.id.meapplicable:
                mepos = position;

                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * upload from button handled here
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.downloadxboardmarksheet:
                downloadFile(xmarksheetUrl);
                break;
            case R.id.downloadxiiboardmarksheet:
                downloadFile(xiimarksheetUrl);
                break;
            case R.id.downloaddboardmarksheet:
                downloadFile(dmarksheetUrl);
                break;
            case R.id.downloadbe1marksheet:
                downloadFile(be1marksheetUrl);
                break;
            case R.id.downloadbe2marksheet:
                downloadFile(be2marksheetUrl);
                break;
            case R.id.downloadbe3marksheet:
                downloadFile(be3marksheetUrl);
                break;
            case R.id.downloadbe4marksheet:
                downloadFile(be4marksheetUrl);
                break;
            case R.id.downloadbe5marksheet:
                downloadFile(be5marksheetUrl);
                break;
            case R.id.downloadbe6marksheet:
                downloadFile(be6marksheetUrl);
                break;
            case R.id.downloadbe7marksheet:
                downloadFile(be7marksheetUrl);
                break;
            case R.id.downloadbe8marksheet:
                downloadFile(be8marksheetUrl);
                break;
            case R.id.downloadme1marksheet:
                downloadFile(me1marksheetUrl);
                break;
            case R.id.downloadme2marksheet:
                downloadFile(me2marksheetUrl);
                break;
            case R.id.downloadme3marksheet:
                downloadFile(me3marksheetUrl);
                break;
            case R.id.downloadme4marksheet:
                downloadFile(me4marksheetUrl);
                break;
            case R.id.xmarksheetButton:
                uploadFile(STD10);
                break;
            case R.id.xiimarksheetButton:
                uploadFile(STD12);
                break;
            case R.id.dmarksheetButton:
                uploadFile(DIPLOMA);
                break;
            case R.id.be1marksheetButton:
                uploadFile(BE1);
                break;
            case R.id.be2marksheetButton:
                uploadFile(BE2);
                break;
            case R.id.be3marksheetButton:
                uploadFile(BE3);
                break;
            case R.id.be4marksheetButton:
                uploadFile(BE4);
                break;
            case R.id.be5marksheetButton:
                uploadFile(BE5);
                break;
            case R.id.be6marksheetButton:
                uploadFile(BE6);
                break;
            case R.id.be7marksheetButton:
                uploadFile(BE7);
                break;
            case R.id.be8marksheetButton:
                uploadFile(BE8);
                break;
            case R.id.me1marksheetButton:
                uploadFile(ME1);
                break;
            case R.id.me2marksheetButton:
                uploadFile(ME2);
                break;
            case R.id.me3marksheetButton:
                uploadFile(ME3);
                break;
            case R.id.me4marksheetButton:
                uploadFile(ME4);
                break;
            case R.id.updateButtonaacads:


                String beuniv = univValues.get(beunivspinner.getSelectedItemPosition());
                String becolg = eboardValues.get(besem1applicablespinner.getSelectedItemPosition());
                String besem1 = iAppValues.get(besem1applicablespinner.getSelectedItemPosition());
                String besem2 = iiAppValues.get(besem2applicablespinner.getSelectedItemPosition());
                String meapp = mAppValues.get(meapplicablespinner.getSelectedItemPosition());
                String xboa = xBoardValues.get(xBoard.getSelectedItemPosition());
                String xiiboa = xiiBoardValues.get(xiiBoard.getSelectedItemPosition());

                String dboa = dBoardValues.get(dBoard.getSelectedItemPosition());
                Log.d("allspinneers", beuniv + "\n"
                        + becolg + "\n"
                        + besem1 + "\n"
                        + besem2 + "\n"
                        + meapp + "\n"
                        + xboa + "\n"
                        + xiiboa + "\n"
                        + dboa);


                UpdateAcademics updateAcademics = new UpdateAcademics();
                updateAcademics.execute(xboa, xboardother.getText().toString(), xmonth.getText().toString(), xyear.getText().toString(), xmo.getText().toString(), xto.getText().toString(),
                        xiiboa, xiiboardother.getText().toString(), xiimonth.getText().toString(), xiiyear.getText().toString(), xiimo.getText().toString(), xiito.getText().toString(),
                        dboa, dboardother.getText().toString(), dmonth.getText().toString(), dyear.getText().toString(), dmo.getText().toString(), dto.getText().toString(),
                        beuniv, otheruniv.getText().toString(), becolg, othercolg.getText().toString(), besem1, besem2, meapp,
                        b1cm.getText().toString(), b1cy.getText().toString(), b1mo.getText().toString(), b1tm.getText().toString(), b1cgpa.getText().toString(),
                        b2cm.getText().toString(), b2cy.getText().toString(), b2mo.getText().toString(), b2tm.getText().toString(), b2cgpa.getText().toString(),
                        b3cm.getText().toString(), b3cy.getText().toString(), b3mo.getText().toString(), b3tm.getText().toString(), b3cgpa.getText().toString(),
                        b4cm.getText().toString(), b4cy.getText().toString(), b4mo.getText().toString(), b4tm.getText().toString(), b4cgpa.getText().toString(),
                        b5cm.getText().toString(), b5cy.getText().toString(), b5mo.getText().toString(), b5tm.getText().toString(), b5cgpa.getText().toString(),
                        b6cm.getText().toString(), b6cy.getText().toString(), b6mo.getText().toString(), b6tm.getText().toString(), b6cgpa.getText().toString(),
                        b7cm.getText().toString(), b7cy.getText().toString(), b7mo.getText().toString(), b7tm.getText().toString(), b7cgpa.getText().toString(),
                        b8cm.getText().toString(), b8cy.getText().toString(), b8mo.getText().toString(), b8tm.getText().toString(), b8cgpa.getText().toString(),
                        m1cm.getText().toString(), m1cy.getText().toString(), m1mo.getText().toString(), m1tm.getText().toString(), m1cgpa.getText().toString(),
                        m2cm.getText().toString(), m2cy.getText().toString(), m2mo.getText().toString(), m2tm.getText().toString(), m2cgpa.getText().toString(),
                        m3cm.getText().toString(), m3cy.getText().toString(), m3mo.getText().toString(), m3tm.getText().toString(), m3cgpa.getText().toString(),
                        m4cm.getText().toString(), m4cy.getText().toString(), m4mo.getText().toString(), m4tm.getText().toString(), m4cgpa.getText().toString(),
                        lkt.getText().toString(), dkt.getText().toString());

                break;
            case R.id.resetuttonacads:
                break;

        }
    }

    public void uploadFile(int whom) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select the Marksheet"),
                    whom);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "There is no application to handle the request", Toast.LENGTH_LONG).show();
            // Potentially direct the user to the Market with a Dialog

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppCompatActivity.RESULT_OK) {
            switch (requestCode) {
                case 647:
                    std10uri = data;
                    std10path = resolveIAmPassingData(data, 647);
                    std10up = true;
                    break;
                case 648:
                    std12uri = data;
                    std12path = resolveIAmPassingData(data, 648);
                    std12up = true;
                    break;
                case 649:
                    diplomauri = data;
                    diplomapath = resolveIAmPassingData(data, 649);
                    diplomaup = true;
                    break;
                case 650:
                    b1uri = data;
                    b1path = resolveIAmPassingData(data, 650);
                    b1up = true;
                    break;
                case 651:
                    b2uri = data;
                    b2path = resolveIAmPassingData(data, 651);
                    b2up = true;
                    break;
                case 652:
                    b3uri = data;
                    b3path = resolveIAmPassingData(data, 652);
                    b3up = true;
                    break;
                case 653:
                    b4uri = data;
                    b4path = resolveIAmPassingData(data, 653);
                    b4up = true;
                    break;
                case 654:
                    b5uri = data;
                    b5path = resolveIAmPassingData(data, 654);
                    b5up = true;
                    break;
                case 655:
                    b6uri = data;
                    b6path = resolveIAmPassingData(data, 655);
                    b6up = true;
                    break;
                case 656:
                    b7uri = data;
                    b7path = resolveIAmPassingData(data, 656);
                    b7up = true;
                    break;
                case 657:
                    b8uri = data;
                    b8path = resolveIAmPassingData(data, 657);
                    b8up = true;
                    break;
                case 658:
                    m1uri = data;
                    m1path = resolveIAmPassingData(data, 658);
                    m1up = true;
                    break;
                case 659:
                    m2uri = data;
                    m2path = resolveIAmPassingData(data, 659);
                    m2up = true;
                    break;
                case 660:
                    m3uri = data;
                    m3path = resolveIAmPassingData(data, 660);
                    m3up = true;
                    break;
                case 661:
                    m4uri = data;
                    m4path = resolveIAmPassingData(data, 661);
                    m4up = true;
                    break;
            }
        }
    }

    public String resolveIAmPassingData(Intent data, int whom) {
        String fpath = data.getData().getPath();
        Uri s = data.getData();
        try {
            fpath = PathUtil.getPath(getActivity(), s);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        gUri = s;
        //File f = new File(s.toString());


        return fpath;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d("timecomplexity", "oncreateViewstart");
        ROLL_NO = PrefUtils.getString(getActivity(), "roll_no", "").toUpperCase();
        sessId = Utilities.getSESSID(); //PrefUtils.getString(getActivity(), "php_session_id", "");
        View v = inflater.inflate(R.layout.academic_edit_layout, container, false);


        Log.d("timecomplexity", "oncreateViewEnd");
        this.globalView = v;
        return v;
    }

    public void settingUpIds(View v) {
        xBoard = (Spinner) v.findViewById(R.id.xboardspinner);
        xBoard.setOnItemSelectedListener(this);
        b1 = (TextView) v.findViewById(R.id.downloadbe1marksheet);
        b2 = (TextView) v.findViewById(R.id.downloadbe2marksheet);
        b3 = (TextView) v.findViewById(R.id.downloadbe3marksheet);
        b4 = (TextView) v.findViewById(R.id.downloadbe4marksheet);
        b5 = (TextView) v.findViewById(R.id.downloadbe5marksheet);
        b6 = (TextView) v.findViewById(R.id.downloadbe6marksheet);
        b7 = (TextView) v.findViewById(R.id.downloadbe7marksheet);
        b8 = (TextView) v.findViewById(R.id.downloadbe8marksheet);
        m1 = (TextView) v.findViewById(R.id.downloadme1marksheet);
        m2 = (TextView) v.findViewById(R.id.downloadme2marksheet);
        m3 = (TextView) v.findViewById(R.id.downloadme3marksheet);
        m4 = (TextView) v.findViewById(R.id.downloadme4marksheet);

        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        b4.setOnClickListener(this);
        b5.setOnClickListener(this);
        b6.setOnClickListener(this);
        b7.setOnClickListener(this);
        b8.setOnClickListener(this);
        m1.setOnClickListener(this);
        m2.setOnClickListener(this);
        m3.setOnClickListener(this);
        m4.setOnClickListener(this);


        buttonb1 = (Button) v.findViewById(R.id.be1marksheetButton);
        buttonb2 = (Button) v.findViewById(R.id.be2marksheetButton);
        buttonb3 = (Button) v.findViewById(R.id.be3marksheetButton);
        buttonb4 = (Button) v.findViewById(R.id.be4marksheetButton);
        buttonb5 = (Button) v.findViewById(R.id.be5marksheetButton);
        buttonb6 = (Button) v.findViewById(R.id.be6marksheetButton);
        buttonb7 = (Button) v.findViewById(R.id.be7marksheetButton);
        buttonb8 = (Button) v.findViewById(R.id.be8marksheetButton);
        buttonm1 = (Button) v.findViewById(R.id.me1marksheetButton);
        buttonm2 = (Button) v.findViewById(R.id.me2marksheetButton);
        buttonm3 = (Button) v.findViewById(R.id.me3marksheetButton);
        buttonm4 = (Button) v.findViewById(R.id.me4marksheetButton);
        buttonb1.setOnClickListener(this);
        buttonb2.setOnClickListener(this);
        buttonb3.setOnClickListener(this);
        buttonb4.setOnClickListener(this);
        buttonb5.setOnClickListener(this);
        buttonb6.setOnClickListener(this);
        buttonb7.setOnClickListener(this);
        buttonb7.setOnClickListener(this);
        buttonm1.setOnClickListener(this);
        buttonm2.setOnClickListener(this);
        buttonm3.setOnClickListener(this);
        buttonm4.setOnClickListener(this);


        xboardother = (EditText) v.findViewById(R.id.otherboardetinxboard);
        xmonth = (EditText) v.findViewById(R.id.completionMonthxboard);
        xyear = (EditText) v.findViewById(R.id.completionYearxboard);
        xdownload = (TextView) v.findViewById(R.id.downloadxboardmarksheet);
        xdownload.setOnClickListener(this);
        xmo = (EditText) v.findViewById(R.id.marksObtainedxboard);
        xto = (EditText) v.findViewById(R.id.totalMarksxBoard);
        uploadxmarksheet = (Button) v.findViewById(R.id.xmarksheetButton);
        uploadxmarksheet.setOnClickListener(this);

        xiiBoard = (Spinner) v.findViewById(R.id.xiiboardspinner);
        xiiBoard.setOnItemSelectedListener(this);
        xiiboardother = (EditText) v.findViewById(R.id.otherboardetinxiiboard);
        xiimonth = (EditText) v.findViewById(R.id.completionMonthxiiboard);
        xiiyear = (EditText) v.findViewById(R.id.completionYearxiiboard);
        xiidownload = (TextView) v.findViewById(R.id.downloadxiiboardmarksheet);
        xiidownload.setOnClickListener(this);
        xiimo = (EditText) v.findViewById(R.id.marksObtainedxiiboard);
        xiito = (EditText) v.findViewById(R.id.totalMarksxiiBoard);
        uploadxiimarksheet = (Button) v.findViewById(R.id.xiimarksheetButton);
        uploadxiimarksheet.setOnClickListener(this);
        dBoard = (Spinner) v.findViewById(R.id.dboardspinner);
        dboardother = (EditText) v.findViewById(R.id.otherboardetindboard);
        dmonth = (EditText) v.findViewById(R.id.completionMonthdboard);
        dyear = (EditText) v.findViewById(R.id.completionYeardboard);
        ddownload = (TextView) v.findViewById(R.id.downloaddboardmarksheet);
        dmo = (EditText) v.findViewById(R.id.marksObtaineddboard);
        dto = (EditText) v.findViewById(R.id.totalMarksdBoard);
        uploaddmarksheet = (Button) v.findViewById(R.id.dmarksheetButton);
        uploaddmarksheet.setOnClickListener(this);
        ddownload.setOnClickListener(this);


        beunivspinner = (Spinner) v.findViewById(R.id.beunivspinner);
        beunivspinner.setOnItemSelectedListener(this);
        becollegespinner = (Spinner) v.findViewById(R.id.becollegeSpinner);
        becollegespinner.setOnItemSelectedListener(this);
        besem1applicablespinner = (Spinner) v.findViewById(R.id.besem1applicable);
        besem1applicablespinner.setOnItemSelectedListener(this);
        besem2applicablespinner = (Spinner) v.findViewById(R.id.besem2applicable);
        besem2applicablespinner.setOnItemSelectedListener(this);
        meapplicablespinner = (Spinner) v.findViewById(R.id.meapplicable);
        meapplicablespinner.setOnItemSelectedListener(this);
        otheruniv = (EditText) v.findViewById(R.id.otherBeUniv);
        othercolg = (EditText) v.findViewById(R.id.otherBECollege);

        b1cm = (EditText) v.findViewById(R.id.completionMonthbe1);
        b1cy = (EditText) v.findViewById(R.id.completionYearbe1);
        b1mo = (EditText) v.findViewById(R.id.marksObtainedbe1);
        b1tm = (EditText) v.findViewById(R.id.totalMarksbe1);
        b1cgpa = (EditText) v.findViewById(R.id.cgpabe1);


        b2cm = (EditText) v.findViewById(R.id.completionMonthbe2);
        b2cy = (EditText) v.findViewById(R.id.completionYearbe2);
        b2mo = (EditText) v.findViewById(R.id.marksObtainedbe2);
        b2tm = (EditText) v.findViewById(R.id.totalMarksbe2);
        b2cgpa = (EditText) v.findViewById(R.id.cgpabe2);

        b3cm = (EditText) v.findViewById(R.id.completionMonthbe3);
        b3cy = (EditText) v.findViewById(R.id.completionYearbe3);
        b3mo = (EditText) v.findViewById(R.id.marksObtainedbe3);
        b3tm = (EditText) v.findViewById(R.id.totalMarksbe3);
        b3cgpa = (EditText) v.findViewById(R.id.cgpabe3);

        b4cm = (EditText) v.findViewById(R.id.completionMonthbe4);
        b4cy = (EditText) v.findViewById(R.id.completionYearbe4);
        b4mo = (EditText) v.findViewById(R.id.marksObtainedbe4);
        b4tm = (EditText) v.findViewById(R.id.totalMarksbe4);
        b4cgpa = (EditText) v.findViewById(R.id.cgpabe4);

        b5cm = (EditText) v.findViewById(R.id.completionMonthbe5);
        b5cy = (EditText) v.findViewById(R.id.completionYearbe5);
        b5mo = (EditText) v.findViewById(R.id.marksObtainedbe5);
        b5tm = (EditText) v.findViewById(R.id.totalMarksbe5);
        b5cgpa = (EditText) v.findViewById(R.id.cgpabe5);

        b6cm = (EditText) v.findViewById(R.id.completionMonthbe6);
        b6cy = (EditText) v.findViewById(R.id.completionYearbe6);
        b6mo = (EditText) v.findViewById(R.id.marksObtainedbe6);
        b6tm = (EditText) v.findViewById(R.id.totalMarksbe6);
        b6cgpa = (EditText) v.findViewById(R.id.cgpabe6);

        b7cm = (EditText) v.findViewById(R.id.completionMonthbe7);
        b7cy = (EditText) v.findViewById(R.id.completionYearbe7);
        b7mo = (EditText) v.findViewById(R.id.marksObtainedbe7);
        b7tm = (EditText) v.findViewById(R.id.totalMarksbe7);
        b7cgpa = (EditText) v.findViewById(R.id.cgpabe7);

        b8cm = (EditText) v.findViewById(R.id.completionMonthbe8);
        b8cy = (EditText) v.findViewById(R.id.completionYearbe8);
        b8mo = (EditText) v.findViewById(R.id.marksObtainedbe8);
        b8tm = (EditText) v.findViewById(R.id.totalMarksbe8);
        b8cgpa = (EditText) v.findViewById(R.id.cgpabe8);

        m1cm = (EditText) v.findViewById(R.id.completionMonthme1);
        m1cy = (EditText) v.findViewById(R.id.completionYearme1);
        m1mo = (EditText) v.findViewById(R.id.marksObtainedme1);
        m1tm = (EditText) v.findViewById(R.id.totalMarksme1);
        m1cgpa = (EditText) v.findViewById(R.id.cgpame1);

        m2cm = (EditText) v.findViewById(R.id.completionMonthme2);
        m2cy = (EditText) v.findViewById(R.id.completionYearme2);
        m2mo = (EditText) v.findViewById(R.id.marksObtainedme2);
        m2tm = (EditText) v.findViewById(R.id.totalMarksme2);
        m2cgpa = (EditText) v.findViewById(R.id.cgpame2);

        m3cm = (EditText) v.findViewById(R.id.completionMonthme3);
        m3cy = (EditText) v.findViewById(R.id.completionYearme3);
        m3mo = (EditText) v.findViewById(R.id.marksObtainedme3);
        m3tm = (EditText) v.findViewById(R.id.totalMarksme3);
        m3cgpa = (EditText) v.findViewById(R.id.cgpame3);

        m4cm = (EditText) v.findViewById(R.id.completionMonthme4);
        m4cy = (EditText) v.findViewById(R.id.completionYearme4);
        m4mo = (EditText) v.findViewById(R.id.marksObtainedme4);
        m4tm = (EditText) v.findViewById(R.id.totalMarksme4);
        m4cgpa = (EditText) v.findViewById(R.id.cgpame4);

        lkt = (EditText) v.findViewById(R.id.livekts);
        dkt = (EditText) v.findViewById(R.id.deadkts);

        updateButtonaacads = (Button) v.findViewById(R.id.updateButtonaacads);
        resetuttonacads = (Button) v.findViewById(R.id.resetuttonacads);
        updateButtonaacads.setOnClickListener(this);
        resetuttonacads.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private class RetrieveAcademicDetails extends AsyncTask<String, Void, Document> {
        @Override
        protected void onPreExecute() {
            Log.d("timecomplexity", "onPreExecute in academic frag");
            try {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Fetching");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPreExecute();

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
        protected void onPostExecute(Document document) {
            super.onPostExecute(document);

            // Document d = Jsoup.parse(kalu);
            if (document != null) {


                String kalu = null;
                for (Element e : document.getAllElements()) {
                    for (Node n : e.childNodes()) {

                        if (n instanceof Comment) {

                            kalu = ((Comment) n).getData();
                        }

                    }


                }
/**
 * Here come all the spinner
 */

                spinnerLogic(document, "xboard", xboardDisplay, xBoardValues, xBoard);
                spinnerLogic(document, "xiiboard", xiiboardDisplay, xiiBoardValues, xiiBoard);
                spinnerLogic(document, "dboard", dboardDisplay, dBoardValues, dBoard);
                spinnerLogic(document, "euni", univDisplay, univValues, beunivspinner);
                spinnerLogic(document, "eboard", eboardDisplay, eboardValues, becollegespinner);
                spinnerLogic(document, "iapp", iAppDisplay, iAppValues, besem1applicablespinner);
                spinnerLogic(document, "iiapp", iiAppDisplay, iiAppValues, besem2applicablespinner);
                spinnerLogic(document, "mapp", mAppDisplay, mAppValues, meapplicablespinner);


                /**
                 * put here the other editextboxes visibility on selection of other option in Spinner
                 */
                if (xBoard.getSelectedItemPosition() == 5) {
                    xboardother.setEnabled(true);
                    xboardother.setVisibility(View.VISIBLE);
                    xboardother.setText(document.select("input[name=xboardotr]").first().attr("value"));
                } else {
                    xboardother.setVisibility(View.GONE);
                    xboardother.setEnabled(false);
                }
                if (xiiBoard.getSelectedItemPosition() == 5) {
                    xiiboardother.setEnabled(true);
                    xiiboardother.setVisibility(View.VISIBLE);
                    xiiboardother.setText(document.select("input[name=xiiboardotr]").first().attr("value"));
                } else {
                    xiiboardother.setVisibility(View.GONE);
                    xiiboardother.setEnabled(false);
                }


                if (dBoard.getSelectedItemPosition() == 3) {
                    dboardother.setEnabled(true);
                    dboardother.setVisibility(View.VISIBLE);
                    dboardother.setText(document.select("input[name=dboardotr]").first().attr("value"));
                } else {
                    dboardother.setVisibility(View.GONE);
                    dboardother.setEnabled(false);
                }
                if (beunivspinner.getSelectedItemPosition() == 1) {
                    otheruniv.setEnabled(true);
                    otheruniv.setVisibility(View.VISIBLE);
                    otheruniv.setText(document.select("input[name=euniotr]").first().attr("value"));
                } else {
                    otheruniv.setVisibility(View.GONE);
                    otheruniv.setEnabled(false);
                }
                if (becollegespinner.getSelectedItemPosition() == 1) {
                    othercolg.setEnabled(true);
                    othercolg.setVisibility(View.VISIBLE);
                    othercolg.setText(document.select("input[name=eboardotr]").first().attr("value"));
                } else {
                    othercolg.setVisibility(View.GONE);
                    othercolg.setEnabled(false);
                }
/**
 * ALL the Text setteers
 */

                xmonth.setText(document.select("input[name=xmonth]").first().attr("value"));
                xyear.setText(document.select("input[name=xyear]").first().attr("value"));
                xmo.setText(document.select("input[name=xmo]").first().attr("value"));
                xto.setText(document.select("input[name=xtm]").first().attr("value"));


                xiimonth.setText(document.select("input[name=xiimonth]").first().attr("value"));
                xiiyear.setText(document.select("input[name=xiiyear]").first().attr("value"));
                xiimo.setText(document.select("input[name=xiimo]").first().attr("value"));
                xiito.setText(document.select("input[name=xiitm]").first().attr("value"));

                dmonth.setText(document.select("input[name=dmonth]").first().attr("value"));
                dyear.setText(document.select("input[name=dyear]").first().attr("value"));
                dmo.setText(document.select("input[name=dmo]").first().attr("value"));
                dto.setText(document.select("input[name=dtm]").first().attr("value"));

                b1cm.setText(document.select("input[name=imonth]").first().attr("value"));
                b1cy.setText(document.select("input[name=iyear]").first().attr("value"));
                b1mo.setText(document.select("input[name=imo]").first().attr("value"));
                b1tm.setText(document.select("input[name=itm]").first().attr("value"));
                b1cgpa.setText(document.select("input[name=icgpa]").first().attr("value"));

                b2cm.setText(document.select("input[name=iimonth]").first().attr("value"));
                b2cy.setText(document.select("input[name=iiyear]").first().attr("value"));
                b2mo.setText(document.select("input[name=iimo]").first().attr("value"));
                b2tm.setText(document.select("input[name=iitm]").first().attr("value"));
                b2cgpa.setText(document.select("input[name=iicgpa]").first().attr("value"));

                b3cm.setText(document.select("input[name=iiimonth]").first().attr("value"));
                b3cy.setText(document.select("input[name=iiiyear]").first().attr("value"));
                b3mo.setText(document.select("input[name=iiimo]").first().attr("value"));
                b3tm.setText(document.select("input[name=iiitm]").first().attr("value"));
                b3cgpa.setText(document.select("input[name=iiicgpa]").first().attr("value"));

                b4cm.setText(document.select("input[name=ivmonth]").first().attr("value"));
                b4cy.setText(document.select("input[name=ivyear]").first().attr("value"));
                b4mo.setText(document.select("input[name=ivmo]").first().attr("value"));
                b4tm.setText(document.select("input[name=ivtm]").first().attr("value"));
                b4cgpa.setText(document.select("input[name=ivcgpa]").first().attr("value"));

                b5cm.setText(document.select("input[name=vmonth]").first().attr("value"));
                b5cy.setText(document.select("input[name=vyear]").first().attr("value"));
                b5mo.setText(document.select("input[name=vmo]").first().attr("value"));
                b5tm.setText(document.select("input[name=vtm]").first().attr("value"));
                b5cgpa.setText(document.select("input[name=vcgpa]").first().attr("value"));

                b6cm.setText(document.select("input[name=vimonth]").first().attr("value"));
                b6cy.setText(document.select("input[name=viyear]").first().attr("value"));
                b6mo.setText(document.select("input[name=vimo]").first().attr("value"));
                b6tm.setText(document.select("input[name=vitm]").first().attr("value"));
                b6cgpa.setText(document.select("input[name=vicgpa]").first().attr("value"));

                b7cm.setText(document.select("input[name=viimonth]").first().attr("value"));
                b7cy.setText(document.select("input[name=viiyear]").first().attr("value"));
                b7mo.setText(document.select("input[name=viimo]").first().attr("value"));
                b7tm.setText(document.select("input[name=viitm]").first().attr("value"));
                b7cgpa.setText(document.select("input[name=viicgpa]").first().attr("value"));

                b8cm.setText(document.select("input[name=viiimonth]").first().attr("value"));
                b8cy.setText(document.select("input[name=viiiyear]").first().attr("value"));
                b8mo.setText(document.select("input[name=viiimo]").first().attr("value"));
                b8tm.setText(document.select("input[name=viiitm]").first().attr("value"));
                b8cgpa.setText(document.select("input[name=viiicgpa]").first().attr("value"));

                m1cm.setText(document.select("input[name=immonth]").first().attr("value"));
                m1cy.setText(document.select("input[name=imyear]").first().attr("value"));
                m1mo.setText(document.select("input[name=immo]").first().attr("value"));
                m1tm.setText(document.select("input[name=imtm]").first().attr("value"));
                m1cgpa.setText(document.select("input[name=imcgpa]").first().attr("value"));

                m2cm.setText(document.select("input[name=iimmonth]").first().attr("value"));
                m2cy.setText(document.select("input[name=iimyear]").first().attr("value"));
                m2mo.setText(document.select("input[name=iimmo]").first().attr("value"));
                m2tm.setText(document.select("input[name=iimtm]").first().attr("value"));
                m2cgpa.setText(document.select("input[name=iimcgpa]").first().attr("value"));

                m3cm.setText(document.select("input[name=iiimmonth]").first().attr("value"));
                m3cy.setText(document.select("input[name=iiimyear]").first().attr("value"));
                m3mo.setText(document.select("input[name=iiimmo]").first().attr("value"));
                m3tm.setText(document.select("input[name=iiimtm]").first().attr("value"));
                m3cgpa.setText(document.select("input[name=iiimcgpa]").first().attr("value"));

                m4cm.setText(document.select("input[name=ivmmonth]").first().attr("value"));
                m4cy.setText(document.select("input[name=ivmyear]").first().attr("value"));
                m4mo.setText(document.select("input[name=ivmmo]").first().attr("value"));
                m4tm.setText(document.select("input[name=ivmtm]").first().attr("value"));
                m4cgpa.setText(document.select("input[name=ivmcgpa]").first().attr("value"));


                lkt.setText(document.select("input[name=lkt]").first().attr("value"));
                dkt.setText(document.select("input[name=dkt]").first().attr("value"));


                /**
                 * ALL the Marksheet checkers and download visibility
                 */

                if (FileExistsOnInternet(xmarksheetUrl, document)) {
                    xdownload.setVisibility(View.VISIBLE);
                } else {
                    xdownload.setVisibility(View.GONE);
                }
                if (FileExistsOnInternet(xiimarksheetUrl, document)) {
                    xiidownload.setVisibility(View.VISIBLE);
                } else {
                    xiidownload.setVisibility(View.GONE);
                }
                if (FileExistsOnInternet(dmarksheetUrl, document)) {
                    ddownload.setVisibility(View.VISIBLE);
                } else {
                    ddownload.setVisibility(View.GONE);
                }
                if (FileExistsOnInternet(be1marksheetUrl, document)) {
                    b1.setVisibility(View.VISIBLE);
                } else {
                    b1.setVisibility(View.GONE);
                }
                if (FileExistsOnInternet(be2marksheetUrl, document)) {
                    b2.setVisibility(View.VISIBLE);
                } else {
                    b2.setVisibility(View.GONE);
                }
                if (FileExistsOnInternet(be3marksheetUrl, document)) {
                    b3.setVisibility(View.VISIBLE);
                } else {
                    b3.setVisibility(View.GONE);
                }
                if (FileExistsOnInternet(be4marksheetUrl, document)) {
                    b4.setVisibility(View.VISIBLE);
                } else {
                    b4.setVisibility(View.GONE);
                }
                if (FileExistsOnInternet(be5marksheetUrl, document)) {
                    b5.setVisibility(View.VISIBLE);
                } else {
                    b5.setVisibility(View.GONE);
                }
                if (FileExistsOnInternet(be6marksheetUrl, document)) {
                    b6.setVisibility(View.VISIBLE);
                } else {
                    b6.setVisibility(View.GONE);
                }
                if (FileExistsOnInternet(be7marksheetUrl, document)) {
                    b7.setVisibility(View.VISIBLE);
                } else {
                    b7.setVisibility(View.GONE);
                }
                if (FileExistsOnInternet(be8marksheetUrl, document)) {
                    b8.setVisibility(View.VISIBLE);
                } else {
                    b8.setVisibility(View.GONE);
                }
                if (FileExistsOnInternet(me1marksheetUrl, document)) {
                    m1.setVisibility(View.VISIBLE);
                } else {
                    m1.setVisibility(View.GONE);
                }
                if (FileExistsOnInternet(me2marksheetUrl, document)) {
                    m2.setVisibility(View.VISIBLE);
                } else {
                    m2.setVisibility(View.GONE);
                }

                if (FileExistsOnInternet(me3marksheetUrl, document)) {
                    m3.setVisibility(View.VISIBLE);
                } else {
                    m3.setVisibility(View.GONE);
                }
                if (FileExistsOnInternet(me4marksheetUrl, document)) {
                    m4.setVisibility(View.VISIBLE);
                } else {
                    m4.setVisibility(View.GONE);
                }


            } else {
                Toast.makeText(getActivity(), "Failed to Retrieve", Toast.LENGTH_SHORT).show();
            }
            progressDialog.hide();
        }


        @Override
        protected Document doInBackground(String... strings) {
            Document document = null;
            try {
                Log.d("testresp", getArguments().getString("thecookie"));
                document = Jsoup.connect("http://rait.placyms.com/academic.php").cookie("PHPSESSID", getArguments().getString("thecookie")).get();
                Log.d("testresp", document.html());

            } catch (Exception e) {
                e.printStackTrace();

            }
            return document;
        }
    }

    public boolean FileExistsOnInternet(String URLName, Document doc) {


        Elements links = doc.select("a[target=_blank]");
        for (Element a : links) {

            if (a.toString().contains(URLName))
                return true;
        }
        return false;
    }

    private void downloadFile(String url) {
        Log.d("marksheetUrl", url);
        String _path;
       /* try {
            Connection.Response response = Jsoup.connect(url)
                    .cookie("PHPSESSID", sessId)
                    .ignoreContentType(true)
                    .execute();

            byte[] daas = response.bodyAsBytes();


            File data = new File(Environment.getExternalStorageDirectory(), "xmarksheet-" + System.currentTimeMillis() + ".pdf");
            OutputStream op = new FileOutputStream(data);
            op.write(daas);
            op.close();
            _path = data.getPath();*/
        try {
            Uri ur = Uri.parse(url);// FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", new File(_path));

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(ur, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "No Activity found to handle this file ", Toast.LENGTH_SHORT).show();
        }
       /* } catch (Exception e) {
            _path = "failed";
            return; // swallow}
        }*/
    }


    public class UpdateAcademics extends AsyncTask<String, Void, Document> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Saving");
            progressDialog.show();

        }

        @Override
        protected void onPostExecute(Document document) {
            super.onPostExecute(document);

            progressDialog.hide();
        }

        @Override
        protected Document doInBackground(String... params) {

            for (String a : params)
                Log.d("updateparams", a);


            varbody = getNormalFormValues(params);
            OkHttpClient okHttpClient = new OkHttpClient();

            // String finaleName = b1path.substring(b1path.lastIndexOf("/") + 1);

            //RequestBody fileBody = RequestBody.create(MediaType.parse(finaleName), new File(b1path));


            List<MultipartBody.Part> parts = new ArrayList<>();
            if (std10up) {
                parts.add(prepareFilePart("xupload", std10uri.getData()));
            }
            if (std12up) {
                parts.add(prepareFilePart("xiiupload", std12uri.getData()));
            }
            if (diplomaup) {
                parts.add(prepareFilePart("dupload", diplomauri.getData()));
            }
            if (b1up) {
                parts.add(prepareFilePart("iupload", b1uri.getData()));
            }
            if (b2up) {
                parts.add(prepareFilePart("iiupload", b2uri.getData()));
            }
            if (b3up) {
                parts.add(prepareFilePart("iiiupload", b3uri.getData()));
            }
            if (b4up) {
                parts.add(prepareFilePart("ivupload", b4uri.getData()));
            }
            if (b5up) {
                parts.add(prepareFilePart("vupload", b5uri.getData()));
            }
            if (b6up) {
                parts.add(prepareFilePart("viupload", b6uri.getData()));
            }
            if (b7up) {
                parts.add(prepareFilePart("viiupload", b7uri.getData()));
            }
            if (b8up) {
                parts.add(prepareFilePart("viiiupload", b8uri.getData()));
            }

            if (m1up) {
                parts.add(prepareFilePart("imupload", m1uri.getData()));
            }
            if (m2up) {
                parts.add(prepareFilePart("iimupload", m2uri.getData()));
            }
            if (m3up) {
                parts.add(prepareFilePart("iiimupload", m3uri.getData()));
            }
            if (m4up) {
                parts.add(prepareFilePart("ivmupload", m4uri.getData()));
            }


            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl("http://rait.placyms.com/")

                    //.baseUrl("http://127.0.0.1/acads/")
                    .client(genericClient());

            Retrofit retrofit = builder.build();


            FileUploadService fileUploadService = retrofit.create(FileUploadService.class);

            Call<ResponseBody> call = fileUploadService.uploadMultipleFilesDynamic(parts);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        Log.d("Respoo", response.body().string() + "");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("Respoo", "Fail");
                    t.printStackTrace();
                }
            });


            Document d = null;


          /*  Request request = new Request.Builder()
                    .url("http://rait.placyms.com/academic.php")
                    .post(varbody)
                    .addHeader("Cookie", "PHPSESSID=" + getArguments().getString("thecookie"))
                    .build();
            FileUploadService fileUploadService = r

            try {
                Response response = okHttpClient.newCall(request).execute();

                d = Jsoup.parse(response.body().string());

                Log.d("responseexec", response.body().string());
            } catch (Exception e) {
                e.printStackTrace();

            }*/


            return d;
        }
    }


    public OkHttpClient genericClient() {

        OkHttpClient httpClient = new OkHttpClient.Builder()

                .addInterceptor(new Interceptor() {

                    @Override

                    public okhttp3.Response intercept(Chain chain) throws IOException {

                        Request request = chain.request()

                                .newBuilder()


                                .post(varbody)

                                .addHeader("Cookie", "PHPSESSID=" + getArguments().getString("thecookie"))

                                .build();

                        return chain.proceed(request);

                    }


                })

                .build();


        return httpClient;

    }


    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                okhttp3.MultipartBody.FORM, descriptionString);
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
        File file = FileUtils.getFile(getActivity(), fileUri);

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(getContext().getContentResolver().getType(fileUri)),
                        file
                );

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }


    public RequestBody getNormalFormValues(String... params) {
        RequestBody x = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("xboard", params[0])
                .addFormDataPart("xboardotr", params[1])
                .addFormDataPart("xmonth", params[2])
                .addFormDataPart("xyear", params[3])
                .addFormDataPart("xmo", params[4])
                .addFormDataPart("xtm", params[5])
                .addFormDataPart("xiiboard", params[6])
                .addFormDataPart("xiiboardotr", params[7])
                .addFormDataPart("xiimonth", params[8])
                .addFormDataPart("xiiyear", params[9])
                .addFormDataPart("xiimo", params[10])
                .addFormDataPart("xiitm", params[11])
                .addFormDataPart("dboard", params[12])
                .addFormDataPart("dboardotr", params[13])
                .addFormDataPart("dmonth", params[14])
                .addFormDataPart("dyear", params[15])
                .addFormDataPart("dmo", params[16])
                .addFormDataPart("dtm", params[17])
                .addFormDataPart("euni", params[18])
                .addFormDataPart("euniotr", params[19])
                .addFormDataPart("eboard", params[20])
                .addFormDataPart("eboardotr", params[21])
                .addFormDataPart("iapp", params[22])
                .addFormDataPart("iiapp", params[23])
                .addFormDataPart("mapp", params[24])
                .addFormDataPart("imonth", params[25])
                .addFormDataPart("iyear", params[26])
                .addFormDataPart("imo", params[27])
                .addFormDataPart("itm", params[28])
                .addFormDataPart("icgpa", params[29])
                .addFormDataPart("iimonth", params[30])
                .addFormDataPart("iiyear", params[31])
                .addFormDataPart("iimo", params[32])
                .addFormDataPart("iitm", params[33])
                .addFormDataPart("iicgpa", params[34])
                .addFormDataPart("iiimonth", params[35])
                .addFormDataPart("iiiyear", params[36])
                .addFormDataPart("iiimo", params[37])
                .addFormDataPart("iiitm", params[38])
                .addFormDataPart("iiicgpa", params[39])
                .addFormDataPart("ivmonth", params[40])
                .addFormDataPart("ivyear", params[41])
                .addFormDataPart("ivmo", params[42])
                .addFormDataPart("ivtm", params[43])
                .addFormDataPart("ivcgpa", params[44])
                .addFormDataPart("vmonth", params[45])
                .addFormDataPart("vyear", params[46])
                .addFormDataPart("vmo", params[47])
                .addFormDataPart("vtm", params[48])
                .addFormDataPart("vcgpa", params[49])
                .addFormDataPart("vimonth", params[50])
                .addFormDataPart("viyear", params[51])
                .addFormDataPart("vimo", params[52])
                .addFormDataPart("vitm", params[53])
                .addFormDataPart("vicgpa", params[54])
                .addFormDataPart("viimonth", params[55])
                .addFormDataPart("viiyear", params[56])
                .addFormDataPart("viimo", params[57])
                .addFormDataPart("viitm", params[58])
                .addFormDataPart("viicgpa", params[59])
                .addFormDataPart("viiimonth", params[60])
                .addFormDataPart("viiiyear", params[61])
                .addFormDataPart("viiimo", params[62])
                .addFormDataPart("viiitm", params[63])
                .addFormDataPart("viiicgpa", params[64])
                .addFormDataPart("immonth", params[65])
                .addFormDataPart("imyear", params[66])
                .addFormDataPart("immo", params[67])
                .addFormDataPart("imtm", params[68])
                .addFormDataPart("imcgpa", params[69])
                .addFormDataPart("iimmonth", params[70])
                .addFormDataPart("iimyear", params[71])
                .addFormDataPart("iimmo", params[72])
                .addFormDataPart("iimtm", params[73])
                .addFormDataPart("iimcgpa", params[74])
                .addFormDataPart("iiimmonth", params[75])
                .addFormDataPart("iiimyear", params[76])
                .addFormDataPart("iiimmo", params[77])
                .addFormDataPart("iiimtm", params[78])
                .addFormDataPart("iiimcgpa", params[79])
                .addFormDataPart("ivmmonth", params[80])
                .addFormDataPart("ivmyear", params[81])
                .addFormDataPart("ivmmo", params[82])
                .addFormDataPart("ivmtm", params[83])
                .addFormDataPart("ivmcgpa", params[84])
                .addFormDataPart("lkt", params[85])
                .addFormDataPart("dkt", params[86])
                .addFormDataPart("submit", "Update Academic Info")
                .build();
        return x;
    }
}
