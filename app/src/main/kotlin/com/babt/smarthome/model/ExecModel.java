package com.babt.smarthome.model;

import com.android.volley.Request;
import com.babt.smarthome.HomePreference;
import com.cylee.androidlib.net.InputBase;
import com.cylee.androidlib.util.PreferenceUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cylee on 16/12/18.
 */

public class ExecModel {
    public String result;
    public static class Input extends InputBase {
        private String command;
        private Input(String command){
            this.url = "/exec";
            this.method = Request.Method.POST;
            this.aClass = ExecModel.class;
            this.command = command;
        }
        @Override
        public Map<String, Object> getParams() {
            Map<String, Object> params = new HashMap<>();
            params.put("appid", PreferenceUtils.getString(HomePreference.APPID));
            params.put("command", command);
            return params;
        }
    }


    public static InputBase buidInput(String command) {
        return new Input(command);
    }
}
