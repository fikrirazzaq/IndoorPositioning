package com.juvetic.rssi.ui;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.juvetic.rssi.R;
import com.juvetic.rssi.util.ToolUtil;


public class InformationDialogMapAll extends BottomSheetDialogFragment {

    TextView d1, d2, d3, d1Kalman1, d2Kalman1, d3Kalman1,
            d1Kalman2, d2Kalman2, d3Kalman2, d1Feedback, d2Feedback, d3Feedback,
            xPos, yPos, xPosKalman1, yPosKalman1,
            xPosKalman2, yPosKalman2, xPosFeedback, yPosFeedback,
            x1, y1, x2, y2, x3, y3;

    public static InformationDialogMapAll getInstance() {
        return new InformationDialogMapAll();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_information_dialog_map_all, container, false);

        d1 = view.findViewById(R.id.tv_d1);
        d2 = view.findViewById(R.id.tv_d2);
        d3 = view.findViewById(R.id.tv_d3);
        d1Kalman1 = view.findViewById(R.id.tv_d1_kalman1);
        d2Kalman1 = view.findViewById(R.id.tv_d2_kalman1);
        d3Kalman1 = view.findViewById(R.id.tv_d3_kalman1);
        d1Kalman2 = view.findViewById(R.id.tv_d1_kalman2);
        d2Kalman2 = view.findViewById(R.id.tv_d2_kalman2);
        d3Kalman2 = view.findViewById(R.id.tv_d3_kalman2);
        d1Feedback = view.findViewById(R.id.tv_d1_feedback);
        d2Feedback = view.findViewById(R.id.tv_d2_feedback);
        d3Feedback = view.findViewById(R.id.tv_d3_feedback);

        xPos = view.findViewById(R.id.tv_x_pos);
        yPos = view.findViewById(R.id.tv_y_pos);
        xPosKalman1 = view.findViewById(R.id.tv_x_pos_k1);
        yPosKalman1 = view.findViewById(R.id.tv_y_pos_k1);
        xPosKalman2 = view.findViewById(R.id.tv_x_pos_k2);
        yPosKalman2 = view.findViewById(R.id.tv_y_pos_k2);
        xPosFeedback = view.findViewById(R.id.tv_x_pos_fb);
        yPosFeedback = view.findViewById(R.id.tv_y_pos_fb);

        x1 = view.findViewById(R.id.tv_x1);
        y1 = view.findViewById(R.id.tv_y1);
        x2 = view.findViewById(R.id.tv_x2);
        y2 = view.findViewById(R.id.tv_y2);
        x3 = view.findViewById(R.id.tv_x3);
        y3 = view.findViewById(R.id.tv_y3);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        x1.setText(ToolUtil.Storage.getValueString(getContext(), "x1"));
        y1.setText(ToolUtil.Storage.getValueString(getContext(), "y1"));
        x2.setText(ToolUtil.Storage.getValueString(getContext(), "x2"));
        y2.setText(ToolUtil.Storage.getValueString(getContext(), "y2"));
        x3.setText(ToolUtil.Storage.getValueString(getContext(), "x3"));
        y3.setText(ToolUtil.Storage.getValueString(getContext(), "y3"));

        d1.setText(ToolUtil.Storage.getValueString(getContext(), "d1"));
        d2.setText(ToolUtil.Storage.getValueString(getContext(), "d2"));
        d3.setText(ToolUtil.Storage.getValueString(getContext(), "d3"));
        d1Kalman1.setText(ToolUtil.Storage.getValueString(getContext(), "dist_kalman_ap1_type_a"));
        d2Kalman1.setText(ToolUtil.Storage.getValueString(getContext(), "dist_kalman_ap2_type_a"));
        d3Kalman1.setText(ToolUtil.Storage.getValueString(getContext(), "dist_kalman_ap3_type_a"));
        d1Kalman2.setText(ToolUtil.Storage.getValueString(getContext(), "dist_kalman_ap1_type_b"));
        d2Kalman2.setText(ToolUtil.Storage.getValueString(getContext(), "dist_kalman_ap2_type_b"));
        d3Kalman2.setText(ToolUtil.Storage.getValueString(getContext(), "dist_kalman_ap3_type_b"));
        d1Feedback.setText(ToolUtil.Storage.getValueString(getContext(), "dist_feedback_ap1"));
        d2Feedback.setText(ToolUtil.Storage.getValueString(getContext(), "dist_feedback_ap2"));
        d3Feedback.setText(ToolUtil.Storage.getValueString(getContext(), "dist_feedback_ap3"));

        xPos.setText(ToolUtil.Storage.getValueString(getContext(), "xPos"));
        yPos.setText(ToolUtil.Storage.getValueString(getContext(), "yPos"));
        xPosKalman1.setText(ToolUtil.Storage.getValueString(getContext(), "xPosKalman1"));
        yPosKalman1.setText(ToolUtil.Storage.getValueString(getContext(), "yPosKalman1"));
        xPosKalman2.setText(ToolUtil.Storage.getValueString(getContext(), "xPosKalman2"));
        yPosKalman2.setText(ToolUtil.Storage.getValueString(getContext(), "yPosKalman2"));
        xPosFeedback.setText(ToolUtil.Storage.getValueString(getContext(), "xPosFeedback"));
        yPosFeedback.setText(ToolUtil.Storage.getValueString(getContext(), "yPosFeedback"));
    }
}
