package com.juvetic.rssi.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.juvetic.rssi.R;
import com.juvetic.rssi.util.ToolUtil;

@SuppressLint("ValidFragment")
public class InformationDialog extends BottomSheetDialogFragment {

    TextView d1, d2, d3, xPos, yPos, x1, y1, x2, y2, x3, y3;

    public static InformationDialog getInstance() {
        return new InformationDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.custom_bottom_sheet, container, false);

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

        return view;
    }

}


