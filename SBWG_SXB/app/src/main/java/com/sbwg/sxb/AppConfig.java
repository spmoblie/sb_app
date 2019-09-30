package com.sbwg.sxb;

import android.Manifest;
import android.os.Environment;


public class AppConfig {

	/**
	 ******************************************* URL设置开始 ******************************************
	 */

	// 是否正式发布
	public static final boolean IS_PUBLISH = false;
	// http协议
	public final static String APP_HTTP = "http://";
	// Base域名
	public final static String BASE_URL_1 = APP_HTTP + "119.145.16.116:8080/app/";
	// Base域名
	public final static String BASE_URL_2 = APP_HTTP + "192.168.1.143/app/";
	// 图片域名
	public final static String IMAGE_URL = "file:///android_asset/";
	// 推广域名
	public final static String SHARE_URL = BASE_URL_1;
	// 关于我们
	public final static String ABOUT_US = "https://www.sbwg.cn/";

	// 短信验证码
	public final static String URL_AUTH_MESSAGE = "auth/regCaptcha";
	// 提交注册
	public final static String URL_AUTH_REGISTER = "auth/register";
	// 提交重置
	public final static String URL_AUTH_RESET = "auth/reset";
	// 提交登录
	public final static String URL_AUTH_LOGIN = "auth/login";
	// 提交注销
	public final static String URL_AUTH_LOGOUT = "auth/logout";

	// 首页banner列表
	public final static String URL_HOME_BANNER = "home/index";
	// 首页活动列表
	public final static String URL_HOME_LIST = "activity/list";
	// 报名数据提交
	public final static String URL_SIGN_UP_ADD = "activity/sign_up/add";

	// 获取用户资料
	public final static String URL_USER_GET = "user/get";
	// 修改用户资料
	public final static String URL_USER_SAVE = "user/save";
	// 获取我的课程
	public final static String URL_USER_ACTIVITY = "user/activity";

	// 获取我的设计
	public final static String URL_DESIGN_ALL = "design/all";

	// 上传接口
	public final static String URL_UPLOAD_PUSH = "upload/push";

	/**
	 ******************************************* URL设置结束 ******************************************
	 */

	/**
	 ******************************************* 全局常量设置开始 ******************************************
	 */

	// 全局对话框“确定”
	public static final int DIALOG_CLICK_OK = 0X0666;
	// 全局对话框“取消”
	public static final int DIALOG_CLICK_NO = 0X0999;

	// 相片类型-圆形
	public static final int PHOTO_TYPE_ROUND = 0X0011;
	// 相片类型-方形
	public static final int PHOTO_TYPE_SQUARE = 0X0022;

	// Error状态码：加载成功
	public static final int ERROR_CODE_SUCCESS = 0;
	// Error状态码：登录超时
	public static final int ERROR_CODE_TIMEOUT = 501;
	// Error状态码：手机号已注册
	public static final int ERROR_CODE_PHONE_REGISTERED = 705;
	// Error状态码：手机号未注册
	public static final int ERROR_CODE_PHONE_UNREGISTERED = 706;

	// 加载缓冲时间
	public static final int LOADING_TIME = 1000;
	// 验证码倒计时
	public static final long SEND_TIME = 60000;

	// 动态授权-权限集
	public static final String[] PERMISSIONS = new String[]{
			Manifest.permission.CAMERA,
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,};

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
	// 我的头部数据文件名
	public static final String mineHeadFileName = "mine_head";
	// 我的列表数据文件名
	public static final String mineListFileName = "mine_list";
	// 缓存路径应用名称
	public static final String SAVE_APP_NAME = "SongBao";
	// 内置SD卡路径
	public static final String SD_PATH = Environment.getExternalStorageDirectory().toString() + "/" + SAVE_APP_NAME + "/";
	// Apk临时缓存路径（应用关闭时清除）
	public static final String SAVE_PATH_APK_DICE = SD_PATH + "apk/" + SAVE_APP_NAME + "_AD/";
	// 文本长久保存路径
	public static final String SAVE_PATH_TXT_SAVE = SD_PATH + "txt/" + SAVE_APP_NAME + "_TS/";
	// 文本临时缓存路径（应用关闭时清除）
	public static final String SAVE_PATH_TXT_DICE = SD_PATH + "txt/" + SAVE_APP_NAME + "_TD/";
	// 图片长久保存路径
	public static final String SAVE_PATH_IMAGE_SAVE = SD_PATH + "image/" + SAVE_APP_NAME + "_IS/";
	// 图片临时缓存路径（应用关闭时清除）
	public static final String SAVE_PATH_IMAGE_DICE = SD_PATH + "image/" + SAVE_APP_NAME + "_ID/";
	// 媒体长久保存路径
	public static final String SAVE_PATH_MEDIA_SAVE = SD_PATH + "media/" + SAVE_APP_NAME + "_MS/";
	// 媒体临时缓存路径（应用关闭时清除）
	public static final String SAVE_PATH_MEDIA_DICE = SD_PATH + "media/" + SAVE_APP_NAME + "_MD/";

	// 用户数据存储路径
	public static final String SAVE_USER_DATA_PATH = SD_PATH + "user/";
	// 用户头像存储路径
	public static final String SAVE_USER_HEAD_PATH = SAVE_PATH_IMAGE_SAVE + "user_head.png";

	/**
	 ******************************************* 全局常量设置结束 ******************************************
	 */

	/**
	 ******************************************* RequestCode参数设置开始 ******************************************
	 */

	// 授权监听
	public static final int REQUEST_CORD_PERMISSION = 0X1000;
	// 校验Sessions
	public static final int REQUEST_SV_GET_SESSIONS = 0X1001;
	// 检测版本更新
	public static final int REQUEST_SV_POST_VERSION = 0X1002;
	// 短信验证码
	public static final int REQUEST_SV_AUTH_MESSAGE = 0X1003;
	// 提交注册
	public static final int REQUEST_SV_AUTH_REGISTER = 0X1004;
	// 提交重置
	public static final int REQUEST_SV_AUTH_RESET = 0X1005;
	// 提交登录
	public static final int REQUEST_SV_AUTH_LOGIN = 0X1006;

	// 首頁头部数据
	public static final int REQUEST_SV_POST_HOME_HEAD = 0X2001;
	// 首頁列表数据
	public static final int REQUEST_SV_POST_HOME_LIST = 0X2002;
	// 提交报名数据
	public static final int REQUEST_SV_POST_SIGN_DATA = 0X2003;

	// 上传用户头像
	public static final int REQUEST_SV_POST_UPLOAD_HEAD = 0X3001;
	// 获取用户资料
	public static final int REQUEST_SV_POST_USER_GET = 0X3002;
	// 修改用户资料
	public static final int REQUEST_SV_POST_USER_SAVE = 0X3003;
	// 获取我的课程
	public static final int REQUEST_SV_POST_USER_ACTIVITY = 0X3004;
	// 获取我的设计
	public static final int REQUEST_SV_POST_DESIGN_ALL = 0X3005;

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
	// 偏好设置Key-记录用户报名的课程Id
	public static final String KEY_SIGN_UP_ID = "sign_up_id";
	// 偏好设置Key-记录用户登录授权码
	public static final String KEY_X_APP_TOKEN = "x_app_token";
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
	// 偏好设置Key-记录刷新用户资料
	public static final String KEY_UPDATE_USER_DATA = "update_user_data";
	// 偏好设置Key-记录刷新"我的"数据
	public static final String KEY_UPDATE_MINE_DATA = "update_mine_data";
	// 偏好设置Key-记录推送服务的开关状态
	public static final String KEY_PUSH_STATUS = "push_status";
	// 偏好设置Key-记录首页当前的下标索引
	public static final String KEY_MAIN_CURRENT_INDEX = "main_current_index";
	// 偏好设置Key-记录是否跳转子页面
	public static final String KEY_JUMP_PAGE = "jump_page";
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

	public static final String ACTIVITY_KEY_PHOTO_PATH = "photo_path";
	public static final String ACTIVITY_KEY_USER_INFO = "user_info";
	public static final String ACTIVITY_KEY_SELECT_LIST = "select_list";
	public static final String ACTIVITY_KEY_CHOICE_DATE = "choice_date";

	public static final int ACTIVITY_CODE_VIA_CAMERA = 0X9001;
	public static final int ACTIVITY_CODE_USER_NICK = 0X9002;
	public static final int ACTIVITY_CODE_USER_GENDER = 0X9003;
	public static final int ACTIVITY_CODE_USER_AREA = 0X9004;
	public static final int ACTIVITY_CODE_USER_INTRO = 0X9005;
	public static final int ACTIVITY_CODE_CHOICE_DATE = 0X9006;

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