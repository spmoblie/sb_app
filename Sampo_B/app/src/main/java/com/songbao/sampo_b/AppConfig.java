package com.songbao.sampo_b;

import android.Manifest;
import android.os.Environment;


public class AppConfig {

	/**
	 ******************************************* URL协议开始 ******************************************
	 */

	// 发布控制
	public static final boolean IS_PUBLISH = true;
	// http协议
	private final static String APP_HTTP = "http://";
	// https协议
	private final static String APP_HTTPS = "https://";
	// Base类型
	public final static String BASE_TYPE = "base_1"; //base_1:发布、base_2:测试
	// 发布Url
	public final static String BASE_URL_1 = APP_HTTP + "xiaobao.sbwg.cn/";
	// 测试Url
	public final static String BASE_URL_2 = APP_HTTP + "test.sbwg.cn/";
	// 推广域名
	public final static String SHARE_URL = BASE_URL_1;
	// 图片域名
	public final static String IMAGE_URL = "file:///android_asset/";
	// 关于我们
	public final static String ABOUT_US = "https://www.sbwg.cn/";

	// 校验设备
	public final static String URL_AUTH_DEVICE = "app/auth/deviceToken";
	// 提交登录
	public final static String URL_AUTH_LOGIN = "app/auth/login";
	// 提交注销
	public final static String URL_AUTH_LOGOUT = "app/auth/logout";

	// 支付参数
	public final static String URL_PAY_PARAMETER = "app/payment/paymentType";
	// 支付结果
	public final static String URL_PAY_CHECK_RESULT = "app/payment/callback";

	// 上传接口
	public final static String URL_UPLOAD_PUSH = "app/upload/push";

	// 首页头部
	public final static String URL_HOME_BANNER = "app/home/index";
	// 首页列表
	public final static String URL_HOME_LIST = "app/activity/list";

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

	// 获取资料
	public final static String URL_USER_GET = "app/user/get";
	// 修改资料
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
	public final static String URL_ADDRESS_LIST = "trade/app/consignee/list";
	// 修改地址
	public final static String URL_ADDRESS_EDIT = "trade/app/consignee/edit";
	// 删除地址
	public final static String URL_ADDRESS_DELETE = "trade/app/consignee/delete";
	// 默认地址
	public final static String URL_ADDRESS_DEFAULT = "trade/app/consignee/setConsignee";

	// 订单地址
	public final static String URL_ORDER_UPDATE = "trade/app/order/update";

	// 提交定制
	public final static String URL_BOOKING_CREATE = "trade/app/booking/create";
	// 取消定制
	public final static String URL_BOOKING_CANCEL = "trade/app/booking/cancel";
	// 删除定制
	public final static String URL_BOOKING_DELETE = "trade/app/booking/delete";
	// 定制列表
	public final static String URL_BOOKING_LIST = "trade/app/booking/list";
	// 定制详情
	public final static String URL_BOOKING_INFO = "trade/app/booking/info";
	// 确认设计
	public final static String URL_BOOKING_DESIGNS = "trade/app/booking/designs";
	// 确认支付
	public final static String URL_BOOKING_PAYMENT = "trade/app/booking/payment";
	// 确认收货
	public final static String URL_BOOKING_RECEIPT = "trade/app/booking/received";
	// 确认安装
	public final static String URL_BOOKING_INSTALL = "trade/app/booking/installed";

	/**
	 ******************************************* URL协议结束 ******************************************

	 ******************************************* RequestCode协议开始 ******************************************
	 */

	// 动态授权
	public static final int REQUEST_CORD_PERMISSION = 0X0001;
	// 访问授权
	public static final int REQUEST_SV_GET_SESSIONS = 0X0002;
	// 检测版本
	public static final int REQUEST_SV_POST_VERSION = 0X0003;

	// 提交登录
	public static final int REQUEST_SV_AUTH_LOGIN = 0X0121;
	// 授权登录
	public static final int REQUEST_SV_AUTH_OAUTH = 0X0131;
	// 微信授权
	public static final int REQUEST_SV_AUTH_WX_TOKEN = 0X0132;
	// 微信资料
	public static final int REQUEST_SV_AUTH_WX_USER = 0X0133;
	// 微博资料
	public static final int REQUEST_SV_AUTH_WB_USER = 0X0134;

	// 支付参数
	public static final int REQUEST_SV_PAY_PARAMETER = 0X0201;
	// 支付结果
	public static final int REQUEST_SV_PAY_CHECK_RESULT = 0X0202;

	// 首頁头部
	public static final int REQUEST_SV_HOME_HEAD = 0X1001;
	// 首页列表
	public static final int REQUEST_SV_HOME_LIST = 0X1002;

	// 上传头像
	public static final int REQUEST_SV_UPLOAD_HEAD = 0X3001;
	// 获取资料
	public static final int REQUEST_SV_USER_GET = 0X3002;
	// 修改资料
	public static final int REQUEST_SV_USER_SAVE = 0X3003;
	// 动态数据
	public static final int REQUEST_SV_USER_DYNAMIC = 0X3004;

	// 我的消息
	public static final int REQUEST_SV_USER_MESSAGE = 0X3101;
	// 定制设计
	public static final int REQUEST_SV_USER_DESIGNER = 0X3111;

	// 分类列表
	public static final int REQUEST_SV_SORT_LIST = 0X2001;
	// 商品列表
	public static final int REQUEST_SV_GOODS_LIST = 0X2011;
	// 筛选属性
	public static final int REQUEST_SV_SCREEN_ATTR = 0X2012;
	// 商品详情
	public static final int REQUEST_SV_GOODS_DETAIL = 0X2013;
	// 商品评价
	public static final int REQUEST_SV_GOODS_COMMENT = 0X2014;

	// 我的地址
	public static final int REQUEST_SV_ADDRESS_LIST = 0X3201;
	// 修改地址
	public static final int REQUEST_SV_ADDRESS_EDIT = 0X3202;
	// 删除地址
	public static final int REQUEST_SV_ADDRESS_DELETE = 0X3203;
	// 默认地址
	public static final int REQUEST_SV_ADDRESS_DEFAULT = 0X3204;

	// 订单地址
	public static final int REQUEST_SV_ORDER_UPDATE = 0X3301;

	// 提交定制
	public static final int REQUEST_SV_BOOKING_CREATE = 0X3402;
	// 取消定制
	public static final int REQUEST_SV_BOOKING_CANCEL = 0X3403;
	// 删除定制
	public static final int REQUEST_SV_BOOKING_DELETE = 0X3404;
	// 定制列表
	public static final int REQUEST_SV_BOOKING_LIST= 0X3411;
	// 定制详情
	public static final int REQUEST_SV_BOOKING_INFO = 0X3412;
	// 确认设计
	public static final int REQUEST_SV_BOOKING_DESIGNS = 0X3413;
	// 确认支付
	public static final int REQUEST_SV_BOOKING_PAYMENT = 0X3414;
	// 确认收货
	public static final int REQUEST_SV_BOOKING_RECEIPT = 0X3415;
	// 确认安装
	public static final int REQUEST_SV_BOOKING_INSTALL = 0X3416;

	// 商品售后
	public static final int REQUEST_SV_GOODS_SALE = 0X3501;
	// 退款详情
	public static final int REQUEST_SV_REFUND_DETAIL = 0X3511;
	// 评论照片
	public static final int REQUEST_SV_UPLOAD_COMMENT_PHOTO = 0X3521;

	/**
	 ******************************************* RequestCode协议结束 ******************************************

	 ******************************************* 全局常量设置开始 ******************************************
	 */

	// 主页子页面“我的”下标索引值
	public static final int PAGE_MAIN_MINE = 2;

	// 全局对话框“确定”
	public static final int DIALOG_CLICK_OK = 0X8666;
	// 全局对话框“取消”
	public static final int DIALOG_CLICK_NO = 0X8999;

	// 相片类型-圆形
	public static final int PHOTO_TYPE_ROUND = 0X8011;
	// 相片类型-方形
	public static final int PHOTO_TYPE_SQUARE = 0X8022;

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
	// 加载数据标识
	public static final int DATA_TYPE = 2;
	// 加载数据数量
	public static final int LOAD_SIZE = 10;
	// 加载缓冲时间
	public static final int LOADING_TIME = 200;
	// 验证码倒计时
	public static final long SEND_TIME = 60000;

	// 售后服务电话
	public static final String SALE_PHONE = "4008806558";
	// 联系客服网址-吐个槽
	public static final String SALE_HELP = "https://support.qq.com/product/135418";

	// 动态授权-权限集
	public static final String[] PERMISSIONS = new String[]{
			Manifest.permission.CAMERA,
			Manifest.permission.VIBRATE,
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE };

	// UM
	public static final String UM_MESSAGE_SECRET = "91f2dbb2d7f3c63d663cead167391580";
	// QQ AppID
	public static final String QQ_APP_ID = "1104891333";
	// QQ授权接口参数：Scope权限
	public static final String QQ_SCOPE = "all";
	// 微信AppID
	public static final String WX_APP_ID = "wx1202dda899b1358e";
	// 微信AppSecret
	public static final String WX_APP_SECRET = "5a9dd284d8b3976f123c9937f1568b70";
	// 微信商户号
	public static final String WX_MCH_ID = "1571075271";
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
	// 应用偏好设置名称
	public static final String APP_SP_NAME = "sp_sampo_b";
	// 应用缓存路径名称
	public static final String APP_ROOT_NAME = "Sampo_B";
	// 内置SD卡路径
	private static final String SD_PATH = Environment.getExternalStorageDirectory().toString() + "/" + APP_ROOT_NAME + "/";
	// Apk临时缓存路径（应用关闭时清除）
	public static final String SAVE_PATH_APK_DICE = SD_PATH + "apk/" + APP_ROOT_NAME + "_AD/";
	// 文本长久保存路径
	public static final String SAVE_PATH_TXT_SAVE = SD_PATH + "txt/" + APP_ROOT_NAME + "_TS/";
	// 文本临时缓存路径（应用关闭时清除）
	public static final String SAVE_PATH_TXT_DICE = SD_PATH + "txt/" + APP_ROOT_NAME + "_TD/";
	// 图片长久保存路径
	public static final String SAVE_PATH_IMAGE_SAVE = SD_PATH + "image/" + APP_ROOT_NAME + "_IS/";
	// 图片临时缓存路径（应用关闭时清除）
	public static final String SAVE_PATH_IMAGE_DICE = SD_PATH + "image/" + APP_ROOT_NAME + "_ID/";
	// 媒体长久保存路径
	public static final String SAVE_PATH_MEDIA_SAVE = SD_PATH + "media/" + APP_ROOT_NAME + "_MS/";
	// 媒体临时缓存路径（应用关闭时清除）
	public static final String SAVE_PATH_MEDIA_DICE = SD_PATH + "media/" + APP_ROOT_NAME + "_MD/";

	// 用户数据存储路径
	public static final String SAVE_USER_DATA_PATH = SD_PATH + "user/";
	// 用户头像存储路径
	public static final String SAVE_USER_HEAD_PATH = SAVE_PATH_IMAGE_SAVE + "user_head.png";

	/**
	 ******************************************* 全局常量设置结束 ******************************************

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
	public static final String KEY_USER_HEAD = "user_head";
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
	public static final int ACTIVITY_CODE_USER_NICK = 0X9011;
	public static final int ACTIVITY_CODE_USER_GENDER = 0X9012;
	public static final int ACTIVITY_CODE_USER_AREA = 0X9013;
	public static final int ACTIVITY_CODE_USER_INTRO = 0X9014;
	public static final int ACTIVITY_CODE_SELECT_ADDS = 0X9101;
	public static final int ACTIVITY_CODE_EDIT_ADDRESS = 0X9102;
	public static final int ACTIVITY_CODE_ORDER_UPDATE = 0X9111;

	/**
	 ******************************************* Activity协议结束 ******************************************

	 ******************************************* 推送协议开始 ******************************************
	 */

	// 刷新预约核销码
	public static final int PUSH_MSG_TYPE_001 = 8001;

	/**
	 ******************************************* 推送协议结束 ******************************************

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