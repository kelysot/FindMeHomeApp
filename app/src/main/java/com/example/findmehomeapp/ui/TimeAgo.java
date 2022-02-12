package com.example.findmehomeapp.ui;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeAgo {
    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }
//
        String convTime = null;
        String prefix = "";
        String suffix = "Ago";
        Date nowTime = new Date();
        long now = nowTime.getTime();
        long dateDiff = now - time;

        long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
        long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
        long hour = TimeUnit.MILLISECONDS.toHours(dateDiff);
        long day = TimeUnit.MILLISECONDS.toDays(dateDiff);

        if (second < 60) {
            convTime = second + " Seconds " + suffix;
        } else if (minute < 60) {
            convTime = minute + " Minutes " + suffix;
        } else if (hour < 24) {
            convTime = hour + " Hours " + suffix;
        } else if (day >= 7) {
            if (day > 360) {
                convTime = (day / 360) + " Years " + suffix;
            } else if (day > 30) {
                convTime = (day / 30) + " Months " + suffix;
            } else {
                convTime = (day / 7) + " Week " + suffix;
            }
        } else if (day < 7) {
            convTime = day + " Days " + suffix;
        }

        return convTime;
    }

}
