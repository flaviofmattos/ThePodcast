package com.myproject.thepodcast.repository;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class QueryParam {

    private Map<String, String> mQueryParams;

    private QueryParam(@NonNull final Map<String, String> queryParams) {
        mQueryParams = new HashMap<>();
        mQueryParams.putAll(queryParams);
    }

    public Map<String, String> getMapParams() {
        return new HashMap<>(mQueryParams);
    }

    public static class QueryParamBuilder {

        private Map<String, String> mQueryParams = new HashMap<>();

        public QueryParamBuilder put(@NonNull final String key, @NonNull final String value) {
            mQueryParams.put(key, value);
            return this;
        }

        public QueryParam build() {
            return new QueryParam(mQueryParams);
        }

    }

}
