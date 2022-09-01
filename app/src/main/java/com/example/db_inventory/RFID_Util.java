package com.example.db_inventory;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.Map;

public class RFID_Util {


	public static SoundPool sp;
	public static Map<Integer, Integer> soundMap;
	public static Context context;
	static boolean muted = false;

	//初始化声音池
	public static SoundPool getInstance(Context context){
		RFID_Util.context = context;
//		sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
		if(sp==null){
			sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
		}
		sp.load(context, R.raw.msg, 1);

		return  sp;

	}

	//播放声音池声音
	public static  void play(int soundID){
		try{
			if(!muted && soundMap.containsKey(soundID)){
				sp.play(soundMap.get(soundID), 1, 1, 0, 0, 1f);
			}

		}catch (Exception e){
			System.out.println("????????????????????????????????????????");
		}

	}

}
