package com.juvetic.rssi.ui;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.juvetic.rssi.R;
import com.juvetic.rssi.util.ToolUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class InformationDialogMap extends BottomSheetDialogFragment {


    TextView d1, d2, d3, xPos, yPos, x1, y1, x2, y2, x3, y3;

    public static InformationDialogMap getInstance() {
        return new InformationDialogMap();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_information_dialog_map, container, false);

        d1 = view.findViewById(R.id.tv_d1);
        d2 = view.findViewById(R.id.tv_d2);
        d3 = view.findViewById(R.id.tv_d3);
        xPos = view.findViewById(R.id.tv_x_pos);
        yPos = view.findViewById(R.id.tv_y_pos);
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
        xPos.setText(ToolUtil.Storage.getValueString(getContext(), "xPos"));
        yPos.setText(ToolUtil.Storage.getValueString(getContext(), "yPos"));
    }
}
