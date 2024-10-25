package com.example.android_snack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;

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
    private static final String SHARED_PREF_NAME = "user_records";// 记录条数
    private static final String RECORD_COUNT_KEY = "record_count";
    private static final String RECORD_PREFIX = "record_";

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 99;
    private AMapLocationClient locationClient;
    private AMapLocationClientOption locationOption;
    private DBHelper dbHelper;

//    纬度
    private double latitude;
//    经度
    private double longitude;

    @Override
    protected void onStop() {
        super.onStop();
        if (dbHelper!=null){
            dbHelper.close();
            dbHelper=null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        deleteDatabase("locationDatabase.db");
        setContentView(R.layout.activity_star_view);
        InitPositionTools();
        Init();
        addRecord();
    }

    void InitPositionTools(){
//        过设置隐私同意
        AMapLocationClient.updatePrivacyAgree(this,true);
        AMapLocationClient.updatePrivacyShow(this,true,true);

        // 初始化定位客户端

        try {
            locationClient = new AMapLocationClient(this);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        locationOption = new AMapLocationClientOption();
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locationOption.setNeedAddress(true);
        locationClient.setLocationOption(locationOption);

        AMapLocation lastKnownLocation = locationClient.getLastKnownLocation();
        latitude=lastKnownLocation.getLatitude();
        longitude=lastKnownLocation.getLongitude();
    }

    /**
     * Init：初始化该活动界面信息
     */
    void Init(){
        if (dbHelper==null){
            dbHelper=new DBHelper(this);
        }
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

    public void addRecord() {
        String formattedDate = getCurrentFormattedDate();
        String heavenlyStemEarthlyBranch = convertToHeavenlyStemEarthlyBranch(formattedDate);
        long js=dbHelper.insertLocation(formattedDate,latitude,longitude,heavenlyStemEarthlyBranch);
//        System.out.println(js);
    }

    // 获取当前格式化的日期字符串
    private String getCurrentFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss", Locale.CHINA);
        return sdf.format(new Date());
    }

    // 天干地支转换函数，根据日期计算
    public static String convertToHeavenlyStemEarthlyBranch(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss", Locale.CHINA);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(date));
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1; // 注意：月份是从 0 开始计数的
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY); // 24小时制
            String ymdh_GZ=Lauar.getLunarGZ(year,month,day,hour);
            return ymdh_GZ;
        } catch (ParseException e) {
            e.printStackTrace();
            return "未知";
        }
    }

    // 解析日期字符串为Calendar对象
    // 日期操作是java的基础操作
    private static Calendar parseDate(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss", Locale.CHINA);
        Date d = sdf.parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal;
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