package com.songbao.sampo.utils;

import com.songbao.sampo.AppConfig;
import com.songbao.sampo.entity.AddressEntity;
import com.songbao.sampo.entity.BaseEntity;
import com.songbao.sampo.entity.CartEntity;
import com.songbao.sampo.entity.CommentEntity;
import com.songbao.sampo.entity.CouponEntity;
import com.songbao.sampo.entity.DesignerEntity;
import com.songbao.sampo.entity.GoodsAttrEntity;
import com.songbao.sampo.entity.GoodsEntity;
import com.songbao.sampo.entity.GoodsSaleEntity;
import com.songbao.sampo.entity.GoodsSortEntity;
import com.songbao.sampo.entity.MessageEntity;
import com.songbao.sampo.entity.OCustomizeEntity;
import com.songbao.sampo.entity.OPurchaseEntity;
import com.songbao.sampo.entity.OptionEntity;
import com.songbao.sampo.entity.PaymentEntity;
import com.songbao.sampo.entity.ThemeEntity;
import com.songbao.sampo.entity.UserInfoEntity;
import com.songbao.sampo.wxapi.WXPayEntryActivity;

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
                    childEn.setThemeId(String.valueOf(item.getInt("id")));
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
                    childEn.setId(item.getInt("activityId"));
                    childEn.setThemeId(String.valueOf(item.getInt("activityId")));
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
                childEn.setThemeId(String.valueOf(item.getInt("id")));
                childEn.setTitle(item.getString("title"));
                //childEn.setPicUrl(item.getString("picUrl"));
                //childEn.setLinkUrl(item.getString("linkUrl"));
                //childEn.setLinkUrl("http://xiaobao.sbwg.cn:8080/description/description.html?activityId=21");
                childEn.setLinkUrl("https://baijiahao.baidu.com/s?id=1626134258413691915&wfr=spider&for=pc");
                childEn.setUserId(item.getString("adminId"));
                childEn.setSuit(item.getString("crowd"));
                childEn.setUserName(item.getString("userName"));
                childEn.setSeries(item.getString("typeValue"));
                childEn.setAddress(item.getString("address"));
                childEn.setQuantity(item.getInt("quantity"));
                childEn.setPeople(item.getInt("people"));
                childEn.setStatus(item.getInt("status"));
                childEn.setThemeType(item.getInt("isReservation"));
                childEn.setFees(item.getDouble("fee"));

                if (StringUtil.notNull(item, "description")) {
                    childEn.setDescription(item.getString("description"));
                }
                if (StringUtil.notNull(item, "timeStr")) {
                    childEn.setDateSlot(item.getString("timeStr"));
                }
                if (StringUtil.notNull(item, "startTimeValue")) {
                    childEn.setStartTime(item.getString("startTimeValue"));
                }
                if (StringUtil.notNull(item, "endTimeValue")) {
                    childEn.setEndTime(item.getString("endTimeValue"));
                }
                if (StringUtil.notNull(item, "reservationDateValue")) {
                    childEn.setReserveDate(item.getString("reservationDateValue"));
                }
                if (StringUtil.notNull(item, "reservationTimeValue")) {
                    childEn.setReserveTime(item.getString("reservationTimeValue"));
                }
                if (StringUtil.notNull(item, "checkValue")) {
                    childEn.setCheckValue(item.getString("checkValue"));
                }
                if (StringUtil.notNull(item, "writeOffStatus")) {
                    childEn.setWriteOffStatus(item.getInt("writeOffStatus"));
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

        if (StringUtil.notNull(jsonObject, "data")) {
            PaymentEntity payEn = new PaymentEntity();
            switch (payType) {
                case WXPayEntryActivity.PAY_WX: //微信支付
                    JSONObject data = jsonObject.getJSONObject("data");
                    payEn.setPrepayid(data.getString("prepayId"));
                    payEn.setNoncestr(data.getString("nonceStr"));
                    payEn.setTimestamp(data.getString("timeStamp"));
                    payEn.setSign(data.getString("sign"));
                    break;
                case WXPayEntryActivity.PAY_ZFB: //支付宝支付
                case WXPayEntryActivity.PAY_UNION: //银联支付
                    payEn.setContent(jsonObject.getString("data"));
                    break;
            }
            mainEn.setData(payEn);
        }

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
            }
        }
        mainEn.setData(userInfo);
        return mainEn;
    }

    /**
     * 解析上传图片结果
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
     * 解析用户动态数据
     */
    public static BaseEntity getUserDynamic(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            if (StringUtil.notNull(jsonData, "messageCount")) {
                mainEn.setDataTotal(jsonData.getInt("messageCount"));
            }
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
            if (StringUtil.notNull(jsonData, "total")) {
                mainEn.setDataTotal(jsonData.getInt("total"));
            }
            if (StringUtil.notNull(jsonData, "dataList")) {
                JSONArray data = jsonData.getJSONArray("dataList");
                MessageEntity childEn;
                List<MessageEntity> lists = new ArrayList<>();
                for (int j = 0; j < data.length(); j++) {
                    JSONObject item = data.getJSONObject(j);
                    childEn = new MessageEntity();
                    childEn.setId(item.getString("id"));
                    childEn.setTitle(item.getString("title"));
                    childEn.setContent(item.getString("text"));
                    childEn.setAddTime(item.getString("addTime"));
                    childEn.setRead(item.getInt("status") == 4 ? true : false);
                    lists.add(childEn);
                }
                mainEn.setLists(lists);
            }
        }
        return mainEn;
    }

    /**
     * 解析设计师数据
     */
    public static BaseEntity getDesignData(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            if (StringUtil.notNull(jsonData, "values")) {
                JSONArray data = jsonData.getJSONArray("values");
                DesignerEntity childEn;
                List<DesignerEntity> lists = new ArrayList<>();
                for (int j = 0; j < data.length(); j++) {
                    JSONObject item = data.getJSONObject(j);
                    childEn = new DesignerEntity();
                    childEn.setImgUrl(item.getString("url"));
                    lists.add(childEn);
                }
                mainEn.setLists(lists);
            }
        }
        return mainEn;
    }

    /**
     * 解析我的购买数据
     */
    public static BaseEntity getMyPurchaseData(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            if (StringUtil.notNull(jsonData, "total")) {
                mainEn.setDataTotal(jsonData.getInt("total"));
            }
            if (StringUtil.notNull(jsonData, "activityList")) {
                JSONArray data = jsonData.getJSONArray("activityList");
                OPurchaseEntity childEn;
                GoodsEntity goodsEn;
                GoodsSaleEntity saleEn;
                List<OPurchaseEntity> lists = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject item = data.getJSONObject(i);
                    childEn = new OPurchaseEntity();
                    int id = i + 1;
                    childEn.setId(id);
                    childEn.setGoodsNum(id);
                    childEn.setTotalPrice(5908);
                    childEn.setAddTime("2019-11-18 18:18");
                    childEn.setStatus(id);

                    List<GoodsEntity> goods = new ArrayList<>();
                    for (int j = 0; j < 2; j++) {
                        goodsEn = new GoodsEntity();
                        int is = id * 10 + j;
                        goodsEn.setId(is);
                        goodsEn.setPicUrl(AppConfig.IMAGE_URL + "design_001.png");
                        goodsEn.setName("松堡王国现代简约彩条双层床");
                        goodsEn.setAttribute("天蓝色；1350*1900");
                        goodsEn.setNumber(is);
                        goodsEn.setPrice(2999);
                        goods.add(goodsEn);
                        goodsEn.setStatus(3);
                    }
                    childEn.setGoodsLists(goods);

                    lists.add(childEn);
                }
                mainEn.setLists(lists);
            }
        }
        return mainEn;
    }

    /**
     * 解析我的定制数据
     */
    public static BaseEntity getMyCustomizeData(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            if (StringUtil.notNull(jsonData, "total")) {
                mainEn.setDataTotal(jsonData.getInt("total"));
            }
            if (StringUtil.notNull(jsonData, "activityList")) {
                JSONArray data = jsonData.getJSONArray("activityList");
                OCustomizeEntity childEn;
                GoodsEntity gdEn;
                DesignerEntity dgEn;
                List<OCustomizeEntity> lists = new ArrayList<>();
                for (int j = 0; j < data.length(); j++) {
                    JSONObject item = data.getJSONObject(j);
                    childEn = new OCustomizeEntity();
                    int id = j + 1;
                    childEn.setId(id);
                    childEn.setStatus(id);
                    childEn.setNodeTime1("2019-12-14 10:18");

                    gdEn = new GoodsEntity();
                    gdEn.setName("松堡王国运动男孩双层床");
                    gdEn.setPicUrl(AppConfig.IMAGE_URL + "design_001.png");
                    childEn.setGdEn(gdEn);

                    dgEn = new DesignerEntity();
                    dgEn.setName("设计师C");
                    dgEn.setPhone("13686436466");
                    childEn.setDgEn(dgEn);

                    lists.add(childEn);
                }
                mainEn.setLists(lists);
            }
        }
        return mainEn;
    }

    /**
     * 解析我的门票数据
     */
    public static BaseEntity getMyTicketsData(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            if (StringUtil.notNull(jsonData, "dataList")) {
                JSONArray data = jsonData.getJSONArray("dataList");
                CouponEntity childEn;
                List<CouponEntity> lists = new ArrayList<>();
                for (int j = 0; j < data.length(); j++) {
                    JSONObject item = data.getJSONObject(j);
                    childEn = new CouponEntity();
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
                    childEn.setThemeId(String.valueOf(item.getInt("activityId")));
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
                        childEn.setWriteOffStatus(item.getInt("status"));
                    }
                    if (StringUtil.notNull(item, "timeValue")) {
                        childEn.setReserveTime(item.getString("timeValue"));
                    }

                    lists.add(childEn);
                }
                mainEn.setLists(lists);
            }
        }
        return mainEn;
    }

    /**
     * 解析分类列表数据
     */
    public static BaseEntity getSortListData(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONArray data = jsonObject.getJSONArray("data");
            GoodsSortEntity childEn;
            List<GoodsSortEntity> lists = new ArrayList<>();
            for (int i = 0; i < data.length(); i++) {
                JSONObject item = data.getJSONObject(i);
                childEn = new GoodsSortEntity();
                childEn.setId(item.getInt("value"));
                childEn.setName(item.getString("label"));
                childEn.setSortCode(item.getString("catCode"));
                lists.add(childEn);

                /*List<GoodsSortEntity> childList = new ArrayList<>();
                for (int j = 0; j < 2; j++) {
                    childEn = new GoodsSortEntity();
                    int ij = id * 10 + j;
                    childEn.setId(ij);
                    childEn.setParentId(id);

                    List<GoodsEntity> goodsList = new ArrayList<>();
                    for (int k = 0; k < 2; k++) {
                        goodsEn = new GoodsEntity();
                        int ik = ij * 10 + k;
                        goodsEn.setId(ik);
                        goodsEn.setPicUrl(AppConfig.IMAGE_URL + "design_001.png");
                        goodsEn.setName("松堡王国现代简约彩条双层床");
                        goodsEn.setAttribute(ik + " mm");
                        goodsEn.setPrice(999999.99);

                        goodsList.add(goodsEn);
                    }
                    childEn.setGoodsLists(goodsList);
                    childList.add(childEn);
                }

                sortEn.setChildLists(childList);
                lists.add(childEn);*/
            }
            mainEn.setLists(lists);
        }
        return mainEn;
    }

    /**
     * 解析分类商品数据
     */
    public static BaseEntity getSortGoodsData(JSONObject jsonObject, int sortId, String sortCode) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject data = jsonObject.getJSONObject("data");
            GoodsSortEntity childEn;
            GoodsEntity goodsEn;
            List<GoodsSortEntity> lists = new ArrayList<>();
            if (StringUtil.notNull(data, "news")) {
                JSONArray news = data.getJSONArray("news");
                childEn = new GoodsSortEntity();
                childEn.setParentId(sortId);
                childEn.setSortCode(sortCode);

                ArrayList<GoodsEntity> newsList = new ArrayList<>();
                for (int i = 0; i < news.length(); i++) {
                    JSONObject newsItem = news.getJSONObject(i);
                    goodsEn = new GoodsEntity();
                    goodsEn.setId(newsItem.getInt("id"));
                    goodsEn.setPicUrl(newsItem.getString("skuPic"));
                    goodsEn.setName(newsItem.getString("goodsName"));
                    goodsEn.setAttribute(newsItem.getString("skuComboName"));
                    goodsEn.setPrice(newsItem.getDouble("price"));

                    newsList.add(goodsEn);
                }
                childEn.setGoodsLists(newsList);
                lists.add(childEn);
            }
            if (StringUtil.notNull(data, "hot")) {
                JSONArray hot = data.getJSONArray("hot");
                childEn = new GoodsSortEntity();
                childEn.setSortCode(sortCode);

                ArrayList<GoodsEntity> hotList = new ArrayList<>();
                for (int i = 0; i < hot.length(); i++) {
                    JSONObject hotItem = hot.getJSONObject(i);
                    goodsEn = new GoodsEntity();
                    goodsEn.setId(hotItem.getInt("id"));
                    goodsEn.setPicUrl(hotItem.getString("skuPic"));
                    goodsEn.setName(hotItem.getString("goodsName"));
                    goodsEn.setAttribute(hotItem.getString("skuComboName"));
                    goodsEn.setPrice(hotItem.getDouble("price"));

                    hotList.add(goodsEn);
                }
                childEn.setGoodsLists(hotList);
                lists.add(childEn);
            }
            mainEn.setLists(lists);
        }
        return mainEn;
    }

    /**
     * 解析商品列表数据
     */
    public static BaseEntity getGoodsListData(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            if (StringUtil.notNull(jsonData, "data")) {
                JSONArray data = jsonData.getJSONArray("data");
                GoodsEntity childEn;
                List<GoodsEntity> lists = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject item = data.getJSONObject(i);
                    childEn = new GoodsEntity();
                    childEn.setId(i + 1);
                    childEn.setGoodsCode(item.getString("skuCode"));
                    childEn.setPicUrl(item.getString("skuPic"));
                    childEn.setName(item.getString("goodsName"));
                    childEn.setAttribute(item.getString("skuComboName"));
                    childEn.setPrice(item.getDouble("price"));

                    lists.add(childEn);
                }
                mainEn.setLists(lists);
            }
        }
        return mainEn;
    }


    /**
     * 解析筛选属性数据
     */
    public static GoodsAttrEntity getScreenAttrData(JSONObject jsonObject) throws JSONException {
        GoodsAttrEntity mainEn = new GoodsAttrEntity();

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            if (StringUtil.notNull(jsonData, "data")) {
                JSONArray data = jsonData.getJSONArray("data");
                GoodsAttrEntity childEn, attrEn;
                ArrayList<GoodsAttrEntity> lists = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject item = data.getJSONObject(i);
                    childEn = new GoodsAttrEntity();
                    childEn.setAttrName(item.getString("name"));

                    JSONArray items = item.getJSONArray("children");
                    ArrayList<GoodsAttrEntity> childLists = new ArrayList<>();
                    for (int j = 0; j < items.length(); j++) {
                        JSONObject attrs = items.getJSONObject(j);
                        attrEn = new GoodsAttrEntity();
                        attrEn.setAttrId(attrs.getInt("id"));
                        attrEn.setAttrName(attrs.getString("attrValues"));
                        childLists.add(attrEn);
                    }
                    attrEn = new GoodsAttrEntity();
                    attrEn.setAttrId(-1);
                    attrEn.setAttrName("全部");
                    attrEn.setSelect(true);
                    childLists.add(attrEn);

                    childEn.setAttrLists(childLists);
                    lists.add(childEn);
                }
                //构建"价格"属性面板
                childEn = new GoodsAttrEntity();
                lists.add(childEn);

                mainEn.setAttrLists(lists);
            }
        }
        return mainEn;
    }

    /**
     * 解析获取商品属性数据
     */
    public static BaseEntity getGoodsAttrData(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            GoodsAttrEntity attrEn = new GoodsAttrEntity();
            attrEn.setGoodsId(1);
            attrEn.setFirstImgUrl(AppConfig.IMAGE_URL + "design_001.png");
            attrEn.setComputePrice(2999.99);
            // 解析商品attr
            attrEn.setAttrLists(getGoodsAttrLists(jsonObject, "message"));
            // 解析商品sku
            //attrEn.setSkuLists(getGoodsSkuLists(jsonObject, "sku"));

            mainEn.setData(attrEn);
        }

        return mainEn;
    }

    /**
     * 解析获取商品Attr
     */
    private static ArrayList<GoodsAttrEntity> getGoodsAttrLists(JSONObject jsonObject, String key) throws JSONException {
        ArrayList<GoodsAttrEntity> attrLists = new ArrayList<>();
        if (!StringUtil.notNull(jsonObject, key)) {
            //JSONArray attr = jsonObject.getJSONArray(key);
            GoodsAttrEntity listEn;
            for (int i = 0; i < 6; i++) {
                //JSONObject as = attr.getJSONObject(i);
                if (i < 2) {
                    listEn = new GoodsAttrEntity();
                    listEn.setAttrId(i+1);
                    if (i == 0) {
                        listEn.setAttrName("颜色");
                        ArrayList<GoodsAttrEntity> asLists = new ArrayList<>();
                        GoodsAttrEntity asEn;
                        //JSONArray list = as.getJSONArray("values");
                        for (int j = 0; j < 3; j++) {
                            //JSONObject ls = list.getJSONObject(j);
                            asEn = new GoodsAttrEntity();
                            asEn.setAttrId(listEn.getAttrId()*10 + j);
                            asEn.setSkuNum(99);
                            if (j == 0) {
                                asEn.setAttrImg(AppConfig.IMAGE_URL + "design_001.png");
                                asEn.setAttrName("天蓝色");
                            } else if (j == 1) {
                                asEn.setAttrImg(AppConfig.IMAGE_URL + "design_004.png");
                                asEn.setAttrName("原木色");
                            } else {
                                asEn.setAttrImg(AppConfig.IMAGE_URL + "design_006.png");
                                asEn.setAttrName("水洗白");
                            }
                            asEn.setAttrPrice(909);
                            asEn.setAttrImg("");
                            asLists.add(asEn);
                        }
                        listEn.setAttrLists(asLists);
                    } else if (i == 1) {
                        listEn.setAttrName("尺寸");
                        ArrayList<GoodsAttrEntity> asLists = new ArrayList<>();
                        GoodsAttrEntity asEn;
                        //JSONArray list = as.getJSONArray("values");
                        for (int j = 0; j < 2; j++) {
                            //JSONObject ls = list.getJSONObject(j);
                            asEn = new GoodsAttrEntity();
                            asEn.setAttrId(listEn.getAttrId()*10 + j);
                            asEn.setSkuNum(99);
                            if (j == 0) {
                                asEn.setAttrName("1200*1900");
                            } else if (j == 1) {
                                asEn.setAttrName("1350*1900");
                            }
                            asEn.setAttrPrice(909);
                            asEn.setAttrImg("");
                            asLists.add(asEn);
                        }
                        listEn.setAttrLists(asLists);
                    }
                    attrLists.add(listEn);
                }
            }
            listEn = new GoodsAttrEntity();
            attrLists.add(listEn);
        }
        return attrLists;
    }

    /**
     * 解析JSON获取商品SKU
     */
    private static ArrayList<GoodsAttrEntity> getGoodsSkuLists(JSONObject jsonObject, String key) throws JSONException {
        ArrayList<GoodsAttrEntity> skuLists = new ArrayList<>();
        if (StringUtil.notNull(jsonObject, key)) {
            JSONArray sku = jsonObject.getJSONArray(key);
            GoodsAttrEntity skuEn;
            for (int i = 0; i < sku.length(); i++) {
                JSONObject ks = sku.getJSONObject(i);
                skuEn = new GoodsAttrEntity();
                skuEn.setSku_key(ks.getString("goods_attr"));
                skuEn.setSku_value(StringUtil.getInteger(ks.getString("product_number")));
                skuLists.add(skuEn);
            }
        }
        return skuLists;
    }

    /**
     * 解析购物车列表数据
     */
    public static BaseEntity getCartListData(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            if (StringUtil.notNull(jsonData, "dataList")) {
                JSONArray data = jsonData.getJSONArray("dataList");
                CartEntity childEn;
                GoodsEntity goodsEn;
                GoodsAttrEntity attrEn;
                List<CartEntity> lists = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    //JSONObject item = data.getJSONObject(i);
                    childEn = new CartEntity();
                    goodsEn = new GoodsEntity();
                    childEn.setId(i + 1);
                    goodsEn.setId(i + 1);
                    goodsEn.setPicUrl(AppConfig.IMAGE_URL + "design_001.png");
                    goodsEn.setName("松堡王国现代简约彩条双层床");
                    goodsEn.setPrice(999.99);

                    attrEn = new GoodsAttrEntity();
                    attrEn.setBuyNum(1);
                    attrEn.setS_id_1(10);
                    attrEn.setS_id_2(20);
                    attrEn.setS_name_1("天蓝色");
                    attrEn.setS_name_2("1200*1900");
                    attrEn.setAttrNameStr("天蓝色; 1200*1900");
                    goodsEn.setAttrEn(attrEn);

                    if (i == 1) {
                        goodsEn.setName("松堡王国现代简约彩条双层床");
                        goodsEn.setPrice(9999);

                        attrEn = new GoodsAttrEntity();
                        attrEn.setBuyNum(20);
                        attrEn.setS_id_1(11);
                        attrEn.setS_id_2(21);
                        attrEn.setS_name_1("原木色");
                        attrEn.setS_name_2("1350*1900");
                        attrEn.setAttrNameStr("原木色; 135*1900");
                        goodsEn.setAttrEn(attrEn);
                    }

                    childEn.setGoodsEn(goodsEn);
                    lists.add(childEn);
                }
                mainEn.setLists(lists);
            }
        }
        return mainEn;
    }

    /**
     * 解析购物车列表数据
     */
    public static BaseEntity getAddressListData(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            if (StringUtil.notNull(jsonData, "dataList")) {
                JSONArray data = jsonData.getJSONArray("dataList");
                AddressEntity childEn;
                List<AddressEntity> lists = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    childEn = new AddressEntity();
                    childEn.setId(i + 1);
                    childEn.setName("张先生");
                    childEn.setPhone("1888888888" + i);
                    childEn.setDistrict("广东省深圳市南山区");
                    childEn.setAddress("粤海街道科发路大冲城市花园5栋16B");
                    lists.add(childEn);
                }
                mainEn.setLists(lists);
            }
        }
        return mainEn;
    }

    /**
     * 解析评价列表数据
     */
    public static BaseEntity getCommentGoodsListData(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            if (StringUtil.notNull(jsonData, "dataList")) {
                JSONArray data = jsonData.getJSONArray("dataList");
                CommentEntity childEn;
                List<CommentEntity> lists = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    childEn = new CommentEntity();
                    childEn.setId("" + i + 1);
                    childEn.setNick("草莓味的冰淇淋");
                    childEn.setGoodsAttr("天蓝色；1350*1900");
                    childEn.setAddTime("2019/12/25");
                    childEn.setContent("很不错，稳固，用料足，没有味道，安装师傅说质量很好，值得购买，还会回购");

                    if (i == 0) {
                        childEn.setStarNum(0);
                        ArrayList<String> imgList = new ArrayList<>();
                        imgList.add(AppConfig.IMAGE_URL + "design_001.png");
                        childEn.setImgList(imgList);
                        childEn.setType(1);

                        childEn.setAddDay(26);
                        childEn.setAddContent("床垫搭配效果很不错，非常满意，床垫搭配效果很不错，非常满意。");
                    } else if (i == 1) {
                        childEn.setStarNum(1);
                        ArrayList<String> imgList = new ArrayList<>();
                        imgList.add(AppConfig.IMAGE_URL + "design_001.png");
                        imgList.add(AppConfig.IMAGE_URL + "design_004.png");
                        childEn.setImgList(imgList);
                        childEn.setType(1);
                    } else if (i == 2) {
                        childEn.setStarNum(2);
                        ArrayList<String> imgList = new ArrayList<>();
                        imgList.add(AppConfig.IMAGE_URL + "design_001.png");
                        imgList.add(AppConfig.IMAGE_URL + "design_004.png");
                        imgList.add(AppConfig.IMAGE_URL + "design_006.png");
                        childEn.setImgList(imgList);
                        childEn.setType(1);
                    } else if (i == 3) {
                        childEn.setStarNum(3);
                        ArrayList<String> imgList = new ArrayList<>();
                        imgList.add(AppConfig.IMAGE_URL + "design_001.png");
                        imgList.add(AppConfig.IMAGE_URL + "design_004.png");
                        imgList.add(AppConfig.IMAGE_URL + "design_006.png");
                        imgList.add(AppConfig.IMAGE_URL + "design_001.png");
                        imgList.add(AppConfig.IMAGE_URL + "design_006.png");
                        childEn.setImgList(imgList);
                        childEn.setType(1);
                    }

                    lists.add(childEn);
                }
                mainEn.setLists(lists);
            }
        }
        return mainEn;
    }

    /**
     * 解析评价列表数据
     */
    public static BaseEntity getCommentOrderListData(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            if (StringUtil.notNull(jsonData, "dataList")) {
                CommentEntity childEn;
                GoodsEntity goodsEn;
                List<CommentEntity> lists = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    childEn = new CommentEntity();
                    childEn.setId("" + i + 1);

                    goodsEn = new GoodsEntity();
                    goodsEn.setId(10*i);
                    goodsEn.setPicUrl(AppConfig.IMAGE_URL + "design_001.png");
                    goodsEn.setName("松堡王国现代简约彩条双层床");
                    goodsEn.setAttribute("天蓝色；1350*1900");
                    childEn.setGoodsEn(goodsEn);

                    childEn.setStarNum(5);
                    childEn.setAddTime("2019/12/25 15:27");
                    childEn.setContent("全实木的床很结实，款式很简约，整体很满意，安装师傅也很负责，半个小时安装好，床垫搭配效果很不错，非常满意。");

                    if (i == 0) {
                        childEn.setStarNum(0);
                        ArrayList<String> imgList = new ArrayList<>();
                        imgList.add(AppConfig.IMAGE_URL + "design_001.png");
                        childEn.setImgList(imgList);
                        childEn.setType(1);

                        childEn.setAddDay(26);
                        childEn.setAddContent("床垫搭配效果很不错，非常满意，床垫搭配效果很不错，非常满意。");
                    } else if (i == 1) {
                        childEn.setAdd(true);
                        childEn.setStarNum(1);
                        ArrayList<String> imgList = new ArrayList<>();
                        imgList.add(AppConfig.IMAGE_URL + "design_001.png");
                        imgList.add(AppConfig.IMAGE_URL + "design_004.png");
                        childEn.setImgList(imgList);
                        childEn.setType(1);
                    } else if (i == 2) {
                        childEn.setStarNum(2);
                        ArrayList<String> imgList = new ArrayList<>();
                        imgList.add(AppConfig.IMAGE_URL + "design_001.png");
                        imgList.add(AppConfig.IMAGE_URL + "design_004.png");
                        imgList.add(AppConfig.IMAGE_URL + "design_006.png");
                        childEn.setImgList(imgList);
                        childEn.setType(1);
                    }

                    lists.add(childEn);
                }
                mainEn.setLists(lists);
            }
        }
        return mainEn;
    }

}
