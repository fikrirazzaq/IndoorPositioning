package com.juvetic.rssi;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

public class ApAdapter extends RecyclerView.Adapter<ApAdapter.MyViewHolder> {

    private List<AccessPoint> accessPointList;

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
        holder.distance.setText(accessPoint.getDistance());
        holder.bssid.setText(accessPoint.getBssid());

        switch (accessPoint.getCh()) {
            case "0":
                holder.img.setImageResource(R.drawable.ic_wifi_none);
                break;
            case "1":
                holder.img.setImageResource(R.drawable.ic_wifi_weak);
                break;
            case "2":
                holder.img.setImageResource(R.drawable.ic_wifi_good);
                break;
            case "3":
                holder.img.setImageResource(R.drawable.ic_wifi_full);
                break;
        }

    }

    public void setItems(List<AccessPoint> accessPointList) {
        this.accessPointList = accessPointList;
    }


    @Override
    public int getItemCount() {
        return accessPointList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name, rssi, cap, freq, distance, bssid, ch, venue;
        public ImageView img;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tv_name);
            rssi = view.findViewById(R.id.tv_rssi);
            cap = view.findViewById(R.id.tv_cap);
            freq = view.findViewById(R.id.tv_freq);
            img = view.findViewById(R.id.img_wifi);
            distance = view.findViewById(R.id.tx_distance);
            bssid = view.findViewById(R.id.tv_bssid);
        }
    }
}
