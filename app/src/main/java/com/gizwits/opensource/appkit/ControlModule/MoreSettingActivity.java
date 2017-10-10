package com.gizwits.opensource.appkit.ControlModule;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gizwits.opensource.appkit.CommonModule.GosBaseActivity;
import com.gizwits.opensource.appkit.CommonModule.GosDeploy;
import com.gizwits.opensource.appkit.R;

import java.text.DecimalFormat;

public class MoreSettingActivity extends GosBaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private SeekBar seek_voc;
    private TextView tv_data_voc;

    private SeekBar seek_pm;
    private TextView tv_data_pm;

    // 数据点"voc"对应seekbar滚动条补偿值
    protected static final int VOC_OFFSET = 0;
    // 数据点"voc"对应数据点增量值
    protected static final int VOC_ADDITION = 0;
    // 数据点"voc"对应数据点定义的分辨率
    protected static final int VOC_RATIO = 1;

    // 数据点"pm"对应seekbar滚动条补偿值
    protected static final int PM_OFFSET = 0;
    // 数据点"pm"对应数据点增量值
    protected static final int PM_ADDITION = 0;
    // 数据点"pm"对应数据点定义的分辨率
    protected static final int PM_RATIO = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_setting);

        setActionBar(true, true, "设置");


        //initview
        tv_data_voc=(TextView)findViewById(R.id.tv_data_voc);
        seek_voc= (SeekBar) findViewById(R.id.sb_data_voc);

        tv_data_pm=(TextView)findViewById(R.id.tv_data_pm);
        seek_pm= (SeekBar) findViewById(R.id.sb_data_pm);

        seek_voc.setOnSeekBarChangeListener(this);
        seek_pm.setOnSeekBarChangeListener(this);


        Button confirm= (Button) findViewById(R.id.confirm);
        confirm.setOnClickListener(this);


        int data_voc=getIntent().getIntExtra("voc",0);
        tv_data_voc.setText(data_voc+"");
        seek_voc.setProgress((int)((data_voc - VOC_ADDITION) / VOC_RATIO - VOC_OFFSET));

        int data_pm=getIntent().getIntExtra("pm",0);
        tv_data_pm.setText(data_pm+"");
        seek_pm.setProgress((int)((data_pm - PM_ADDITION) / PM_RATIO - PM_OFFSET));


        // 配置文件部署
        confirm.setBackgroundDrawable(GosDeploy.setButtonBackgroundColor());
        confirm.setTextColor(GosDeploy.setButtonTextColor());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.confirm:
                Intent data=new Intent();
                data.putExtra("voc",seek_voc.getProgress());
                data.putExtra("pm",seek_pm.getProgress());
                setResult(RESULT_OK,data);
                finish();
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sb_data_voc:
                tv_data_voc.setText(formatValue((progress + VOC_OFFSET) * VOC_RATIO + VOC_ADDITION, 1));
                break;
            case R.id.sb_data_pm:
                tv_data_pm.setText(formatValue((progress + VOC_OFFSET) * VOC_RATIO + VOC_ADDITION, 1));
                break;
            default:
                break;
        }


    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }



}
