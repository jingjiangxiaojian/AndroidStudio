package com.gizwits.opensource.appkit.ControlModule;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.TimePicker;

import com.gizwits.opensource.appkit.CommonModule.GosBaseActivity;
import com.gizwits.opensource.appkit.R;

public class SchedulerSettingActivity extends GosBaseActivity {
    private TimePicker timePicker;
    private CheckBox cb1,cb2,cb3,cb4,cb5,cb6,cb7;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduler_setting);

        setActionBar(true, true, "设置时间");

        timePicker=(TimePicker)findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        cb1=(CheckBox)findViewById(R.id.cb1);
        cb2=(CheckBox)findViewById(R.id.cb2);
        cb3=(CheckBox)findViewById(R.id.cb3);
        cb4=(CheckBox)findViewById(R.id.cb4);
        cb5=(CheckBox)findViewById(R.id.cb5);
        cb6=(CheckBox)findViewById(R.id.cb6);
        cb7=(CheckBox)findViewById(R.id.cb7);
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

