package com.sankuai.waimai.router.utils;

public class CommonUtils {
    /**
     * Returns true if the string is null or 0-length.
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * 字符串首字母大写
     *
     * @param self 字符串
     * @return 转换后的字符串
     */
    public static String capitalize(CharSequence self) {
        return self.length() == 0 ? "" : "" + Character.toUpperCase(self.charAt(0)) + self.subSequence(1, self.length());
    }
}
