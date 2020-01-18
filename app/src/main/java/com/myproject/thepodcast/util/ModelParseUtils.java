package com.myproject.thepodcast.util;

import androidx.annotation.NonNull;

public final class ModelParseUtils {


    private ModelParseUtils(){}

    public static int toOptInt(@NonNull final Object object) {
        int val = 0;
        try {
            val  = Integer.parseInt(object.toString());
        } catch (Exception e) {

        }
        return val;
    }

    public static long toOptLong(@NonNull final Object object) {
        long val = 0L;
        try {
            val = (long) Double.parseDouble(object.toString());
        } catch (Exception e) {

        }
        return val;
    }


    public static String toOptString(@NonNull final Object object) {
        String val = "";
        try {
            val  = object.toString();
        } catch (Exception e) {

        }
        return val;
    }


    public static boolean toOptBoolean(@NonNull final Object object) {
        boolean val = false;
        try {
            val  = Boolean.parseBoolean(object.toString());
        } catch (Exception e) {

        }
        return val;
    }


}
