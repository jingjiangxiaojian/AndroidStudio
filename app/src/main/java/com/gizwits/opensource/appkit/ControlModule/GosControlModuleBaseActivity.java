package com.gizwits.opensource.appkit.ControlModule;

import android.content.Context;
import android.util.Log;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;
import com.gizwits.opensource.appkit.CommonModule.GosBaseActivity;
import com.gizwits.opensource.appkit.utils.HexStrUtils;

import java.text.DecimalFormat;
import java.util.concurrent.ConcurrentHashMap;

public class GosControlModuleBaseActivity extends GosBaseActivity {

	/*
	 * ===========================================================
	 * 以下key值对应开发者在云端定义的数据点标识名
	 * ===========================================================
	 */
	// 数据点"onoff"对应的标识名
	protected static final String KEY_ONOFF = "openclose";
	// 数据点"auto"对应的标识名
	protected static final String KEY_AUTO = "auto";
	// 数据点"filterTag"对应的标识名
	protected static final String KEY_FILTERTAG = "filterTag";
	// 数据点"lock"对应的标识名
	protected static final String KEY_LOCK = "lock";
	// 数据点"valve"对应的标识名
	protected static final String KEY_VALVE = "door";
	// 数据点"supply"对应的标识名
	protected static final String KEY_SUPPLY = "send";
	// 数据点"exhaust"对应的标识名
	protected static final String KEY_EXHAUST = "remove";
	// 数据点"temperature"对应的标识名
	protected static final String KEY_TEMPERATURE = "temperature";
	// 数据点"humidity"对应的标识名
	protected static final String KEY_HUMIDITY = "humidity";
	// 数据点"PM25"对应的标识名
	protected static final String KEY_PM25 = "PM25";
	// 数据点"voc"对应的标识名
	protected static final String KEY_VOC = "voc";


	// 数据点"设置PM2.5"对应的标识名
    protected static final String KEY_PM25SET = "PM25Set";
	// 数据点"VOC显示值"对应的标识名
    protected static final String KEY_VOCSHOW = "VOCShow";
	// 数据点"clockclosetag"对应的标识名
    protected static final String KEY_CLOCKCLOSETAG = "clockclosetag";
	// 数据点"clockopentag"对应的标识名
    protected static final String KEY_CLOCKOPENTAG = "clockopentag";
	// 数据点"clockopentime"对应的标识名
    protected static final String KEY_CLOCKOPENTIME = "clockopentime";
	// 数据点"clockclosetime"对应的标识名
    protected static final String KEY_CLOCKCLOSETIME = "clockclosetime";



    // 数据点"alerttest"对应的标识名
	protected static final String KEY_ALERTTEST = "alerttest";

	/*
	 * ===========================================================
	 * 以下数值对应开发者在云端定义的可写数值型数据点增量值、数据点定义的分辨率、seekbar滚动条补偿值
	 * _ADDITION:数据点增量值
	 * _RATIO:数据点定义的分辨率
	 * _OFFSET:seekbar滚动条补偿值
	 * APP与设备定义的协议公式为：y（APP接收的值）=x（设备上报的值）* RATIO（分辨率）+ADDITION（增量值）
	 * 由于安卓的原生seekbar无法设置最小值，因此代码中增加了一个补偿量OFFSET
	 * 实际上公式中的：x（设备上报的值）=seekbar的值+补偿值
	 * ===========================================================
	 */
	// 数据点"voc"对应seekbar滚动条补偿值
	protected static final int VOC_OFFSET = 0;
	// 数据点"voc"对应数据点增量值
	protected static final int VOC_ADDITION = 0;
	// 数据点"voc"对应数据点定义的分辨率
	protected static final int VOC_RATIO = 1;

    // 数据点"vocshow"对应seekbar滚动条补偿值
    protected static final int VOCSHOW_OFFSET = 0;
    // 数据点"vocshow"对应数据点增量值
    protected static final int VOCSHOW_ADDITION = 0;
    // 数据点"vocshow"对应数据点定义的分辨率
    protected static final int VOCSHOW_RATIO = 1;



    // 数据点"pm"对应seekbar滚动条补偿值
	protected static final int PM_OFFSET = 0;
	// 数据点"pm"对应数据点增量值
	protected static final int PM_ADDITION = 0;
	// 数据点"pm"对应数据点定义的分辨率
	protected static final int PM_RATIO = 1;


    // 数据点"pmset"对应seekbar滚动条补偿值
    protected static final int PMSET_OFFSET = 0;
    // 数据点"pmset"对应数据点增量值
    protected static final int PMSET_ADDITION = 0;
    // 数据点"pmset"对应数据点定义的分辨率
    protected static final int PMSET_RATIO = 1;




    /*
	 * ===========================================================
	 * 以下变量对应设备上报类型为布尔、数值、扩展数据点的数据存储
	 * ===========================================================
	 */
	// 数据点"onoff"对应的存储数据
	protected static boolean data_onoff;
	// 数据点"auto"对应的存储数据
	protected static boolean data_auto;
	// 数据点"filterTag"对应的存储数据
	protected static boolean data_filterTag;
	// 数据点"lock"对应的存储数据
	protected static boolean data_lock;
	// 数据点"valve"对应的存储数据
	protected static boolean data_valve;
	// 数据点"supply"对应的存储数据
	protected static int data_supply;
	// 数据点"exhaust"对应的存储数据
	protected static int data_exhaust;
	// 数据点"temperature"对应的存储数据
	protected static int data_temperature;
	// 数据点"humidity"对应的存储数据
	protected static int data_humidity;
	// 数据点"PM25"对应的存储数据
	protected static int data_PM25;
	// 数据点"voc"对应的存储数据
	protected static int data_voc;

	// 数据点"alerttest"对应的存储数据
	protected static boolean data_alerttest;

    // 数据点"clockclosetag"对应的存储数据
    protected static boolean data_clockclosetag;
    // 数据点"clockopentag"对应的存储数据
    protected static boolean data_clockopentag;
    // 数据点"clockopentime"对应的存储数据
    protected static byte[] data_clockopentime;
    // 数据点"clockclosetime"对应的存储数据
    protected static byte[] data_clockclosetime;
    // 数据点"设置PM2.5"对应的存储数据
    protected static int data_PM25Set;
    // 数据点"VOC显示值"对应的存储数据
    protected static int data_VOCShow;
	/*
	 * ===========================================================
	 * 以下key值对应设备硬件信息各明细的名称，用与回调中提取硬件信息字段。
	 * ===========================================================
	 */
	protected static final String WIFI_HARDVER_KEY = "wifiHardVersion";
	protected static final String WIFI_SOFTVER_KEY = "wifiSoftVersion";
	protected static final String MCU_HARDVER_KEY = "mcuHardVersion";
	protected static final String MCU_SOFTVER_KEY = "mcuSoftVersion";
	protected static final String WIFI_FIRMWAREID_KEY = "wifiFirmwareId";
	protected static final String WIFI_FIRMWAREVER_KEY = "wifiFirmwareVer";
	protected static final String PRODUCT_KEY = "productKey";

	private Toast mToast;
	
	@SuppressWarnings("unchecked")
	protected void getDataFromReceiveDataMap(ConcurrentHashMap<String, Object> dataMap) {
		// 已定义的设备数据点，有布尔、数值和枚举型数据

		if (dataMap.get("data") != null) {
			ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) dataMap.get("data");
			for (String dataKey : map.keySet()) {
				if (dataKey.equals(KEY_ONOFF)) {
					data_onoff = (Boolean) map.get(dataKey);			
				}
				if (dataKey.equals(KEY_AUTO)) {
					data_auto = (Boolean) map.get(dataKey);			
				}
				if (dataKey.equals(KEY_FILTERTAG)) {
					data_filterTag = (Boolean) map.get(dataKey);
				}
				if (dataKey.equals(KEY_LOCK)) {
					data_lock = (Boolean) map.get(dataKey);			
				}
				if (dataKey.equals(KEY_VALVE)) {
					data_valve = (Boolean) map.get(dataKey);			
				}
				if (dataKey.equals(KEY_SUPPLY)) {
					data_supply = (Integer) map.get(dataKey);			
				}
				if (dataKey.equals(KEY_EXHAUST)) {
					data_exhaust = (Integer) map.get(dataKey);			
				}
				if (dataKey.equals(KEY_TEMPERATURE)) {
			
					data_temperature = (Integer) map.get(dataKey);
				}
				if (dataKey.equals(KEY_HUMIDITY)) {
			
					data_humidity = (Integer) map.get(dataKey);
				}
				if (dataKey.equals(KEY_PM25)) {
			
					data_PM25 = (Integer) map.get(dataKey);
				}
				if (dataKey.equals(KEY_VOC)) {
			
					data_voc = (Integer) map.get(dataKey);
				}
				if (dataKey.equals(KEY_ALERTTEST)) {
					data_alerttest = (Boolean) map.get(dataKey);
				}

				if (dataKey.equals(KEY_PM25SET)) {

					data_PM25Set = (Integer) map.get(dataKey);
				}
				if (dataKey.equals(KEY_VOCSHOW)) {

					data_VOCShow = (Integer) map.get(dataKey);
				}
				if (dataKey.equals(KEY_CLOCKOPENTIME)) {

					data_clockopentime = (byte[]) map.get(dataKey);
				}
				if (dataKey.equals(KEY_CLOCKCLOSETIME)) {

					data_clockclosetime = (byte[]) map.get(dataKey);
				}

				if (dataKey.equals(KEY_CLOCKCLOSETAG)) {
					data_clockclosetag = (Boolean) map.get(dataKey);
				}
				if (dataKey.equals(KEY_CLOCKOPENTAG)) {
					data_clockopentag = (Boolean) map.get(dataKey);
				}

			}
		}

		StringBuilder sBuilder = new StringBuilder();

		// 已定义的设备报警数据点，设备发生报警后该字段有内容，没有发生报警则没内容
		if (dataMap.get("alerts") != null) {
			ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) dataMap.get("alerts");
			for (String alertsKey : map.keySet()) {
				if ((Boolean) map.get(alertsKey)) {
					sBuilder.append("报警:" + alertsKey + "=true" + "\n");
				}
			}
		}

		// 已定义的设备故障数据点，设备发生故障后该字段有内容，没有发生故障则没内容
		if (dataMap.get("faults") != null) {
			ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) dataMap.get("faults");
			for (String faultsKey : map.keySet()) {
				if ((Boolean) map.get(faultsKey)) {
					sBuilder.append("故障:" + faultsKey + "=true" + "\n");
				}
			}
		}

		if (sBuilder.length() > 0) {
			sBuilder.insert(0, "[设备故障或报警]\n");
			myToast(sBuilder.toString().trim());
		}

		// 透传数据，无数据点定义，适合开发者自行定义协议自行解析
		if (dataMap.get("binary") != null) {
			byte[] binary = (byte[]) dataMap.get("binary");
			Log.i("", "Binary data:" + HexStrUtils.bytesToHexString(binary));
		}
	}

	GizWifiDeviceListener gizWifiDeviceListener = new GizWifiDeviceListener() {

		/** 用于设备订阅 */
		public void didSetSubscribe(GizWifiErrorCode result, GizWifiDevice device, boolean isSubscribed) {
			GosControlModuleBaseActivity.this.didSetSubscribe(result, device, isSubscribed);
		};

		/** 用于获取设备状态 */
		public void didReceiveData(GizWifiErrorCode result, GizWifiDevice device,
				java.util.concurrent.ConcurrentHashMap<String, Object> dataMap, int sn) {
			GosControlModuleBaseActivity.this.didReceiveData(result, device, dataMap, sn);
		};

		/** 用于设备硬件信息 */
		public void didGetHardwareInfo(GizWifiErrorCode result, GizWifiDevice device,
				java.util.concurrent.ConcurrentHashMap<String, String> hardwareInfo) {
			GosControlModuleBaseActivity.this.didGetHardwareInfo(result, device, hardwareInfo);
		};

		/** 用于修改设备信息 */
		public void didSetCustomInfo(GizWifiErrorCode result, GizWifiDevice device) {
			GosControlModuleBaseActivity.this.didSetCustomInfo(result, device);
		};

		/** 用于设备状态变化 */
		public void didUpdateNetStatus(GizWifiDevice device, GizWifiDeviceNetStatus netStatus) {
			GosControlModuleBaseActivity.this.didUpdateNetStatus(device, netStatus);
		};

	};



	/**
	 * 设备订阅回调
	 * 
	 * @param result
	 *            错误码
	 * @param device
	 *            被订阅设备
	 * @param isSubscribed
	 *            订阅状态
	 */
	protected void didSetSubscribe(GizWifiErrorCode result, GizWifiDevice device, boolean isSubscribed) {
	}

	/**
	 * 设备状态回调
	 * 
	 * @param result
	 *            错误码
	 * @param device
	 *            当前设备
	 * @param dataMap
	 *            当前设备状态
	 * @param sn
	 *            命令序号
	 */
	protected void didReceiveData(GizWifiErrorCode result, GizWifiDevice device,
			java.util.concurrent.ConcurrentHashMap<String, Object> dataMap, int sn) {
	}

	/**
	 * 设备硬件信息回调
	 * 
	 * @param result
	 *            错误码
	 * @param device
	 *            当前设备
	 * @param hardwareInfo
	 *            当前设备硬件信息
	 */
	protected void didGetHardwareInfo(GizWifiErrorCode result, GizWifiDevice device,
			java.util.concurrent.ConcurrentHashMap<String, String> hardwareInfo) {
	}

	/**
	 * 修改设备信息回调
	 * 
	 * @param result
	 *            错误码
	 * @param device
	 *            当前设备
	 */
	protected void didSetCustomInfo(GizWifiErrorCode result, GizWifiDevice device) {
	}

	/**
	 * 设备状态变化回调
	 */
	protected void didUpdateNetStatus(GizWifiDevice device, GizWifiDeviceNetStatus netStatus) {
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void myToast(String string) {
		if (mToast != null) {
			mToast.setText(string);
		} else {
			mToast = Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG);
		}
		mToast.show();
	}

	protected void hideKeyBoard() {
		// 隐藏键盘
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
		}
	}
	
	
	/**
	 *Description:显示格式化数值，保留对应分辨率的小数个数，比如传入参数（20.3656，0.01），将返回20.37
	 *@param date 传入的数值
	 *@param radio 保留多少位小数
	 *@return
	 */
	protected String formatValue(double date, Object scale) {
		if (scale instanceof Double) {
			DecimalFormat df = new DecimalFormat(scale.toString());
			return df.format(date);
		}
		return Math.round(date) + "";
	}

}