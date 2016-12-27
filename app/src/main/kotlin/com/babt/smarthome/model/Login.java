package com.babt.smarthome.model;

import com.android.volley.Request;
import com.cylee.androidlib.net.InputBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cylee on 16/12/18.
 */

public class Login {
    public String id;
    public static class Input extends InputBase {
        private String name;
        private String passd;
        private Input(String name, String passd){
            this.name = name;
            this.passd = passd;
            this.url = "/login";
            this.method = Request.Method.POST;
            this.aClass = Login.class;
        }
        @Override
        public Map<String, Object> getParams() {
            Map<String, Object> params = new HashMap<>();
            params.put("loginName", name);
            params.put("loginPassd", passd);
            return params;
        }
    }


    public static InputBase buidInput(String name, String passd) {
        return new Input(name, passd);
    }
}
