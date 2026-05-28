package com.pge.kraken.cis.utils;

import java.time.LocalDateTime;

public final class DateUtil {

    private DateUtil() {
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}
