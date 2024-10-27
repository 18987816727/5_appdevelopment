package com.example.android_snack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class HttpBuild {
    public static final String POST_DATA_URL="http://116.62.44.106:8080/data";
    public static final String POST_DATAS_URL="http://116.62.44.106:8080/datas";

    public static String buildRequestBody(
            String formattedDate,
            double latitude,
            double longitude,
            String heavenlyStemEarthlyBranch
    ) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("colDate", formattedDate);
        jsonObject.put("latitude", latitude);
        jsonObject.put("longitude", longitude);
        jsonObject.put("ganZhi", heavenlyStemEarthlyBranch);
        return jsonObject.toString();
    }

    public static String buildRequestBody(
            List<TimePositionDataEntity> list
    ){
        JSONArray jsonArray = new JSONArray();
        for (TimePositionDataEntity timePositionDataEntity : list) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("colDate", timePositionDataEntity.getDateTime());
                jsonObject.put("latitude", timePositionDataEntity.getLatitude());
                jsonObject.put("longitude", timePositionDataEntity.getLongitude());
                jsonObject.put("ganZhi", timePositionDataEntity.getGanZhi());
                jsonArray.put(jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonArray.toString();
    }
}
