package com.myproject.thepodcast.util;

import android.content.Context;

import androidx.annotation.NonNull;

import com.myproject.thepodcast.R;

public final class PeriodParseUtils {

    private PeriodParseUtils(){}

    public static String toHourMinSec(@NonNull final Context context, final long seconds) {
        long displaySeconds = seconds % 60;
        long min = seconds / 60;
        long displayMinutes = min % 60;
        long hours = min / 60;
        long displayHours = hours % 60;
        return context.getString(R.string.episode_duration_format, displayHours, displayMinutes, displaySeconds);
    }


}
