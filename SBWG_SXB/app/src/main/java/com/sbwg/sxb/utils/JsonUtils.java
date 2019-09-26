package com.sbwg.sxb.utils;

import com.sbwg.sxb.entity.BaseEntity;
import com.sbwg.sxb.entity.DesignEntity;
import com.sbwg.sxb.entity.ThemeEntity;
import com.sbwg.sxb.entity.UserInfoEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class JsonUtils {

    /**
     * 解析通用数据
     */
    private static BaseEntity getCommonKeyValue(JSONObject jsonObj) throws JSONException {
        BaseEntity baseEn = new BaseEntity();
        if (jsonObj.has("errno")) {
            baseEn.setErrno(jsonObj.getInt("errno"));
        }
        if (jsonObj.has("errmsg")) {
            baseEn.setErrmsg(jsonObj.getString("errmsg"));
        }
        return baseEn;
    }

    /**
     * 解析返回的状态码
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    public static BaseEntity getBaseErrorData(JSONObject jsonObject) throws JSONException {
        return getCommonKeyValue(jsonObject);
    }

    /**
     * 解析首页头部数据
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    public static BaseEntity getHomeHead(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            if (StringUtil.notNull(jsonData, "banner")) {
                JSONArray data = jsonData.getJSONArray("banner");
                ThemeEntity childEn;
                List<ThemeEntity> lists = new ArrayList<>();
                for (int j = 0; j < data.length(); j++) {
                    JSONObject item = data.getJSONObject(j);
                    childEn = new ThemeEntity();
                    childEn.setId(item.getInt("id"));
                    childEn.setTitle(item.getString("name"));
                    childEn.setPicUrl(item.getString("url"));
                    childEn.setLinkUrl(item.getString("link"));
                    lists.add(childEn);
                }
                mainEn.setLists(lists);
            }
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
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            if (StringUtil.notNull(jsonData, "total")) {
                mainEn.setDataTotal(jsonData.getInt("total"));
            }
            if (StringUtil.notNull(jsonData, "activityList")) {
                JSONArray data = jsonData.getJSONArray("activityList");
                ThemeEntity childEn;
                List<ThemeEntity> lists = new ArrayList<>();
                for (int j = 0; j < data.length(); j++) {
                    JSONObject item = data.getJSONObject(j);
                    childEn = new ThemeEntity();
                    childEn.setId(item.getInt("id"));
                    childEn.setTitle(item.getString("title"));
                    childEn.setPicUrl(item.getString("picUrl"));
                    childEn.setLinkUrl(item.getString("linkUrl"));
                    //childEn.setUserId(item.getString("adminId"));
                    childEn.setUserName(item.getString("userName"));
                    childEn.setUserHead(item.getString("avatar"));
                    childEn.setSynopsis(item.getString("synopsis"));
                    childEn.setDescription(item.getString("description"));
                    childEn.setAddress(item.getString("address"));
                    childEn.setStartTime(item.getString("startTimeValue"));
                    childEn.setEndTime(item.getString("endTimeValue"));
                    childEn.setQuantity(item.getInt("quantity"));
                    childEn.setPeople(item.getInt("people"));
                    childEn.setStatus(item.getInt("status"));
                    childEn.setFees(item.getDouble("fee"));
                    lists.add(childEn);
                }
                mainEn.setLists(lists);
            }
        }
        return mainEn;
    }

    /**
     * 解析用户资料数据
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    public static BaseEntity getUserInfo(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

        UserInfoEntity userInfo = new UserInfoEntity();
        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            if (StringUtil.notNull(jsonData, "userInfo")) {
                JSONObject data = jsonData.getJSONObject("userInfo");
                userInfo.setUserNick(data.getString("nickname"));
                userInfo.setUserHead(data.getString("avatar"));
                userInfo.setGenderCode(data.getInt("gender"));
                userInfo.setBirthday(data.getString("birthdayValue"));
                userInfo.setUserArea(data.getString("address"));
                userInfo.setUserIntro(data.getString("signature"));
                userInfo.setSignUpId(data.getString("activityValues"));
            }
        }
        mainEn.setData(userInfo);
        return mainEn;
    }

    /**
     * 解析用户资料数据
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    public static BaseEntity getUploadResult(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            mainEn.setOthers(jsonData.getString("url"));
        }
        return mainEn;
    }

    /**
     * 解析我的设计数据
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    public static BaseEntity getDesignData(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            if (StringUtil.notNull(jsonData, "values")) {
                JSONArray data = jsonData.getJSONArray("values");
                DesignEntity childEn;
                List<DesignEntity> lists = new ArrayList<>();
                for (int j = 0; j < data.length(); j++) {
                    JSONObject item = data.getJSONObject(j);
                    childEn = new DesignEntity();
                    childEn.setImgUrl(item.getString("url"));
                    lists.add(childEn);
                }
                mainEn.setLists(lists);
            }
        }
        return mainEn;
    }

    /**
     * 解析我的课程数据
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    public static BaseEntity getMineList(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            if (StringUtil.notNull(jsonData, "total")) {
                mainEn.setDataTotal(jsonData.getInt("total"));
            }
            if (StringUtil.notNull(jsonData, "activityList")) {
                JSONArray data = jsonData.getJSONArray("activityList");
                ThemeEntity childEn;
                UserInfoEntity userEn;
                List<ThemeEntity> lists = new ArrayList<>();
                for (int j = 0; j < data.length(); j++) {
                    JSONObject item = data.getJSONObject(j);
                    childEn = new ThemeEntity();
                    userEn = new UserInfoEntity();
                    childEn.setId(item.getInt("id"));
                    childEn.setTitle(item.getString("title"));
                    childEn.setPicUrl(item.getString("picUrl"));
                    childEn.setLinkUrl(item.getString("linkUrl"));
                    childEn.setUserId(item.getString("adminId"));
                    childEn.setSynopsis(item.getString("synopsis"));
                    childEn.setDescription(item.getString("description"));
                    childEn.setArea(item.getString("areaName"));
                    childEn.setAddress(item.getString("address"));
                    childEn.setStartTime(item.getString("startTime"));
                    childEn.setEndTime(item.getString("endTime"));
                    childEn.setQuantity(item.getInt("quantity"));
                    childEn.setPeople(item.getInt("people"));
                    childEn.setStatus(item.getInt("status"));
                    childEn.setFees(item.getDouble("fee"));

                    userEn.setUserName(item.getString("name"));
                    userEn.setGenderCode(item.getInt("gender"));
                    userEn.setBirthday(item.getString("ageStage"));
                    userEn.setUserPhone(item.getString("mobile"));
                    childEn.setUserData(userEn);

                    lists.add(childEn);
                }
                mainEn.setLists(lists);
            }
        }
        return mainEn;
    }

}
