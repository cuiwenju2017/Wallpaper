package com.example.wallpaper.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 毫秒转日期类
 */
public class FormatedDateTime {
    public static String getFormatedDateTime(String pattern, long dateTime) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(pattern);
        return sDateFormat.format(new Date(dateTime + 0));
    }
}
