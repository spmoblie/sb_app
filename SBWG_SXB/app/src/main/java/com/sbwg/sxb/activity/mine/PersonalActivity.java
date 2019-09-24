package com.sbwg.sxb.activity.mine;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.AppConfig;
import com.sbwg.sxb.R;
import com.sbwg.sxb.activity.BaseActivity;
import com.sbwg.sxb.activity.common.ClipImageCircularActivity;
import com.sbwg.sxb.activity.common.ClipPhotoGridActivity;
import com.sbwg.sxb.activity.common.SelectListActivity;
import com.sbwg.sxb.adapter.SelectListAdapter;
import com.sbwg.sxb.entity.BaseEntity;
import com.sbwg.sxb.entity.SelectListEntity;
import com.sbwg.sxb.entity.UserInfoEntity;
import com.sbwg.sxb.utils.BitmapUtil;
import com.sbwg.sxb.utils.CommonTools;
import com.sbwg.sxb.utils.ExceptionUtil;
import com.sbwg.sxb.utils.JsonUtils;
import com.sbwg.sxb.utils.LogUtil;
import com.sbwg.sxb.utils.StringUtil;
import com.sbwg.sxb.utils.retrofit.HttpRequests;
import com.sbwg.sxb.widgets.RoundImageView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class PersonalActivity extends BaseActivity implements OnClickListener {

    String TAG = PersonalActivity.class.getSimpleName();

    RelativeLayout rl_head, rl_nick, rl_gender, rl_birthday, rl_area, rl_intro;
    RoundImageView iv_head;
    TextView tv_nick, tv_gender, tv_birthday, tv_area, tv_intro;

    private String nickStr, genderStr, birthdayStr, areaStr, introStr;
    private String changeStr, userKey, clip_head_path;
    private File saveFile;
    private int genderCode = 0;

    private UserInfoEntity infoEn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        infoEn = (UserInfoEntity) getIntent().getExtras().get("data");

        findViewById();
        initView();
    }

    private void findViewById() {
        rl_head = findViewById(R.id.personal_rl_head);
        rl_nick = findViewById(R.id.personal_rl_nick);
        rl_gender = findViewById(R.id.personal_rl_gender);
        rl_birthday = findViewById(R.id.personal_rl_birthday);
        rl_area = findViewById(R.id.personal_rl_area);
        rl_intro = findViewById(R.id.personal_rl_intro);
        iv_head = findViewById(R.id.personal_iv_head);
        tv_nick = findViewById(R.id.personal_tv_nick_content);
        tv_gender = findViewById(R.id.personal_tv_gender_content);
        tv_birthday = findViewById(R.id.personal_tv_birthday_content);
        tv_area = findViewById(R.id.personal_tv_area_content);
        tv_intro = findViewById(R.id.personal_tv_intro_content);
    }

    private void initView() {
        rl_head.setOnClickListener(this);
        rl_nick.setOnClickListener(this);
        rl_gender.setOnClickListener(this);
        rl_birthday.setOnClickListener(this);
        rl_area.setOnClickListener(this);
        rl_intro.setOnClickListener(this);

        setTitle(R.string.mine_page);
    }

    private void setView() {
        if (infoEn != null) {
            nickStr = infoEn.getUserNick();
            genderCode = infoEn.getGenderCode();
            birthdayStr = infoEn.getBirthday();
            areaStr = infoEn.getUserArea();
            introStr = infoEn.getUserIntro();
        } else {
            nickStr = userManager.getUserNick();
            genderCode = userManager.getUserGender();
            birthdayStr = userManager.getUserBirthday();
            areaStr = userManager.getUserArea();
            introStr = userManager.getUserIntro();
        }
        switch (genderCode) { //定义性别
            case 1:
                genderStr = getString(R.string.mine_gender_male);
                break;
            case 2:
                genderStr = getString(R.string.mine_gender_female);
                break;
            default:
                genderStr = getString(R.string.mine_gender_confidential);
                break;
        }
        tv_gender.setText(genderStr);
        tv_nick.setText(nickStr);
        tv_birthday.setText(birthdayStr);
        tv_area.setText(areaStr);
        tv_intro.setText(introStr);
        // 头像
        Bitmap headBitmap = BitmapFactory.decodeFile(AppConfig.SAVE_USER_HEAD_PATH);
        if (headBitmap != null) {
            iv_head.setImageBitmap(headBitmap);
        } else {
            iv_head.setImageResource(R.drawable.icon_default_head);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.personal_rl_head:
                if (!checkPermission()) return;
                changeHead();
                break;
            case R.id.personal_rl_nick:
                intent = new Intent(mContext, EditUserInfoActivity.class);
                intent.putExtra(EditUserInfoActivity.KEY_TITLE, getString(R.string.mine_change_nick));
                intent.putExtra(EditUserInfoActivity.KEY_SHOW, nickStr);
                intent.putExtra(EditUserInfoActivity.KEY_HINT, getString(R.string.mine_input_nick));
                intent.putExtra(EditUserInfoActivity.KEY_TIPS, "");
                intent.putExtra(EditUserInfoActivity.KEY_USER, "nickname");
                startActivityForResult(intent, AppConfig.ACTIVITY_CHANGE_USER_NICK);
                return;
            case R.id.personal_rl_gender:
                SelectListEntity selectEn = getGenderListEntity();
                intent = new Intent(mContext, SelectListActivity.class);
                intent.putExtra("data", selectEn);
                intent.putExtra("dataType", SelectListAdapter.DATA_TYPE_5);
                startActivityForResult(intent, AppConfig.ACTIVITY_CHANGE_USER_GENDER);
                return;
            case R.id.personal_rl_birthday:
                showDateDialog();
                break;
            case R.id.personal_rl_area:
                intent = new Intent(mContext, SelectAreaActivity.class);
                startActivityForResult(intent, AppConfig.ACTIVITY_CHANGE_USER_AREA);
                return;
            case R.id.personal_rl_intro:
                intent = new Intent(mContext, EditUserInfoActivity.class);
                intent.putExtra(EditUserInfoActivity.KEY_TITLE, getString(R.string.mine_change_intro));
                intent.putExtra(EditUserInfoActivity.KEY_SHOW, introStr);
                intent.putExtra(EditUserInfoActivity.KEY_HINT, getString(R.string.mine_intro_hint));
                intent.putExtra(EditUserInfoActivity.KEY_TIPS, "");
                intent.putExtra(EditUserInfoActivity.KEY_USER, "signature");
                startActivityForResult(intent, AppConfig.ACTIVITY_CHANGE_USER_INTRO);
                return;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    private void changeHead() {
        // 标记裁剪相片的类型为圆形
        shared.edit().putInt(AppConfig.KEY_CLIP_PHOTO_TYPE, AppConfig.PHOTO_TYPE_ROUND).apply();
        showListDialog(R.string.photo_change_head, getResources().getStringArray(R.array.array_photo_choose),
                screenWidth * 1 / 2, true, new Handler() {

                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        switch (msg.what) {
                            case 0: //拍照
                                String status = Environment.getExternalStorageState();
                                if (status.equals(Environment.MEDIA_MOUNTED)) { //先验证手机是否有sdcard
                                    try {
                                        saveFile = BitmapUtil.createPath("IMG_" + System.currentTimeMillis() + ".jpg", true);
                                        //注意：AndroidManifest.xml处的android:authorities必须跟getPackageName() + ".fileprovider"一样
                                        Uri uri = FileProvider.getUriForFile(mContext, getPackageName() + ".fileprovider", saveFile);
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                                        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                                        startActivityForResult(intent, AppConfig.ACTIVITY_GET_IMAGE_VIA_CAMERA);
                                    } catch (ActivityNotFoundException e) {
                                        ExceptionUtil.handle(e);
                                        CommonTools.showToast(getString(R.string.photo_save_directory_error));
                                    }
                                } else {
                                    CommonTools.showToast(getString(R.string.photo_save_sd_error));
                                }
                                break;
                            case 1: //本地
                                openActivity(ClipPhotoGridActivity.class);
                                break;
                        }
                    }

                });
    }

    @SuppressWarnings("ResourceType")
    private void showDateDialog() {
        String[] dates = null;
        if (!StringUtil.isNull(birthdayStr) && birthdayStr.contains("-")) { //解析当前生日
            dates = birthdayStr.split("-");
        }
        int year = 0;
        int month = 0;
        int day = 0;
        if (dates != null && dates.length > 2) { //判定当前生日有效性1
            year = StringUtil.getInteger(dates[0]);
            month = StringUtil.getInteger(dates[1]) - 1;
            day = StringUtil.getInteger(dates[2]);
        }
        if (year == 0 || month == 0 || day == 0) { //判定当前生日有效性2
            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }
        new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, new OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // 修改本地生日
                String yearStr = year + "-";
                String monthStr = "";
                if (monthOfYear < 9) {
                    monthStr = "0" + (monthOfYear + 1) + "-";
                } else {
                    monthStr = (monthOfYear + 1) + "-";
                }
                String dayStr = "";
                if (dayOfMonth < 10) {
                    dayStr = "0" + dayOfMonth;
                } else {
                    dayStr = "" + dayOfMonth;
                }
                birthdayStr = yearStr + monthStr + dayStr;
                // 修改本地缓存
                if (infoEn != null) {
                    infoEn.setBirthday(birthdayStr);
                }
                userManager.saveUserBirthday(birthdayStr);
                setView();
                // 修改服务器生日
                changeStr = birthdayStr;
                userKey = "birthdayValue";
                saveUserInfo();
            }
        }, year, month, day).show();
    }

    /**
     * 跳转至相片编辑器
     */
    private void startClipImageActivity(String path) {
        Intent intent = new Intent(this, ClipImageCircularActivity.class);
        intent.putExtra(AppConfig.ACTIVITY_CLIP_PHOTO_PATH, path);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == AppConfig.ACTIVITY_GET_IMAGE_VIA_CAMERA) //拍照
            {
                AppApplication.updatePhoto(saveFile);
                startClipImageActivity(saveFile.getAbsolutePath());
            } else if (requestCode == AppConfig.ACTIVITY_CHANGE_USER_NICK) //修改昵称
            {
                nickStr = data.getExtras().getString(AppConfig.ACTIVITY_CHANGE_USER_CONTENT);
                if (infoEn != null) {
                    infoEn.setUserNick(nickStr);
                }
                userManager.saveUserNick(nickStr);
                setView();
            } else if (requestCode == AppConfig.ACTIVITY_CHANGE_USER_GENDER) //修改性别
            {
                genderCode = data.getExtras().getInt(AppConfig.ACTIVITY_SELECT_LIST_POSITION, 1);
                if (infoEn != null) {
                    infoEn.setGenderCode(genderCode);
                }
                userManager.saveUserGender(genderCode);
                setView();
            } else if (requestCode == AppConfig.ACTIVITY_CHANGE_USER_AREA) //修改地区
            {
                areaStr = data.getExtras().getString(AppConfig.ACTIVITY_CHANGE_USER_CONTENT);
                if (infoEn != null) {
                    infoEn.setUserArea(areaStr);
                }
                userManager.saveUserArea(areaStr);
                setView();
                userKey = "address";
                changeStr = areaStr;
                saveUserInfo();
            } else if (requestCode == AppConfig.ACTIVITY_CHANGE_USER_INTRO) //修改简介
            {
                introStr = data.getExtras().getString(AppConfig.ACTIVITY_CHANGE_USER_CONTENT);
                if (infoEn != null) {
                    infoEn.setUserIntro(introStr);
                }
                userManager.saveUserIntro(introStr);
                setView();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
        // 页面开始
        AppApplication.onPageStart(this, TAG);

        setView();
        clip_head_path = shared.getString(AppConfig.KEY_CLIP_HEAD_PATH, "");
        if (!StringUtil.isNull(clip_head_path)) { //上传修改的头像
            uploadHead();
            shared.edit().putString(AppConfig.KEY_CLIP_HEAD_PATH, "").apply();
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onPause");
        // 页面结束
        AppApplication.onPageEnd(this, TAG);

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 生成性别列表数据
     */
    private SelectListEntity getGenderListEntity() {
        SelectListEntity selectEn = new SelectListEntity();
        selectEn.setTypeName(getString(R.string.mine_change_gender));
        ArrayList<SelectListEntity> childLists = new ArrayList<>();
        SelectListEntity childEn1 = new SelectListEntity();
        childEn1.setChildId(1);
        childEn1.setChildShowName(getString(R.string.mine_gender_male));
        childLists.add(childEn1);
        SelectListEntity childEn2 = new SelectListEntity();
        childEn2.setChildId(2);
        childEn2.setChildShowName(getString(R.string.mine_gender_female));
        childLists.add(childEn2);
        SelectListEntity childEn3 = new SelectListEntity();
		childEn3.setChildId(3);
		childEn3.setChildShowName(getString(R.string.mine_gender_confidential));
		childLists.add(childEn3);
        switch (genderCode) {
            case 1:
                selectEn.setSelectEn(childEn1);
                break;
            case 2:
                selectEn.setSelectEn(childEn2);
                break;
		case 3:
			selectEn.setSelectEn(childEn3);
			break;
        }
        selectEn.setChildLists(childLists);
        return selectEn;
    }

    /**
     * 上传头像
     */
    private void uploadHead() {
        if (!StringUtil.isNull(clip_head_path)) {
            startAnimation();
            CommonTools.showToast(getString(R.string.photo_upload_img, getString(R.string.mine_head)));
            uploadPushFile(new File(clip_head_path), 1, AppConfig.REQUEST_SV_POST_UPLOAD_HEAD);
        } else {
            CommonTools.showToast(getString(R.string.photo_img_url_error, getString(R.string.mine_head)));
        }
    }

    /**
     * 修改用户资料
     */
    private void saveUserInfo() {
        if (!StringUtil.isNull(changeStr) && !StringUtil.isNull(userKey)) {
            HashMap<String, String> map = new HashMap<>();
            map.put(userKey, changeStr);
            loadSVData(AppConfig.URL_USER_SAVE, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_POST_USER_SAVE);
        }
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        BaseEntity baseEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_POST_UPLOAD_HEAD:
                    baseEn = JsonUtils.getUploadResult(jsonObject);
                    if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
                        changeStr = baseEn.getOthers();
                        userKey = "avatar";
                        saveUserInfo();
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_POST_USER_SAVE:
                    baseEn = JsonUtils.getUploadResult(jsonObject);
                    if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
                        if (userKey.equals("avatar")) {
                            // 替换头像
                            Bitmap clipBitmap = BitmapFactory.decodeFile(clip_head_path);
                            AppApplication.saveBitmapFile(clipBitmap, new File(AppConfig.SAVE_USER_HEAD_PATH), 100);
                            // 刷新头像
                            setView();
                            AppApplication.updateUserData(true);
                            CommonTools.showToast(getString(R.string.photo_upload_img_ok, getString(R.string.mine_head)), Toast.LENGTH_SHORT);
                        }
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
            }
        } catch (Exception e) {
            loadFailHandle();
            ExceptionUtil.handle(e);
        }
    }

    @Override
    protected void loadFailHandle() {
        super.loadFailHandle();
        handleErrorCode(null);
    }

}
