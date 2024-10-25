package com.example.android_snack;

import android.os.Bundle;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;

public class ShowRecord extends AppCompatActivity {

    private static final String SHARED_PREF_NAME = "user_records";
    private static final String RECORD_COUNT_KEY = "record_count";
    private static final String RECORD_PREFIX = "record_";
    private DBHelper dbHelper;

    /*
    // 生命周期估计是在create后？
    @Override
    protected void onStart() {
        super.onStart();
        if (dbHelper==null){
            dbHelper=new DBHelper(this);
        }
    }*/

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

        if (dbHelper==null){
            dbHelper=new DBHelper(this);
        }

        setContentView(R.layout.activity_show_record);

        // 获取 LinearLayout 容器
        LinearLayout recordContainer = findViewById(R.id.record_container);

        List<TimePositionDataEntity> list = dbHelper.queryAllLocations();

        // 遍历所有记录并添加到 LinearLayout
        for (int i = 0; i < list.toArray().length; i++) {
            TimePositionDataEntity timePositionDataEntity=list.get(i);
            if (timePositionDataEntity!=null) {
                // 将一条记录添加到 LinearLayout 中
                addRecordToView(recordContainer, timePositionDataEntity, i);
            }
        }
    }

    // 将一条记录添加到 LinearLayout 中
    private void addRecordToView(LinearLayout container, TimePositionDataEntity record, final int index) {

        String formattedDate = record.getDateTime();
        String heavenlyStemEarthlyBranch = record.getGanZhi();
        double latitude = record.getLatitude();
        double longitude = record.getLongitude();

        // 创建 LinearLayout 来包含 TextView 和删除按钮
        LinearLayout recordLayout = new LinearLayout(this);
        recordLayout.setOrientation(LinearLayout.HORIZONTAL);
        recordLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        recordLayout.setPadding(8, 8, 8, 8); // 设置内边距

        // 创建 TextView 并设置内容
        TextView textView = new TextView(this);
        textView.setText(String.format(Locale.getDefault(),
                "记录时间: %s\n天干地支: %s\n经度: %.6f\n纬度: %.6f\n",
                formattedDate, heavenlyStemEarthlyBranch, longitude, latitude));
        textView.setTextSize(16); // 设置字体大小
        textView.setTextColor(getResources().getColor(android.R.color.black)); // 设置文字颜色
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        ));


        // 将 TextView 和删除按钮添加到 LinearLayout
        recordLayout.addView(textView);

        // 添加 LinearLayout 到容器
        container.addView(recordLayout);

        // 添加分隔线（可选）
        addDivider(container);

    }

    // 添加分隔线
    private void addDivider(LinearLayout container) {
        View divider = new View(this);
        divider.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1, // 分隔线高度
                1f
        ));
        divider.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        container.addView(divider);
    }

}