package com.dragonide.raitplacementcell;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;

import java.util.ArrayList;


/**
 * Created by Ankit on 6/6/2017.
 */

public class NotifcationAdapter extends ArrayAdapter<NotifItem> {
    private AppCompatActivity activity;
    Context context;
    ImageView imgProfile;

    ArrayList<NotifItem> data;

    public NotifcationAdapter(@NonNull Context context, ArrayList<NotifItem> data) {
        super(context, -1, data);
        this.context = context;
        this.data = data;
    }

    private int getRandomMaterialColor() {
        int returnColor = Color.GRAY;
        int arrayId = context.getResources().getIdentifier("mdcolor_" + "400", "array", context.getPackageName());

        if (arrayId != 0) {
            TypedArray colors = context.getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;//super.getView(position, convertView, parent);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.dashboard_item, parent, false);


        TextView textView = (TextView) view.findViewById(R.id.t1);
        imgProfile = (ImageView) view.findViewById(R.id.icon_profile);
        TextView textView1 = (TextView) view.findViewById(R.id.icon_text);
        TextView textView2 = (TextView) view.findViewById(R.id.t2);

        textView.setMaxLines(2);



        String a = null;
        try {
            a = data.get(position).getN_title().charAt(0) + "";
        } catch (Exception e) {
            e.printStackTrace();
            a="0";
        }
        textView1.setText(a);
        textView.setText(data.get(position).getN_title());
        textView2.setText(html2text(data.get(position).getN_content()));

        imgProfile.setBackgroundColor(getRandomMaterialColor());

        return view;
    }


    public static String html2text(String html) {
        return Jsoup.parse(html).text();
    }

}
