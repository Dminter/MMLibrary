package com.zncm.library.data;

/**
 * Created by MX on 2014/8/21.
 */
public class ObjEvent {

    public int type;//1 project 2 task
    public BaseData obj;

    public ObjEvent() {
    }

    public ObjEvent(int type, BaseData obj) {
        this.type = type;
        this.obj = obj;
    }

}
