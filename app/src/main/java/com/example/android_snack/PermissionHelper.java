package com.example.android_snack;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionHelper {
    private static final int PERMISSION_REQUEST_CODE = 100; // 请求码
    private final Activity activity;

    public PermissionHelper(Activity activity) {
        this.activity = activity;
    }

    // 检查并请求权限
    public void checkAndRequestPermissions(String[] permissions) {
        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        // 如果有任何权限没有被授予，则请求这些权限
        if (!permissionsToRequest.isEmpty()) {
//            ActivityCompat.requestPermissions(activity,
//                    permissionsToRequest.toArray(new String[0]), PERMISSION_REQUEST_CODE);
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_CODE);
        } else {
            Toast.makeText(activity, "所有权限已经被授予", Toast.LENGTH_SHORT).show();
        }
    }

    // 处理权限请求的结果
    public void handlePermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                boolean isGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                if (isGranted) {
                    Toast.makeText(activity, permission + " 权限被授予", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, permission + " 权限被拒绝", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

