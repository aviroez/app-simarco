package com.ppproperti.simarco.utils;

import java.util.Locale;

public final class Constants {
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME =
            "com.ppproperti.simarco.utils";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";
    public static final int KPA_ID = 4;
    public static final int LIBUR_BAYAR_ID = 5;
    public static final String MALE = "m";
    public static final String FEMALE = "f";
    public static final int PICK_IMAGE_CAMERA = 101;
    public static final int PICK_IMAGE_GALLERY = 102;
    public static final int PICK_PHONE_NUMBER = 103;
    public static final double MAX_DISCOUNT = 50000000;
    public static final Locale locale = new Locale("in", "ID");
    public static final String DATE_FORMAT_SQL = "yyyy-MM-dd";
    public static final String DATE_FORMAT_LOCAL = "dd/MM/yyyy";
    public static final String DATE_FORMAT_LOCAL_S = "dd/MM/yyyy";
    public static final String DATE_FORMAT_LOCAL_M = "dd MMM yyyy";
    public static final String DATE_TIME_FORMAT_SQL = "yyyy-MM-dd hh:mm:ss";
    public static final String DATE_TIME_FORMAT_LOCAL = "dd/MM/yyyy hh:mm";
    public static final String DATE_TIME_FORMAT_LOCAL_M = "dd MMM yyyy hh:mm";
    public static final String DATE_TIME_ZONE = "Asia/Jakarta";
    public static final String DATE_TIME_SIMPLE = "yyyyMMdd_HHmmss";
}