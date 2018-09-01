package com.juvetic.rssi;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

public class ApAdapter extends RecyclerView.Adapter<ApAdapter.MyViewHolder> {

    private List<AccessPoint> accessPointList;
    SwipeRefreshLayout swiper;

    public ApAdapter(List<AccessPoint> accessPointList) {
        this.accessPointList = accessPointList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_rssi, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AccessPoint accessPoint = accessPointList.get(position);
        holder.name.setText(accessPoint.getName());
        holder.rssi.setText(accessPoint.getLevel());
        holder.cap.setText(accessPoint.getCap());
        holder.freq.setText(accessPoint.getFreq());

    }

    private void refresh()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                accessPointList.add(0,accessPointList.get(new Random().nextInt(accessPointList.size())));

                notifyDataSetChanged();

                swiper.setRefreshing(false);
            }
        },3000);
    }


    // Clean all elements of the recycler
    public void clear() {
        accessPointList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<AccessPoint> list) {
        accessPointList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return accessPointList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name, rssi, cap, freq, ch, venue;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tv_name);
            rssi = view.findViewById(R.id.tv_rssi);
            cap = view.findViewById(R.id.tv_cap);
            freq = view.findViewById(R.id.tv_freq);
        }
    }
}
