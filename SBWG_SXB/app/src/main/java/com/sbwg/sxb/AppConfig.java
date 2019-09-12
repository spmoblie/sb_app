package com.sbwg.sxb;

import android.Manifest;
import android.os.Environment;


public class AppConfig {

	/**
	 ******************************************* URL设置开始 ******************************************
	 */

	// 是否正式发布
	public static final boolean IS_PUBLISH = false;
	// 网络传输协议http
	public final static String APP_HTTP = "http://";
	// 网络传输协议https
	public final static String APP_HTTPS = "https://";
	// 域名1
	public final static String ENVIRONMENT_TEST_APP_1 = APP_HTTP + "192.168.1.143/app/";
	// 域名2
	public final static String ENVIRONMENT_TEST_APP_2 = APP_HTTP + "";
	// Base域名
	public final static String ENVIRONMENT_BASE_URL = ENVIRONMENT_TEST_APP_1;
	// 图片域名
	public final static String ENVIRONMENT_PRESENT_IMG_APP = "file:///android_asset/";
	// 推广域名
	public final static String ENVIRONMENT_PRESENT_SHARE_URL = ENVIRONMENT_TEST_APP_1;

	// 获取首页banner列表
	public final static String URL_HOME_BANNER = "home/index";
	// 获取首页活动列表
	public final static String URL_HOME_LIST = "activity/list";

	// SP微信公众号
	public static final String SP_WECHAT_PUBLIC = "http://weixin.qq.com/r/MnXVzWXE-jiBrSGu9yAg";
	// 联系客服URL
	public static final String API_CUSTOMER_SERVICE = "http://webim.qiao.baidu.com/im/gateway?siteid=3888057&type=n&ucid=6374202";

	/**
	 ******************************************* URL设置结束 ******************************************
	 */

	/**
	 ******************************************* 全局常量设置开始 ******************************************
	 */

	// 全局对话框“确定”
	public static final int DIALOG_CLICK_OK = 656;
	// 全局对话框“取消”
	public static final int DIALOG_CLICK_NO = 787;

	// 相片类型-圆形
	public static final int PHOTO_TYPE_ROUND = 701;
	// 相片类型-方形
	public static final int PHOTO_TYPE_SQUARE = 702;

	// Error状态码：加载成功
	public static final int ERROR_CODE_SUCCESS = 1;
	// Error状态码：登录失效
	public static final int ERROR_CODE_LOGOUT = 999;

	// 加载缓冲时间
	public static final int LOADING_TIME = 1000;

	// 动态授权-权限集
	public static final String[] PERMISSIONS = new String[]{
			Manifest.permission.CAMERA,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE };

	// QQ AppID
	public static final String QQ_APP_ID = "1104891333";
	// QQ授权接口参数：Scope权限
	public static final String QQ_SCOPE = "all";
	// 微信AppID
	public static final String WX_APP_ID = "wxe75d3ed35d5ec0a3";
	// 微信AppSecret
	public static final String WX_APP_SECRET = "6dbb91d8aa799a13237179092ab690c8";
	// 微信商户号
	public static final String WX_MCH_ID = "1376997902";
	// 微博AppID
	public static final String WB_APP_ID = "2435385654";
	// 微博授权回调Url
	public static final String WB_REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
	// 微博授权接口参数：Scope权限
	public static final String WB_SCOPE = "email,direct_messages_read,direct_messages_write,friendships_groups_read,"
			+ "friendships_groups_write,statuses_to_me_read,follow_app_official_microblog,invitation_write";


	// 缓存Cookies文件名
	public static final String cookiesFileName = "cookies";
	// 首页头部数据文件名
	public static final String homeHeadFileName = "home_head";
	// 首页列表数据文件名
	public static final String homeListFileName = "home_list";
	// 缓存路径应用名称
	public static final String SAVE_APP_NAME = "SBWG";
	// 内置SD卡路径
	public static final String SDPATH = Environment.getExternalStorageDirectory().toString() + "/" + SAVE_APP_NAME + "/";
	// Apk临时缓存路径（应用关闭时须清除）
	public static final String SAVE_PATH_APK_DICE = SDPATH + "apk/" + SAVE_APP_NAME + "_AD/";
	// 文本长久保存路径
	public static final String SAVE_PATH_TXT_SAVE = SDPATH + "txt/" + SAVE_APP_NAME + "_TS/";
	// 文本临时缓存路径（应用关闭时须清除）
	public static final String SAVE_PATH_TXT_DICE = SDPATH + "txt/" + SAVE_APP_NAME + "_TD/";
	// 图片长久保存路径
	public static final String SAVE_PATH_IMAGE_SAVE = SDPATH + "image/" + SAVE_APP_NAME + "_IS/";
	// 图片临时缓存路径（应用关闭时须清除）
	public static final String SAVE_PATH_IMAGE_DICE = SDPATH + "image/" + SAVE_APP_NAME + "_ID/";
	// 媒体长久保存路径
	public static final String SAVE_PATH_MEDIA_SAVE = SDPATH + "media/" + SAVE_APP_NAME + "_MS/";
	// 媒体临时缓存路径（应用关闭时须清除）
	public static final String SAVE_PATH_MEDIA_DICE = SDPATH + "media/" + SAVE_APP_NAME + "_MD/";

	// 用户头像存储路径
	public static final String SAVE_USER_HEAD_PATH = SAVE_PATH_IMAGE_SAVE + "user_head.png";

	/**
	 ******************************************* 全局常量设置结束 ******************************************
	 */

	/**
	 ******************************************* RequestCode参数设置开始 ******************************************
	 */

	// 校验Sessions
	public static final int REQUEST_SV_GET_SESSIONS_CODE = 0X0001;
	// 检测版本更新
	public static final int REQUEST_SV_POST_VERSION_CODE = 0X0002;

	// 首頁头部数据
	public static final int REQUEST_SV_POST_HOME_HEAD = 0X1001;
	// 首頁列表数据
	public static final int REQUEST_SV_POST_HOME_LIST = 0X1002;

	/**
	 ******************************************* RequestCode参数设置结束 ******************************************
	 */

	/**
	 ******************************************* 偏好设置Key值设置开始 ******************************************
	 */

	// 偏好设置Key-记录屏幕宽
	public static final String KEY_SCREEN_WIDTH = "screen_width";
	// 偏好设置Key-记录屏幕高
	public static final String KEY_SCREEN_HEIGHT = "screen_height";
	// 偏好设置Key-记录状态栏高
	public static final String KEY_STATUS_HEIGHT = "status_height";
	// 偏好设置Key-记录标题栏高
	public static final String KEY_TITLE_HEIGHT = "title_height";

	// 偏好设置Key-记录剪切相片的类型
	public static final String KEY_CLIP_PHOTO_TYPE = "clip_photo_type";
	// 偏好设置Key-记录剪切头像的路径
	public static final String KEY_CLIP_HEAD_PATH = "clip_head_path";
	// 偏好设置Key-记录剪切卡片的路径
	public static final String KEY_CLIP_CARD_PATH = "clip_card_path";

	// 偏好设置Key-记录用户ID
	public static final String KEY_USER_ID = "user_id";
	// 偏好设置Key-记录分享ID
	public static final String KEY_SHARE_ID = "share_id";
	// 偏好设置Key-记录登入账号
	public static final String KEY_LOGIN_ACCOUNT = "login_account";
	// 偏好设置Key-记录用户姓名
	public static final String KEY_USER_NAME = "user_name";
	// 偏好设置Key-记录用户昵称
	public static final String KEY_USER_NICK = "user_nick";
	// 偏好设置Key-记录用户头像
	public static final String KEY_USER_HEAD_URL = "user_Head_url";
	// 偏好设置Key-记录用户简介
	public static final String KEY_USER_INTRO = "user_intro";
	// 偏好设置Key-记录用户性别
	public static final String KEY_USER_GENDER = "user_gender";
	// 偏好设置Key-记录用户生日
	public static final String KEY_USER_BIRTHDAY = "user_birthday";
	// 偏好设置Key-记录用户地区
	public static final String KEY_USER_AREA = "user_area";
	// 偏好设置Key-记录用户邮箱
	public static final String KEY_USER_EMAIL = "user_email";
	// 偏好设置Key-记录用户手机号码
	public static final String KEY_USER_PHONE = "user_phone";
	// 偏好设置Key-记录用户账户余额
	public static final String KEY_USER_MONEY = "user_money";
	// 偏好设置Key-记录用户购物车中商品数量
	public static final String KEY_CART_NUM = "cart_num";
	// 偏好设置Key-记录用户的微信授权码
	public static final String KEY_WX_ACCESS_TOKEN = "wx_access_token";
	// 偏好设置Key-记录用户的微信校验码
	public static final String KEY_WX_REFRESH_TOKEN = "wx_refresh_token";
	// 偏好设置Key-记录用户的微信登录ID
	public static final String KEY_WX_OPEN_ID = "wx_open_id";
	// 偏好设置Key-记录用户的微信身份ID
	public static final String KEY_WX_UNION_ID = "wx_union_id";
	// 偏好设置Key-记录同步远程服务器数据的日期
	public static final String KEY_LOAD_SV_DATA_DAY = "load_sv_data_day";
	// 偏好设置Key-记录是否重加载分类数据
	public static final String KEY_LOAD_SORT_DATA = "load_sort_data";
	// 偏好设置Key-记录推送服务的开关状态
	public static final String KEY_PUSH_STATUS = "push_status";
	// 偏好设置Key-记录首页当前的下标索引
	public static final String KEY_MAIN_CURRENT_INDEX = "main_current_index";
	// 偏好设置Key-记录是否自动跳转到会员页面
	public static final String KEY_PUSH_PAGE_MEMBER = "push_page_member";
	// 偏好设置Key-记录发送短信验证码的累计次数
	public static final String KEY_SEND_VERIFY_NUMBER = "send_verify_number";
	// 偏好设置Key-记录最近一次发送短信验证码的时间
	public static final String KEY_SEND_VERIFY_LAST_TIME = "send_verify_last_time";
	// 偏好设置Key-记录最近一次更新APP版本的时间
	public static final String KEY_UPDATE_VERSION_LAST_TIME = "update_version_last_time";

	/**
	 ******************************************* 偏好设置Key值设置结束 ******************************************
	 */

	/**
	 ******************************************* Activity传参设置开始 ******************************************
	 */

	public static final String ACTIVITY_CLIP_PHOTO_PATH = "clip_photo_path";
	public static final String ACTIVITY_CHANGE_USER_CONTENT = "change_user_content";
	public static final String ACTIVITY_SELECT_LIST_POSITION = "select_list_position";

	public static final int ACTIVITY_SELECT_PHOTO_PICKER = 0X9001;
	public static final int ACTIVITY_SHOW_PHOTO_PICKER = 0X9002;
	public static final int ACTIVITY_GET_IMAGE_VIA_CAMERA = 0X9003;
	public static final int ACTIVITY_CHANGE_USER_NICK = 0X9004;
	public static final int ACTIVITY_CHANGE_USER_GENDER = 0X9005;
	public static final int ACTIVITY_CHANGE_USER_AREA = 0X9006;
	public static final int ACTIVITY_CHANGE_USER_INTRO = 0X9007;
	public static final int ACTIVITY_CHANGE_USER_EMAIL = 0X9008;

	/**
	 ******************************************* Activity传参设置结束 ******************************************
	 */

	/**
	 ******************************************* 广播参数设置开始 ******************************************
	 */

	public static final String RECEIVER_ACTION_HOME_DATA = "更新Home的数据";
	public static final String RECEIVER_ACTION_MAIN_DATA = "更新Main的数据";
	public static final String RECEIVER_ACTION_HOME_MSG_KEY = "传递数据给Home";
	public static final String RECEIVER_ACTION_MAIN_MSG_KEY = "传递数据给Main";

	/**
	 ******************************************* 广播参数设置结束 ******************************************
	 */

}