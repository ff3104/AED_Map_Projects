package com.freefly3104.satoshi.mymap01;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

public class RadioButtonFragment extends DialogFragment{

//    private String[] array = {"Walking", "Driving"};
//
    private int mCheckedId;
    private RadioButtonDialogOnOkClickListener mListener;

//    private int index;
//
//    public RadioButtonFragment(int index){
//        this.index = index;
//        mCheckedId = index;
//    }

    public void setListener(RadioButtonDialogOnOkClickListener listener){
        this.mListener = listener;
    }

    public static RadioButtonFragment newInstance(int title, String[] items, int checkedItem) {
        RadioButtonFragment fragment = new RadioButtonFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        args.putStringArray("items", items);
        args.putInt("checked_item", checkedItem);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int title = getArguments().getInt("title");
        String[] items = getArguments().getStringArray("items");
        int checkedItem = getArguments().getInt("checked_item");

        // AlertDoalog.Builderインスタンス生成
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // DialogTitle追加
        builder.setTitle(title)

                // シングルチョイス選択肢追加
                .setSingleChoiceItems(items, checkedItem,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // 選択後の処理をここに記述;
                                mCheckedId = which;
                            }
                        })

                // PositiveButton（OKボタン）追加
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // OKボタンクリック時の処理をここに記述
                                // 呼び出し元フラグメントのビューを更新
                                Bundle arg = new Bundle();
                                arg.putInt("KEY_MYDIALOG", mCheckedId);

                                Log.d("id", "" + mCheckedId);

                                // MyFragmentのonOkClickedをコール
                                mListener.onRadioButtonDialogOkClicked(arg);

                            }
                        });


        // AlertDialogインスタンスを生成し戻り値(オブジェクト)として返す
        return builder.create();
    }
}
