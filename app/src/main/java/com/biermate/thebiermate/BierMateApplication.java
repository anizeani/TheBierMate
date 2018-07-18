package com.biermate.thebiermate;

import android.app.Application;
import android.content.Intent;

import com.sumup.merchant.api.SumUpState;

public class BierMateApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        SumUpState.init(this);
    }

}
