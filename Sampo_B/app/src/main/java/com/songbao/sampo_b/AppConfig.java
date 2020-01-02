package com.songbao.sampo_b;

import android.Manifest;
import android.os.Environment;


public class AppConfig {

	/**
	 ******************************************* URL协议开始 ******************************************
	 */

	// 是否正式发布
	public static final boolean IS_PUBLISH = false;
	// http协议
	public final static String APP_HTTP = "http://";
	// https协议
	public final static String APP_HTTPS = "https://";
	// Base类型
	public final static String BASE_TYPE = "base_2"; //base_1:发布Url、base_2:测试Url
	// Base域名
	public final static String BASE_URL_1 = APP_HTTPS + "xiaobao.sbwg.cn/";
	// Base域名
	public final static String BASE_URL_2 = APP_HTTP + "192.168.1.110:8080/";
	public final static String BASE_URL_3 = APP_HTTP + "192.168.1.100:8802/";
	// 图片域名
	public final static String IMAGE_URL = "file:///android_asset/";
	// 推广域名
	public final static String SHARE_URL = BASE_URL_1;
	// 关于我们
	public final static String ABOUT_US = "https://www.sbwg.cn/";

	// 保存设备号
	public final static String URL_AUTH_DEVICE = "/app/auth/deviceToken";
	// 提交登录
	public final static String URL_AUTH_LOGIN = "app/auth/login";
	// 提交注销
	public final static String URL_AUTH_LOGOUT = "app/auth/logout";

	// 提交支付参数
	public final static String URL_PAY_PARAMETER = "app/payment/paymentType";
	// 查询支付结果
	public final static String URL_PAY_CHECK_RESULT = "app/payment/callback";

	// 首页轮播列表
	public final static String URL_HOME_BANNER = "app/home/index";
	// 首页活动列表
	public final static String URL_HOME_LIST = "app/activity/list";

	// 获取用户资料
	public final static String URL_USER_GET = "app/user/get";
	// 修改用户资料
	public final static String URL_USER_SAVE = "app/user/save";

	// 动态数据
	public final static String URL_USER_DYNAMIC = "app/user/dynamic";
	// 我的消息
	public final static String URL_USER_MESSAGE = "app/user/message";
	// 消息状态
	public final static String URL_USER_MESSAGE_STATUS = "app/user/message/updateStatus";
	// 驻店设计
	public final static String URL_USER_DESIGNER = "app/user/designer/getList";
	// 我的地址
	public final static String URL_USER_ADDRESS = "app/consignee/list";
	// 修改地址
	public final static String URL_USER_ADDRESS_EDIT = "app/consignee/edit";
	// 删除地址
	public final static String URL_USER_ADDRESS_DELETE = "app/consignee/delete";
	// 默认地址
	public final static String URL_USER_ADDRESS_DEFAULT = "app/consignee/setConsignee";

	// 分类列表
	public final static String URL_SORT_LIST = "app/shopping/getCatInfo";
	// 筛选属性
	public final static String URL_SCREEN_ATTR = "app/shopping/getAttrValues";
	// 商品列表
	public final static String URL_GOODS_LIST = "app/shopping/searchGoodsInfo";
	// 商品详情
	public final static String URL_GOODS_DETAIL = "app/shopping/goodsInfo/offlineDetail";
	// 商品评价
	public final static String URL_GOODS_COMMENT = "app/shopping/goodsInfo/goodsInfoEvaluation";
	// 订单收货地址
	public final static String URL_ORDER_UPDATE = "app/order/update";
	// 取消订单
	public final static String URL_ORDER_CANCEL = "app/order/cancel";
	// 删除订单
	public final static String URL_ORDER_DELETE = "app/order/delete";
	// 提交定制
	public final static String URL_BOOKING_CREATE = "app/booking/create";
	// 定制列表
	public final static String URL_BOOKING_LIST = "app/booking/list";
	// 定制详情
	public final static String URL_BOOKING_INFO = "app/booking/Info";
	// 确认效果图
	public final static String URL_CONFIRM_DESIGNS = "app/booking/Designs";
	// 确认支付
	public final static String URL_CONFIRM_PAYMENT = "app/booking/Payment";
	// 确认收货
	public final static String URL_CONFIRM_RECEIPT = "app/booking/Recieved";
	// 确认安装
	public final static String URL_CONFIRM_INSTALL = "app/booking/Installed";

	// 上传接口
	public final static String URL_UPLOAD_PUSH = "app/upload/push";

	/**
	 ******************************************* URL协议结束 ******************************************
	 */

	/**
	 ******************************************* RequestCode协议开始 ******************************************
	 */

	// 授权监听
	public static final int REQUEST_CORD_PERMISSION = 0X1000;
	// 校验Sessions
	public static final int REQUEST_SV_GET_SESSIONS = 0X1001;
	// 检测版本更新
	public static final int REQUEST_SV_POST_VERSION = 0X1002;
	// 提交登录
	public static final int REQUEST_SV_AUTH_LOGIN = 0X1007;
	// 微信授权token
	public static final int REQUEST_SV_AUTH_WX_TOKEN = 0X1011;
	// 微信用户信息
	public static final int REQUEST_SV_AUTH_WX_USER = 0X1012;
	// 微博用户信息
	public static final int REQUEST_SV_AUTH_WB_USER = 0X1013;

	// 提交支付参数
	public static final int REQUEST_SV_PAY_PARAMETER = 0X1101;
	// 查询支付结果
	public static final int REQUEST_SV_PAY_CHECK_RESULT = 0X1102;

	// 首頁头部
	public static final int REQUEST_SV_HOME_HEAD = 0X2001;
	// 首页列表
	public static final int REQUEST_SV_HOME_LIST = 0X2002;

	// 上传用户头像
	public static final int REQUEST_SV_UPLOAD_HEAD = 0X3001;
	// 获取用户资料
	public static final int REQUEST_SV_USER_GET = 0X3002;
	// 修改用户资料
	public static final int REQUEST_SV_USER_SAVE = 0X3003;
	// 获取动态数据
	public static final int REQUEST_SV_USER_DYNAMIC = 0X3004;
	// 获取我的消息
	public static final int REQUEST_SV_USER_MESSAGE = 0X3005;
	// 获取驻店设计
	public static final int REQUEST_SV_USER_DESIGNER = 0X3007;
	// 获取我的地址
	public static final int REQUEST_SV_USER_ADDRESS = 0X3020;
	// 新建修改地址
	public static final int REQUEST_SV_USER_ADDRESS_EDIT = 0X3021;
	// 删除收货地址
	public static final int REQUEST_SV_USER_ADDRESS_DELETE = 0X3022;
	// 设置默认地址
	public static final int REQUEST_SV_USER_ADDRESS_DEFAULT = 0X3023;

	// 获取分类列表
	public static final int REQUEST_SV_SORT_LIST = 0X4001;
	// 获取商品列表
	public static final int REQUEST_SV_GOODS_LIST = 0X4003;
	// 获取筛选属性
	public static final int REQUEST_SV_SCREEN_ATTR = 0X4004;
	// 获取商品详情
	public static final int REQUEST_SV_GOODS_DETAIL = 0X4005;
	// 获取商品评价
	public static final int REQUEST_SV_GOODS_COMMENT = 0X4007;
	// 订单收货地址
	public static final int REQUEST_SV_ORDER_UPDATE = 0X4010;
	// 取消订单
	public static final int REQUEST_SV_ORDER_CANCEL = 0X4011;
	// 删除订单
	public static final int REQUEST_SV_ORDER_DELETE = 0X4012;
	// 提交定制
	public static final int REQUEST_SV_BOOKING_CREATE = 0X4021;
	// 定制列表
	public static final int REQUEST_SV_BOOKING_LIST= 0X4022;
	// 定制详情
	public static final int REQUEST_SV_BOOKING_INFO = 0X4023;
	// 确认效果图
	public static final int REQUEST_SV_CONFIRM_DESIGNS = 0X4024;
	// 确认支付
	public static final int REQUEST_SV_CONFIRM_PAYMENT = 0X4025;
	// 确认收货
	public static final int REQUEST_SV_CONFIRM_RECEIPT = 0X4026;
	// 确认安装
	public static final int REQUEST_SV_CONFIRM_INSTALL = 0X4027;

	// 上传评论照片
	public static final int REQUEST_SV_UPLOAD_COMMENT_PHOTO = 0X5001;
	// 商品售后信息
	public static final int REQUEST_SV_GOODS_SALE = 0X5002;
	// 商品退款详情
	public static final int REQUEST_SV_REFUND_DETAIL = 0X5003;

	/**
	 ******************************************* RequestCode协议结束 ******************************************
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

	// 订单状态码
	public static final int ORDER_STATUS_101 = 101; //待付款
	public static final int ORDER_STATUS_102 = 102; //已取消
	public static final int ORDER_STATUS_201 = 201; //生产中
	public static final int ORDER_STATUS_301 = 301; //待发货
	public static final int ORDER_STATUS_302 = 302; //退款中
	public static final int ORDER_STATUS_303 = 303; //已退款
	public static final int ORDER_STATUS_401 = 401; //待收货
	public static final int ORDER_STATUS_501 = 501; //已签收
	public static final int ORDER_STATUS_701 = 701; //待安装
	public static final int ORDER_STATUS_801 = 800; //已完成

	// 图片宽高比例
	public static final int IMG_WIDTHS = 16;
	public static final int IMG_HEIGHT = 9;
	// 网络请求标识
	public static final String LOAD_TYPE = "APP";
	// 加载数据数量
	public static final String LOAD_SIZE = "10";
	// 加载缓冲时间
	public static final int LOADING_TIME = 200;
	// 验证码倒计时
	public static final long SEND_TIME = 60000;

	// 动态授权-权限集
	public static final String[] PERMISSIONS = new String[]{
			Manifest.permission.CAMERA,
			Manifest.permission.VIBRATE,
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE };

	// UM
	public static final String UM_MESSAGE_SECRET = "bd15bd03b6f410f2b5c0bfb5e7d470cb";
	// QQ AppID
	public static final String QQ_APP_ID = "1104891333";
	// QQ授权接口参数：Scope权限
	public static final String QQ_SCOPE = "all";
	// 微信AppID
	public static final String WX_APP_ID = "wx3752761ec2277f9a";
	// 微信AppSecret
	public static final String WX_APP_SECRET = "84221323948ca7853d110a3b1b7c5aa3";
	// 微信商户号
	public static final String WX_MCH_ID = "1519773011";
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
	 ******************************************* 偏好设置Key协议开始 ******************************************
	 */

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
	// 偏好设置Key-记录用户上传相片地址
	public static final String KEY_POST_PHOTO_URL = "post_photo_url";
	// 偏好设置Key-记录用户新消息数量
	public static final String KEY_USER_MSG_NUM = "user_msg_num";
	// 偏好设置Key-记录用户登录授权码
	public static final String KEY_X_APP_TOKEN = "x_app_token";
	// 偏好设置Key-记录用户登录设备号
	public static final String KEY_DEVICE_TOKEN = "device_token";
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
	// 偏好设置Key-记录是否打开我的消息
	public static final String KEY_OPEN_MESSAGE = "open_message";
	// 偏好设置Key-记录最近一次更新APP版本的时间
	public static final String KEY_UPDATE_VERSION_LAST_TIME = "update_version_last_time";

	/**
	 ******************************************* 偏好设置Key协议结束 ******************************************
	 */

	/**
	 ******************************************* Activity协议开始 ******************************************
	 */

	public static final String PAGE_TYPE = "page_type";
	public static final String PAGE_DATA = "page_data";
	public static final String ACTIVITY_KEY_PAY_RESULT = "pay_result";
	public static final String ACTIVITY_KEY_PHOTO_PATH = "photo_path";
	public static final String ACTIVITY_KEY_USER_INFO = "user_info";
	public static final String ACTIVITY_KEY_SELECT_LIST = "select_list";

	public static final int ACTIVITY_CODE_VIA_CAMERA = 0X9001;
	public static final int ACTIVITY_CODE_PAY_DATA = 0X9002;
	public static final int ACTIVITY_CODE_USER_NICK = 0X9003;
	public static final int ACTIVITY_CODE_USER_GENDER = 0X9004;
	public static final int ACTIVITY_CODE_USER_AREA = 0X9005;
	public static final int ACTIVITY_CODE_USER_INTRO = 0X9006;
	public static final int ACTIVITY_CODE_SELECT_ADDS = 0X9007;
	public static final int ACTIVITY_CODE_EDIT_ADDRESS = 0X9008;
	public static final int ACTIVITY_CODE_ORDER_UPDATE = 0X9009;

	/**
	 ******************************************* Activity协议结束 ******************************************
	 */

	/**
	 ******************************************* 推送协议开始 ******************************************
	 */

	// 刷新预约核销码
	public static final int PUSH_MSG_TYPE_001 = 8001;

	/**
	 ******************************************* 推送协议结束 ******************************************
	 */

	/**
	 ******************************************* 广播协议开始 ******************************************
	 */

	public static final String RA_PAGE_MAIN = "receiver_action_main";
	public static final String RA_PAGE_MAIN_KEY = "receiver_action_main_key";
	public static final String RA_PAGE_RESERVE = "receiver_action_reserve";
	public static final String RA_PAGE_RESERVE_KEY = "receiver_action_reserve_key";
	public static final String RA_PAGE_LOGIN = "receiver_action_login";

	/**
	 ******************************************* 广播协议结束 ******************************************
	 */

}