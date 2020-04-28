package com.songbao.sampo_b.utils;

import com.songbao.sampo_b.entity.AddressEntity;
import com.songbao.sampo_b.entity.BaseEntity;
import com.songbao.sampo_b.entity.CommentEntity;
import com.songbao.sampo_b.entity.DesignerEntity;
import com.songbao.sampo_b.entity.GoodsAttrEntity;
import com.songbao.sampo_b.entity.GoodsEntity;
import com.songbao.sampo_b.entity.GoodsSortEntity;
import com.songbao.sampo_b.entity.MessageEntity;
import com.songbao.sampo_b.entity.OCustomizeEntity;
import com.songbao.sampo_b.entity.PaymentEntity;
import com.songbao.sampo_b.entity.ThemeEntity;
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
                List<OCustomizeEntity> lists = new ArrayList<>();
                for (int j = 0; j < data.length(); j++) {
                    JSONObject item = data.getJSONObject(j);
                    childEn = new OCustomizeEntity();
                    childEn.setId(item.getInt("id"));
                    childEn.setStatus(item.getInt("bookingStatus"));
                    childEn.setOrderNo(item.getString("bookingCode"));
                    childEn.setNodeTime1(item.getString("createTime"));

                    lists.add(childEn);
                }
                mainEn.setLists(lists);
            }
            if (StringUtil.notNull(jsonData, "records")) {
                OCustomizeEntity childEn;
                GoodsEntity gdEn;
                ArrayList<GoodsEntity> goodsList;
                ArrayList<OCustomizeEntity> lists = new ArrayList<>();
                for (int j = 0; j < 7; j++) {
                    childEn = new OCustomizeEntity();
                    childEn.setId(j);
                    switch (j) {
                        case 0:
                            childEn.setStatus(101);
                            break;
                        case 1:
                            childEn.setStatus(201);
                            break;
                        case 2:
                            childEn.setStatus(301);
                            break;
                        case 3:
                            childEn.setStatus(401);
                            break;
                        case 4:
                            childEn.setStatus(800);
                            break;
                        case 5:
                            childEn.setStatus(102);
                            break;
                        case 6:
                            childEn.setStatus(103);
                            break;
                    }
                    childEn.setOrderNo("bookingCode");
                    childEn.setNodeTime1("2020-04-27 14:18:18");

                    goodsList = new ArrayList<>();
                    for (int k = 0; k < 2; k++) {
                        gdEn = new GoodsEntity();
                        gdEn.setName("goodsName");
                        gdEn.setPicUrl("goodsPics");
                        gdEn.setNumber(k + 1);
                        gdEn.setPrice(9000);
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

            ocEn.setId(1);
            ocEn.setStatus(101);

            GoodsEntity gdEn;
            ArrayList<GoodsEntity> goodsList = new ArrayList<>();
            for (int k = 0; k < 2; k++) {
                gdEn = new GoodsEntity();
                gdEn.setName("goodsName");
                gdEn.setPicUrl("goodsPics");
                gdEn.setNumber(k + 1);
                gdEn.setPrice(9000);
                goodsList.add(gdEn);
            }
            ocEn.setGoodsList(goodsList);

            ocEn.setPriceOne(18888);
            ocEn.setPriceTwo(19999);
            ocEn.setOrderNo("88888888888");
            ocEn.setTermTime("2020-05-20");
            ocEn.setNodeTime1("2020-05-20 18:18:18");
            ocEn.setNodeTime2("2020-05-20 18:18:18");
            ocEn.setNodeTime3("2020-05-20 18:18:18");
            //ocEn.setNodeTime4("2020-05-20 18:18:18");
            //ocEn.setNodeTime5("2020-05-20 18:18:18");
            ocEn.setOrderRemarks("订单备注内容订单备注内容订单备注内容订单备注内容订单备注内容");

            ocEn.setPriceRemarks("核价备注内容");
            ArrayList<String> imageList = new ArrayList<>();
            imageList.add("http://xiaobao.sbwg.cn:9090/app/files/fetch/jvt9mc9vspsp2wufiaq9.png");
            //imageList.add("http://xiaobao.sbwg.cn:9090/app/files/fetch/jvt9mc9vspsp2wufiaq9.png");
            //imageList.add("http://xiaobao.sbwg.cn:9090/app/files/fetch/jvt9mc9vspsp2wufiaq9.png");
            //imageList.add("http://xiaobao.sbwg.cn:9090/app/files/fetch/jvt9mc9vspsp2wufiaq9.png");
            //imageList.add("http://xiaobao.sbwg.cn:9090/app/files/fetch/jvt9mc9vspsp2wufiaq9.png");
            ocEn.setImageList(imageList);

            ocEn.setCheckRemarks("审核备注内容");
            ArrayList<String> filesList = new ArrayList<>();
            //filesList.add("skdasjfajfljllsfjaldjlkjsdlfjklfjkfjlkfjljdflkfjampo_13566.pdf");
            filesList.add("sampo_13566.pdf");
            //filesList.add("skdasjfajfljllsfjaldjlkjsdlfjklfjkfjlkfjljdflkfjampo_13566.pdf");
            ocEn.setFilesList(filesList);

            mainEn.setData(ocEn);
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
                    childEn.setPrice(item.getDouble("price"));

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
                    childEn.setEffectUrl("https://yun.kujiale.com/design/3FO4B5NB7E2L/airoaming");

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
            goodsEn.setEffectUrl("https://yun.kujiale.com/design/3FO4B5NB7E2L/airoaming");
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

    /**
     * 解析商品售后数据
     */
    public static BaseEntity getGoodsSaleData(JSONObject jsonObject) throws JSONException {
        return getCommonKeyValue(jsonObject);
    }

    /**
     * 解析商品退款详情
     */
    public static BaseEntity getRefundDetailData(JSONObject jsonObject) throws JSONException {
        return getCommonKeyValue(jsonObject);
    }

}
