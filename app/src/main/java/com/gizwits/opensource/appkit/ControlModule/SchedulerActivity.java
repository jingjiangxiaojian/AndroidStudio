package com.gizwits.opensource.appkit.ControlModule;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizDeviceScheduler;
import com.gizwits.gizwifisdk.api.GizDeviceSchedulerCenter;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizScheduleWeekday;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizDeviceSchedulerCenterListener;
import com.gizwits.opensource.appkit.CommonModule.GosBaseActivity;
import com.gizwits.opensource.appkit.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.SimpleFormatter;

import static com.gizwits.opensource.appkit.ControlModule.GosControlModuleBaseActivity.KEY_ONOFF;

public class SchedulerActivity extends GosControlModuleBaseActivity implements View.OnClickListener {
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





        tv_on_time=(TextView)findViewById(R.id.textView7);
        tv_on_week=(TextView)findViewById(R.id.textView9);
        tv_off_time=(TextView)findViewById(R.id.textView77);
        tv_off_week=(TextView)findViewById(R.id.textView99);
        s_on=(Switch) findViewById(R.id.switch2);
        s_off=(Switch)findViewById(R.id.switch22);

        s_on.setOnClickListener(this);
        s_on.setOnClickListener(this);



        initDevice();







    }


    private enum handler_key {

        /** 更新界面 */
        UPDATE_UI,

        DISCONNECT,
    }
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

    private void initDevice() {
        Intent intent = getIntent();
        mDevice = (GizWifiDevice) intent.getParcelableExtra("GizWifiDevice");
        mDevice.setListener(gizWifiDeviceListener);
        Log.i("Apptest", mDevice.getDid());
    }



    @Override
    protected void onResume() {
        super.onResume();
        getStatusOfDevice();
        getSchedulerList();
    }



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

    private void updateUI(){
        s_on.setChecked(data_clockopentag);
        tv_on_time.setText( data_clockopentime+"");
        s_off.setChecked(data_clockclosetag);
        tv_off_time.setText(data_clockclosetime+"");

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

    private Runnable mRunnable = new Runnable() {
        public void run() {
            if (isDeviceCanBeControlled()) {
                progressDialog.cancel();
            } else {
                toastDeviceNoReadyAndExit();
            }
        }

    };


    private boolean isDeviceCanBeControlled() {
        return mDevice.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceControlled;
    }

    private void toastDeviceNoReadyAndExit() {
        Toast.makeText(this, "设备无响应，请检查设备是否正常工作", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void toastDeviceDisconnectAndExit() {
        Toast.makeText(SchedulerActivity.this, "连接已断开", Toast.LENGTH_SHORT).show();
        finish();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
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
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_on:
                startActivityForResult(new Intent(SchedulerActivity.this,SchedulerSettingActivity.class),100);
                break;
            case R.id.rl_off:
                startActivityForResult(new Intent(SchedulerActivity.this,SchedulerSettingActivity.class),101);
                break;
            case R.id.switch2:
                sendCommand(KEY_CLOCKOPENTAG,s_on.isChecked());
                break;
            case R.id.switch22:
                sendCommand(KEY_CLOCKCLOSETAG,s_off.isChecked());
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            if(requestCode==100){
                sendSchedulerCommand(1,data);
            }
            if(requestCode==101){
                sendSchedulerCommand(2,data);
            }
        }
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

                    List<GizScheduleWeekday> weekDays =    s.getWeekdays();
                    String weekStr="";
                    if(weekDays==null){
                        weekStr="单次";
                    }else if(weekDays.size()==7){
                        weekStr="每日";
                    }else{
                        for(GizScheduleWeekday w:weekDays){

                            if(w== GizScheduleWeekday.GizScheduleMonday){
                                weekStr+="周一、";
                            }
                            if(w== GizScheduleWeekday.GizScheduleTuesday){
                                weekStr+="周二、";
                            }
                            if(w== GizScheduleWeekday.GizScheduleWednesday){
                                weekStr+="周三、";
                            }
                            if(w== GizScheduleWeekday.GizScheduleThursday){
                                weekStr+="周四、";
                            }
                            if(w== GizScheduleWeekday.GizScheduleFriday){
                                weekStr+="周五、";
                            }
                            if(w== GizScheduleWeekday.GizScheduleSaturday){
                                weekStr+="周六、";
                            }
                            if(w== GizScheduleWeekday.GizScheduleSunday){
                                weekStr+="周日、";
                            }
                        }

                    }

                    if("task_on".equals(s.getRemark())){
                       // s_on.setChecked(true);
                        tv_on_time.setText(s.getTime());

                        tv_on_week.setText(weekStr);

                        rl_on.setTag(s);
                    }
                    if("task_off".equals(s.getRemark())){
                     //   s_off.setChecked(true);
                       tv_off_time.setText(s.getTime());
                        tv_on_week.setText(weekStr);
                        rl_off.setTag(s);
                    }

                }
            } else {
                // 创建失败
            }
        }



    };

    private void sendSchedulerCommand(int taskType,Intent data){
        GizDeviceSchedulerCenter.setListener(mListener);

        Object task=taskType==1?rl_on.getTag():rl_off.getTag();
        GizDeviceScheduler scheduler;
        if(task==null){
            scheduler= new  GizDeviceScheduler ();
        }else{
            scheduler= (GizDeviceScheduler) task;
        }



        int h= data.getIntExtra("hour",0);
        int m= data.getIntExtra("min",0);
        boolean sun=data.getBooleanExtra("Sun",false);
        boolean mon=data.getBooleanExtra("Mon",false);
        boolean tue=data.getBooleanExtra("Tue",false);
        boolean wed=data.getBooleanExtra("Wed",false);
        boolean thu=data.getBooleanExtra("Thu",false);
        boolean fri=data.getBooleanExtra("Fri",false);
        boolean sat=data.getBooleanExtra("Sat",false);


        SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2=new SimpleDateFormat("HH:mm");

        Calendar calendar=Calendar.getInstance();
        if(calendar.get(Calendar.HOUR_OF_DAY)<h||(calendar.get(Calendar.HOUR_OF_DAY)==h)&&calendar.get(Calendar.MINUTE)<=m){ //未来
            scheduler.setDate(sdf1.format(calendar.getTime()));
        }else{
            calendar.set(Calendar.DAY_OF_MONTH,1);
            scheduler.setDate(sdf1.format(calendar.getTime()));
        }
        calendar.set(Calendar.HOUR_OF_DAY,h);
        calendar.set(Calendar.MINUTE,m);
        scheduler.setTime(sdf2.format(calendar.getTime()));

        scheduler.setRemark(taskType==1?"task_on":"task_off");
        ConcurrentHashMap<String, Object> attrs = new ConcurrentHashMap<String, Object>();
        attrs.put("openclose", taskType==1?true:false);
        scheduler.setAttrs(attrs);

        if(sun||mon ||tue|| wed ||thu ||fri || sat ){
            List<GizScheduleWeekday> weekDays = new ArrayList<GizScheduleWeekday>();
            if(mon){
                weekDays.add(GizScheduleWeekday.GizScheduleMonday);
            }
            if(tue){
                weekDays.add(GizScheduleWeekday.GizScheduleTuesday);
            }
            if(wed){
                weekDays.add(GizScheduleWeekday.GizScheduleWednesday);
            }
            if(thu){
                weekDays.add(GizScheduleWeekday.GizScheduleThursday);
            }
            if(fri){
                weekDays.add(GizScheduleWeekday.GizScheduleFriday);
            }
            if(sat){
                weekDays.add(GizScheduleWeekday.GizScheduleSaturday);
            }
            if(sun){
                weekDays.add(GizScheduleWeekday.GizScheduleSunday);
            }
            scheduler.setWeekdays(weekDays);
        }

        if(scheduler.getSchedulerID()==null){
            // 创建设备的定时任务，mDevice为在设备列表中得到的设备对象
            GizDeviceSchedulerCenter.createScheduler(spf.getString("Uid",""), spf.getString("Token",""), mDevice, scheduler);
        }else{
            GizDeviceSchedulerCenter.editScheduler(spf.getString("Uid",""), spf.getString("Token",""), mDevice, scheduler);
        }





    }

}
