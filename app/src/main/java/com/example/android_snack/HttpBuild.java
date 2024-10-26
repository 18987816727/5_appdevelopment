package com.example.android_snack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class HttpBuild {
    public static final String POST_DATA_URL="http://test";
    public static final String POST_DATAS_URL="http://test";

    public static String buildRequestBody(
            String formattedDate,
            double latitude,
            double longitude,
            String heavenlyStemEarthlyBranch
    ) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("formattedDate", formattedDate);
        jsonObject.put("latitude", latitude);
        jsonObject.put("longitude", longitude);
        jsonObject.put("heavenlyStemEarthlyBranch", heavenlyStemEarthlyBranch);
        return jsonObject.toString();
    }

    public static String buildRequestBody(
            List<TimePositionDataEntity> list
    ){
        JSONArray jsonArray = new JSONArray();
        for (TimePositionDataEntity timePositionDataEntity : list) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("formattedDate", timePositionDataEntity.getDateTime());
                jsonObject.put("latitude", timePositionDataEntity.getLatitude());
                jsonObject.put("longitude", timePositionDataEntity.getLongitude());
                jsonObject.put("heavenlyStemEarthlyBranch", timePositionDataEntity.getGanZhi());
                jsonArray.put(jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonArray.toString();
    }
}
