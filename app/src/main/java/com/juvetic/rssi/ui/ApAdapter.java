package com.juvetic.rssi.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.juvetic.rssi.R;
import com.juvetic.rssi.model.AccessPoint;
import java.util.List;

public class ApAdapter extends RecyclerView.Adapter<ApAdapter.MyViewHolder> {

    private List<AccessPoint> accessPointList;

    ApAdapter(List<AccessPoint> accessPointList) {
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
        holder.kalmanRssi.setText(accessPoint.getRssiKalmanTypeA());
        holder.kalmanDistance.setText(accessPoint.getDistanceKalmanTypeA());
        holder.kalmanRssiTypeB.setText(accessPoint.getRssiKalmanTypeB());
        holder.kalmanDistanceTypeB.setText(accessPoint.getDistanceKalmanTypeB());
        holder.feedbackRssi.setText(accessPoint.getRssiFeedback());
        holder.feedbackDistance.setText(accessPoint.getDistanceFeedback());

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

        switch (accessPoint.getBssid()) {
            case "b6:e6:2d:23:84:90":
                holder.ap.setVisibility(View.VISIBLE);
                holder.ap.setText("AP1");
                break;
            case "6a:c6:3a:d6:9c:92":
                holder.ap.setVisibility(View.VISIBLE);
                holder.ap.setText("AP2");
                break;
            case "be:dd:c2:fe:3b:0b":
                holder.ap.setVisibility(View.VISIBLE);
                holder.ap.setText("AP3");
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

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, rssi, cap, freq, distance, bssid, ch, venue, ap, rata,
                kalmanRssi, kalmanDistance, kalmanRssiTypeB, kalmanDistanceTypeB, feedbackRssi, feedbackDistance;

        ImageView img;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tv_name);
            rssi = view.findViewById(R.id.tv_rssi);
            cap = view.findViewById(R.id.tv_cap);
            freq = view.findViewById(R.id.tv_freq);
            img = view.findViewById(R.id.img_wifi);
            distance = view.findViewById(R.id.tx_distance);
            bssid = view.findViewById(R.id.tv_bssid);
            ap = view.findViewById(R.id.tv_ap);
            kalmanDistance = view.findViewById(R.id.tx_distance_kalman);
            kalmanRssi = view.findViewById(R.id.tv_rssi_kalman);
            kalmanDistanceTypeB = view.findViewById(R.id.tx_distance_kalman_type_b);
            kalmanRssiTypeB = view.findViewById(R.id.tv_rssi_kalman_type_b);
            feedbackDistance = view.findViewById(R.id.tx_distance_feedback);
            feedbackRssi = view.findViewById(R.id.tv_rssi_feedback);
        }
    }
}
