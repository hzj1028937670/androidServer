package com.android.server.util;

import java.text.SimpleDateFormat;

public class SimpleDateUtil {

    /***
     * 将时间戳转换成时间数据
     * @param paramLong
     * @return
     */
    public static long formatBig(long paramLong) {
        return Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmss").format(Long.valueOf(paramLong)));
    }


}
