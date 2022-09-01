package com.example.db_inventory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.magicrf.uhfreaderlib.reader.Tools;
import com.magicrf.uhfreaderlib.reader.UhfReader;
import com.znht.iodev2.PowerCtl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RFID_MainActivity extends Activity implements OnClickListener ,OnItemClickListener{

	private Button buttonClear;
	private Button buttonConnect;
	private Button buttonStart;
	private Button buttonPowerOn;
	private Button buttonPowerOff;
	private TextView textVersion ;
	private ListView listViewData;
	private ArrayList<EPC> listEPC;
	private ArrayList<Map<String, Object>> listMap;
	private boolean runFlag = true;
	private boolean startFlag = false;
	private boolean connectFlag = false;
	private String serialPortPath = "/dev/ttysWK2";
	private UhfReader reader ; //超高频读写器
	private RFID_UhfReaderDevice readerDevice; // 读写器设备，抓哟操作读写器电源
	private WakeLock mWakeLock;

	private PowerCtl powerCtl ;
	private RFID_ScreenStateReceiver screenReceiver ;

	DatabaseReference RFIDDatabase;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setOverflowShowingAlways();
		setContentView(R.layout.rfid_main);
		initView();
		//获取读写器实例，若返回Null,则串口初始化失败
		SharedPreferences sharedPortPath = getSharedPreferences("portPath", 0);
		serialPortPath =  sharedPortPath.getString("portPath", "/dev/ttysWK2");
		//打开供电
		powerCtl = new PowerCtl() ;
		powerCtl.identity_uhf_power(1);
		powerCtl.identity_ctl(1);
		powerCtl.uhf_ctl(1);
		//设置串口
		UhfReader.setPortPath(serialPortPath);
		reader = UhfReader.getInstance();
		//获取读写器设备示例，若返回null，则设备电源打开失败
		readerDevice = RFID_UhfReaderDevice.getInstance();
		if(reader == null){
			textVersion.setText("serialport init fail");
			setButtonClickable(buttonClear, false);
			setButtonClickable(buttonStart, false);
			setButtonClickable(buttonConnect, false);
			return ;
		}
		if (readerDevice == null)
		{
			textVersion.setText("UHF reader power on failed");
//			setButtonClickable(buttonClear, false);
//			setButtonClickable(buttonStart, false);
//			setButtonClickable(buttonConnect, false);
//			return ;
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//获取用户设置功率,并设置
		SharedPreferences shared = getSharedPreferences("power", 0);
		int value = shared.getInt("value", 26);
		Log.d("", "value" + value);
		reader.setOutputPower(value);


		//添加广播，默认屏灭时休眠，屏亮时唤醒
		screenReceiver = new RFID_ScreenStateReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(screenReceiver, filter);

		/**************************/

//		String serialNum = "";
//        try {
//            Class<?> classZ = Class.forName("android.os.SystemProperties");
//            Method get = classZ.getMethod("get", String.class);
//            serialNum = (String) get.invoke(classZ, "ro.serialno");
//        } catch (Exception e) {
//        }
//        Log.e("serialNum", serialNum);

		/*************************/


		Thread thread = new InventoryThread();
		thread.start();
		//初始化声音池
		acquireWakeLock(this);
	}

	private void initView(){
		buttonStart = (Button) findViewById(R.id.button_start);
		buttonConnect = (Button) findViewById(R.id.button_connect);
		buttonClear = (Button) findViewById(R.id.button_clear);
		buttonPowerOn = (Button) findViewById(R.id.button_power_on);
		buttonPowerOff = (Button) findViewById(R.id.button_power_off);
		listViewData = (ListView) findViewById(R.id.listView_data);
		textVersion = (TextView) findViewById(R.id.textView_version);
		buttonStart.setOnClickListener(this);
		buttonConnect.setOnClickListener(this);
		buttonClear.setOnClickListener(this);
		buttonPowerOn.setOnClickListener(this);
		buttonPowerOff.setOnClickListener(this);
		//setButtonClickable(buttonStart, false);
		listEPC = new ArrayList<EPC>();
		listViewData.setOnItemClickListener(this);

	}


	@Override
	protected void onPause() {
		startFlag = false;
		super.onPause();
		releaseWakeLock();
	}

	/**
	 * 盘存线程
	 * @author Administrator
	 *
	 */
	class InventoryThread extends Thread{
		private List<byte[]> epcList;

		@Override
		public void run() {
			super.run();
			while(runFlag){
				if(startFlag){
//					reader.stopInventoryMulti()
					epcList = reader.inventoryRealTime(); //实时盘存
					if(epcList != null && !epcList.isEmpty()){
						//播放提示音
						//Util.play(1);
						for(byte[] epc:epcList){
							if (epc != null)
							{
								String epcStr = Tools.Bytes2HexString(epc, epc.length);
								addToList(listEPC, epcStr);
							}
						}
					}
					epcList = null ;
					try {
						Thread.sleep(40);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	//将读取的EPC添加到LISTVIEW
	private void addToList(final List<EPC> list, final String epc){
		final MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.msg);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {

				//第一次读入数据
				if(list.isEmpty()){
					EPC epcTag = new EPC();
					epcTag.setEpc(epc);
					epcTag.setCount(1);
					list.add(epcTag);
				}else{
					for(int i = 0; i < list.size(); i++){
						EPC mEPC = list.get(i);
						//list中有此EPC
						if(epc.equals(mEPC.getEpc())){
//							mEPC.setCount(mEPC.getCount() + 1);
//							list.set(i, mEPC);
							break;
						}else if(i == (list.size() - 1)){
							mediaPlayer.start();
							//list中没有此epc
							EPC newEPC = new EPC();
							newEPC.setEpc(epc);
							newEPC.setCount(1);
							list.add(newEPC);
						}
					}
				}
				//将数据添加到ListView
				listMap = new ArrayList<Map<String,Object>>();
				int idcount = 1;
				for(EPC epcdata:list){
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("ID", idcount);
					map.put("EPC", epcdata.getEpc());
					map.put("COUNT", epcdata.getCount());
					idcount++;
					listMap.add(map);

					Map<String, Object> datamap = new HashMap<String, Object>();
					datamap.put("ID", idcount);
					datamap.put("EPC", epcdata.getEpc());
					RFIDDatabase = FirebaseDatabase.getInstance().getReference("RFID_database").child(epcdata.getEpc());
					RFIDDatabase.setValue(datamap);
				}
				listViewData.setAdapter(new SimpleAdapter(RFID_MainActivity.this,
						listMap, R.layout.rfid_listview_item,
						new String[]{"ID", "EPC", "COUNT"},
						new int[]{R.id.textView_id, R.id.textView_epc, R.id.textView_count}));
			}
		});
	}

	//设置按钮是否可用
	private void setButtonClickable(Button button, boolean flag){
		button.setClickable(flag);
		if(flag){
			button.setTextColor(Color.BLACK);
		}else{
			button.setTextColor(Color.GRAY);
		}
	}

	@Override
	protected void onDestroy() {
		if (screenReceiver != null) {
			unregisterReceiver(screenReceiver);
		}
		runFlag = false;
		if(reader != null){
			reader.close();
		}
		if (readerDevice != null) {
			readerDevice.powerOff();
		}
		powerCtl.identity_uhf_power(0);
		powerCtl.identity_ctl(0);
		powerCtl.uhf_ctl(0);
		super.onDestroy();
		releaseWakeLock();
	}

	@Override
	protected void onResume() {
		super.onResume();
		acquireWakeLock(this);



	}

	/**
	 * 清空listview
	 */
	private void clearData(){
		listEPC.removeAll(listEPC);
		listViewData.setAdapter(null);
	}


	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.button_start:
				if(!startFlag){
					startFlag = true ;
					buttonStart.setText(R.string.stop_inventory);
				}else{
					startFlag = false;
					buttonStart.setText(R.string.inventory);
				}
				break;

			case R.id.button_clear:
				clearData();
				break;

			default:
				break;
		}
	}

	private int value = 2600;
//	private int values = 432 ;
//	private int mixer = 0;
//	private int if_g = 0;

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		TextView epcTextview = (TextView) view.findViewById(R.id.textView_epc);
		String epc = epcTextview.getText().toString();
		//选择EPC
//		reader.selectEPC(Tools.HexString2Bytes(epc));

		Toast.makeText(getApplicationContext(), epc, Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(this, RFID_MoreHandleActivity.class);
		intent.putExtra("epc", epc);
		startActivity(intent);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
//		Log.e("", "adfasdfasdf");
//		Intent intent = new Intent(this, SettingActivity.class);
//		startActivity(intent);
		Intent intent = new Intent(this, RFID_SettingPower.class);
		startActivity(intent);
		return super.onMenuItemSelected(featureId, item);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod(
							"setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e) {
				}
			}
		}
		return super.onMenuOpened(featureId, menu);
	}

	/**
	 * 在actionbar上显示菜单按钮
	 */
	private void setOverflowShowingAlways() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("obj");
			if(menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void acquireWakeLock(Context context) {
//		if (mWakeLock == null) {
//			PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
//			mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
//					"uhf_inventory");
//			mWakeLock.acquire();
//		}
	}

	private void releaseWakeLock() {
//		if (mWakeLock != null && mWakeLock.isHeld() ) {
//			mWakeLock.release();
//			mWakeLock = null;
//		}
	}

	@Override
	public void onBackPressed() {
		String users = getIntent().getStringExtra("Users");
		super.onBackPressed();
		Intent intent = new Intent(getApplicationContext(), Home_Page.class);
		intent.putExtra("Users", users);
		startActivity(intent);
		finish();

	}
}
