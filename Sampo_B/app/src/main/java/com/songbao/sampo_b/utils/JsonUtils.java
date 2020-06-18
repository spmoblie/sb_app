package com.songbao.sampo_b.utils;

import com.songbao.sampo_b.entity.AddressEntity;
import com.songbao.sampo_b.entity.BaseEntity;
import com.songbao.sampo_b.entity.CommentEntity;
import com.songbao.sampo_b.entity.DesignerEntity;
import com.songbao.sampo_b.entity.GoodsEntity;
import com.songbao.sampo_b.entity.GoodsSortEntity;
import com.songbao.sampo_b.entity.MessageEntity;
import com.songbao.sampo_b.entity.OCustomizeEntity;
import com.songbao.sampo_b.entity.PaymentEntity;
import com.songbao.sampo_b.entity.ThemeEntity;
import com.songbao.sampo_b.entity.VersionEntity;
import com.songbao.sampo_b.entity.UserInfoEntity;
import com.songbao.sampo_b.wxapi.WXPayEntryActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class JsonUtils {

    /**
     * 解析公共数据
     */
    private static <T extends BaseEntity> BaseEntity<T> getCommonKeyValue(JSONObject jsonObj) throws JSONException {
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
    public static <T extends BaseEntity> BaseEntity<T> getBaseErrorData(JSONObject jsonObject) throws JSONException {
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
    public static BaseEntity<VersionEntity> checkVersionUpdate(JSONObject jsonObject) throws JSONException {
        BaseEntity<VersionEntity> mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            VersionEntity uvEn = new VersionEntity();
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
     * 解析获取支付订单号
     */
    public static BaseEntity getPayOrderOn(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            mainEn.setOthers(jsonObject.getString("data"));
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

                if (StringUtil.notNull(data, "storeList")) {
                    JSONArray nameArr = data.getJSONArray("storeList");
                    StringBuilder sb_name = new StringBuilder();
                    for (int i = 0; i < nameArr.length(); i++) {
                        JSONObject nameObj = nameArr.getJSONObject(i);
                        sb_name.append(nameObj.getString("storeName"));
                        sb_name.append("_");
                    }
                    if (sb_name.toString().contains("_")) {
                        sb_name.deleteCharAt(sb_name.length() - 1);
                    }
                    userInfo.setStoreStr(sb_name.toString());
                }
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
    public static BaseEntity<UserInfoEntity> getUserDynamic(JSONObject jsonObject) throws JSONException {
        BaseEntity<UserInfoEntity> mainEn = getCommonKeyValue(jsonObject);

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
     * 解析提交定制订单结果
     */
    public static BaseEntity<DesignerEntity> getCustomizeResult(JSONObject jsonObject) throws JSONException {
        BaseEntity<DesignerEntity> mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            mainEn.setOthers(jsonObject.getString("data"));
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
                ArrayList<GoodsEntity> goodsList;
                ArrayList<OCustomizeEntity> lists = new ArrayList<>();
                for (int j = 0; j < data.length(); j++) {
                    JSONObject item = data.getJSONObject(j);
                    childEn = new OCustomizeEntity();
                    childEn.setId(item.getInt("id"));
                    childEn.setStatus(item.getInt("customStatus"));
                    childEn.setOrderNo(item.getString("customCode"));
                    childEn.setNodeTime1(item.getString("createTime"));

                    JSONArray goodsArr = item.getJSONArray("products");
                    goodsList = new ArrayList<>();
                    for (int k = 0; k < goodsArr.length(); k++) {
                        JSONObject goodsObj = goodsArr.getJSONObject(k);
                        gdEn = new GoodsEntity();
                        gdEn.setId(goodsObj.getInt("id"));
                        gdEn.setName(goodsObj.getString("productName"));
                        gdEn.setPicUrl(goodsObj.getString("productPic"));
                        gdEn.setNumber(goodsObj.getInt("buyNum"));
                        gdEn.setOnePrice(goodsObj.getDouble("buyPrice"));
                        gdEn.setTwoPrice(goodsObj.getDouble("buyModifyPrice"));
                        goodsList.add(gdEn);
                    }
                    childEn.setGoodsList(goodsList);

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

            if (StringUtil.notNull(jsonData, "customVO")) {
                JSONObject infoData = jsonData.getJSONObject("customVO");
                ocEn.setId(infoData.getInt("shopId"));
                ocEn.setStatus(infoData.getInt("customStatus"));
                ocEn.setStatusDesc(infoData.getString("customStatusDesc"));
                ocEn.setPriceOne(infoData.getDouble("customPrice"));
                double twoPrice1 = infoData.getDouble("pricingPrice");
                double twoPrice2 = infoData.getDouble("pricedPrice");
                if (twoPrice2 > 0) {
                    ocEn.setPriceTwo(twoPrice2);
                } else {
                    ocEn.setPriceTwo(twoPrice1);
                }
                ocEn.setOrderNo(infoData.getString("customCode"));
                ocEn.setCustomerName(infoData.getString("clientName"));
                ocEn.setCustomerPhone(infoData.getString("clientPhone"));
                ocEn.setBuildName(infoData.getString("propertyName"));
                ocEn.setDealStore(infoData.getString("dealShop"));
                ocEn.setHopeTime(infoData.getString("expectedSpan"));
                ocEn.setTermTime(infoData.getString("preDeliveryDate"));
                ocEn.setNodeTime1(infoData.getString("createTime"));
                ocEn.setNodeTime2(infoData.getString("verifyTime"));
                ocEn.setNodeTime3(infoData.getString("pricedTime"));
                ocEn.setNodeTime4(infoData.getString("deliveryTime"));
                ocEn.setNodeTime5(infoData.getString("finishTime"));
                ocEn.setOrderRemarks(infoData.getString("remark"));
            }

            if (StringUtil.notNull(jsonData, "products")) {
                JSONArray goodsArr = jsonData.getJSONArray("products");
                GoodsEntity gdEn;
                ArrayList<GoodsEntity> goodsList = new ArrayList<>();
                for (int k = 0; k < goodsArr.length(); k++) {
                    JSONObject goodsObj = goodsArr.getJSONObject(k);
                    gdEn = new GoodsEntity();
                    gdEn.setId(goodsObj.getInt("id"));
                    gdEn.setSkuCode(goodsObj.getString("customCode"));
                    // 效果图主图
                    gdEn.setPicUrl(goodsObj.getString("productPic"));
                    // 效果图链接
                    gdEn.setEffectUrl(goodsObj.getString("vcrUrl"));
                    // 商品信息
                    gdEn.setName(goodsObj.getString("productName"));
                    gdEn.setNumber(goodsObj.getInt("buyNum"));
                    gdEn.setOnePrice(goodsObj.getDouble("buyPrice"));
                    gdEn.setTwoPrice(goodsObj.getDouble("buyModifyPrice"));
                    goodsList.add(gdEn);
                }
                ocEn.setGoodsList(goodsList);
            }

            if (StringUtil.notNull(jsonData, "verify")) {
                JSONObject verifyObj = jsonData.getJSONObject("verify");
                if (StringUtil.notNull(verifyObj, "verifyRemark")) {
                    ocEn.setCheckRemarks(verifyObj.getString("verifyRemark"));
                }
                if (StringUtil.notNull(verifyObj, "resultPics")) {
                    ocEn.setImageList(getStringList(verifyObj.getString("resultPics")));
                }
                if (StringUtil.notNull(verifyObj, "resultFiles")) {
                    ocEn.setFilesList(getStringList(verifyObj.getString("resultFiles")));
                }
            }

            if (StringUtil.notNull(jsonData, "pricing")) {
                JSONObject priceObj = jsonData.getJSONObject("pricing");
                if (StringUtil.notNull(priceObj, "verifyRemark")) {
                    ocEn.setPriceRemarks(priceObj.getString("verifyRemark"));
                }
                if (StringUtil.notNull(priceObj, "resultPics")) {
                    ocEn.setImageList(getStringList(priceObj.getString("resultPics")));
                }
            }

            mainEn.setData(ocEn);
        }
        return mainEn;
    }

    /**
     * 解析定制商品详情数据
     */
    public static BaseEntity<GoodsEntity> getCustomizeGoodsData(JSONObject jsonObject) throws JSONException {
        BaseEntity<GoodsEntity> mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            JSONObject jsonData = jsonObject.getJSONObject("data");
            GoodsEntity gdEn = new GoodsEntity();
            // 效果图图片
            gdEn.setImageList(getStringList(jsonData.getString("pics")));
            // 效果图文件
            gdEn.setLabelList(getStringList(jsonData.getString("files")));
            // 效果图链接
            gdEn.setEffectUrl(jsonData.getString("vcrUrl"));
            // 商品信息
            gdEn.setName(jsonData.getString("productName"));
            gdEn.setNumber(jsonData.getInt("buyNum"));
            gdEn.setOnePrice(jsonData.getDouble("buyPrice"));
            gdEn.setTwoPrice(jsonData.getDouble("buyModifyPrice"));
            gdEn.setRemarks(jsonData.getString("remark"));
            mainEn.setData(gdEn);
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
                    childEn.setGoodsCode(item.getString("goodsCode"));
                    childEn.setSkuCode(item.getString("skuCode"));
                    childEn.setPicUrl(item.getString("skuPic"));
                    childEn.setName(item.getString("goodsName"));
                    childEn.setAttribute(item.getString("skuComboName"));
                    childEn.setOnePrice(item.getDouble("price"));

                    if (StringUtil.notNull(item, "kjlCode")) {
                        childEn.setEffectUrl(item.getString("kjlCode"));
                    }
                    // 商品图片集
                    if (StringUtil.notNull(item, "skuPic")) {
                        /*JSONArray images = jsonData.getJSONArray("goodsPics");
                        ArrayList<String> urls = new ArrayList<>();
                        for (int j = 0; j < images.length(); j++) {
                            JSONObject pics = images.getJSONObject(i);
                            urls.add(pics.getString("goodsPic"));
                        }*/
                        ArrayList<String> urls = new ArrayList<>();
                        urls.add(item.getString("skuPic"));
                        childEn.setImageList(urls);
                    }

                    lists.add(childEn);
                }
                mainEn.setLists(lists);
            }
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
            if (StringUtil.notNull(jsonData, "kjlCode")) {
                goodsEn.setEffectUrl(jsonData.getString("kjlCode"));
            }
            mainEn.setData(goodsEn);
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
                    childEn.setHeadUrl(item.getString("customerName"));
                    childEn.setGoodsAttr(item.getString("skuComboName"));
                    childEn.setAddTime(item.getString("evaluateTime"));
                    childEn.setContent(item.getString("evaluateContent"));
                    childEn.setStarNum((float) item.getInt("levels"));
                    childEn.setImg(item.getBoolean("isImg"));

                    if (childEn.isImg()) { //有图
                        childEn.setImgList(getStringList(item.getString("evaluateImages")));
                    }
                    if (StringUtil.notNull(item, "content")) {
                        childEn.setAddContent(item.getString("content"));
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
        return getCommonKeyValue(jsonObject);
    }

}
