package com.example.tools;

import com.example.tools.utils.CommonUtils;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.List;

public class UtilTest {
    @Test
    public void md5Test() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        LocalDateTime nowDateTime = LocalDateTime.now();
        ZonedDateTime zonedNow = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        Instant instantNow = Instant.now();

        System.out.println("Today: " + today);
        System.out.println("Current Time: " + now);
        System.out.println("Current DateTime: " + nowDateTime);
        System.out.println("Current ZonedDateTime: " + zonedNow);
        System.out.println("Current Instant: " + instantNow);

        Duration duration = Duration.between(LocalTime.of(14, 0, 0), LocalTime.of(16, 30, 0));
        System.out.println("Duration: " + duration.toHours() + " hours and " + duration.toMinutes() % 60 + " minutes");
//        System.out.println(CommonUtils.getMD5Hash("123456"));
    }
}
