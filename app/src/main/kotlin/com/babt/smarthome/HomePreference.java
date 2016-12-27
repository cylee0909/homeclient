package com.babt.smarthome;

import com.cylee.androidlib.util.PreferenceUtils;

/**
 * Created by cylee on 16/9/25.
 */

public enum HomePreference implements PreferenceUtils.DefaultValueInterface {
    ROOMS(null),
    APPID(""),
    AUTO_RUN(false),
    CHANGE_FILTER_USED_TIME(0L),
    CHANGE_FILTER_TIP_TIME(System.currentTimeMillis());
    HomePreference(Object def) {
        mDef = def;
    }

    private Object mDef;
    @Override
    public Object getDefaultValue() {
        return mDef;
    }

    @Override
    public String getNameSpace() {
        return "HomePreference";
    }
}
