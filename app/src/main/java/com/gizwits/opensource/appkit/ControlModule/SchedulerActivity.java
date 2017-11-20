package com.gizwits.opensource.appkit.ControlModule;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.gizwits.gizwifisdk.api.GizDeviceScheduler;
import com.gizwits.gizwifisdk.api.GizDeviceSchedulerCenter;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizDeviceSchedulerCenterListener;
import com.gizwits.opensource.appkit.CommonModule.GosBaseActivity;
import com.gizwits.opensource.appkit.R;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.gizwits.opensource.appkit.ControlModule.GosControlModuleBaseActivity.KEY_ONOFF;

public class SchedulerActivity extends GosBaseActivity implements View.OnClickListener {
    private RelativeLayout rl_on,rl_off;

    /** 设备列表传入的设备变量 */
    private GizWifiDevice mDevice;

    private TextView tv_on_time,tv_on_week;
    private TextView tv_off_time,tv_off_week;
    private Switch s_on,s_off;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduler);

        setActionBar(true, true, "定时");

        rl_on=(RelativeLayout)findViewById(R.id.rl_on);
        rl_off=(RelativeLayout)findViewById(R.id.rl_off);

        rl_on.setOnClickListener(this);
        rl_off.setOnClickListener(this);


        mDevice = (GizWifiDevice) getIntent().getParcelableExtra("GizWifiDevice");


        tv_on_time=(TextView)findViewById(R.id.textView7);
        tv_on_week=(TextView)findViewById(R.id.textView9);
        tv_off_time=(TextView)findViewById(R.id.textView77);
        tv_off_week=(TextView)findViewById(R.id.textView99);
        s_on=(Switch) findViewById(R.id.switch2);
        s_off=(Switch)findViewById(R.id.switch22);

        getSchedulerList();
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
        switch (v.getId()){
            case R.id.rl_on:
                startActivityForResult(new Intent(SchedulerActivity.this,SchedulerSettingActivity.class),100);
                break;
            case R.id.rl_off:
                startActivityForResult(new Intent(SchedulerActivity.this,SchedulerSettingActivity.class),101);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            if(requestCode==100){

            }
            if(requestCode==101){

            }
        }
    }

    private void sendSchedulerCommand(){
        // 设置定时任务监听

// 一次性定时任务，在2017年1月16日早上6点30分开灯
        GizDeviceScheduler scheduler = new GizDeviceScheduler();
        scheduler.setDate("2017-10-23");
        scheduler.setTime("11:30");
        scheduler.setRemark("task_on");
        ConcurrentHashMap<String, Object> attrs = new ConcurrentHashMap<String, Object>();
        attrs.put(KEY_ONOFF, true);
        scheduler.setAttrs(attrs);
// 创建设备的定时任务，mDevice为在设备列表中得到的设备对象

        GizDeviceSchedulerCenter.createScheduler(spf.getString("Uid",""), spf.getString("Token",""), mDevice, scheduler);

        GizDeviceSchedulerCenter.setListener(mListener);
    }


    private void getSchedulerList(){
        // 设置定时任务监听
        GizDeviceSchedulerCenter.setListener(mListener);
// 同步更新设备的定时任务列表，mDevice为在设备列表中得到的设备对象
        GizDeviceSchedulerCenter.updateSchedulers(spf.getString("Uid",""), spf.getString("Token",""), mDevice);
// 实现回调
    }

    GizDeviceSchedulerCenterListener mListener = new GizDeviceSchedulerCenterListener() {
        @Override
        public void didUpdateSchedulers(GizWifiErrorCode result, GizWifiDevice schedulerOwner, List<GizDeviceScheduler> schedulerList) {
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                // 定时任务创建成功

                for(GizDeviceScheduler s:schedulerList){
                    if("task_on".equals(s.getRemark())){

                    }
                    if("task_off".equals(s.getRemark())){

                    }
                }
            } else {
                // 创建失败
            }
        }



    };

}
