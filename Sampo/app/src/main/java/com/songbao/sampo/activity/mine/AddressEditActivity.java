package com.songbao.sampo.activity.mine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.district.selector.wheel.adapter.ArrayWheelAdapter;
import com.district.selector.wheel.model.CityModel;
import com.district.selector.wheel.model.DistrictModel;
import com.district.selector.wheel.model.ProvinceModel;
import com.district.selector.wheel.service.XmlParserHandler;
import com.district.selector.wheel.widget.OnWheelChangedListener;
import com.district.selector.wheel.widget.WheelView;
import com.songbao.sampo.AppApplication;
import com.songbao.sampo.AppConfig;
import com.songbao.sampo.R;
import com.songbao.sampo.activity.BaseActivity;
import com.songbao.sampo.entity.AddressEntity;
import com.songbao.sampo.utils.CommonTools;
import com.songbao.sampo.utils.ExceptionUtil;
import com.songbao.sampo.utils.LogUtil;
import com.songbao.sampo.utils.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import butterknife.BindView;

@SuppressLint("UseSparseArrays")
public class AddressEditActivity extends BaseActivity implements OnClickListener, OnWheelChangedListener {

    String TAG = AddressEditActivity.class.getSimpleName();

    @BindView(R.id.address_edit_et_name)
    EditText et_name;

    @BindView(R.id.address_edit_et_phone)
    EditText et_phone;

    @BindView(R.id.address_edit_et_detail)
    EditText et_detail;

    @BindView(R.id.address_edit_tv_area_show)
    TextView tv_area;

    @BindView(R.id.address_edit_tv_default)
    TextView tv_default;

    @BindView(R.id.address_edit_iv_default)
    ImageView iv_default;

    @BindView(R.id.address_edit_tv_save)
    TextView tv_save;

    @BindView(R.id.address_edit_wheel_main)
    LinearLayout wheel_main;

    @BindView(R.id.address_edit_wheel_finish)
    View wheel_finish;

    @BindView(R.id.address_edit_wheel_dismiss)
    RelativeLayout wheel_dismiss;

    @BindView(R.id.wheel_province)
    WheelView wheel_province;

    @BindView(R.id.wheel_city)
    WheelView wheel_city;

    @BindView(R.id.wheel_district)
    WheelView wheel_district;

    @BindView(R.id.address_edit_wheel_tv_confirm)
    TextView tv_confirm;

    private AddressEntity data;
    private int addressId = 0;
    private boolean isUpdate;
    private boolean isDefault;
    private String nameStr, phoneStr, areaStr, addressStr;

    private String mProvinceName; //当前省名称
    private String mCityName; //当前市名称
    private String mDistrictName; //当前区名称
    private String[] mProvinceData; //所有省数集
    private Map<String, String[]> mCityDataMap = new HashMap<>(); //key - 省 value - 市
    private Map<String, String[]> mDistrictDataMap = new HashMap<>(); //key - 市 values - 区
    private Map<String, String> mZCodeDataMap = new HashMap<>(); //key - 区 values - 邮编

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_edit);

        data = (AddressEntity) getIntent().getSerializableExtra(AppConfig.PAGE_DATA);

        initView();
    }

    private void initView() {
        setTitle("编辑收货地址");
        setRightViewText(getString(R.string.delete));

        if (data != null) {
            et_name.setText(data.getName());
            et_phone.setText(data.getPhone());
            tv_area.setText(data.getDistrict());
            et_detail.setText(data.getAddress());
            addressId = data.getId();
        }
        initViewListener();
    }

    private void initViewListener() {
        tv_default.setOnClickListener(this);
        iv_default.setOnClickListener(this);
        tv_area.setOnClickListener(this);
        tv_save.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
        wheel_finish.setOnClickListener(this);
        wheel_dismiss.setOnClickListener(this);
        wheel_province.addChangingListener(this);
        wheel_city.addChangingListener(this);
        wheel_district.addChangingListener(this);
        setUpData();
    }

    @Override
    public void OnListenerRight() {
        postDeleteAddress();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.address_edit_tv_default:
            case R.id.address_edit_iv_default:
                isDefault = !isDefault;
                iv_default.setSelected(isDefault);
                break;
            case R.id.address_edit_tv_save:
                if (checkData()) {
                    postEditAddress();
                }
                break;
            case R.id.address_edit_tv_area_show: //选择省、市、区
                wheel_main.setVisibility(View.VISIBLE);
                break;
            case R.id.address_edit_wheel_tv_confirm:
                wheel_main.setVisibility(View.GONE);
                if (mProvinceName.isEmpty() || mCityName.isEmpty() || mDistrictName.isEmpty()) {
                    updateCities();
                }
                tv_area.setText(mProvinceName + mCityName + mDistrictName);
                break;
            case R.id.address_edit_wheel_finish:
            case R.id.address_edit_wheel_dismiss:
                wheel_main.setVisibility(View.GONE);
                break;
        }
    }

    private boolean checkData() {
        // 判定姓名
        nameStr = et_name.getText().toString();
        if (StringUtil.isNull(nameStr)) {
            CommonTools.showToast(getString(R.string.address_error_name_empty));
            return false;
        }
        // 判定手机号
        phoneStr = et_phone.getText().toString();
        if (StringUtil.isNull(phoneStr)) {
            CommonTools.showToast(getString(R.string.address_error_phone_empty));
            return false;
        }
        // 判定所在地区
        areaStr = tv_area.getText().toString();
        if (StringUtil.isNull(phoneStr)) {
            CommonTools.showToast(getString(R.string.address_error_area_empty));
            return false;
        }
        // 判定详细地址
        addressStr = et_detail.getText().toString();
        if (StringUtil.isNull(addressStr)) {
            CommonTools.showToast(getString(R.string.address_error_address_empty));
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        LogUtil.i(TAG, "onResume");
        // 页面开始
        AppApplication.onPageStart(this, TAG);

        super.onResume();
    }

    @Override
    protected void onPause() {
        LogUtil.i(TAG, "onPause");
        // 页面结束
        AppApplication.onPageEnd(this, TAG);

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        LogUtil.i(TAG, "onDestroy");

        super.onDestroy();
    }

    @Override
    public void finish() {
        if (isUpdate) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(AppConfig.PAGE_DATA, isUpdate);
            setResult(RESULT_OK, returnIntent);
        }
        super.finish();
    }

    @SuppressWarnings("unused")
    private void setUpData() {
        initProvinceDatas();
        ArrayWheelAdapter awp_adapter = new ArrayWheelAdapter<>(mContext, mProvinceData);
        awp_adapter.setTextColor(getResources().getColor(R.color.shows_text_color));
        wheel_province.setViewAdapter(awp_adapter);
        // 设置可见条目数量
        wheel_province.setVisibleItems(7);
        wheel_city.setVisibleItems(7);
        wheel_district.setVisibleItems(7);
        updateCities();
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities() {
        // 获取省
        int pCurrent = wheel_province.getCurrentItem();
        if (mProvinceData != null && pCurrent >= 0 && pCurrent < mProvinceData.length) {
            mProvinceName = mProvinceData[pCurrent];
            String[] cities = mCityDataMap.get(mProvinceName);
            if (cities == null) {
                cities = new String[]{""};
            }
            ArrayWheelAdapter awc_adapter = new ArrayWheelAdapter<>(mContext, cities);
            awc_adapter.setTextColor(getResources().getColor(R.color.shows_text_color));
            wheel_city.setViewAdapter(awc_adapter);
            wheel_city.setCurrentItem(0);
            updateAreas();
        }
    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas() {
        // 获取市
        int pCurrent = wheel_city.getCurrentItem();
        String[] city = mCityDataMap.get(mProvinceName);
        if (city != null && pCurrent >= 0 && pCurrent < city.length) {
            mCityName = city[pCurrent];
            String[] areas = mDistrictDataMap.get(mCityName);
            if (areas == null) {
                areas = new String[]{""};
            }
            ArrayWheelAdapter awd_adapter = new ArrayWheelAdapter<>(mContext, areas);
            awd_adapter.setTextColor(getResources().getColor(R.color.shows_text_color));
            wheel_district.setViewAdapter(awd_adapter);
            wheel_district.setCurrentItem(0);
            // 获取区
            int dCurrent = wheel_district.getCurrentItem();
            updateDistrict(dCurrent);
        }
    }

    /**
     * 获取区WheelView的信息
     */
    private void updateDistrict(int dCurrent) {
        String[] districts = mDistrictDataMap.get(mCityName);
        if (districts != null && dCurrent >= 0 && dCurrent < districts.length) {
            mDistrictName = districts[dCurrent];
        }
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == wheel_province) {
            updateCities();
        } else if (wheel == wheel_city) {
            updateAreas();
        } else if (wheel == wheel_district) {
            updateDistrict(newValue);
        }
    }

    /**
     * 解析省市区的XML数据
     */
    protected void initProvinceDatas() {
        InputStream input = null;
        List<ProvinceModel> provinceList;
        AssetManager asset = getAssets();
        try {
            input = asset.open("province_data.xml");
            // 创建一个解析xml的工厂对象
            SAXParserFactory spf = SAXParserFactory.newInstance();
            // 解析xml
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            // 获取解析出来的数据
            provinceList = handler.getDataList();
            // 初始化默认选中的省、市、区
            if (provinceList != null && !provinceList.isEmpty()) {
                mProvinceName = provinceList.get(0).getName();
                List<CityModel> cityList = provinceList.get(0).getCityList();
                if (cityList != null && !cityList.isEmpty()) {
                    mCityName = cityList.get(0).getName();
                    List<DistrictModel> districtList = cityList.get(0)
                            .getDistrictList();
                    mDistrictName = districtList.get(0).getName();
                }
            }
            mProvinceData = new String[provinceList.size()];
            for (int i = 0; i < provinceList.size(); i++) {
                // 遍历所有省的数据
                mProvinceData[i] = provinceList.get(i).getName();
                List<CityModel> cityList = provinceList.get(i).getCityList();
                String[] cityNames = new String[cityList.size()];
                for (int j = 0; j < cityList.size(); j++) {
                    // 遍历省下面的所有市的数据
                    cityNames[j] = cityList.get(j).getName();
                    List<DistrictModel> districtList = cityList.get(j)
                            .getDistrictList();
                    String[] districtNameArray = new String[districtList
                            .size()];
                    DistrictModel[] districtArray = new DistrictModel[districtList
                            .size()];
                    for (int k = 0; k < districtList.size(); k++) {
                        // 遍历市下面所有区/县的数据
                        DistrictModel districtModel = new DistrictModel(
                                districtList.get(k).getName(), districtList
                                .get(k).getZipcode());
                        // 区/县对于的邮编，保存到mZCodeDataMap
                        mZCodeDataMap.put(districtList.get(k).getName(),
                                districtList.get(k).getZipcode());
                        districtArray[k] = districtModel;
                        districtNameArray[k] = districtModel.getName();
                    }
                    // 市-区/县的数据，保存到mDistrictDataMap
                    mDistrictDataMap.put(cityNames[j], districtNameArray);
                }
                // 省-市的数据，保存到mCityDataMap
                mCityDataMap.put(provinceList.get(i).getName(), cityNames);
            }
        } catch (Exception e) {
            ExceptionUtil.handle(e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    ExceptionUtil.handle(e);
                }
            }
        }
    }

    /**
     * 提交删除收货地址
     */
    private void postDeleteAddress() {
        isUpdate = true;
        CommonTools.showToast("删除成功");
        finish();
    }

    /**
     * 提交修改收货地址
     */
    private void postEditAddress() {
        if (isDefault) {
            userManager.saveDefaultAddressId(addressId);
        }
        isUpdate = true;
        CommonTools.showToast("保存成功");
        finish();
    }

}
