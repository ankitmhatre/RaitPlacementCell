package com.dragonide.raitplacementcell;


import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ankit on 9/3/2017.
 */

public class ResumeAdapter extends RecyclerView.Adapter<ResumeAdapter.MyViewHolder> {

    private List<Item> resumelist;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView time;
        String uri;

        public MyViewHolder(View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.r_time);
        }
    }

    public ResumeAdapter(List<Item> resumelist) {
        this.resumelist = resumelist;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Item movie = resumelist.get(position);
        holder.time.setText(movie.getTimeStamp());
        holder.uri = movie.getrUri();

    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.resume_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return resumelist.size();
    }
}
