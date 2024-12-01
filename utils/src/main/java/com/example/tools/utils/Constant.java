package com.example.tools.utils;

import java.time.format.DateTimeFormatter;

public class Constant {
    public static final String ROLE_SUPER_ADMIN = "ROLE_SUPER_ADMIN";

    public static final String DATETIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";

    public static final String DATETIME_FORMAT_MIS_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN);
    public static final DateTimeFormatter DATETIME_MIS_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_FORMAT_MIS_PATTERN);

    public static final String SECURITY_HEADER_TOKEN = "token";

    public static final String SECURITY_HEADER_USERNAME = "username";

    public static final long SUPER_ADMIN_ROLE_ID = 1L;
}
