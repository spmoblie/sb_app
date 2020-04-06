package com.songbao.sampo_c.utils;

import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.entity.AddressEntity;
import com.songbao.sampo_c.entity.BaseEntity;
import com.songbao.sampo_c.entity.CartEntity;
import com.songbao.sampo_c.entity.CommentEntity;
import com.songbao.sampo_c.entity.CouponEntity;
import com.songbao.sampo_c.entity.DesignerEntity;
import com.songbao.sampo_c.entity.GoodsAttrEntity;
import com.songbao.sampo_c.entity.GoodsEntity;
import com.songbao.sampo_c.entity.GoodsSaleEntity;
import com.songbao.sampo_c.entity.GoodsSortEntity;
import com.songbao.sampo_c.entity.HouseEntity;
import com.songbao.sampo_c.entity.MessageEntity;
import com.songbao.sampo_c.entity.OCustomizeEntity;
import com.songbao.sampo_c.entity.OLogisticsEntity;
import com.songbao.sampo_c.entity.OProgressEntity;
import com.songbao.sampo_c.entity.OPurchaseEntity;
import com.songbao.sampo_c.entity.OptionEntity;
import com.songbao.sampo_c.entity.PaymentEntity;
import com.songbao.sampo_c.entity.StoreEntity;
import com.songbao.sampo_c.entity.ThemeEntity;
import com.songbao.sampo_c.entity.UpdateVersionEntity;
import com.songbao.sampo_c.entity.UserDataEntity;
import com.songbao.sampo_c.entity.UserInfoEntity;
import com.songbao.sampo_c.wxapi.WXPayEntryActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class JsonUtils {

    /**
     * 解析公共数据
     */
    private static <T>BaseEntity<T> getCommonKeyValue(JSONObject jsonObj) throws JSONException {
        BaseEntity<T> baseEn = new BaseEntity<>();
        if (jsonObj.has("errno")) {
            baseEn.setErrNo(jsonObj.getInt("errno"));
        }
        if (jsonObj.has("errmsg")) {
            baseEn.setErrMsg(jsonObj.getString("errmsg"));
        }
        return baseEn;
    }

    /**
     * 解析返回的状态码
     */
    public static <T>BaseEntity<T> getBaseErrorData(JSONObject jsonObject) throws JSONException {
        return getCommonKeyValue(jsonObject);
    }

    /**
     * 解析获取字符串集
     */
    private static ArrayList<String> getStringList(String urlStr) throws JSONException {
        ArrayList<String> urls = new ArrayList<>();
        JSONArray param = new JSONArray(urlStr);
        for (int i = 0; i < param.length(); i++) {
            urls.add(param.get(i).toString());
        }
        return urls;
    }

    /**
     * 检查版本更新
     */
    public static BaseEntity<UpdateVersionEntity> checkVersionUpdate(JSONObject jsonObject) throws JSONException {
        BaseEntity<UpdateVersionEntity> mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            UpdateVersionEntity uvEn = new UpdateVersionEntity();
            uvEn.setDescription(jsonData.getString("description"));
            uvEn.setVersion(jsonData.getString("verison"));
            uvEn.setUrl(jsonData.getString("url"));
            uvEn.setForce(jsonData.getBoolean("forces"));
            mainEn.setData(uvEn);
        }
        return mainEn;
    }

    /**
     * 解析首页头部数据
     */
    public static BaseEntity<ThemeEntity> getHomeHead(JSONObject jsonObject) throws JSONException {
        BaseEntity<ThemeEntity> mainEn = getCommonKeyValue(jsonObject);

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
    public static BaseEntity<ThemeEntity> getHomeList(JSONObject jsonObject) throws JSONException {
        BaseEntity<ThemeEntity> mainEn = getCommonKeyValue(jsonObject);

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
    public static BaseEntity<ThemeEntity> getThemeDetail(JSONObject jsonObject) throws JSONException {
        BaseEntity<ThemeEntity> mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            if (StringUtil.notNull(jsonData, "data")) {
                JSONObject item = jsonData.getJSONObject("data");
                ThemeEntity childEn = new ThemeEntity();
                childEn.setId(item.getInt("id"));
                childEn.setThemeId(String.valueOf(item.getInt("id")));
                childEn.setTitle(item.getString("title"));
                childEn.setLinkUrl(item.getString("linkUrl"));
                //childEn.setLinkUrl("https://baijiahao.baidu.com/s?id=1626134258413691915&wfr=spider&for=pc");
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
                if (StringUtil.notNull(item, "description")) {
                    childEn.setDescription(item.getString("description"));
                }
                if (StringUtil.notNull(item, "descriptionImgList")) {
                    childEn.setDesUrls(getStringList(item.getString("descriptionImgList")));
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
            String data = jsonObject.getString("data");
            if (data.contains("orderCode")) {
                JSONObject dataObj = jsonObject.getJSONObject("data");
                mainEn.setOthers(dataObj.getString("orderCode"));
            } else {
                mainEn.setOthers(data);
            }
        }
        return mainEn;
    }

    /**
     * 解析获取支付信息
     */
    public static BaseEntity<PaymentEntity> getPayInfo(JSONObject jsonObject, int payType) throws JSONException {
        BaseEntity<PaymentEntity> mainEn = getCommonKeyValue(jsonObject);

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
    public static BaseEntity<String> getDateList(JSONObject jsonObject) throws JSONException {
        BaseEntity<String> mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            mainEn.setLists(getStringList(jsonObject.getString("data")));
        }
        return mainEn;
    }

    /**
     * 解析获取时间段数据
     */
    public static BaseEntity<OptionEntity> getTimeSlot(JSONObject jsonObject) throws JSONException {
        BaseEntity<OptionEntity> mainEn = getCommonKeyValue(jsonObject);

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
    public static BaseEntity<UserInfoEntity> getUserInfo(JSONObject jsonObject) throws JSONException {
        BaseEntity<UserInfoEntity> mainEn = getCommonKeyValue(jsonObject);

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
    public static BaseEntity<UserDataEntity> getUserDynamic(JSONObject jsonObject) throws JSONException {
        BaseEntity<UserDataEntity> mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            UserDataEntity dataEn = new UserDataEntity();
            if (StringUtil.notNull(jsonData, "messageCount")) {
                dataEn.setMessageNum(jsonData.getInt("messageCount"));
            }
            if (StringUtil.notNull(jsonData, "getCartNum")) {
                dataEn.setCartGoodsNum(jsonData.getInt("getCartNum"));
            }
            mainEn.setData(dataEn);
        }
        return mainEn;
    }

    /**
     * 解析我的消息数据
     */
    public static BaseEntity<MessageEntity> getMessageData(JSONObject jsonObject) throws JSONException {
        BaseEntity<MessageEntity> mainEn = getCommonKeyValue(jsonObject);

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

                    if (item.getInt("status") == 4) {
                        childEn.setRead(true);
                    }
                    lists.add(childEn);
                }
                mainEn.setLists(lists);
            }
        }
        return mainEn;
    }

    /**
     * 解析全屋案例数据
     */
    public static BaseEntity<HouseEntity> getHouseData(JSONObject jsonObject) throws JSONException {
        BaseEntity<HouseEntity> mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            if (StringUtil.notNull(jsonData, "list")) {
                JSONArray jsonList = jsonData.getJSONArray("list");
                HouseEntity childEn;
                List<HouseEntity> lists = new ArrayList<>();
                for (int j = 0; j < jsonList.length(); j++) {
                    JSONObject item = jsonList.getJSONObject(j);
                    childEn = new HouseEntity();
                    childEn.setId(item.getInt("id"));
                    childEn.setImgUrl(item.getString("renderingUrl"));
                    childEn.setName(item.getString("renderingTitle"));
                    childEn.setInfo(item.getString("renderingIntroduction"));
                    childEn.setWebUrl(item.getString("renderingVcrUrl"));
                    //childEn.setWebUrl("https://yun.kujiale.com/design/3FO4B5NB7E2L/airoaming");
                    lists.add(childEn);
                }
                mainEn.setLists(lists);
            }
        }
        return mainEn;
    }

    /**
     * 解析门店数据
     */
    public static BaseEntity<StoreEntity> getStoreData(JSONObject jsonObject) throws JSONException {
        BaseEntity<StoreEntity> mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONArray jsonData = jsonObject.getJSONArray("data");
            StoreEntity childEn;
            List<StoreEntity> lists = new ArrayList<>();
            for (int j = 0; j < jsonData.length(); j++) {
                JSONObject item = jsonData.getJSONObject(j);
                childEn = new StoreEntity();
                childEn.setId(item.getInt("id"));
                childEn.setImgUrl(item.getString("storePic"));
                childEn.setName(item.getString("storeName"));
                childEn.setArea(item.getString("storeAddress"));
                childEn.setInfo(item.getString("storeDetails"));
                lists.add(childEn);
            }
            mainEn.setLists(lists);
        }
        return mainEn;
    }

    /**
     * 解析设计师数据
     */
    public static BaseEntity<DesignerEntity> getDesignData(JSONObject jsonObject) throws JSONException {
        BaseEntity<DesignerEntity> mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONArray jsonData = jsonObject.getJSONArray("data");
            DesignerEntity childEn;
            List<DesignerEntity> lists = new ArrayList<>();
            for (int j = 0; j < jsonData.length(); j++) {
                JSONObject item = jsonData.getJSONObject(j);
                childEn = new DesignerEntity();
                childEn.setId(item.getInt("id"));
                childEn.setImgUrl(item.getString("avatar"));
                childEn.setName(item.getString("nickname"));
                childEn.setPhone(item.getString("mobile"));
                childEn.setInfo(item.getString("description"));
                lists.add(childEn);
            }
            mainEn.setLists(lists);
        }
        return mainEn;
    }

    /**
     * 解析提交定制订单返回数据
     */
    public static BaseEntity<DesignerEntity> getCustomizeResult(JSONObject jsonObject) throws JSONException {
        BaseEntity<DesignerEntity> mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            mainEn.setOthers(jsonObject.getString("data"));
        }
        return mainEn;
    }

    /**
     * 解析订单列表数据
     */
    public static BaseEntity<OPurchaseEntity> getOrderListData(JSONObject jsonObject) throws JSONException {
        BaseEntity<OPurchaseEntity> mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            if (StringUtil.notNull(jsonData, "total")) {
                mainEn.setDataTotal(jsonData.getInt("total"));
            }
            if (StringUtil.notNull(jsonData, "records")) {
                JSONArray data = jsonData.getJSONArray("records");
                OPurchaseEntity childEn;
                GoodsEntity goodsEn;
                List<OPurchaseEntity> lists = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject item = data.getJSONObject(i);
                    childEn = new OPurchaseEntity();
                    childEn.setId(item.getInt("id"));
                    childEn.setOrderNo(item.getString("orderCode"));
                    childEn.setGoodsNum(item.getInt("goodsCount"));
                    childEn.setTotalPrice(item.getDouble("orderPrice"));
                    childEn.setAddTime(item.getString("createTime"));
                    childEn.setStatus(item.getInt("orderStatus"));

                    JSONArray goodsAry = item.getJSONArray("orderGoods");
                    List<GoodsEntity> goodsList = new ArrayList<>();
                    for (int j = 0; j < goodsAry.length(); j++) {
                        JSONObject goodsObj = goodsAry.getJSONObject(j);
                        goodsEn = new GoodsEntity();
                        goodsEn.setGoodsCode(goodsObj.getString("goodsCode"));
                        goodsEn.setSkuCode(goodsObj.getString("skuCode"));
                        goodsEn.setPicUrl(goodsObj.getString("goodsPic"));
                        goodsEn.setName(goodsObj.getString("goodsName"));
                        goodsEn.setPrice(goodsObj.getDouble("buyPrice"));
                        goodsEn.setNumber(goodsObj.getInt("buyNum"));
                        goodsEn.setAttribute(goodsObj.getString("comboName"));
                        goodsList.add(goodsEn);
                    }
                    childEn.setGoodsLists(goodsList);

                    lists.add(childEn);
                }
                mainEn.setLists(lists);
            }
        }
        return mainEn;
    }

    /**
     * 解析订单详情数据
     */
    public static BaseEntity<OPurchaseEntity> getOrderInfoData(JSONObject jsonObject) throws JSONException {
        BaseEntity<OPurchaseEntity> mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject data = jsonObject.getJSONObject("data");
            OPurchaseEntity opEn = new OPurchaseEntity();

            // 地址
            AddressEntity addEn = new AddressEntity();
            addEn.setId(data.getInt("recieverId"));
            addEn.setName(data.getString("recieverName"));
            addEn.setPhone(data.getString("recieverPhone"));
            addEn.setDistrict(data.getString("addrArea"));
            addEn.setAddress(data.getString("recieverAddr"));
            opEn.setAddEn(addEn);

            // 明细
            opEn.setStatus(data.getInt("orderStatus"));
            opEn.setOrderNo(data.getString("orderCode"));
            opEn.setPayType(data.getString("payType"));
            opEn.setPayNo(data.getString("payNo"));
            opEn.setAddTime(data.getString("createTime"));
            opEn.setPayTime(data.getString("paymentTime"));
            opEn.setSendTime(data.getString("deliverTime"));
            opEn.setDoneTime(data.getString("finishTime"));
            opEn.setGoodsPrice(data.getDouble("goodsPrice"));
            opEn.setFreightPrice(data.getDouble("logisticsPrice"));
            opEn.setDiscountPrice(data.getDouble("discountAmount"));
            opEn.setTotalPrice(data.getDouble("orderPrice"));

            // 商品
            JSONArray goodsAry = data.getJSONArray("orderGoods");
            List<GoodsEntity> goodsList = new ArrayList<>();
            GoodsEntity goodsEn;
            for (int j = 0; j < goodsAry.length(); j++) {
                JSONObject goodsObj = goodsAry.getJSONObject(j);
                goodsEn = new GoodsEntity();
                goodsEn.setGoodsCode(goodsObj.getString("goodsCode"));
                goodsEn.setSkuCode(goodsObj.getString("skuCode"));
                goodsEn.setPicUrl(goodsObj.getString("goodsPic"));
                goodsEn.setName(goodsObj.getString("goodsName"));
                goodsEn.setPrice(goodsObj.getDouble("buyPrice"));
                goodsEn.setNumber(goodsObj.getInt("buyNum"));
                goodsEn.setAttribute(goodsObj.getString("comboName"));
                goodsEn.setSaleStatus(AppConfig.GOODS_SALE_01);
                //goodsEn.setCommentStatus(AppConfig.GOODS_COMM_01);
                goodsEn.setCommentStatus(goodsObj.getInt("isEvaluate"));
                goodsList.add(goodsEn);
            }
            opEn.setGoodsLists(goodsList);
            mainEn.setData(opEn);
        }
        return mainEn;
    }

    /**
     * 解析定制列表数据
     */
    public static BaseEntity<OCustomizeEntity> getCustomizeListData(JSONObject jsonObject) throws JSONException {
        BaseEntity<OCustomizeEntity> mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            if (StringUtil.notNull(jsonData, "total")) {
                mainEn.setDataTotal(jsonData.getInt("total"));
            }
            if (StringUtil.notNull(jsonData, "records")) {
                JSONArray data = jsonData.getJSONArray("records");
                OCustomizeEntity childEn;
                GoodsEntity gdEn;
                DesignerEntity dgEn;
                List<OCustomizeEntity> lists = new ArrayList<>();
                for (int j = 0; j < data.length(); j++) {
                    JSONObject item = data.getJSONObject(j);
                    childEn = new OCustomizeEntity();
                    childEn.setId(item.getInt("id"));
                    childEn.setStatus(item.getInt("bookingStatus"));
                    childEn.setOrderNo(item.getString("bookingCode"));
                    childEn.setNodeTime1(item.getString("createTime"));

                    gdEn = new GoodsEntity();
                    gdEn.setName(item.getString("goodsName"));
                    gdEn.setSkuCode(item.getString("skuCode"));
                    gdEn.setGoodsCode(item.getString("goodsCode"));
                    gdEn.setPicUrl(item.getString("goodsPics"));
                    childEn.setGdEn(gdEn);

                    dgEn = new DesignerEntity();
                    dgEn.setId(item.getInt("designerId"));
                    dgEn.setName(item.getString("designerName"));
                    dgEn.setPhone(item.getString("designerPhone"));
                    childEn.setDgEn(dgEn);

                    lists.add(childEn);
                }
                mainEn.setLists(lists);
            }
        }
        return mainEn;
    }

    /**
     * 解析定制订单详情数据
     */
    public static BaseEntity<OCustomizeEntity> getCustomizeDetailData(JSONObject jsonObject) throws JSONException {
        BaseEntity<OCustomizeEntity> mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            OCustomizeEntity ocEn = new OCustomizeEntity();
            int noteNo = jsonData.getInt("level");
            ocEn.setNodeNo(noteNo);
            if (noteNo > 0 && StringUtil.notNull(jsonData, "bookingVO")) {
                JSONObject note_01 = jsonData.getJSONObject("bookingVO");
                ocEn.setId(note_01.getInt("id"));
                ocEn.setOrderNo(note_01.getString("bookingCode"));
                ocEn.setStatus(note_01.getInt("bookingStatus"));
                ocEn.setStatusDesc(note_01.getString("bookingStatusDesc"));
                ocEn.setNodeTime1(note_01.getString("createTime"));

                //商品信息
                GoodsEntity gdEn = new GoodsEntity();
                gdEn.setName(note_01.getString("goodsName"));
                gdEn.setSkuCode(note_01.getString("skuCode"));
                gdEn.setGoodsCode(note_01.getString("goodsCode"));
                gdEn.setPicUrl(note_01.getString("goodsPics"));
                ocEn.setGdEn(gdEn);

                //设计师
                DesignerEntity dgEn = new DesignerEntity();
                dgEn.setId(note_01.getInt("designerId"));
                dgEn.setName(note_01.getString("designerName"));
                dgEn.setPhone(note_01.getString("designerPhone"));
                ocEn.setDgEn(dgEn);
            }
            // 上门量尺
            if (noteNo > 1 && StringUtil.notNull(jsonData, "scaleInfoVO")) {
                JSONObject note_02 = jsonData.getJSONObject("scaleInfoVO");
                ocEn.setNodeTime2(note_02.getString("scaleTime"));
                GoodsEntity gdEn = ocEn.getGdEn();
                if (gdEn == null) {
                    gdEn = new GoodsEntity();
                }
                if (StringUtil.notNull(note_02, "measurePic")) {
                    String picStr = note_02.getString("measurePic");
                    String[] pics = picStr.split(",");
                    ArrayList<String> picList = new ArrayList<>(pics.length);
                    Collections.addAll(picList, pics);
                    ocEn.setSizeImgList(picList);
                }
                if (StringUtil.notNull(note_02, "layoutPic")) {
                    String picStr = note_02.getString("layoutPic");
                    String[] pics = picStr.split(",");
                    ArrayList<String> picList = new ArrayList<>(pics.length);
                    Collections.addAll(picList, pics);
                    ocEn.setLayoutImgList(picList);
                }
                if (StringUtil.notNull(note_02, "productSpec")) {
                    gdEn.setSize(note_02.getString("productSpec"));
                }
                if (StringUtil.notNull(note_02, "productColor")) {
                    gdEn.setColor(note_02.getString("productColor"));
                }
                if (StringUtil.notNull(note_02, "productStyle")) {
                    gdEn.setStyle(note_02.getString("productStyle"));
                }
                if (StringUtil.notNull(note_02, "remark")) {
                    gdEn.setRemarks(note_02.getString("remark"));
                }
                ocEn.setGdEn(gdEn);
            }
            // 效果图
            if (noteNo > 2 && StringUtil.notNull(jsonData, "sketchVO")) {
                JSONObject note_03 = jsonData.getJSONObject("sketchVO");
                ocEn.setNodeTime3(note_03.getString("createTime"));
                if (StringUtil.notNull(note_03, "pics")) {
                    JSONArray jsonImg = note_03.getJSONArray("pics");
                    ArrayList<String> imgLists = new ArrayList<>();
                    for (int i = 0; i < jsonImg.length(); i++) {
                        JSONObject items = jsonImg.getJSONObject(i);
                        imgLists.add(items.getString("designPic"));
                    }
                    ocEn.setEffectImgList(imgLists);
                }
                ocEn.setDesigns(note_03.getBoolean("confirm"));
            }
            // 支付信息
            if (noteNo > 3 && StringUtil.notNull(jsonData, "payment")) {
                JSONObject note_04 = jsonData.getJSONObject("payment");
                ocEn.setNodeTime4(note_04.getString("paymentTime"));
                ocEn.setPrice(note_04.getDouble("orderPrice"));
                ocEn.setCycle(note_04.getInt("leadtimeSpan"));
                ocEn.setPayment(note_04.getBoolean("confirm"));
            }
            // 收货信息
            if (noteNo > 4 && StringUtil.notNull(jsonData, "orderReciever")) {
                JSONObject note_05 = jsonData.getJSONObject("orderReciever");
                ocEn.setNodeTime5(note_05.getString("recieveTime"));
                AddressEntity adEn = new AddressEntity();
                adEn.setId(note_05.getInt("recieverId"));
                adEn.setName(note_05.getString("recieverName"));
                adEn.setPhone(note_05.getString("recieverPhone"));
                adEn.setAddress(note_05.getString("addrDetail"));
                ocEn.setAdEn(adEn);
            }
            // 生产进度
            if (noteNo > 5 && StringUtil.notNull(jsonData, "tracker")) {
                JSONArray note_06 = jsonData.getJSONArray("tracker");
                OProgressEntity opEn;
                ArrayList<OProgressEntity> opList = new ArrayList<>();
                for (int i = 0; i < note_06.length(); i++) {
                    JSONObject item = note_06.getJSONObject(i);
                    ocEn.setNodeTime6(item.getString("createTime"));

                    opEn = new OProgressEntity();
                    opEn.setId(item.getInt("id"));
                    opEn.setAddTime(item.getString("createTime"));
                    if (StringUtil.notNull(item, "trackerDesc")) {
                        opEn.setContent(item.getString("trackerDesc"));
                    }
                    if (StringUtil.notNull(item, "trackerPics")) {
                        opEn.setType(1);
                        ArrayList<String> imgList = new ArrayList<>();
                        imgList.add(item.getString("trackerPics"));
                        opEn.setImgList(imgList);
                        //opEn.setImgList(getStringList(item.getString("pics")));
                    }
                    opList.add(opEn);
                }
                ocEn.setOpList(opList);
            }
            // 物流发货
            if (noteNo > 6 && StringUtil.notNull(jsonData, "deliver")) {
                JSONObject note_07 = jsonData.getJSONObject("deliver");
                ocEn.setNodeTime7(note_07.getString("deliverTime"));
                JSONArray logs = note_07.getJSONArray("logisticsList");
                OLogisticsEntity olEn;
                ArrayList<OLogisticsEntity> olList = new ArrayList<>();
                for (int i = 0; i < logs.length(); i++) {
                    JSONObject log = logs.getJSONObject(i);
                    olEn = new OLogisticsEntity();
                    olEn.setName(log.getString("logisticsName"));
                    olEn.setNumber(log.getString("logisticsCode"));
                    olEn.setOrderNo(log.getString("orderCode"));
                    olList.add(olEn);
                }
                ocEn.setOlList(olList);
                ocEn.setReceipt(note_07.getBoolean("confirm"));
            }
            // 产品安装
            if (noteNo > 6 && StringUtil.notNull(jsonData, "installing")) {
                JSONObject note_08 = jsonData.getJSONObject("installing");
                ocEn.setNodeTime8(note_08.getString("installTime"));
                ocEn.setInstall(note_08.getBoolean("confirm"));
                ocEn.setInstallName(note_08.getString("installName"));
                ocEn.setInstallCall(note_08.getString("installPhone"));
            }
            // 订单完成
            if (noteNo > 8 && StringUtil.notNull(jsonData, "finish")) {
                JSONObject note_09 = jsonData.getJSONObject("finish");
                ocEn.setNodeTime9(note_09.getString("finishTime"));
            }
            mainEn.setData(ocEn);
        }
        return mainEn;
    }

    /**
     * 解析我的门票数据
     */
    public static BaseEntity<CouponEntity> getMyTicketsData(JSONObject jsonObject) throws JSONException {
        BaseEntity<CouponEntity> mainEn = getCommonKeyValue(jsonObject);

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
    public static BaseEntity<ThemeEntity> getMyThemeList(JSONObject jsonObject) throws JSONException {
        BaseEntity<ThemeEntity> mainEn = getCommonKeyValue(jsonObject);

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
    public static BaseEntity<GoodsSortEntity> getSortListData(JSONObject jsonObject) throws JSONException {
        BaseEntity<GoodsSortEntity> mainEn = getCommonKeyValue(jsonObject);

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
            }
            mainEn.setLists(lists);
        }
        return mainEn;
    }

    /**
     * 解析分类商品数据
     */
    public static BaseEntity<GoodsSortEntity> getSortGoodsData(JSONObject jsonObject, int sortId, String sortCode) throws JSONException {
        BaseEntity<GoodsSortEntity> mainEn = getCommonKeyValue(jsonObject);

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
                childEn.setParentId(sortId);
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
    public static BaseEntity<GoodsEntity> getGoodsListData(JSONObject jsonObject) throws JSONException {
        BaseEntity<GoodsEntity> mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            if (StringUtil.notNull(jsonData, "total")) {
                mainEn.setDataTotal(jsonData.getInt("total"));
            }
            if (StringUtil.notNull(jsonData, "data")) {
                JSONArray data = jsonData.getJSONArray("data");
                GoodsEntity childEn;
                List<GoodsEntity> lists = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject item = data.getJSONObject(i);
                    childEn = new GoodsEntity();
                    childEn.setId(item.getInt("id"));
                    childEn.setSkuCode(item.getString("skuCode"));
                    childEn.setPicUrl(item.getString("skuPic"));
                    childEn.setName(item.getString("goodsName"));
                    childEn.setAttribute(item.getString("skuComboName"));
                    childEn.setPrice(item.getDouble("price"));

                    if (StringUtil.notNull(item, "goodsCode")) {
                        childEn.setGoodsCode(item.getString("goodsCode"));
                    }

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
                    //构建"全部"属性选项
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
     * 解析获取线下商品数据
     */
    public static BaseEntity<GoodsEntity> getGoodsDownData(JSONObject jsonObject) throws JSONException {
        BaseEntity<GoodsEntity> mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            GoodsEntity goodsEn = new GoodsEntity();
            goodsEn.setName(jsonData.getString("goodsName"));

            // 商品图片集
            if (StringUtil.notNull(jsonData, "goodsPics")) {
                JSONArray images = jsonData.getJSONArray("goodsPics");
                ArrayList<String> urls = new ArrayList<>();
                for (int i = 0; i < images.length(); i++) {
                    JSONObject item = images.getJSONObject(i);
                    urls.add(item.getString("goodsPic"));
                }
                goodsEn.setImageList(urls);
            }
            mainEn.setData(goodsEn);
        }
        return mainEn;
    }

    /**
     * 解析获取商品详情数据
     */
    public static BaseEntity<GoodsEntity> getGoodsDetailData(JSONObject jsonObject) throws JSONException {
        BaseEntity<GoodsEntity> mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            GoodsEntity goodsEn = new GoodsEntity();
            goodsEn.setId(jsonData.getInt("skuId"));
            goodsEn.setGoodsCode(jsonData.getString("goodsCode"));
            goodsEn.setName(jsonData.getString("goodsName"));
            goodsEn.setPrice(jsonData.getDouble("price"));

            // 商品标签集
            if (StringUtil.notNull(jsonData, "afterServices")) {
                goodsEn.setLabelList(getStringList(jsonData.getString("afterServices")));
            }

            // 商品已选属性
            String attrIds = jsonData.getString("attrIds");
            GoodsAttrEntity attrEn = new GoodsAttrEntity();
            String[] ids = attrIds.split(",");
            for (int i = 0; i < ids.length; i++) {
                switch (i) {
                    case 0:
                        attrEn.setS_id_1(Integer.valueOf(ids[i]));
                        break;
                    case 1:
                        attrEn.setS_id_2(Integer.valueOf(ids[i]));
                        break;
                    case 2:
                        attrEn.setS_id_3(Integer.valueOf(ids[i]));
                        break;
                }
            }
            String attrNames = jsonData.getString("skuComboName");
            String[] names = attrNames.split(" ");
            for (int i = 0; i < names.length; i++) {
                switch (i) {
                    case 0:
                        attrEn.setS_name_1(names[i]);
                        break;
                    case 1:
                        attrEn.setS_name_2(names[i]);
                        break;
                    case 2:
                        attrEn.setS_name_3(names[i]);
                        break;
                }
            }
            attrEn.setAttrNameStr(attrNames);
            attrEn.setBuyNum(1);
            goodsEn.setAttrEn(attrEn);

            // 商品图片集
            if (StringUtil.notNull(jsonData, "goodsPics")) {
                JSONArray images = jsonData.getJSONArray("goodsPics");
                ArrayList<String> urls = new ArrayList<>();
                for (int i = 0; i < images.length(); i++) {
                    JSONObject item = images.getJSONObject(i);
                    urls.add(item.getString("goodsPic"));
                }
                goodsEn.setImageList(urls);
            }
            // 详情图片集
            if (StringUtil.notNull(jsonData, "goodsDetailImgs")) {
                goodsEn.setDetailList(getStringList(jsonData.getString("goodsDetailImgs")));
            }
            mainEn.setData(goodsEn);
        }
        return mainEn;
    }

    /**
     * 解析获取商品属性数据
     */
    public static BaseEntity<GoodsAttrEntity> getGoodsAttrData(JSONObject jsonObject) throws JSONException {
        BaseEntity<GoodsAttrEntity> mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            GoodsAttrEntity attrEn = new GoodsAttrEntity();
            // 解析商品attr
            attrEn.setAttrLists(getGoodsAttrLists(jsonData, "allSales"));
            // 解析商品sku
            attrEn.setSkuLists(getGoodsSkuLists(jsonData, "skuAttrValue"));

            mainEn.setData(attrEn);
        }

        return mainEn;
    }

    /**
     * 解析获取商品Attr
     */
    private static ArrayList<GoodsAttrEntity> getGoodsAttrLists(JSONObject jsonObject, String key) throws JSONException {
        ArrayList<GoodsAttrEntity> attrLists = new ArrayList<>();
        if (StringUtil.notNull(jsonObject, key)) {
            JSONArray attr = jsonObject.getJSONArray(key);
            GoodsAttrEntity listEn, asEn;
            for (int i = 0; i < attr.length(); i++) {
                JSONObject as = attr.getJSONObject(i);
                listEn = new GoodsAttrEntity();
                listEn.setAttrName(as.getString("name"));

                ArrayList<GoodsAttrEntity> asLists = new ArrayList<>();
                JSONArray list = as.getJSONArray("children");
                for (int j = 0; j < list.length(); j++) {
                    JSONObject ls = list.getJSONObject(j);
                    asEn = new GoodsAttrEntity();
                    asEn.setAttrId(ls.getInt("id"));
                    asEn.setAttrName(ls.getString("attrValue"));
                    asLists.add(asEn);
                }
                listEn.setAttrLists(asLists);
                attrLists.add(listEn);
            }
            //构建"数量"属性面板
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
            GoodsAttrEntity skuEn, skuValue;
            for (int i = 0; i < sku.length(); i++) {
                JSONObject ks = sku.getJSONObject(i);
                skuEn = new GoodsAttrEntity();
                skuEn.setSku_key(ks.getString("attrIds"));

                skuValue = new GoodsAttrEntity();
                skuValue.setAttrId(ks.getInt("id"));
                skuValue.setSkuNum(ks.getInt("stockNum"));
                skuValue.setAttrPrice(ks.getDouble("price"));
                skuValue.setAttrImg(ks.getString("skuPic"));
                skuValue.setSkuCode(ks.getString("skuCode"));
                skuEn.setSku_value(skuValue);
                skuLists.add(skuEn);
            }
        }
        return skuLists;
    }

    /**
     * 解析购物车商品数量
     */
    public static BaseEntity getCartGoodsNum(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            mainEn.setDataTotal(jsonObject.getInt("data"));
        }
        return mainEn;
    }

    /**
     * 解析购物车列表数据
     */
    public static BaseEntity<CartEntity> getCartListData(JSONObject jsonObject) throws JSONException {
        BaseEntity<CartEntity> mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            if (StringUtil.notNull(jsonData, "cartItem")) {
                JSONArray data = jsonData.getJSONArray("cartItem");
                CartEntity childEn;
                GoodsEntity goodsEn;
                GoodsAttrEntity attrEn;
                List<CartEntity> lists = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject item = data.getJSONObject(i);
                    childEn = new CartEntity();
                    childEn.setId(item.getInt("id"));

                    goodsEn = new GoodsEntity();
                    goodsEn.setGoodsCode(item.getString("goodsCode"));
                    goodsEn.setSkuCode(item.getString("skuCode"));
                    goodsEn.setPicUrl(item.getString("goodsPic"));
                    goodsEn.setName(item.getString("goodsName"));
                    goodsEn.setPrice(item.getDouble("buyPrice"));

                    // 商品已选属性
                    String attrIds = item.getString("attrIds");
                    attrEn = new GoodsAttrEntity();
                    String[] ids = attrIds.split(",");
                    for (int j = 0; j < ids.length; j++) {
                        switch (j) {
                            case 0:
                                attrEn.setS_id_1(Integer.valueOf(ids[j]));
                                break;
                            case 1:
                                attrEn.setS_id_2(Integer.valueOf(ids[j]));
                                break;
                            case 2:
                                attrEn.setS_id_3(Integer.valueOf(ids[j]));
                                break;
                        }
                    }
                    String attrNames = item.getString("skuComboName");
                    String[] names = attrNames.split(" ");
                    for (int k = 0; k < names.length; k++) {
                        switch (k) {
                            case 0:
                                attrEn.setS_name_1(names[k]);
                                break;
                            case 1:
                                attrEn.setS_name_2(names[k]);
                                break;
                            case 2:
                                attrEn.setS_name_3(names[k]);
                                break;
                        }
                    }
                    attrEn.setAttrNameStr(attrNames);
                    attrEn.setBuyNum(item.getInt("buyNum"));
                    goodsEn.setAttrEn(attrEn);

                    childEn.setGoodsEn(goodsEn);
                    lists.add(childEn);
                }
                mainEn.setLists(lists);
            }
        }
        return mainEn;
    }

    /**
     * 解析填写订单数据
     */
    public static BaseEntity<OPurchaseEntity> getOrderFillData(JSONObject jsonObject) throws JSONException {
        BaseEntity<OPurchaseEntity> mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            OPurchaseEntity opEn = new OPurchaseEntity();

            // 明细
            opEn.setGoodsPrice(jsonData.getDouble("cartPrice"));
            opEn.setFreightPrice(jsonData.getDouble("logisticsPrice"));
            opEn.setDiscountPrice(jsonData.getDouble("favourablePrice"));
            opEn.setTotalPrice(jsonData.getDouble("countprice"));

            // 商品
            if (StringUtil.notNull(jsonData, "cartItem")) {
                JSONArray data = jsonData.getJSONArray("cartItem");
                GoodsEntity goodsEn;
                List<GoodsEntity> lists = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject item = data.getJSONObject(i);
                    goodsEn = new GoodsEntity();
                    goodsEn.setGoodsCode(item.getString("goodsCode"));
                    goodsEn.setSkuCode(item.getString("skuCode"));
                    goodsEn.setPicUrl(item.getString("goodsPic"));
                    goodsEn.setName(item.getString("goodsName"));
                    goodsEn.setPrice(item.getDouble("buyPrice"));
                    goodsEn.setNumber(item.getInt("buyNum"));
                    goodsEn.setAttribute(item.getString("skuComboName"));
                    lists.add(goodsEn);
                }
                opEn.setGoodsLists(lists);
            }
            mainEn.setData(opEn);
        }
        return mainEn;
    }

    /**
     * 解析地址列表数据
     */
    public static BaseEntity<AddressEntity> getAddressListData(JSONObject jsonObject) throws JSONException {
        BaseEntity<AddressEntity> mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONArray jsonData = jsonObject.getJSONArray("data");
            AddressEntity childEn;
            List<AddressEntity> lists = new ArrayList<>();
            for (int i = 0; i < jsonData.length(); i++) {
                JSONObject item = jsonData.getJSONObject(i);
                childEn = new AddressEntity();
                childEn.setId(item.getInt("consigneeId"));
                childEn.setName(item.getString("consigneeName"));
                childEn.setPhone(item.getString("consigneePhone"));
                childEn.setDistrict(item.getString("addrArea"));
                childEn.setAddress(item.getString("addrDetail"));
                childEn.setDefault(item.getBoolean("isDefault"));
                lists.add(childEn);
            }
            mainEn.setLists(lists);
        }
        return mainEn;
    }

    /**
     * 解析商品评价数据
     */
    public static BaseEntity<CommentEntity> getCommentGoodsListData(JSONObject jsonObject) throws JSONException {
        BaseEntity<CommentEntity> mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            if (StringUtil.notNull(jsonData, "list")) {
                JSONArray data = jsonData.getJSONArray("list");
                CommentEntity childEn;
                List<CommentEntity> lists = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject item = data.getJSONObject(i);
                    childEn = new CommentEntity();
                    childEn.setNumber(jsonData.getInt("total"));
                    childEn.setGoodStar(jsonData.getInt("goodsRate"));

                    childEn.setId(item.getInt("id"));
                    childEn.setNick(item.getString("customerName"));
                    childEn.setHeadUrl(item.getString("avatar"));
                    childEn.setGoodsAttr(item.getString("skuComboName"));
                    childEn.setAddTime(item.getString("evaluateTime"));
                    childEn.setContent(item.getString("evaluateContent"));
                    childEn.setStarNum((float) item.getDouble("levels"));
                    // 是否有图
                    childEn.setImg(item.getBoolean("isImg"));
                    if (childEn.isImg()) {
                        childEn.setImgList(getStringList(item.getString("evaluateImages")));
                    }
                    // 是否追评
                    if (StringUtil.notNull(item, "children")) {
                        JSONArray addAry = item.getJSONArray("children");
                        if (addAry.length() > 0) {
                            JSONObject addObj = addAry.getJSONObject(0);
                            childEn.setAddContent(addObj.getString("content"));
                        }
                    }
                    if (StringUtil.notNull(item, "days")) {
                        childEn.setAddDay(item.getInt("days"));
                    }
                    lists.add(childEn);
                }
                mainEn.setLists(lists);
            }
        }
        return mainEn;
    }

    /**
     * 解析我的评价数据
     */
    public static BaseEntity<CommentEntity> getCommentOrderListData(JSONObject jsonObject) throws JSONException {
        BaseEntity<CommentEntity> mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            if (StringUtil.notNull(jsonData, "list")) {
                JSONArray data = jsonData.getJSONArray("list");
                CommentEntity childEn;
                GoodsEntity goodsEn;
                List<CommentEntity> lists = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject item = data.getJSONObject(i);
                    childEn = new CommentEntity();
                    childEn.setId(item.getInt("id"));

                    goodsEn = new GoodsEntity();
                    goodsEn.setPicUrl(item.getString("mainImg"));
                    goodsEn.setName(item.getString("goodsName"));
                    goodsEn.setAttribute(item.getString("skuComboName"));
                    childEn.setGoodsEn(goodsEn);

                    childEn.setStarNum((float) item.getDouble("levels"));
                    childEn.setAddTime(item.getString("evaluateTime"));
                    childEn.setContent(item.getString("evaluateContent"));
                    // 是否有图
                    childEn.setImg(item.getBoolean("isImg"));
                    if (childEn.isImg()) { //有图
                        childEn.setImgList(getStringList(item.getString("evaluateImages")));
                    }
                    // 可否追评
                    childEn.setAdd(!item.getBoolean("isEvaluate"));
                    if (StringUtil.notNull(item, "children")) {
                        JSONArray addAry = item.getJSONArray("children");
                        if (addAry.length() > 0) {
                            JSONObject addObj = addAry.getJSONObject(0);
                            childEn.setAddContent(addObj.getString("content"));
                        }
                    }
                    if (StringUtil.notNull(item, "days")) {
                        childEn.setAddDay(item.getInt("days"));
                    }
                    lists.add(childEn);
                }
                mainEn.setLists(lists);
            }
        }
        return mainEn;
    }

    /**
     * 解析查询评价数据
     */
    public static BaseEntity<CommentEntity> getCommentInfoData(JSONObject jsonObject) throws JSONException {
        BaseEntity<CommentEntity> mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            CommentEntity childEn = new CommentEntity();
            childEn.setId(jsonData.getInt("id"));
            childEn.setOrderNo(jsonData.getString("orderNo"));
            childEn.setGoodsAttr(jsonData.getString("skuComboName"));
            childEn.setAddTime(jsonData.getString("evaluateTime"));
            childEn.setContent(jsonData.getString("evaluateContent"));
            childEn.setStarNum((float) jsonData.getDouble("levels"));
            // 是否有图
            childEn.setImg(jsonData.getBoolean("isImg"));
            if (childEn.isImg()) {
                childEn.setImgList(getStringList(jsonData.getString("evaluateImages")));
            }
            mainEn.setData(childEn);
        }
        return mainEn;
    }

    /**
     * 解析商品售后数据
     */
    public static BaseEntity<GoodsSaleEntity> getGoodsSaleData(JSONObject jsonObject) throws JSONException {
        BaseEntity<GoodsSaleEntity> mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            GoodsSaleEntity saleEn = new GoodsSaleEntity();
            saleEn.setSaleType(2);
            saleEn.setSaleStatus(7);
            saleEn.setSaleReason("买的太大了，需要换小的一款");
            //saleEn.setExpressNo("NS56468416489456");
            mainEn.setData(saleEn);
        }
        return mainEn;
    }


    /**
     * 解析商品退款详情
     */
    public static BaseEntity<GoodsSaleEntity> getRefundDetailData(JSONObject jsonObject) throws JSONException {
        BaseEntity<GoodsSaleEntity> mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            GoodsSaleEntity saleEn = new GoodsSaleEntity();
            saleEn.setSaleStatus(10);
            saleEn.setRefundPrice(2999.99);
            saleEn.setAddTime("2019-12-21 10:28:28");
            saleEn.setEndTime("2019-12-26 18:18:18");
            saleEn.setRefundNo("NS8888888188888888818888888881");

            mainEn.setData(saleEn);
        }
        return mainEn;
    }

}
