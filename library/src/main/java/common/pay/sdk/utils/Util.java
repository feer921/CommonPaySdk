package common.pay.sdk.utils;

import android.util.Log;

import java.util.Iterator;
import java.util.Map;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-09-26
 * Time: 19:45
 * DESC:
 */
public class Util {
    public static boolean isEmpty(CharSequence charSequence) {
        if (charSequence == null || charSequence.toString().trim()
                .length() == 0 || charSequence.length() == 0 || "null".equalsIgnoreCase(charSequence.toString())) {
            return true;
        }
        return false;
    }

    public static void i(final String tag, Object... logInfos) {
        Log.i(tag, getInfo(logInfos));
    }
    private static String getInfo(Object... objs) {
        if (objs == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Object object : objs) {
            sb.append(object);
        }
        return sb.toString();
    }

    public static void e(final String tag, Object... logInfos) {
        Log.e(tag, getInfo(logInfos));
    }

    /**
     * 将一个Map集合中的Key Value 键值对以Key-Value之间 以及 每个Key-Value之间 按照分别指定的分隔符串接起来
     * @param map
     * @param kvSplitChar key value之间的分隔符 eg.: key-value  key:value key&value 其中的"-"、":"、"&"字符
     * @param groupKvSplitChar 每对Key value之间的分隔符 eg.: key1-value1;key2-value2;key3-value3; 其中的";"字符
     * @return 串接成的字符串
     */
    public static String concatMapKeyValues(Map map, String kvSplitChar, String groupKvSplitChar) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Iterator kvIterator = map.entrySet().iterator();
        while (kvIterator.hasNext()) {
            Map.Entry<Object,Object> kvEntry = (Map.Entry<Object, Object>) kvIterator.next();
            sb.append(kvEntry.getKey()).append(kvSplitChar).append(kvEntry.getValue()).append(groupKvSplitChar);
        }
//        for (Object key : map.keySet()) {
//            sb.append(key).append(kvSplitChar).append(map.get(key)).append(groupKvSplitChar);
//        }
        return sb.toString();
    }
}
