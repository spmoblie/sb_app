package com.sbwg.sxb.utils;

import com.sbwg.sxb.entity.BaseEntity;
import com.sbwg.sxb.entity.ThemeEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class JsonUtils {

    /**
     * 解析首页头部数据
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    public static BaseEntity getHomeHead(JSONObject jsonObject) throws JSONException {
        if (jsonObject == null) return null;
        BaseEntity mainEn = new BaseEntity();
        getCommonKeyValue(mainEn, jsonObject);

        JSONObject jsonData = jsonObject.getJSONObject("data");
        if (StringUtil.notNull(jsonData, "banner")) {
            JSONArray datas = jsonData.getJSONArray("banner");
            ThemeEntity childEn;
            List<ThemeEntity> lists = new ArrayList<>();
            for (int j = 0; j < datas.length(); j++) {
                JSONObject item = datas.getJSONObject(j);
                childEn = new ThemeEntity();
                childEn.setId(item.getInt("id"));
                childEn.setTitle(item.getString("name"));
                childEn.setPicUrl(item.getString("url"));
                childEn.setLinkUrl(item.getString("link"));
                lists.add(childEn);
            }
            mainEn.setLists(lists);
        }
        return mainEn;
    }

    /**
     * 解析首页列表数据
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    public static BaseEntity getHomeList(JSONObject jsonObject) throws JSONException {
        if (jsonObject == null) return null;
        BaseEntity mainEn = new BaseEntity();
        getCommonKeyValue(mainEn, jsonObject);

        JSONObject jsonData = jsonObject.getJSONObject("data");
        if (StringUtil.notNull(jsonData, "total")) {
            mainEn.setDataTotal(jsonData.getInt("total"));
        }
        if (StringUtil.notNull(jsonData, "activityList")) {
            JSONArray datas = jsonData.getJSONArray("activityList");
            ThemeEntity childEn;
            List<ThemeEntity> lists = new ArrayList<>();
            for (int j = 0; j < datas.length(); j++) {
                JSONObject item = datas.getJSONObject(j);
                childEn = new ThemeEntity();
                childEn.setId(item.getInt("id"));
                childEn.setTitle(item.getString("title"));
                childEn.setPicUrl(item.getString("picUrl"));
                childEn.setLinkUrl(item.getString("linkUrl"));
                childEn.setUserId(item.getString("adminId"));
                childEn.setUserName(item.getString("userName"));
                childEn.setUserHead(item.getString("avatar"));
                childEn.setSynopsis(item.getString("synopsis"));
                childEn.setDescription(item.getString("description"));
                childEn.setStartTime(analysisTimeString(item.getString("startTime")));
                childEn.setEndTime(analysisTimeString(item.getString("endTime")));
                childEn.setQuantity(item.getInt("quantity"));
                childEn.setPeople(item.getInt("people"));
                childEn.setStatus(item.getInt("status"));
                childEn.setFees(item.getDouble("fee"));
                lists.add(childEn);
            }
            mainEn.setLists(lists);
        }
        return mainEn;
    }

    /**
     * 解析通用数据
     * @param baseEn
     * @param jsonObj
     * @throws JSONException
     */
    private static void getCommonKeyValue(BaseEntity baseEn, JSONObject jsonObj) throws JSONException {
        if (jsonObj.has("errno")) {
            baseEn.setErrno(jsonObj.getInt("errno"));
        }
        if (jsonObj.has("errmsg")) {
            baseEn.setErrmsg(jsonObj.getString("errmsg"));
        }
    }

    /**
     * 解析时间戳格式数据
     * @param timeStr 2019-09-09T09:09:09.000+0000
     * @return 2019-09-09 09:09:09
     */
    private static String analysisTimeString(String timeStr) {
        String time_1;
        String time_2;
        if (timeStr.contains("T") && timeStr.contains(".")) {
            time_1 = timeStr.substring(0, timeStr.indexOf("T"));
            time_2 = timeStr.substring(timeStr.indexOf("T") + 1, timeStr.indexOf("."));
        } else {
            return timeStr;
        }
        return time_1 + " " + time_2;
    }

}
