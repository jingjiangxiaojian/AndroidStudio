package com.gizwits.opensource.appkit.SettingsModule;

import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.opensource.appkit.R;
import com.gizwits.opensource.appkit.CommonModule.GosBaseActivity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GosAboutActivity extends GosBaseActivity implements View.OnClickListener {


	/** the tv appCode */
	TextView tv_AppVersion;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gos_about);
		// 设置ActionBar
		setActionBar(true, true, R.string.about);

		initView();

	}

	/**
	 * Inits the view.
	 */
	private void initView() {

		tv_AppVersion = (TextView) findViewById(R.id.appCode);
		findViewById(R.id.ll_1).setOnClickListener(this);
		findViewById(R.id.ll_2).setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();

		tv_AppVersion.setText(getAppVersionName(this));



	}

	/**
	 * 返回当前程序版本名
	 */
	public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				return "";
			}
		} catch (Exception e) {
			Log.e("Apptest", "Exception", e);
		}
		return versionName;
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

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(GosAboutActivity.this, About1Activity.class);
		switch (v.getId()){
			case R.id.ll_1:

				startActivity(intent);
				break;
			case R.id.ll_2:

				startActivity(intent);
				break;

		}
	}
}
