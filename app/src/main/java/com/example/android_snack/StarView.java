package com.example.android_snack;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
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
    private PermissionHelper permissionHelper;

    // 定义要请求的权限
    String[] permissions = {
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.ACCESS_WIFI_STATE,
            android.Manifest.permission.CHANGE_WIFI_STATE,
            android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            android.Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            android.Manifest.permission.WRITE_SETTINGS,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

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

        permissionHelper = new PermissionHelper(this);

        // 检查并请求权限
        permissionHelper.checkAndRequestPermissions(permissions);

    }

    void InitPositionTools() throws InterruptedException {

        AMapLocationClient.updatePrivacyAgree(this,true);
        AMapLocationClient.updatePrivacyShow(this,true,true);

        // 启动定位服务
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
    }

    /*
     * 在Android开发中，启动一个Service和启动一个Activity有明显的不同。下面是两者之间的主要区别
     * 一个是startService
     * 一个是startView
     * */

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