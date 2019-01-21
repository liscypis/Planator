package com.wojteklisowski.planator.utils;

import java.time.Duration;
import java.time.LocalTime;

public class ConvertTime {
    public static String convertTime(int minutes) {
        if(minutes < 60){
            return LocalTime.MIN.plus(
                    Duration.ofMinutes(minutes)
            ).toString() + "min";
        } else {
            return LocalTime.MIN.plus(
                    Duration.ofMinutes(minutes)
            ).toString() + "h";
        }
    }
}
