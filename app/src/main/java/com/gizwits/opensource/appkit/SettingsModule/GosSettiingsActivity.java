package com.gizwits.opensource.appkit.SettingsModule;

import com.gizwits.opensource.appkit.R;
import com.gizwits.opensource.appkit.CommonModule.GosBaseActivity;
import com.gizwits.opensource.appkit.UserModule.GosUserManager;
import com.gizwits.opensource.appkit.sharingdevice.SharedDeviceListAcitivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GosSettiingsActivity extends GosBaseActivity implements OnClickListener {

	private static final int SETTINGS = 123;
	/** The ll About */
	private LinearLayout llAbout;

	/** The Intent */
	Intent intent;

	private LinearLayout feedback;

	private LinearLayout llquit;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gos_settings);
		// 设置ActionBar
		// setActionBar(true, true, R.string.personal_center);

		initView();
		initEvent();
	}

	private void initView() {
		llAbout = (LinearLayout) findViewById(R.id.llAbout);

		feedback = (LinearLayout) findViewById(R.id.opinion_feedback);

		llquit = (LinearLayout) findViewById(R.id.llQuit);




	}

	private void initEvent() {
		llAbout.setOnClickListener(this);


		feedback.setOnClickListener(this);
		llquit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llAbout:
			intent = new Intent(GosSettiingsActivity.this, GosAboutActivity.class);
			startActivity(intent);
			llAbout.setEnabled(false);
			llAbout.postDelayed(new Runnable() {
				@Override
				public void run() {
					llAbout.setEnabled(true);
				}
			}, 1000);
			break;

		case R.id.opinion_feedback:

			break;

		case R.id.llQuit:

			finish();
			break;

		default:
			break;
		}

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



}
