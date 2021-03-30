package com.example.dsc_ssgmce;


import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class Progressing_loginScreen {
    Activity mActivity;
    AlertDialog mAlertDialog;

    Progressing_loginScreen(Activity myActivity){
        mActivity = myActivity;
    }

    void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        LayoutInflater inflater = mActivity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_dialog,null));
        builder.setCancelable(true);

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    void dismissDialog(){
        mAlertDialog.dismiss();
    }

}
