package com.example.db_inventory;

import android.app.Application;

/**
 * Created by Administrator on 2018/3/27.
 */

public class RFID_UhfApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RFID_Util.getInstance(this);
    }
}
