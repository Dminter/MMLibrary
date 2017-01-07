package com.zncm.library.data;

public class EnumData {


    public enum FieldsTypeEnum {
        FIELDS_TEXT(0, "文本"), FIELDS_INT(
                1, "整数"), FIELDS_DOUBLE(
                2, "实数"), FIELDS_BOOLEAN(
                3, "布尔"), FIELDS_DATETIME(
                4, "日期/时间"), FIELDS_DATE(
                5, "日期"), FIELDS_TIME(
                6, "时间"),
        FIELDS_PICTURE(
                7, "图像"), FIELDS_START(
                8, "评级"), FIELDS_OPTIONS_ONE(
                9, "单选"), FIELDS_OPTIONS_MANY(
                10, "多选"), FIELDS_NET_PIC(
                11, "网络图片");
        private int value;
        public String strName;

        private FieldsTypeEnum(int value, String strName) {
            this.value = value;
            this.strName = strName;
        }

        public int getValue() {
            return value;
        }

        public String getStrName() {
            return strName;
        }


        public static FieldsTypeEnum nameOf(String strName) {
            for (FieldsTypeEnum typeEnum : FieldsTypeEnum.values()) {
                if (typeEnum.getStrName().equals(strName)) {
                    return typeEnum;
                }
            }
            return FieldsTypeEnum.FIELDS_TEXT;
        }


        public static FieldsTypeEnum valueOf(int value) {
            for (FieldsTypeEnum typeEnum : FieldsTypeEnum.values()) {
                if (typeEnum.value == value) {
                    return typeEnum;
                }
            }
            return null;
        }


    }

    public enum RefreshEnum {

        ITEMS(1, "ITEMS"), FIELDS(2, "FIELDS"), LIB(3, "LIB"), LOCLIB(4, "LOCLIB");
        private int value;
        private String strName;

        private RefreshEnum(int value, String strName) {
            this.value = value;
            this.strName = strName;
        }

        public int getValue() {
            return value;
        }

        public String getStrName() {
            return strName;
        }

    }
}
