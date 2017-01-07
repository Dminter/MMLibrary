package com.zncm.library.data;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

public class BaseData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        Object obj = JSON.toJSON(this);
        return obj.toString();

    }

}
