package com.sbwg.sxb.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	public static boolean isNull(String str) {
		return str == null || str.length() == 0 || "".equals(str) || "null".equals(str);
	}

	public static boolean priceIsNull(String price) {
		return isNull(price) || price.equals("0") || price.equals("0.00");
	}

	public static boolean notNull(JSONObject jsonObj, String key) throws JSONException {
		return jsonObj.has(key) && !jsonObj.isNull(key) && !jsonObj.get(key).equals("");
	}

	public static int getInteger(String str) {
		if (isNull(str) || !isNumeric(str)) {
			return 0;
		}
		return Integer.parseInt(str);
	}

	public static double getDouble(String str) {
		if (isNull(str) || str.startsWith(".") ||
				str.contains(".") && str.substring(0, str.lastIndexOf(".")).contains(".")) {
			return 0.00;
		}
		return Double.parseDouble(str);
	}

	public static long getLong(String str) {
		if (isNull(str)) {
			return 0;
		}
		return Long.parseLong(str);
	}

	/***
	 * 匹配特殊字符，将其过滤
	 * @param edit
	 * @return
	 */
	public static String stringFilter(String edit) {
		String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<> \"/?~！@#￥%……&*（）——+|{}【】《》‘；：”“’。，、？]";
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(edit);
		return matcher.replaceAll("");
	}

	/**
	 * 手机号中间4位加密
	 * @param mobile
	 * @return 188****8888
	 */
	public static String replaceMobileNo(String mobile) {
		return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
	}

	/**
	 * 输入手机号时添加空格
	 * @param mobile
	 */
	public static String enterAddSpaces(String mobile) {
		if (isNull(mobile)) return "";
		mobile = mobile.replace(" ", "");
		int lens = mobile.length();
		if (lens == 3 || lens == 7) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < lens + 1; i++) {
				if (i == 3 || i == 7) {
					sb.append(" ");
				}
				if (i < lens) {
					sb.append(mobile.charAt(i));
				}
			}
			return sb.toString();
		}
		return mobile;
	}

	/**
	 * 转换手机号格式
	 * @param mobile
	 * @return 188 8888 8888
	 */
	public static String changeMobileNo(String mobile) {
		if (isNull(mobile) || !StringUtil.isMobileNO(mobile)) return "";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mobile.length(); i++) {
			if (i == 3 || i == 7) {
				sb.append(" ");
			}
			sb.append(mobile.charAt(i));
		}
		return sb.toString();
	}

	/**
	 * 正则表达式—手机格式
	 * "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$"
	 */
	public static boolean isMobileNO(String mobile) {
		String str = "^(1[3-9][0-9])\\d{8}$";
		return Pattern.compile(str).matcher(mobile).matches();
	}

	/**
	 * 正则表达式—email格式
	 */
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		return Pattern.compile(str).matcher(email).matches();
	}

	/**
	 * 正则表达式—全是数字
	 */
	public static boolean isNumeric(String number) {
		String str = "[0-9]*";
		return Pattern.compile(str).matcher(number).matches();
	}

	/**
	 * 正则表达式—密码格式
	 */
	public static boolean isPassword(String password) {
		String str = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$";
		return Pattern.compile(str).matcher(password).matches();
	}

}
