package com.zncm.library.utils;

import android.content.Context;

import java.lang.reflect.Field;

/**
 * 反射机制根据资源文件名称得到R.java里面的int
 */
public class GetRIdUtils {
    public static int getLayoutIdUtils(Context context, String layoutKey) {
        try {

            Class<?> temp_class = Class.forName(context.getPackageName()
                    + ".R$layout");
            Field field = temp_class.getField(layoutKey);
            return field.getInt(layoutKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;

    }

    public static int getDrawableIdUtils(Context context, String drawableKey) {
        try {
            Class<?> temp_class = Class.forName(context.getPackageName()
                    + ".R$drawable");
            Field field = temp_class.getField(drawableKey);
            return field.getInt(drawableKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;

    }

    public static int getIdUtils(Context context, String idKey) {
        try {
            Class<?> temp_class = Class.forName(context.getPackageName()
                    + ".R$id");
            Field field = temp_class.getField(idKey);
            return field.getInt(idKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;

    }

}
