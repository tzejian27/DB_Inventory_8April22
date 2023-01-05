package com.example.db_inventory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
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

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magicrf.uhfreaderlib.reader.Tools;
import com.magicrf.uhfreaderlib.reader.UhfReader;
import com.znht.iodev2.PowerCtl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RFID_MainActivity_Stock_In extends Activity implements OnClickListener, OnItemClickListener {

    DatabaseReference RFIDDatabase;
    DatabaseReference HouseDatabase;
    DatabaseReference HouseDatabase2;
    DatabaseReference stockMovRef;
    DatabaseReference BarcodeRef;
    private Button buttonClear;
    private Button buttonConnect;
    private Button buttonRead;
    private Button buttonAdd;
    private Button buttonPowerOn;
    private Button buttonPowerOff;
    private TextView textVersion;
    private ListView listViewData;
    private ArrayList<EPC> listEPC;
    private ArrayList<Map<String, Object>> listMap;
    private boolean runFlag = true;
    private boolean startFlag = false;
    private boolean connectFlag = false;
    private String serialPortPath = "/dev/ttysWK2";
    private UhfReader reader; //超高频读写器
    private RFID_UhfReaderDevice readerDevice; // 读写器设备，抓哟操作读写器电源
    private WakeLock mWakeLock;
    private PowerCtl powerCtl;
    private RFID_ScreenStateReceiver screenReceiver;
    private int value = 2600;
    static int count = 0;
    String rfid_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setOverflowShowingAlways();
        setContentView(R.layout.rfid_main);

        initView();
        //获取读写器实例，若返回Null,则串口初始化失败
        SharedPreferences sharedPortPath = getSharedPreferences("portPath", 0);
        serialPortPath = sharedPortPath.getString("portPath", "/dev/ttysWK2");
        //打开供电
        powerCtl = new PowerCtl();
        powerCtl.identity_uhf_power(1);
        powerCtl.identity_ctl(1);
        powerCtl.uhf_ctl(1);
        //设置串口
        UhfReader.setPortPath(serialPortPath);
        reader = UhfReader.getInstance();
        //获取读写器设备示例，若返回null，则设备电源打开失败
        readerDevice = RFID_UhfReaderDevice.getInstance();
        if (reader == null) {
            textVersion.setText("serialport init fail");
            setButtonClickable(buttonClear, false);
            setButtonClickable(buttonRead, false);
            setButtonClickable(buttonConnect, false);
            setButtonClickable(buttonAdd, false);
            return;
        }
        if (readerDevice == null) {
            textVersion.setText("UHF reader power on failed");
//			setButtonClickable(buttonClear, false);
//			setButtonClickable(buttonRead, false);
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

    private void initView() {
        buttonRead = (Button) findViewById(R.id.button_read);
        buttonAdd = (Button) findViewById(R.id.button_add);
        buttonConnect = (Button) findViewById(R.id.button_connect);
        buttonClear = (Button) findViewById(R.id.button_clear);
        buttonPowerOn = (Button) findViewById(R.id.button_power_on);
        buttonPowerOff = (Button) findViewById(R.id.button_power_off);
        listViewData = (ListView) findViewById(R.id.listView_data);
        textVersion = (TextView) findViewById(R.id.textView_version);
        buttonRead.setOnClickListener(this);
        buttonAdd.setOnClickListener(this);
        buttonConnect.setOnClickListener(this);
        buttonClear.setOnClickListener(this);
        buttonPowerOn.setOnClickListener(this);
        buttonPowerOff.setOnClickListener(this);
        listEPC = new ArrayList<EPC>();
        listViewData.setOnItemClickListener(this);

    }

    @Override
    protected void onPause() {
        startFlag = false;
        super.onPause();
        releaseWakeLock();
    }

    //将读取的EPC添加到LISTVIEW
    private void addToList(final List<EPC> list, final String epc) {
        final MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.msg);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //CHECK EPC STATUS IN DATABASE, IF NOT EXIST IT SHOW 'Valid' as status
                //else it will be the real status of the epc
                RFIDDatabase = FirebaseDatabase.getInstance().getReference("RFID_database").child(epc);
                RFIDDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            rfid_status = snapshot.child("Status").getValue().toString();
                        }else{
                            rfid_status = "Valid";
                        }

                        //第一次读入数据
                        if (list.isEmpty()) {
                            EPC epcTag = new EPC();
                            epcTag.setEpc(epc);
                            epcTag.setCount(String.valueOf(1));
                            epcTag.setStatus(rfid_status);
                            list.add(epcTag);
                        } else {

                            for (int i = 0; i < list.size(); i++) {
                                long start = System.nanoTime();
                                EPC mEPC = list.get(i);
                                //list中有此EPC
                                if (epc.equals(mEPC.getEpc())) {
//							mEPC.setCount(mEPC.getCount() + 1);
//							list.set(i, mEPC);
                                    break;
                                } else if (i == (list.size() - 1)) {
                                    mediaPlayer.start();
                                    //list中没有此epc
                                    EPC newEPC = new EPC();
                                    newEPC.setEpc(epc);
                                    newEPC.setCount(String.valueOf(1));
                                    newEPC.setStatus(rfid_status);
                                    list.add(newEPC);
                                }
                                System.out.println(System.nanoTime()-start);
                            }

                        }
                        //将数据添加到ListView
                        listMap = new ArrayList<Map<String, Object>>();
                        int idcount = 1;
                        for (EPC epcdata : list) {
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("ID", idcount);
                            map.put("EPC", epcdata.getEpc());
                            map.put("COUNT", epcdata.getCount());
                            map.put("STATUS", epcdata.getStatus());
                            idcount++;
                            listMap.add(map);
                        }
                        listViewData.setAdapter(new SimpleAdapter(RFID_MainActivity_Stock_In.this,
                                listMap, R.layout.rfid_listview_item,
                                new String[]{"ID", "EPC", "STATUS"},
                                new int[]{R.id.textView_id, R.id.textView_epc, R.id.textView_count}));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });
    }

    //设置按钮是否可用
    private void setButtonClickable(Button button, boolean flag) {
        button.setClickable(flag);
        if (flag) {
            button.setTextColor(Color.BLACK);
        } else {
            button.setTextColor(Color.GRAY);
        }
    }

    @Override
    protected void onDestroy() {
        if (screenReceiver != null) {
            unregisterReceiver(screenReceiver);
        }
        runFlag = false;
        if (reader != null) {
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
    private void clearData() {
        listEPC.removeAll(listEPC);
        listViewData.setAdapter(null);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_read:
                if (!startFlag) {
                    startFlag = true;
                    buttonRead.setText(R.string.stop_inventory);
                } else {
                    startFlag = false;
                    buttonRead.setText(R.string.start_inventory);
                }
                break;

            case R.id.button_clear:
                clearData();
                break;

            case R.id.button_add:
                if(buttonRead.getText().equals("Stop")){
                    Toast.makeText(this, "Please stop reading before adding", Toast.LENGTH_SHORT).show();
                    break;
                }
                addRFID();
                break;
            default:
                break;
        }
    }

    public void addRFID() {
        //GET DATA FROM PREVIOUS PAGE
        Intent intent1 = getIntent();
        String barcode = intent1.getStringExtra("barcode");
        String name = intent1.getStringExtra("name");
        String key = intent1.getStringExtra("Key");
        String key2 = intent1.getStringExtra("Key2");
        String users = intent1.getStringExtra("Users");
        String totalqtyh = intent1.getStringExtra("TotalQtyH");
        String batchNum="";
        if(intent1.hasExtra("batchNum")){
            batchNum = intent1.getStringExtra("batchNum");
        }

        //DEFINE THE FIREBASE TABLE LINKED
        HouseDatabase = FirebaseDatabase.getInstance().getReference("House").child(key).child(key2);
        HouseDatabase2 = FirebaseDatabase.getInstance().getReference("House").child(key);

        //connect to EPC List
        List<EPC> list = listEPC;
        //LOOP FOR THE RFID LIST
        for (EPC epcdata : list) {
            //SET THE COUNT FOR THE LOOP
            count = 0;
            //MAPPING THE DATA TO DATAMAP
            Map<String, Object> datamap = new HashMap<String, Object>();
            datamap.put("EPC", epcdata.getEpc());
            datamap.put("Key", key); //House Key
            datamap.put("House", name);
            datamap.put("Barcode", barcode);
            datamap.put("Status", "Stock In");
            datamap.put("Batch", batchNum);

            RFIDDatabase = FirebaseDatabase.getInstance().getReference("RFID_database");
            RFIDDatabase.child(epcdata.getEpc()).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshotRFID) {


                    //CHECK IS EPC EXIST IN DATABASE
                    if (snapshotRFID.exists()) {
                        Toast.makeText(getApplicationContext(), epcdata.getEpc() + " existed!", Toast.LENGTH_SHORT).show();

                    } else {
                        //SAVE WHEN EPC NOT EXIST

                        count++;
                        //CHECK IS THE BARCODE EXIST IN CURRENT HOUSE
                        HouseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot != null) {
                                RFIDDatabase.child(epcdata.getEpc()).setValue(datamap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            //RECORD PERSON INSERT
                                            final DBHandler dbHandler = new DBHandler(getApplicationContext());
                                            Cursor cursor = dbHandler.fetch();
                                            cursor.moveToLast();
                                            String username1 = cursor.getString(1);

                                            HouseDatabase2.child(key2).child("User").setValue(username1);

                                            //RECORD MOVEMENT OF THE STOCK AFTER THE DATA INSERT'S LOOP SUCCESS
                                            stockMovRef = FirebaseDatabase.getInstance().getReference("StockMovement");

                                            //SAVE STOCK IN DATE IN DIFFERENT FORMAT***
                                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss");
                                            String currentDateandTime = sdf.format(new Date());

                                            SimpleDateFormat sdf2 = new SimpleDateFormat("dd_MM_yyyy_HH:mm:ss");
                                            String currentDateandTime2 = sdf2.format(new Date());

                                            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMddHHmmss");
                                            String currentDateandTime3 = sdf3.format(new Date());

                                            //***SAVE STOCK IN DATE IN DIFFERENT FORMAT

                                            //GET CURRENT STOCK DATA WHICH IS ITEM NAME AND THE CURRENT QUANTITY OF BARCODE
                                            String ItemName = snapshot.child("ItemName").getValue().toString().trim();
                                            String Quantity = snapshot.child("Quantity").getValue().toString().trim();

                                            //SET THE STOCK MOVEMENT PARENT NAME IN FORMAT BARCODE_dd_MM_yyyy_HH:mm:ss
                                            // TO MAKE SURE THE SORTING AND DATA FILTER IN SIDE SERVER EASIER
                                            String parentname = barcode + "_" + currentDateandTime2;

                                            //NEW QTY EQUAL TO CURRENT QTY + NUMBER OF RFID (NOT EXIST IN FIREBASE)
                                            int qty = Integer.parseInt(Quantity) + count;

                                            //INSERT STOCK MOVEMENT
                                            Map dataMap4 = new HashMap();
                                            dataMap4.put("ParentName", parentname);
                                            dataMap4.put("Barcode", barcode);
                                            dataMap4.put("Name", ItemName);
                                            dataMap4.put("QtyIn", count+"");
                                            dataMap4.put("QtyOut", 0);
                                            dataMap4.put("QtyInOut_Date", currentDateandTime);
                                            dataMap4.put("QtyInOut_Date2", currentDateandTime3);
                                            //QUANTITY BEFORE STOCK IN
                                            dataMap4.put("Qty", Quantity+"");
                                            //QUANTITY AFTER STOCK IN
                                            dataMap4.put("TotalQty", qty);
                                            dataMap4.put("HouseName", name);
                                            dataMap4.put("User", users);
                                            stockMovRef.child(name).child(parentname).updateChildren(dataMap4);


                                            BarcodeRef = FirebaseDatabase.getInstance().getReference("House").child(key).child(key2);

                                            //TODO 3: UPDATE BASED ON TOTAL ENTERED
                                            //UPDATE STOCK DATA IN HOUSE_BARCODE (NEW QTY!!!)
                                            Map dataMapHouse = new HashMap();
                                            dataMapHouse.put("QtyIn", count+"");
                                            dataMapHouse.put("QtyIn_Date", currentDateandTime);
                                            dataMapHouse.put("Quantity", qty+"");
                                            BarcodeRef.updateChildren(dataMapHouse);

                                            //GET THE TOTAL QTY OF BARCODE IN THE HOUSE
                                            int totalQty = Integer.parseInt(totalqtyh);
                                            //SUM-UP THE INSERTED NUMBER WITH THE TOTAL QUANTITY IN HOUSE
                                            int sum = totalQty + count;
                                            String sum2 = String.valueOf(sum);

                                            HouseDatabase2.child("TotalQty").setValue(sum2);

                                            //ADD HOUSE WHEN HOUSE NOT EXIST IN STOCK MOVEMENT DATABASE
                                            stockMovRef.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (!snapshot.child("housename").exists()) {
                                                        Map map = new HashMap();
                                                        map.put("housename", name);
                                                        stockMovRef.child(name).updateChildren(map);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            Intent intent2SIrfid = new Intent(getApplicationContext(), Stock_In_Success_View.class);
                                            intent2SIrfid.putExtra("barcode", barcode);
                                            intent2SIrfid.putExtra("name", name); //HOUSE'S NAME
                                            intent2SIrfid.putExtra("Key", key); //HOUSE'S RANDOM KEY
                                            intent2SIrfid.putExtra("Key2", key2); //BARCODE'S RANDOM KEY
                                            intent2SIrfid.putExtra("Users", users);
                                            startActivity(intent2SIrfid);
                                            finish();
                                        }
                                    });




                                } else {
                                    Toast.makeText(getApplicationContext(), barcode + "not exist!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }

    }

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
//	private int values = 432 ;
//	private int mixer = 0;
//	private int if_g = 0;

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
            if (menuKeyField != null) {
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
        Intent intent = new Intent(getApplicationContext(), House_List_Stock_In.class);
        intent.putExtra("Users", users);
        startActivity(intent);
        finish();

    }

    /**
     * 盘存线程
     *
     * @author Administrator
     */
    class InventoryThread extends Thread {
        private List<byte[]> epcList;

        @Override
        public void run() {
            super.run();
            while (runFlag) {
                if (startFlag) {
//					reader.stopInventoryMulti()
                    epcList = reader.inventoryRealTime(); //实时盘存
                    if (epcList != null && !epcList.isEmpty()) {
                        //播放提示音
                        //Util.play(1);
                        for (byte[] epc : epcList) {
                            if (epc != null) {
                                String epcStr = Tools.Bytes2HexString(epc, epc.length);
                                addToList(listEPC, epcStr);
                            }
                        }
                    }
                    epcList = null;
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
}
