package com.example.administrator.waterwavedemo;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.administrator.waterwavedemo.view.MyWaterWaveView;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 类功能描述：</br>
 * 自定义水波纹效果
 * @author yuyahao
 * @version 1.0 </p> 修改时间：</br> 修改备注：</br>
 *
 * 秋名山上行人稀，常有车手较高低.
 * 如今山道依旧在，不见当年老司机.
 */
public class MainActivity extends AppCompatActivity {
    private MyWaterWaveView myWaterWaveView ;
    private ImageView imageView;
    public static  Activity activity;
    private int duration = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myWaterWaveView = (MyWaterWaveView) findViewById(R.id.myWaterWaveView);
        activity = this;
        ButterKnife.bind(this);
    }

   @OnClick({R.id.imageView,R.id.btn_add_wind,R.id.btn_increave_rice,R.id.btn_del_rice})
   public void onCLick(View v){
        switch (v.getId()){
            case R.id.imageView://开启动画
                myWaterWaveView.startMyAnamitatin();
                break;
            case R.id.btn_add_wind://起风了
                myWaterWaveView.setMyRise(true);
                break;
            case R.id.btn_del_rice://减速
                myWaterWaveView.setDelMyDuration(200);
                break;
            case R.id.btn_increave_rice://加速度
                myWaterWaveView.setMyDuration(200);
                    break;
        }
   }

    @Override
    protected void onPause() {
        super.onPause();
        myWaterWaveView.setValueAnimatorPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myWaterWaveView.setValueAnimatorResume();
    }
}
