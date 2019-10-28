package com.songbao.sxb.utils;

import com.songbao.sxb.entity.BaseEntity;
import com.songbao.sxb.entity.DesignEntity;
import com.songbao.sxb.entity.MessageEntity;
import com.songbao.sxb.entity.OptionEntity;
import com.songbao.sxb.entity.PaymentEntity;
import com.songbao.sxb.entity.ThemeEntity;
import com.songbao.sxb.entity.UserInfoEntity;
import com.songbao.sxb.wxapi.WXPayEntryActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class JsonUtils {

    /**
     * 解析公共数据
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
     */
    public static BaseEntity getBaseErrorData(JSONObject jsonObject) throws JSONException {
        return getCommonKeyValue(jsonObject);
    }

    /**
     * 解析获取字符串集
     */
    private static List<String> getStringList(String urlStr) throws JSONException {
        ArrayList<String> urls = new ArrayList<>();
        JSONArray param = new JSONArray(urlStr);
        for (int i = 0; i < param.length(); i++) {
            urls.add(param.get(i).toString());
        }
        return urls;
    }

    /**
     * 解析首页头部数据
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
                    childEn.setUserName(item.getString("userName"));
                    childEn.setSeries(item.getString("typeValue"));
                    childEn.setAddTime(item.getString("addTime"));
                    childEn.setThemeType(item.getInt("isReservation"));

                    if (StringUtil.notNull(item, "picUrl")) {
                        childEn.setPicUrls(getStringList(item.getString("picUrl")));
                    }
                    if (StringUtil.notNull(item, "status")) {
                        childEn.setStatus(item.getInt("status"));
                    }
                    lists.add(childEn);
                }
                mainEn.setLists(lists);
            }
        }
        return mainEn;
    }

    /**
     * 解析活动、课程详情数据
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
                //childEn.setLinkUrl(item.getString("linkUrl"));
                childEn.setLinkUrl("https://mp.weixin.qq.com/s/uhg0hWDZCvtkyFQUs5FguQ");
                childEn.setUserId(item.getString("adminId"));
                childEn.setSuit(item.getString("crowd"));
                childEn.setUserName(item.getString("userName"));
                childEn.setSeries(item.getString("userName"));
                childEn.setDescription(item.getString("description"));
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
                if (StringUtil.notNull(item, "checkValue")) {
                    childEn.setCheckValue(item.getString("checkValue"));
                }
                if (StringUtil.notNull(item, "picUrl")) {
                    childEn.setPicUrls(getStringList(item.getString("picUrl")));
                }

                mainEn.setData(childEn);
            }
        }
        return mainEn;
    }

    /**
     * 解析获取支付订单号
     */
    public static BaseEntity getPayOrderOn(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            mainEn.setData(jsonObject.getString("data"));
        }
        return mainEn;
    }

    /**
     * 解析获取支付信息
     */
    public static BaseEntity getPayInfo(JSONObject jsonObject, int payType) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

        PaymentEntity payEn = new PaymentEntity();
        switch (payType) {
            case WXPayEntryActivity.PAY_WX: //微信支付
                JSONObject data = jsonObject.getJSONObject("data");
                payEn.setPrepayid(data.getString("prepayid"));
                payEn.setNoncestr(data.getString("noncestr"));
                payEn.setTimestamp(data.getString("timestamp"));
                payEn.setSign(data.getString("sign"));
                break;
            case WXPayEntryActivity.PAY_ZFB: //支付宝支付
            case WXPayEntryActivity.PAY_UNION: //银联支付
                payEn.setContent(jsonObject.getString("data"));
                break;
        }
        mainEn.setData(payEn);

        return mainEn;
    }

    /**
     * 解析获取日期数集
     */
    public static BaseEntity getDateList(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            mainEn.setLists(getStringList(jsonObject.getString("data")));
        }
        return mainEn;
    }

    /**
     * 解析获取时间段数据
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
                childEn.setState(!item.getBoolean("display"));
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
     * 解析上传头像结果
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
     * 解析我的消息数据
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
     * 解析我的设计数据
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
     * 解析我的活动、预约列表数据
     */
    public static BaseEntity getMyThemeList(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            if (StringUtil.notNull(jsonData, "total")) {
                mainEn.setDataTotal(jsonData.getInt("total"));
            }
            if (StringUtil.notNull(jsonData, "dataList")) {
                JSONArray data = jsonData.getJSONArray("dataList");
                ThemeEntity childEn;
                List<ThemeEntity> lists = new ArrayList<>();
                for (int j = 0; j < data.length(); j++) {
                    JSONObject item = data.getJSONObject(j);
                    childEn = new ThemeEntity();
                    childEn.setId(item.getInt("id"));
                    childEn.setTitle(item.getString("title"));
                    childEn.setPicUrl(item.getString("picUrlValue"));
                    childEn.setAddTime(item.getString("addTime"));
                    childEn.setStartTime(item.getString("startTime"));
                    childEn.setEndTime(item.getString("endTime"));

                    if (StringUtil.notNull(item, "address")) {
                        childEn.setAddress(item.getString("address"));
                    }
                    if (StringUtil.notNull(item, "status")) {
                        childEn.setStatus(item.getInt("status"));
                    }
                    if (StringUtil.notNull(item, "reservationDateValue")) {
                        childEn.setReserveDate(item.getString("reservationDateValue"));
                    }
                    if (StringUtil.notNull(item, "reservationTimeValue")) {
                        childEn.setReserveTime(item.getString("reservationTimeValue"));
                    }
                    if (StringUtil.notNull(item, "WriteOffStatus")) {
                        childEn.setWriteOffStatus(item.getInt("WriteOffStatus"));
                    }

                    lists.add(childEn);
                }
                mainEn.setLists(lists);
            }
        }
        return mainEn;
    }

}
