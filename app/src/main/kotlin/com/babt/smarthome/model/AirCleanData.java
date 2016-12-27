package com.babt.smarthome.model;

import com.cylee.androidlib.GsonBuilderFactory;

/**
 * Created by cylee on 16/12/25.
 */

public class AirCleanData {
    public int ipm;
    public int opm;
    public int hdy;
    public int tmp;
    public boolean autoRun;
    public boolean heat;

    public static AirCleanData fromJson(String json) {
        return GsonBuilderFactory.createBuilder().fromJson(json, AirCleanData.class);
    }
}
