package com.sureping.controller.base.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sureping
 * @create 19-4-21.
 */
public class MsgConfig {
    private Map<String,String> map;
    private static final String KEY_SELECT = "SELECT";
    public MsgConfig() {
        this.map = new HashMap<>();
    }
    public void setSelectDevice(String selectDevice){
        map.put(KEY_SELECT,selectDevice);
    }
    public String getSelectDevice(){
        return map.get(KEY_SELECT);
    }
}
