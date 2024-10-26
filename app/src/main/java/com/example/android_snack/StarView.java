package com.example.android_snack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * The type Star view：游戏开始界面
 */
public class StarView extends AppCompatActivity implements View.OnClickListener {

    private Button Starbutton,Sbutton,Backbutton,ShowHistoryButton;

    // 天干
     /**
     * The Shared：实例化存储对象
     */
    SharedPre shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        deleteDatabase("locationDatabase.db");
        setContentView(R.layout.activity_star_view);
        try {
            InitPositionTools();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Init();

    }

    void InitPositionTools() throws InterruptedException {

        AMapLocationClient.updatePrivacyAgree(this,true);
        AMapLocationClient.updatePrivacyShow(this,true,true);

        // 启动定位服务
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
    }

    /**
     * Init：初始化该活动界面信息
     */
    void Init(){
        Starbutton=findViewById(R.id.star_game);
        Sbutton=findViewById(R.id.MostBUtton);
        Backbutton=findViewById(R.id.BackButton);
        ShowHistoryButton=findViewById(R.id.ShowHistoryButton);

        shared=new SharedPre(this);

        Starbutton.setOnClickListener(this);
        Sbutton.setOnClickListener(this);
        Backbutton.setOnClickListener(this);
        ShowHistoryButton.setOnClickListener(this);


    }


    /*1. RecordManager 构造函数
    使用常量来定义共享首选项文件名。
    初始化时直接获取记录数。
            2. addRecord 方法
    将日期格式化和天干地支转换逻辑分离，使代码更清晰。
    减少重复代码。
            3. convertToHeavenlyStemEarthlyBranch 方法
    将日期解析逻辑提取到一个单独的方法中。
    使用静态数组来存储天干地支。*/


    /**
     * onClick:该界面按钮监听事件
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == ViewIds.STAR_GAME) {
            Intent intent = new Intent(StarView.this, MainActivity.class);
            startActivity(intent);
        } else if (id == ViewIds.BACK_BUTTON) {
            finish();
        } else if (id == ViewIds.MOST_BUTTON) {
            Sbutton.setText(Macro.H_MOST_SCORE + shared.read());
        } else if (id == ViewIds.SHOW_HISTORY_BUTTON) {
            Intent intent_show = new Intent(StarView.this, ShowRecord.class);
            startActivity(intent_show);
        }
    }
}