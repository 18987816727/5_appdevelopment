package com.example.android_snack;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LocationService extends Service {
    private static final int REFRESH_INTERVAL = 30000; // 30秒
    private static final String TAG = "LocationService";

    private AMapLocationClient locationClient;
    private AMapLocationClientOption locationOption;
    private Handler handler;
    private boolean isRunning = false;

    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }
    private DBHelper dbHelper;
    //    纬度
    private double latitude;
    //    经度
    private double longitude;
    private AsyncHttpRequest asyncHttpRequest;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
        // 检查定位权限

        // 初始化本地数据库连接
        if (dbHelper==null){
            dbHelper=new DBHelper(this);
        }
        // 初始化异步请求对象
        asyncHttpRequest = new AsyncHttpRequest();

        // 初始化定位客户端
        try {
            locationClient = new AMapLocationClient(this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create AMapLocationClient", e);
        }

        // 创建定位参数对象
        locationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置是否返回地址信息
        locationOption.setNeedAddress(true);

        // 设置定位参数
        locationClient.setLocationOption(locationOption);

        // 创建Handler
        handler = new Handler(Looper.getMainLooper());

        // 启动定位
        startLocationUpdates();
        try {
            addRecord();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Service bound");
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");
        stopLocationUpdates();
        if (dbHelper!=null){
            dbHelper.close();
            dbHelper=null;
        }
    }

    private void startLocationUpdates() {
        if (!isRunning) {
            isRunning = true;

            // 设置定位监听器
            // 其实是异步线程
            locationClient.setLocationListener(new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation amapLocation) {
                    if (amapLocation != null) {
                        if (amapLocation.getErrorCode() == 0) {
                            // 定位成功
                            latitude = amapLocation.getLatitude();
                            longitude = amapLocation.getLongitude();
                            try {
                                addRecord();
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            // 使用获取到的经纬度信息...
                            Log.d(TAG, "New location received: Latitude=" + latitude + ", Longitude=" + longitude);
                        } else {
                            // 处理定位失败的情况
                            Log.e(TAG, "Location failed with error code: " + amapLocation.getErrorCode());
                        }
                    } else {
                        // 处理amapLocation为null的情况
                        Log.e(TAG, "Received null location object.");
                    }
                }
            });

            // 创建一个Runnable对象来执行定位请求
            final Runnable updateLocationRunnable = new Runnable() {
                @Override
                public void run() {
                    // 重新启动定位请求
                    locationClient.startLocation();

                    // 重新安排下一次定位请求
                    handler.postDelayed(this, REFRESH_INTERVAL);
                }
            };

            // 启动首次定位请求
            handler.post(updateLocationRunnable);
        }
    }

    private void stopLocationUpdates() {
        if (isRunning) {
            isRunning = false;

            // 停止定位
            locationClient.stopLocation();
            // 移除所有待执行的任务
            handler.removeCallbacksAndMessages(null);
        }
    }

    public void addRecord() throws JSONException {
        String formattedDate = getCurrentFormattedDate();
        String heavenlyStemEarthlyBranch = convertToHeavenlyStemEarthlyBranch(formattedDate);

//        使用okhttp库，回调处理，数据库设计
        asyncHttpRequest.sendPostRequest(
                HttpBuild.buildRequestBody(formattedDate, latitude, longitude, heavenlyStemEarthlyBranch),
                HttpBuild.POST_DATA_URL,
                new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.d(TAG, "sever push err");
                        dbHelper.insertLocation(formattedDate,latitude,longitude,heavenlyStemEarthlyBranch,0);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        Log.d(TAG,"sever push suc");
                        dbHelper.insertLocation(formattedDate,latitude,longitude,heavenlyStemEarthlyBranch,1);
                    }
                }
        );

        List<TimePositionDataEntity> list = dbHelper.queryNoPushLocations();

        asyncHttpRequest.sendPostRequest(
                HttpBuild.buildRequestBody(list),
                HttpBuild.POST_DATAS_URL,
                new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.d(TAG, "sever push err");
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        Log.d(TAG,"sever push suc");
                        dbHelper.updateIsPushForObjects(list);
                    }
                }
        );
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
}
