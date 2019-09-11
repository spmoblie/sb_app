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
        if (StringUtil.notNull(jsonData, "activityHead")) {
            JSONArray datas = jsonData.getJSONArray("activityHead");
            ThemeEntity childEn;
            List<ThemeEntity> lists = new ArrayList<>();
            for (int j = 0; j < datas.length(); j++) {
                JSONObject item = datas.getJSONObject(j);
                childEn = new ThemeEntity();
                childEn.setId(item.getInt("id"));
                childEn.setTitle(item.getString("title"));
                childEn.setImgUrl(item.getString("picUrl"));
                childEn.setLink("https://mp.weixin.qq.com/s/p1j-Mv0yAW45tkVvjqLBTA");
                childEn.setUserHead("head_002.jpg");
                childEn.setUserName(item.getString("synopsis"));
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
        if (StringUtil.notNull(jsonData, "activityList")) {
            JSONArray datas = jsonData.getJSONArray("activityList");
            ThemeEntity childEn;
            List<ThemeEntity> lists = new ArrayList<>();
            for (int j = 0; j < datas.length(); j++) {
                JSONObject item = datas.getJSONObject(j);
                childEn = new ThemeEntity();
                childEn.setId(item.getInt("id"));
                childEn.setTitle(item.getString("title"));
                childEn.setImgUrl(item.getString("picUrl"));
                childEn.setLink("https://mp.weixin.qq.com/s/p1j-Mv0yAW45tkVvjqLBTA");
                childEn.setUserHead("head_002.jpg");
                childEn.setUserName(item.getString("synopsis"));
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

}
