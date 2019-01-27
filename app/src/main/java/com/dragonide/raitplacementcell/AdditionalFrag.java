package com.dragonide.raitplacementcell;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jp.wasabeef.richeditor.RichEditor;


/**
 * Created by Ankit on 7/4/2017.
 */

public class AdditionalFrag extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    RichEditor cobj, achieve, certi, projintrn, por, eca, ai;

    Document doc2;
    String content;
    boolean executiondone = false;
    CheckBox c, cpp, java;
    String sessId;
    Button cobjButton, achieveButton, certButton, projintrnButton, porButton, ecaButton, aiButton;
    ProgressDialog progressDialog;
    String DASHURL = "http://rait.placyms.com/additional.php";
    RetrieveAdditionalInfo retrieveAdditionalInfo = new RetrieveAdditionalInfo();
    RichEditor neededVar = null;
    Button updateButtonaadds, resetuttonadds;
    UpdateAdditionalInfo updateAdditionalInfo = new UpdateAdditionalInfo();
    boolean isCChecked, isCppChecked, isJavaChecked;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(getActivity());

        progressDialog.setMessage("Fetching");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);


    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.c_cbox:
                isCChecked = isChecked;
                break;
            case R.id.cpp_cbox:
                isCppChecked = isChecked;
                break;
            case R.id.java_cbox:
                isJavaChecked = isChecked;
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!executiondone) {
            retrieveAdditionalInfo = new RetrieveAdditionalInfo();
            retrieveAdditionalInfo.execute(sessId);
        } else {
            retrieveAdditionalInfo = null;
            retrieveAdditionalInfo = new RetrieveAdditionalInfo();
            retrieveAdditionalInfo.execute(sessId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        sessId = Utilities.getSESSID();
        View v = inflater.inflate(R.layout.additional_edit_layout, container, false);

        c = (CheckBox) v.findViewById(R.id.c_cbox);
        cpp = (CheckBox) v.findViewById(R.id.cpp_cbox);
        java = (CheckBox) v.findViewById(R.id.java_cbox);
        c.setOnCheckedChangeListener(this);
        cpp.setOnCheckedChangeListener(this);
        java.setOnCheckedChangeListener(this);
        updateButtonaadds = (Button) v.findViewById(R.id.updateButtonaadds);
        resetuttonadds = (Button) v.findViewById(R.id.resetuttonadds);
        updateButtonaadds.setOnClickListener(this);
        resetuttonadds.setOnClickListener(this);

        cobj = (RichEditor) v.findViewById(R.id.cobjeditor);
        achieve = (RichEditor) v.findViewById(R.id.academicEditor);
        certi = (RichEditor) v.findViewById(R.id.certificateEditor);
        projintrn = (RichEditor) v.findViewById(R.id.projectInterEditor);
        por = (RichEditor) v.findViewById(R.id.porEditor);
        eca = (RichEditor) v.findViewById(R.id.extraEditor);
        ai = (RichEditor) v.findViewById(R.id.additionalEditor);

        cobjButton = (Button) v.findViewById(R.id.cobjbutton);
        achieveButton = (Button) v.findViewById(R.id.acadEditButton);
        certButton = (Button) v.findViewById(R.id.certiButtonEdit);
        projintrnButton = (Button) v.findViewById(R.id.projectedit);
        porButton = (Button) v.findViewById(R.id.porEdit);
        ecaButton = (Button) v.findViewById(R.id.extraEdit);
        aiButton = (Button) v.findViewById(R.id.additionalEDit);

        cobjButton.setOnClickListener(this);
        achieveButton.setOnClickListener(this);
        certButton.setOnClickListener(this);
        projintrnButton.setOnClickListener(this);
        porButton.setOnClickListener(this);
        ecaButton.setOnClickListener(this);
        aiButton.setOnClickListener(this);


        return v;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.updateButtonaadds:
                updateAdditionalInfo.execute(
                        cobj.getHtml(),
                        achieve.getHtml(),
                        certi.getHtml(),
                        projintrn.getHtml(),
                        por.getHtml(),
                        eca.getHtml(),
                        ai.getHtml());
                break;
            case R.id.resetuttonadds:
                break;
            case R.id.cobjbutton:
                showTheEditorDialog("Career Objectives", doc2.select("textarea[name=cobj]").first().text(), cobj);

                break;
            case R.id.acadEditButton:
                showTheEditorDialog("Academic Achievements", doc2.select("textarea[name=achieve]").first().text(), achieve);

                break;
            case R.id.certiButtonEdit:
                showTheEditorDialog("Certifications", doc2.select("textarea[name=certi]").first().text(), certi);

                break;
            case R.id.projectedit:
                showTheEditorDialog("Projects & Internships", doc2.select("textarea[name=prp]").first().text(), projintrn);
                break;
            case R.id.porEdit:
                showTheEditorDialog("Position of Responsibility", doc2.select("textarea[name=por]").first().text(), por);
                break;
            case R.id.extraEdit:
                showTheEditorDialog("Extra-curricular Achievements", doc2.select("textarea[name=eca]").first().text(), eca);
                break;
            case R.id.additionalEDit:
                showTheEditorDialog("Additional Information", doc2.select("textarea[name=ai]").first().text(), ai);
                break;
        }
    }


    public void showTheEditorDialog(String title, String contentText, final RichEditor richEditor) {
        String acString = contentText; //doc2.select("textarea[name=achieve]").first().text();
        View dialogre;
        final TextView mPreview;
        final RichEditor mEditor;


        MaterialDialog d = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.html_editor, true)
                .title(title)
                .positiveText("Save")
                .negativeText("Cancel")

                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        RichEditor achrich1 = (RichEditor) dialog.getCustomView().findViewById(R.id.editor);
                        //  achieve.loadData(achrich1.getHtml(), "text/html; charset=utf-8", "utf-8");
                        //achieve.setHtml(achrich1.getHtml());
                        //setNeededVar(achrich1);

                        if (achrich1 != null) {
                            String regex = "<html> <body> ";
                            content = achrich1.getHtml();
                            content = content.replaceAll(regex, "");
                            content = content.replaceAll("</body> </html>", "");
                        } else {
                            Toast.makeText(getActivity(), "Cannot Process the Text", Toast.LENGTH_SHORT).show();
                            content = "none";
                        }
                        richEditor.setHtml(content);
                        //Toast.makeText(getActivity(), content, Toast.LENGTH_SHORT).show();

                    }
                })
                .build();


        dialogre = d.getCustomView();

        if (dialogre != null) {
            mPreview = (TextView) dialogre.findViewById(R.id.preview);

            mEditor = (RichEditor) dialogre.findViewById(R.id.editor);

            mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
                @Override
                public void onTextChange(String text) {
                    mPreview.setText(text);
                    content = text;// Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                }
            });
            mEditor.setHtml("<html> <body> " + acString + " </body> </html> ");//, "text/html; charset=utf-8", "utf-8");


            mEditor.setEditorHeight(400);
            mEditor.setEditorFontSize(12);
            mEditor.setPadding(10, 10, 10, 10);

            mEditor.setPlaceholder("Insert text here...");

            dialogre.findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mEditor.setBold();


                }
            });

            dialogre.findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditor.setItalic();
                }
            });
            dialogre.findViewById(R.id.action_insert_bullets).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mEditor.setBullets();

                }
            });

            dialogre.findViewById(R.id.action_insert_numbers).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditor.setNumbers();

                }
            });
            dialogre.findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditor.setUnderline();
                }
            });

        }
        d.show();
        //return content;
    }


    class UpdateAdditionalInfo extends AsyncTask<String, Void, Document> {
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
            if (document != null) {
                try {
                    String isSucces = document.select("h3").first().text();
                    Toast.makeText(getActivity(), isSucces, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected Document doInBackground(String... params) {
            Document document = null;
            try {
                Connection.Response res;
                if (isCChecked && isCppChecked && isJavaChecked) {
                    res = getFromCombination7(params);
                } else {
                    if (isCChecked && isCppChecked && !isJavaChecked) {
                        res = getFromCombination4(params);
                    } else if (isCChecked && isJavaChecked && !isCppChecked) {
                        res = getFromCombination5(params);
                    } else if (isCppChecked && isJavaChecked && !isCChecked) {
                        res = getFromCombination6(params);

                    } else {
                        if (isCChecked && !isCppChecked && !isJavaChecked) {
                            res = getFromCombination1(params);
                        } else if (!isCChecked && isCppChecked && !isJavaChecked) {
                            res = getFromCombination2(params);
                        } else if (!isCChecked && !isCppChecked && isJavaChecked) {
                            res = getFromCombination3(params);
                        } else {
                            res = getFromCombination0(params);
                        }
                    }
                }

                document = res.parse();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("respoadd", document.html());
            return null;
        }
    }

    public Connection.Response getFromCombination0(String... params) throws Exception {
        return Jsoup.connect("http://rait.placyms.com/additional.php")
                .data("cobj", params[0])
                .data("achieve", params[1])
                .data("certi", params[2])
                .data("prp", params[3])
                .data("por", params[4])
                .data("eca", params[5])
                .data("ai", params[6])
                .data("submit", "Update Additional Info")
                .cookie("PHPSESSID", getArguments().getString("thecookie"))
                .method(Connection.Method.POST)
                .execute();
    }

    public Connection.Response getFromCombination1(String... params) throws Exception {
        return Jsoup.connect("http://rait.placyms.com/additional.php")
                .data("cobj", params[0])
                .data("achieve", params[1])
                .data("certi", params[2])
                .data("prp", params[3])
                .data("por", params[4])
                .data("eca", params[5])
                .data("ai", params[6])

                .data("pl[0]", "C")


                .data("submit", "Update Additional Info")
                .cookie("PHPSESSID", getArguments().getString("thecookie"))
                .method(Connection.Method.POST)
                .execute();
    }

    public Connection.Response getFromCombination2(String... params) throws Exception {
        return Jsoup.connect("http://rait.placyms.com/additional.php")
                .data("cobj", params[0])
                .data("achieve", params[1])
                .data("certi", params[2])
                .data("prp", params[3])
                .data("por", params[4])
                .data("eca", params[5])
                .data("ai", params[6])

                .data("pl[0]", "C++")


                .data("submit", "Update Additional Info")
                .cookie("PHPSESSID", getArguments().getString("thecookie"))
                .method(Connection.Method.POST)
                .execute();
    }

    public Connection.Response getFromCombination3(String... params) throws Exception {
        return Jsoup.connect("http://rait.placyms.com/additional.php")
                .data("cobj", params[0])
                .data("achieve", params[1])
                .data("certi", params[2])
                .data("prp", params[3])
                .data("por", params[4])
                .data("eca", params[5])
                .data("ai", params[6])

                .data("pl[0]", "JAVA")


                .data("submit", "Update Additional Info")
                .cookie("PHPSESSID", getArguments().getString("thecookie"))
                .method(Connection.Method.POST)
                .execute();
    }

    public Connection.Response getFromCombination4(String... params) throws Exception {
        return Jsoup.connect("http://rait.placyms.com/additional.php")
                .data("cobj", params[0])
                .data("achieve", params[1])
                .data("certi", params[2])
                .data("prp", params[3])
                .data("por", params[4])
                .data("eca", params[5])
                .data("ai", params[6])

                .data("pl[0]", "C")
                .data("pl[1]", "C++")


                .data("submit", "Update Additional Info")
                .cookie("PHPSESSID", getArguments().getString("thecookie"))
                .method(Connection.Method.POST)
                .execute();
    }

    public Connection.Response getFromCombination5(String... params) throws Exception {
        return Jsoup.connect("http://rait.placyms.com/additional.php")
                .data("cobj", params[0])
                .data("achieve", params[1])
                .data("certi", params[2])
                .data("prp", params[3])
                .data("por", params[4])
                .data("eca", params[5])
                .data("ai", params[6])

                .data("pl[0]", "C")
                .data("pl[1]", "JAVA")


                .data("submit", "Update Additional Info")
                .cookie("PHPSESSID", getArguments().getString("thecookie"))
                .method(Connection.Method.POST)
                .execute();
    }

    public Connection.Response getFromCombination6(String... params) throws Exception {
        return Jsoup.connect("http://rait.placyms.com/additional.php")
                .data("cobj", params[0])
                .data("achieve", params[1])
                .data("certi", params[2])
                .data("prp", params[3])
                .data("por", params[4])
                .data("eca", params[5])
                .data("ai", params[6])

                .data("pl[0]", "C++")
                .data("pl[1]", "JAVA")


                .data("submit", "Update Additional Info")
                .cookie("PHPSESSID", getArguments().getString("thecookie"))
                .method(Connection.Method.POST)
                .execute();
    }

    public Connection.Response getFromCombination7(String... params) throws Exception {
        return Jsoup.connect("http://rait.placyms.com/additional.php")
                .data("cobj", params[0])
                .data("achieve", params[1])
                .data("certi", params[2])
                .data("prp", params[3])
                .data("por", params[4])
                .data("eca", params[5])
                .data("ai", params[6])

                .data("pl[0]", "C")
                .data("pl[1]", "C++")
                .data("pl[2]", "JAVA")


                .data("submit", "Update Additional Info")
                .cookie("PHPSESSID", getArguments().getString("thecookie"))
                .method(Connection.Method.POST)
                .execute();
    }

    class RetrieveAdditionalInfo extends AsyncTask<String, Void, Document> {
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


            if (document != null) {
                doc2 = document;
                Elements chkboxes = document.select("input[type=checkbox]");
                //Elements sem2selec = besem2.select("option");
                int checkcount = 0;
                for (Element element : chkboxes) {

                    if (element.select("input[checked]").attr("value").length() > 0) {
                        switch (checkcount) {
                            case 0:
                                c.setChecked(true);
                                break;
                            case 1:
                                cpp.setChecked(true);
                                break;
                            case 2:
                                java.setChecked(true);
                                break;
                        }

                    }
                    checkcount++;
                }


                cobj.setHtml("<html> <body> " + document.select("textarea[name=cobj]").first().text() + " </body> </html> ");


                achieve.setHtml("<html> <body> " + document.select("textarea[name=achieve]").first().text() + " </body> </html> ");//, "text/html; charset=utf-8", "utf-8");

                //  achieve.setHtml();//, "text/html; charset=utf-8", "utf-8");

                certi.setHtml("<html> <body> " + document.select("textarea[name=certi]").first().text() + " </body> </html> ");

                projintrn.setHtml("<html> <body> " + document.select("textarea[name=prp]").first().text() + " </body> </html> ");
                por.setHtml("<html> <body> " + document.select("textarea[name=por]").first().text() + " </body> </html> ");
                eca.setHtml("<html> <body> " + document.select("textarea[name=eca]").first().text() + " </body> </html> ");
                ai.setHtml("<html> <body> " + document.select("textarea[name=ai]").first().text() + " </body> </html> ");


            } else {
                Toast.makeText(getActivity(), "Failed to Retrieve", Toast.LENGTH_SHORT).show();
            }
            progressDialog.hide();
        }


        @Override
        protected Document doInBackground(String... strings) {
            Document document = null;
            try {
                document = Jsoup.connect(DASHURL).cookie("PHPSESSID", getArguments().getString("thecookie")).get();
                Log.d("ddddddddd", document.html());

            } catch (Exception e) {
                e.printStackTrace();

            }
            return document;
        }
    }

}
