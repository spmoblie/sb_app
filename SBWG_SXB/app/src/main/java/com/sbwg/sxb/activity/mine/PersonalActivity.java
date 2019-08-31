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

import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.AppConfig;
import com.sbwg.sxb.AppManager;
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
import com.sbwg.sxb.utils.LogUtil;
import com.sbwg.sxb.utils.StringUtil;
import com.sbwg.sxb.utils.UserManager;
import com.sbwg.sxb.utils.image.AsyncImageUpload;
import com.sbwg.sxb.widgets.RoundImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class PersonalActivity extends BaseActivity implements OnClickListener {

    private static final String TAG = "PersonalActivity";

    private RelativeLayout rl_head, rl_nick, rl_gender, rl_birthday, rl_area, rl_intro, rl_email, rl_phone;
    private RoundImageView iv_head;
    private TextView tv_nick, tv_gender, tv_birthday, tv_area, tv_intro, tv_email, tv_phone;
    private String nickStr, genderStr, birthdayStr, areaStr, introStr, emailStr, phoneStr;
    private String changeStr, changeTypeKey, clip_head_path;
    private File saveFile;
    private int genderCode = 0;

    private UserInfoEntity infoEn;
    private UserManager userManager;
    private AsyncImageUpload asyncImageUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        LogUtil.i(TAG, "onCreate");
        AppManager.getInstance().addActivity(this); //添加Activity到堆栈

        infoEn = (UserInfoEntity) getIntent().getExtras().get("data");
        userManager = UserManager.getInstance();

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
        rl_email = findViewById(R.id.personal_rl_email);
        rl_phone = findViewById(R.id.personal_rl_phone);
        iv_head = findViewById(R.id.personal_iv_head);
        tv_nick = findViewById(R.id.personal_tv_nick_content);
        tv_gender = findViewById(R.id.personal_tv_gender_content);
        tv_birthday = findViewById(R.id.personal_tv_birthday_content);
        tv_area = findViewById(R.id.personal_tv_area_content);
        tv_intro = findViewById(R.id.personal_tv_intro_content);
        tv_email = findViewById(R.id.personal_tv_email_content);
        tv_phone = findViewById(R.id.personal_tv_phone_content);
    }

    private void initView() {
        rl_head.setOnClickListener(this);
        rl_nick.setOnClickListener(this);
        rl_gender.setOnClickListener(this);
        rl_birthday.setOnClickListener(this);
        rl_area.setOnClickListener(this);
        rl_intro.setOnClickListener(this);
        rl_email.setOnClickListener(this);
        rl_phone.setOnClickListener(this);

        setTitle(R.string.mine_page);
        setView();
    }

    private void setView() {
        if (infoEn != null) {
            nickStr = infoEn.getUserNick();
            genderCode = infoEn.getGenderCode();
            birthdayStr = infoEn.getBirthday();
            areaStr = infoEn.getUserArea();
            introStr = infoEn.getUserIntro();
            emailStr = infoEn.getUserEmail();
            phoneStr = infoEn.getUserPhone();
        } else {
            nickStr = userManager.getUserNick();
            genderCode = userManager.getUserGender();
            birthdayStr = userManager.getUserBirthday();
            areaStr = userManager.getUserArea();
            introStr = userManager.getUserIntro();
            emailStr = userManager.getUserEmail();
            phoneStr = userManager.getUserPhone();
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
        tv_email.setText(emailStr);
        tv_phone.setText(phoneStr);
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
                intent.putExtra("titleStr", getString(R.string.mine_change_nick));
                intent.putExtra("showStr", nickStr);
                intent.putExtra("hintStr", getString(R.string.mine_input_nick));
                intent.putExtra("reminderStr", "");
                intent.putExtra("changeTypeKey", "nickname");
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
                intent.putExtra("titleStr", getString(R.string.mine_change_intro));
                intent.putExtra("showStr", introStr);
                intent.putExtra("hintStr", getString(R.string.mine_intro_hint));
                intent.putExtra("reminderStr", "");
                intent.putExtra("changeTypeKey", "intro");
                startActivityForResult(intent, AppConfig.ACTIVITY_CHANGE_USER_INTRO);
                return;
            case R.id.personal_rl_phone:
                startChangePhone();
                return;
            case R.id.personal_rl_email:
//                checkEmailStatus();
                //Evan临时修改
                startChangeEmail();
                return;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    private void changeHead() {
        // 标记裁剪相片的类型为圆形
        editor.putInt(AppConfig.KEY_CLIP_PHOTO_TYPE, AppConfig.PHOTO_TYPE_ROUND).apply();
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
                                        CommonTools.showToast(getString(R.string.photo_save_directory_error), 1000);
                                    }
                                } else {
                                    CommonTools.showToast(getString(R.string.photo_save_sd_error), 1000);
                                }
                                break;
                            case 1: //本地
                                startActivity(new Intent(mContext, ClipPhotoGridActivity.class));
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
                changeStr = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                changeTypeKey = "birthday";
                postChangeUserContent();
            }
        }, year, month, day).show();
    }

    /**
     * 提交修改用户资料
     */
    private void postChangeUserContent() {
        if (!StringUtil.isNull(changeStr) && !StringUtil.isNull(changeTypeKey)) {
//			request(AppConfig.REQUEST_SV_POST_EDIT_USER_INFO_CODE);
        }
    }

    /**
     * 检测邮箱可修改状态
     */
    private void checkEmailStatus() {
        startAnimation();
//		request(AppConfig.REQUEST_SV_CHECK_USER_EMAIL_STATUS);
    }

    /**
     * 发送邮件给用户
     */
    private void sendEmailToUser() {
//		request(AppConfig.REQUEST_SV_SEND_EMAIL_TO_USER);
    }

    /**
     * 弹出对话框发送邮件确认修改
     */
    private void showSendEmailDialog() {
        showConfirmDialog(null, getString(R.string.mine_send_email_hint, emailStr),
                getString(R.string.cancel), getString(R.string.send_confirm),
                screenWidth * 5 / 6, false, false, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case AppConfig.DIALOG_CLICK_NO:
                                break;
                            case AppConfig.DIALOG_CLICK_OK:
                                sendEmailToUser();
                                break;
                        }
                    }
                });
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

    /**
     * 跳转至修改邮箱页面
     */
    private void startChangeEmail() {
        Intent intent = new Intent(mContext, EditUserInfoActivity.class);
        intent.putExtra("titleStr", getString(R.string.mine_change_email));
        intent.putExtra("showStr", emailStr);
        intent.putExtra("hintStr", getString(R.string.login_input_email));
        intent.putExtra("reminderStr", getString(R.string.mine_change_email_notice));
        intent.putExtra("changeTypeKey", "email");
        startActivityForResult(intent, AppConfig.ACTIVITY_CHANGE_USER_EMAIL);
    }

    /**
     * 修改手机号码
     */
    private void startChangePhone() {

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
            } else if (requestCode == AppConfig.ACTIVITY_CHANGE_USER_INTRO) //修改简介
            {
                introStr = data.getExtras().getString(AppConfig.ACTIVITY_CHANGE_USER_CONTENT);
                if (infoEn != null) {
                    infoEn.setUserIntro(introStr);
                }
                userManager.saveUserIntro(introStr);
                setView();
            } else if (requestCode == AppConfig.ACTIVITY_CHANGE_USER_EMAIL) //修改邮箱
            {
                emailStr = data.getExtras().getString(AppConfig.ACTIVITY_CHANGE_USER_CONTENT);
                if (infoEn != null) {
                    infoEn.setUserEmail(emailStr);
                }
                userManager.saveUserEmail(emailStr);
                setView();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        LogUtil.i(TAG, "onResume");
        // 页面开始
        AppApplication.onPageStart(this, TAG);

        clip_head_path = shared.getString(AppConfig.KEY_CLIP_HEAD_PATH, "");
        if (!StringUtil.isNull(clip_head_path)) { //上传修改的头像
            uploadImage();
            editor.putString(AppConfig.KEY_CLIP_HEAD_PATH, "").apply();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        LogUtil.i(TAG, "onPause");
        // 页面结束
        AppApplication.onPageEnd(this, TAG);
        // 销毁对象
        if (asyncImageUpload != null) {
            asyncImageUpload.clearInstance();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        LogUtil.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        LogUtil.i(TAG, "onDestroy");
        super.onDestroy();
    }

    /**
     * 上传头像到服务器
     */
    private void uploadImage() {
        if (!StringUtil.isNull(clip_head_path)) {
            startAnimation();
            CommonTools.showToast(getString(R.string.photo_upload_img, getString(R.string.mine_head)), 1000);
            // 开启上传线程...
            new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					asyncImageUpload = AsyncImageUpload.getInstance(new AsyncImageUpload.AsyncImageUploadCallback() {

						@Override
						public void uploadImageUrls(BaseEntity baseEn) {
							/*if (baseEn != null) {
								boolean isOk = baseEn.getErrCode() == 1 ? false : true;
								if (!isOk) {
									// 刷新头像
                                    editor.putBoolean(AppConfig.KEY_IS_UPDATE_HEAD, true).apply();
									// 清除图片缓存
									AppApplication.clearGlideCache();
									CommonTools.showToast(getString(R.string.photo_upload_img_ok, getString(R.string.mine_head)), 1000);
								} else {
									if (!StringUtil.isNull(baseEn.getErrInfo())) {
										CommonTools.showToast(baseEn.getErrInfo(), 2000);
									} else {
										CommonTools.showToast(getString(R.string.photo_upload_head_fail), 2000);
									}
								}
							}else {
								CommonTools.showToast(getString(R.string.photo_upload_head_fail), 2000);
							}*/

                            // 将存储在旧头像替换为新头像
                            try {
                                Bitmap clipBitmap = BitmapFactory.decodeFile(clip_head_path);
                                AppApplication.saveBitmapFile(clipBitmap, new File(AppConfig.SAVE_USER_HEAD_PATH), 100);
                                setView(); //刷新头像
                            } catch (Exception e) {
                                ExceptionUtil.handle(e);
                            }
                            CommonTools.showToast(getString(R.string.photo_upload_img_ok, getString(R.string.mine_head)), 1000);

							stopAnimation();
						}

					});
					Map<String, String> postData = new HashMap<>();
					postData.put("fileName", clip_head_path);
					asyncImageUpload.uploadImage(AppConfig.API_UPDATE_PROFILE, postData, clip_head_path);
				}
			}, 2000);
        } else {
            CommonTools.showToast(getString(R.string.photo_img_url_error, getString(R.string.mine_head)), 1000);
        }
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

}
