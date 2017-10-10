package com.gizwits.opensource.appkit.ControlModule;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.opensource.appkit.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GosDeviceControlActivity extends GosControlModuleBaseActivity
		implements OnClickListener, OnEditorActionListener {

	/** 设备列表传入的设备变量 */
	private GizWifiDevice mDevice;

	private CheckBox sw_bool_onoff;
	private CheckBox sw_bool_auto;
	private TextView sw_bool_filterTag;
	private CheckBox sw_bool_lock;
	private CheckBox sw_bool_valve;
	private ImageView iv_supply;
	private ImageView iv_exhaust;
	private TextView tv_data_temperature;
	private TextView tv_data_humidity;
	private TextView tv_data_PM25;
	private TextView tv_data_voc;


	private View tr_supply,tr_exhaust;

	private int[] resIds=new int[]{R.drawable.nowind,R.drawable.low_wind,R.drawable.secondarywind,R.drawable.highwind};

	private enum handler_key {

		/** 更新界面 */
		UPDATE_UI,

		DISCONNECT,
	}

	private Runnable mRunnable = new Runnable() {
		public void run() {
			if (isDeviceCanBeControlled()) {
				progressDialog.cancel();
			} else {
				toastDeviceNoReadyAndExit();
			}
		}

	};

	/** The handler. */
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			handler_key key = handler_key.values()[msg.what];
			switch (key) {
			case UPDATE_UI:
				updateUI();
				break;
			case DISCONNECT:
				toastDeviceDisconnectAndExit();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gos_device_control);
		initDevice();
		setActionBar(true, true, getDeviceName());
		initView();
		initEvent();
	}

	private void initView() {
		
		sw_bool_onoff = (CheckBox) findViewById(R.id.sw_bool_onoff);
		sw_bool_auto = (CheckBox) findViewById(R.id.sw_bool_auto);
		sw_bool_filterTag = (TextView) findViewById(R.id.sd_data_filter);
		sw_bool_lock = (CheckBox) findViewById(R.id.sw_bool_lock);
		sw_bool_valve = (CheckBox) findViewById(R.id.sw_bool_valve);
		iv_supply = (ImageView) findViewById(R.id.iv_supply);
		iv_exhaust = (ImageView) findViewById(R.id.iv_exhaust);
		tv_data_temperature = (TextView) findViewById(R.id.tv_data_temperature);
		tv_data_humidity = (TextView) findViewById(R.id.tv_data_humidity);
		tv_data_PM25 = (TextView) findViewById(R.id.tv_data_PM25);
		tv_data_voc = (TextView) findViewById(R.id.tv_data_voc);

		tr_exhaust=findViewById(R.id.tr_exhaust);
		tr_supply=findViewById(R.id.tr_supply);
	}

	private void initEvent() {

		sw_bool_onoff.setOnClickListener(this);
		sw_bool_auto.setOnClickListener(this);
		//sw_bool_filterTag.setOnClickListener(this);
		sw_bool_lock.setOnClickListener(this);
		sw_bool_valve.setOnClickListener(this);
		setSupplySelection(0);
		setExhaustSelection(0);


		tr_exhaust.setOnClickListener(this);
		tr_supply.setOnClickListener(this);

		findViewById(R.id.sw_set).setOnClickListener(this);
		findViewById(R.id.tr_scheduler).setOnClickListener(this);
	}

	private void setSupplySelection(int position){
		iv_supply.setImageResource(resIds[position]);
	}
	private void setExhaustSelection(int position){
		iv_exhaust.setImageResource(resIds[position]);
	}

	private void initDevice() {
		Intent intent = getIntent();
		mDevice = (GizWifiDevice) intent.getParcelableExtra("GizWifiDevice");
		mDevice.setListener(gizWifiDeviceListener);
		Log.i("Apptest", mDevice.getDid());
	}

	private String getDeviceName() {
		if (TextUtils.isEmpty(mDevice.getAlias())) {
			return mDevice.getProductName();
		}
		return mDevice.getAlias();
	}

	@Override
	protected void onResume() {
		super.onResume();
		getStatusOfDevice();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacks(mRunnable);
		// 退出页面，取消设备订阅
		mDevice.setSubscribe(false);
		mDevice.setListener(null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sw_bool_onoff:
			sendCommand(KEY_ONOFF, sw_bool_onoff.isChecked());
			break;
		case R.id.sw_bool_auto:
			sendCommand(KEY_AUTO, sw_bool_auto.isChecked());
			break;

		case R.id.sw_bool_lock:
			sendCommand(KEY_LOCK, sw_bool_lock.isChecked());
			break;
		case R.id.sw_bool_valve:
			sendCommand(KEY_VALVE, sw_bool_valve.isChecked());
			break;
			case R.id.tr_supply:
				 setSupply(1);
				break;
			case R.id.tr_exhaust:
				setSupply(2);
				break;
			case R.id.sw_set:
				Intent it=	new Intent(GosDeviceControlActivity.this,MoreSettingActivity.class);
				it.putExtra("voc",data_voc);
				it.putExtra("pm",data_PM25);
				startActivityForResult(it,100);
				break;
			case  R.id.tr_scheduler:
				Intent it1=	new Intent(GosDeviceControlActivity.this,SchedulerActivity.class);

				startActivityForResult(it1,101);
				break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==100&&resultCode==RESULT_OK){
			int vocprogress=data.getIntExtra("voc",0);
			tv_data_voc.setText(formatValue((vocprogress + VOC_OFFSET) * VOC_RATIO + VOC_ADDITION, 1));
			sendCommand(KEY_VOC, (vocprogress + VOC_OFFSET ) * VOC_RATIO + VOC_ADDITION);

			int pmprogress=data.getIntExtra("pm",0);
			tv_data_PM25.setText(formatValue((pmprogress + PM_OFFSET) * PM_RATIO + PM_ADDITION, 1));
			sendCommand(KEY_PM25, (pmprogress + PM_OFFSET ) * PM_RATIO + PM_ADDITION);
		}


	}

	/*
         * ========================================================================
         * EditText 点击键盘“完成”按钮方法
         * ========================================================================
         */
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

		switch (v.getId()) {
		default:
			break;
		}
		hideKeyBoard();
		return false;

	}
	


	/*
	 * ========================================================================
	 * 菜单栏
	 * ========================================================================
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.device_more, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_setDeviceInfo:
			setDeviceInfo();
			break;

		case R.id.action_getHardwareInfo:
			if (mDevice.isLAN()) {
				mDevice.getHardwareInfo();
			} else {
				myToast("只允许在局域网下获取设备硬件信息！");
			}
			break;

		case R.id.action_getStatu:
			mDevice.getDeviceStatus();
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Description:根据保存的的数据点的值来更新UI
	 */
	protected void updateUI() {
		
		sw_bool_onoff.setChecked(data_onoff);
		sw_bool_auto.setChecked(data_auto);
		sw_bool_filterTag.setText(data_filterTag+"天");
		sw_bool_lock.setChecked(data_lock);
		sw_bool_valve.setChecked(data_valve);

		setSupplySelection(data_supply);
		setExhaustSelection(data_exhaust);

		tv_data_temperature.setText(data_temperature+"");
		tv_data_humidity.setText(data_humidity+"");
		tv_data_PM25.setText(data_PM25+"");
		tv_data_voc.setText(data_voc+"");

	
	}

	private void setEditText(EditText et, Object value) {
		et.setText(value.toString());
		et.setSelection(value.toString().length());
		et.clearFocus();
	}

	/**
	 * Description:页面加载后弹出等待框，等待设备可被控制状态回调，如果一直不可被控，等待一段时间后自动退出界面
	 */
	private void getStatusOfDevice() {
		// 设备是否可控
		if (isDeviceCanBeControlled()) {
			// 可控则查询当前设备状态
			mDevice.getDeviceStatus();
		} else {
			// 显示等待栏
			progressDialog.show();
			if (mDevice.isLAN()) {
				// 小循环10s未连接上设备自动退出
				mHandler.postDelayed(mRunnable, 10000);
			} else {
				// 大循环20s未连接上设备自动退出
				mHandler.postDelayed(mRunnable, 20000);
			}
		}
	}

	/**
	 * 发送指令,下发单个数据点的命令可以用这个方法
	 * 
	 * <h3>注意</h3>
	 * <p>
	 * 下发多个数据点命令不能用这个方法多次调用，一次性多次调用这个方法会导致模组无法正确接收消息，参考方法内注释。
	 * </p>
	 * 
	 * @param key
	 *            数据点对应的标识名
	 * @param value
	 *            需要改变的值
	 */
	private void sendCommand(String key, Object value) {
		if (value == null) {
			return;
		}
		int sn = 5;
		ConcurrentHashMap<String, Object> hashMap = new ConcurrentHashMap<String, Object>();
		hashMap.put(key, value);
		// 同时下发多个数据点需要一次性在map中放置全部需要控制的key，value值
		// hashMap.put(key2, value2);
		// hashMap.put(key3, value3);
		mDevice.write(hashMap, sn);
		Log.i("liang", "下发命令：" + hashMap.toString());
	}

	private boolean isDeviceCanBeControlled() {
		return mDevice.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceControlled;
	}

	private void toastDeviceNoReadyAndExit() {
		Toast.makeText(this, "设备无响应，请检查设备是否正常工作", Toast.LENGTH_SHORT).show();
		finish();
	}

	private void toastDeviceDisconnectAndExit() {
		Toast.makeText(GosDeviceControlActivity.this, "连接已断开", Toast.LENGTH_SHORT).show();
		finish();
	}

	/**
	 * 展示设备硬件信息
	 * 
	 * @param hardwareInfo
	 */
	private void showHardwareInfo(String hardwareInfo) {
		String hardwareInfoTitle = "设备硬件信息";
		new AlertDialog.Builder(this).setTitle(hardwareInfoTitle).setMessage(hardwareInfo)
				.setPositiveButton(R.string.besure, null).show();
	}

	/**
	 * Description:设置设备别名与备注
	 */
	private void setDeviceInfo() {

		final Dialog mDialog = new AlertDialog.Builder(this).setView(new EditText(this)).create();
		mDialog.show();

		Window window = mDialog.getWindow();
		window.setContentView(R.layout.alert_gos_set_device_info);

		final EditText etAlias;
		final EditText etRemark;
		etAlias = (EditText) window.findViewById(R.id.etAlias);
		etRemark = (EditText) window.findViewById(R.id.etRemark);

		LinearLayout llNo, llSure;
		llNo = (LinearLayout) window.findViewById(R.id.llNo);
		llSure = (LinearLayout) window.findViewById(R.id.llSure);

		if (!TextUtils.isEmpty(mDevice.getAlias())) {
			setEditText(etAlias, mDevice.getAlias());
		}
		if (!TextUtils.isEmpty(mDevice.getRemark())) {
			setEditText(etRemark, mDevice.getRemark());
		}

		llNo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});

		llSure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(etRemark.getText().toString())
						&& TextUtils.isEmpty(etAlias.getText().toString())) {
					myToast("请输入设备别名或备注！");
					return;
				}
				mDevice.setCustomInfo(etRemark.getText().toString(), etAlias.getText().toString());
				mDialog.dismiss();
				String loadingText = (String) getText(R.string.loadingtext);
				progressDialog.setMessage(loadingText);
				progressDialog.show();
			}
		});

		mDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				hideKeyBoard();
			}
		});
	}


	private void setSupply(final int type){
		List<Map<String, Object>> lstImageItem =  new ArrayList<>();

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("itemImage", R.drawable.nowind);//添加图像资源的ID
		map.put("itemText", "无风");//按序号做ItemText
		lstImageItem.add(map);

		map = new HashMap<String, Object>();
		map.put("itemImage", R.drawable.low_wind);//添加图像资源的ID
		map.put("itemText", "低风");//按序号做ItemText
		lstImageItem.add(map);

		map = new HashMap<String, Object>();
		map.put("itemImage", R.drawable.secondarywind);//添加图像资源的ID
		map.put("itemText", "中风");//按序号做ItemText
		lstImageItem.add(map);

		map = new HashMap<String, Object>();
		map.put("itemImage", R.drawable.highwind);//添加图像资源的ID
		map.put("itemText", "高风");//按序号做ItemText
		lstImageItem.add(map);




		SimpleAdapter saImageItems =  new SimpleAdapter(this, lstImageItem,R.layout.layout_simple_list,  new String[]{"itemImage", "itemText"},
				new int[]{R.id.image, R.id.text});

		AlertDialog dialog = new AlertDialog.Builder(this)

						.setAdapter(saImageItems, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int position) {

								switch (type){
									case 1:
										if (position != data_supply) {
											sendCommand(KEY_SUPPLY, position);
											data_supply = position;
											setSupplySelection(position);
										}
										break;
									case 2:
										if (position != data_exhaust) {
											sendCommand(KEY_EXHAUST, position);
											data_exhaust = position;
											setExhaustSelection(position);
										}
										break;
								}

							}
						})
					.setNegativeButton("取消", null)
						.create();

		Window window = dialog.getWindow();
		window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
		window.getAttributes().width= ViewGroup.LayoutParams.MATCH_PARENT;
		//window.setWindowAnimations(R.style.mystyle);  //添加动画
		dialog.show();


	}
	/*
	 * 获取设备硬件信息回调
	 */
	@Override
	protected void didGetHardwareInfo(GizWifiErrorCode result, GizWifiDevice device,
			ConcurrentHashMap<String, String> hardwareInfo) {
		super.didGetHardwareInfo(result, device, hardwareInfo);
		StringBuffer sb = new StringBuffer();
		if (GizWifiErrorCode.GIZ_SDK_SUCCESS != result) {
			myToast("获取设备硬件信息失败：" + result.name());
		} else {
			sb.append("Wifi Hardware Version:" + hardwareInfo.get(WIFI_HARDVER_KEY) + "\r\n");
			sb.append("Wifi Software Version:" + hardwareInfo.get(WIFI_SOFTVER_KEY) + "\r\n");
			sb.append("MCU Hardware Version:" + hardwareInfo.get(MCU_HARDVER_KEY) + "\r\n");
			sb.append("MCU Software Version:" + hardwareInfo.get(MCU_SOFTVER_KEY) + "\r\n");
			sb.append("Wifi Firmware Id:" + hardwareInfo.get(WIFI_FIRMWAREID_KEY) + "\r\n");
			sb.append("Wifi Firmware Version:" + hardwareInfo.get(WIFI_FIRMWAREVER_KEY) + "\r\n");
			sb.append("Product Key:" + "\r\n" + hardwareInfo.get(PRODUCT_KEY) + "\r\n");

			// 设备属性
			sb.append("Device ID:" + "\r\n" + mDevice.getDid() + "\r\n");
			sb.append("Device IP:" + mDevice.getIPAddress() + "\r\n");
			sb.append("Device MAC:" + mDevice.getMacAddress() + "\r\n");
		}
		showHardwareInfo(sb.toString());
	}
	
	/*
	 * 设置设备别名和备注回调
	 */
	@Override
	protected void didSetCustomInfo(GizWifiErrorCode result, GizWifiDevice device) {
		super.didSetCustomInfo(result, device);
		if (GizWifiErrorCode.GIZ_SDK_SUCCESS == result) {
			myToast("设置成功");
			progressDialog.cancel();
			finish();
		} else {
			myToast("设置失败：" + result.name());
		}
	}

	/*
	 * 设备状态改变回调，只有设备状态为可控才可以下发控制命令
	 */
	@Override
	protected void didUpdateNetStatus(GizWifiDevice device, GizWifiDeviceNetStatus netStatus) {
		super.didUpdateNetStatus(device, netStatus);
		if (netStatus == GizWifiDeviceNetStatus.GizDeviceControlled) {
			mHandler.removeCallbacks(mRunnable);
			progressDialog.cancel();
		} else {
			mHandler.sendEmptyMessage(handler_key.DISCONNECT.ordinal());
		}
	}
	
	/*
	 * 设备上报数据回调，此回调包括设备主动上报数据、下发控制命令成功后设备返回ACK
	 */
	@Override
	protected void didReceiveData(GizWifiErrorCode result, GizWifiDevice device,
			ConcurrentHashMap<String, Object> dataMap, int sn) {
		super.didReceiveData(result, device, dataMap, sn);
		Log.i("liang", "接收到数据");
		if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS && dataMap.get("data") != null) {
			getDataFromReceiveDataMap(dataMap);
			mHandler.sendEmptyMessage(handler_key.UPDATE_UI.ordinal());
		}
	}

}