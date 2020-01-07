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
import com.songbao.sampo_b.entity.OLogisticsEntity;
import com.songbao.sampo_b.entity.OProgressEntity;
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
    public static BaseEntity getCustomizeResult(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

        if (StringUtil.notNull(jsonObject, "data")) {
            mainEn.setData(jsonObject.getString("data"));
        }
        return mainEn;
    }

    /**
     * 解析定制列表数据
     */
    public static BaseEntity getCustomizeListData(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

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
    public static BaseEntity getCustomizeDetailData(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

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
                gdEn.setAttribute(note_02.getString("productSpec"));
                gdEn.setColor(note_02.getString("productColor"));
                gdEn.setMaterial(note_02.getString("productMaterials"));
                gdEn.setVeneer(note_02.getString("productFacing"));
                ocEn.setGdEn(gdEn);
            }
            // 效果图
            if (noteNo > 2 && StringUtil.notNull(jsonData, "sketchVO")) {
                JSONObject note_03 = jsonData.getJSONObject("sketchVO");
                ocEn.setNodeTime3(note_03.getString("createTime"));
                if (StringUtil.notNull(note_03, "pics")) {
                    ocEn.setImgList(getStringList(note_03.getString("pics")));
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
            if (noteNo > 7 && StringUtil.notNull(jsonData, "installing")) {
                JSONObject note_08 = jsonData.getJSONObject("installing");
                ocEn.setNodeTime8(note_08.getString("installTime"));
                ocEn.setInstall(note_08.getBoolean("confirm"));
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
    public static BaseEntity getGoodsDetailData(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

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
     * 解析获取商品属性数据
     */
    public static BaseEntity getGoodsAttrData(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

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
    public static BaseEntity getAddressListData(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

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
    public static BaseEntity getCommentGoodsListData(JSONObject jsonObject) throws JSONException {
        BaseEntity mainEn = getCommonKeyValue(jsonObject);

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
    public static BaseEntity getCommentOrderListData(JSONObject jsonObject) throws JSONException {
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
