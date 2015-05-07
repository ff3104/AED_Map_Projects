package com.freefly3104.satoshi.mymap01;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class ScrollViewFragment extends DialogFragment {

    public static ScrollViewFragment newInstance(int title, String message) {
        ScrollViewFragment fragment = new ScrollViewFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        args.putString("message", message);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle safedInstanceState) {
        int title = getArguments().getInt("title");
        String message = getArguments().getString("message");

        // ダイアログのコンテンツ部分
        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_sv, null);

        TextView tvInfo = (TextView) view.findViewById(R.id.tvRouteInfo);
        tvInfo.setText(message);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null);

        return builder.create();
    }

}
