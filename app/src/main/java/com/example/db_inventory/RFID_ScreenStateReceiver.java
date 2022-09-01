package com.example.db_inventory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.magicrf.uhfreaderlib.reader.UhfReader;

public class RFID_ScreenStateReceiver extends BroadcastReceiver {

	private UhfReader reader ;
	@Override
	public void onReceive(Context context, Intent intent) {
		reader = UhfReader.getInstance();
		//屏亮
//		if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
//			reader.powerOn();
//			Log.i("ScreenStateReceiver", "screen on");
//
//		}//屏灭
//		else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
//			reader.powerOff();
//			Log.i("ScreenStateReceiver", "screen off");
//		}

	}

}
