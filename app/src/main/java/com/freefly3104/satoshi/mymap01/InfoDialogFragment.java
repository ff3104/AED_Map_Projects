package com.freefly3104.satoshi.mymap01;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class InfoDialogFragment extends DialogFragment {

    public static InfoDialogFragment newInstance(
            String locationName, String addressAll, String facilityName, String facilityPlace, String contactPoint,String contactTelephone){

        InfoDialogFragment frag = new InfoDialogFragment();
        Bundle args = new Bundle();
        args.putString("locationName", locationName);
        args.putString("addressAll", addressAll);
        args.putString("facilityName", facilityName);
        args.putString("facilityPlace", facilityPlace);
        args.putString("contactPoint", contactPoint);
        args.putString("contactTelephone", contactTelephone);
        frag.setArguments(args);
        return frag;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String locationName = getArguments().getString("locationName");
        String addressAll = getArguments().getString("addressAll");
        String facilityName = getArguments().getString("facilityName");
        String facilityPlace = getArguments().getString("facilityPlace");
        String contactPoint = getArguments().getString("contactPoint");
        String contactTelephone = getArguments().getString("contactTelephone");

        Dialog dialog = new Dialog(getActivity());

        // タイトル非表示
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // フルスクリーン
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.dialog_custom);
        // 背景を透明にする
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tv_name = (TextView) dialog.findViewById(R.id.tv_name);
        TextView tv_address = (TextView) dialog.findViewById(R.id.tv_address);
        TextView tv_facilityName = (TextView) dialog.findViewById(R.id.tv_facilityName);
        TextView tv_facilityPlace = (TextView) dialog.findViewById(R.id.tv_facilityPlace);
        TextView tv_contactPoint = (TextView) dialog.findViewById(R.id.tv_contactPoint);
        TextView tv_contactTelephone = (TextView) dialog.findViewById(R.id.tv_contactTelephone);

        tv_name.setText(locationName);
        tv_address.setText(addressAll);
        tv_facilityName.setText(facilityName);
        tv_facilityPlace.setText(facilityPlace);
        tv_contactPoint.setText(contactPoint);
        tv_contactTelephone.setText(contactTelephone);

        // OK ボタンのリスナ
        dialog.findViewById(R.id.positive_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }

        });

        // Close ボタンのリスナ
        dialog.findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }

        });

        return dialog;

    }
}
