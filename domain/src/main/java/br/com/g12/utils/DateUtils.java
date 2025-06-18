package br.com.g12.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static Date truncateToSeconds(Date date) {
        if (date == null) return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

//    private static final ZoneId DEFAULT_ZONE = ZoneId.of("America/Sao_Paulo");
//
//    public static Date toUtc(Date localDate) {
//        if (localDate == null) return null;
//
//        Instant utcInstant = localDate.toInstant()
//                .atZone(DEFAULT_ZONE)
//                .withZoneSameLocal(ZoneOffset.UTC)
//                .toInstant();
//
//        return Date.from(utcInstant);
//    }
}