package com.songbao.sxb.utils;

import com.songbao.sxb.entity.BaseEntity;
import com.songbao.sxb.entity.DesignEntity;
import com.songbao.sxb.entity.MessageEntity;
import com.songbao.sxb.entity.OptionEntity;
import com.songbao.sxb.entity.ThemeEntity;
import com.songbao.sxb.entity.UserInfoEntity;

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
                    //childEn.setPicUrl(item.getString("picUrl"));
                    childEn.setUserName(item.getString("userName"));
                    childEn.setSeries(item.getString("typeValue"));
                    childEn.setAddTime(item.getString("addTime"));
                    childEn.setStatus(item.getInt("status"));
                    childEn.setThemeType(item.getInt("isReservation"));

                    if (StringUtil.notNull(item, "picUrl")) {
                        childEn.setPicUrls(getImgUrls(item.getString("picUrl")));
                    }
                    lists.add(childEn);
                }
                mainEn.setLists(lists);
            }
        }
        return mainEn;
    }

    /**
     * 解析活动详情数据
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    public static BaseEntity getThemeDetail(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            if (StringUtil.notNull(jsonData, "data")) {
                JSONObject item = jsonData.getJSONObject("data");
                ThemeEntity childEn = new ThemeEntity();
                childEn.setId(item.getInt("id"));
                childEn.setTitle(item.getString("title"));
                //childEn.setPicUrl(item.getString("picUrl"));
                childEn.setLinkUrl(item.getString("linkUrl"));
                childEn.setUserId(item.getString("adminId"));
                childEn.setSuit(item.getString("crowd"));
                childEn.setUserName(item.getString("userName"));
                childEn.setSeries(item.getString("userName"));
                childEn.setSynopsis(item.getString("description"));
                childEn.setAddress(item.getString("address"));
                childEn.setQuantity(item.getInt("quantity"));
                childEn.setPeople(item.getInt("people"));
                childEn.setStatus(item.getInt("status"));
                childEn.setThemeType(item.getInt("isReservation"));
                childEn.setFees(item.getDouble("fee"));

                if (StringUtil.notNull(item, "timeStr")) {
                    childEn.setDateSlot(item.getString("timeStr"));
                }
                if (StringUtil.notNull(item, "startTimeValue")) {
                    childEn.setStartTime(item.getString("startTimeValue"));
                }
                if (StringUtil.notNull(item, "endTimeValue")) {
                    childEn.setEndTime(item.getString("endTimeValue"));
                }
                if (StringUtil.notNull(item, "picUrl")) {
                    childEn.setPicUrls(getImgUrls(item.getString("picUrl")));
                }
                if (StringUtil.notNull(item, "duplicationUrl")) {
                    childEn.setDesUrls(getImgUrls(item.getString("duplicationUrl")));
                }

                mainEn.setData(childEn);
            }
        }
        return mainEn;
    }

    /**
     * 解析获取图片集
     * @param urlStr
     * @return
     * @throws JSONException
     */
    private static List<String> getImgUrls(String urlStr) throws JSONException {
        ArrayList<String> urls = new ArrayList<>();
        JSONArray param = new JSONArray(urlStr);
        for (int i =0; i < param.length(); i++){
            urls.add(param.get(i).toString());
        }
        return urls;
    }

    /**
     * 解析支付信息
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    public static BaseEntity getPayInfo(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            mainEn.setData(jsonObject.getString("data"));
        }
        return mainEn;
    }

    /**
     * 解析获取时间段数据
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    public static BaseEntity getTimeSlot(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONArray data = jsonObject.getJSONArray("data");
            OptionEntity childEn;
            List<OptionEntity> lists = new ArrayList<>();
            for (int j = 0; j < data.length(); j++) {
                JSONObject item = data.getJSONObject(j);
                childEn = new OptionEntity();
                childEn.setId(item.getInt("id"));
                childEn.setTime(item.getString("timeValue"));
                childEn.setState(item.getBoolean("display"));
                childEn.setReserve(item.getBoolean("reservation"));
                childEn.setSelect(item.getBoolean("reservation"));
                lists.add(childEn);
            }
            mainEn.setLists(lists);
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
     * 解析我的消息数据
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    public static BaseEntity getMessageData(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            if (StringUtil.notNull(jsonData, "values")) {
                JSONArray data = jsonData.getJSONArray("values");
                MessageEntity childEn;
                List<MessageEntity> lists = new ArrayList<>();
                for (int j = 0; j < data.length(); j++) {
                    JSONObject item = data.getJSONObject(j);
                    childEn = new MessageEntity();
                    lists.add(childEn);
                }
                mainEn.setLists(lists);
            }
        }
        return mainEn;
    }

    /**
     * 解析我的报名列表
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    public static BaseEntity getMySignUpList(JSONObject jsonObject) throws JSONException {
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
                    childEn.setAddress(item.getString("address"));
                    childEn.setAddTime(item.getString("addTime"));
                    childEn.setStartTime(item.getString("startTime"));
                    childEn.setEndTime(item.getString("endTime"));
                    childEn.setStatus(item.getInt("status"));

                    lists.add(childEn);
                }
                mainEn.setLists(lists);
            }
        }
        return mainEn;
    }

    /**
     * 解析我的预约列表
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    public static BaseEntity getMyReserveList(JSONObject jsonObject) throws JSONException {
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
                    childEn.setSynopsis(item.getString("description"));
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
