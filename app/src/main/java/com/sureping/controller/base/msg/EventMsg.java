package com.sureping.controller.base.msg;

import io.reactivex.functions.Consumer;

/**
 * Rabies
 *
 * @author USER
 * Date:   2019-04-22
 * Time:   上午 11:46
 */
public class EventMsg {
    int code ;
    public static final int KEY_OPEN_BLUETOOTH = 10001;
    public static final int KEY_OPEN_BLUETOOTH_RESULT = 10002;

    public EventMsg(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
