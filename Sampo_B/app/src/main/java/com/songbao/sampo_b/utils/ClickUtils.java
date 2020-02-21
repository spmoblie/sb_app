package com.songbao.sampo_b.utils;


public class ClickUtils {

    private static int lastButtonId = -1;
    private static long DIFF = 1000;
    private static long lastClickTime = 0;

    /**
     * 判断两次点击的间隔，如果小于1000，则认为是多次无效点击
     */
    public static boolean isDoubleClick() {
        return isDoubleClick(-1, DIFF);
    }

    /**
     * 判断两次点击的间隔，如果小于1000，则认为是多次无效点击
     */
    public static boolean isDoubleClick(int buttonId) {
        return isDoubleClick(buttonId, DIFF);
    }

    /**
     * 判断两次点击的间隔，如果小于diff，则认为是多次无效点击
     */
    public static boolean isDoubleClick(int buttonId, long diff) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (lastButtonId == buttonId && lastClickTime > 0 && timeD < diff) {
            LogUtil.i(LogUtil.LOG_TAG, "短时间内按钮多次触发");
            return true;
        }
        lastClickTime = time;
        lastButtonId = buttonId;
        return false;
    }

}
