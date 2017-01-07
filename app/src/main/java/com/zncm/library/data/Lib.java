package com.zncm.library.data;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by MX on 3/20 0020.
 */
public class Lib extends BaseData {
    @DatabaseField(generatedId = true)
    private int lib_id;
    @DatabaseField
    private String lib_name;
    @DatabaseField
    private int lib_color;//置顶
    @DatabaseField
    private Long lib_time;
    @DatabaseField
    private Long lib_modify_time;


    @DatabaseField
    private int lib_exi1 = 0; //type user 0,sys 1,rss 2
    @DatabaseField
    private int lib_exi2;
    @DatabaseField
    private int lib_exi3;
    @DatabaseField
    private int lib_exi4;
    @DatabaseField
    private int lib_exi5;

    @DatabaseField
    private String lib_exs1;// desc描述 -rss 地址
    @DatabaseField
    private String lib_exs2;
    @DatabaseField
    private String lib_exs3;
    @DatabaseField
    private String lib_exs4;
    @DatabaseField
    private String lib_exs5;


    @DatabaseField
    private boolean lib_exb1;//是否内置库-不可修改
    @DatabaseField
    private boolean lib_exb2;
    @DatabaseField
    private boolean lib_exb3;
    @DatabaseField
    private boolean lib_exb4;
    @DatabaseField
    private boolean lib_exb5;


    public enum libType {
        //user 0,sys 1,rss 2
        user(0, "user"), sys(1, "sys"), rss(2, "rss"), net(3, "net");

        private String strName;
        private int value;

        private libType(int value, String strName) {
            this.value = value;
            this.strName = strName;
        }

        public String strName() {
            return strName;
        }

        public int value() {
            return value;
        }


    }


    public Lib() {
    }

    public Lib(String lib_name) {
        this.lib_name = lib_name;
        this.lib_time = System.currentTimeMillis();
        this.lib_modify_time = System.currentTimeMillis();
    }


    public Lib(String lib_name, boolean lib_exb1) {
        this.lib_name = lib_name;
        this.lib_time = System.currentTimeMillis();
        this.lib_modify_time = System.currentTimeMillis();
        this.lib_exb1 = lib_exb1;
    }
    public Lib(String lib_name,String lib_exs1, int lib_exi1) {
        this.lib_name = lib_name;
        this.lib_time = System.currentTimeMillis();
        this.lib_modify_time = System.currentTimeMillis();
        this.lib_exs1 = lib_exs1;
        this.lib_exi1 = lib_exi1;
    }

    public int getLib_id() {
        return lib_id;
    }

    public void setLib_id(int lib_id) {
        this.lib_id = lib_id;
    }

    public String getLib_name() {
        return lib_name;
    }

    public void setLib_name(String lib_name) {
        this.lib_name = lib_name;
    }

    public int getLib_color() {
        return lib_color;
    }

    public void setLib_color(int lib_color) {
        this.lib_color = lib_color;
    }

    public Long getLib_time() {
        return lib_time;
    }

    public void setLib_time(Long lib_time) {
        this.lib_time = lib_time;
    }

    public Long getLib_modify_time() {
        return lib_modify_time;
    }

    public void setLib_modify_time(Long lib_modify_time) {
        this.lib_modify_time = lib_modify_time;
    }

    public int getLib_exi1() {
        return lib_exi1;
    }

    public void setLib_exi1(int lib_exi1) {
        this.lib_exi1 = lib_exi1;
    }

    public int getLib_exi2() {
        return lib_exi2;
    }

    public void setLib_exi2(int lib_exi2) {
        this.lib_exi2 = lib_exi2;
    }

    public int getLib_exi3() {
        return lib_exi3;
    }

    public void setLib_exi3(int lib_exi3) {
        this.lib_exi3 = lib_exi3;
    }

    public int getLib_exi4() {
        return lib_exi4;
    }

    public void setLib_exi4(int lib_exi4) {
        this.lib_exi4 = lib_exi4;
    }

    public int getLib_exi5() {
        return lib_exi5;
    }

    public void setLib_exi5(int lib_exi5) {
        this.lib_exi5 = lib_exi5;
    }

    public String getLib_exs1() {
        return lib_exs1;
    }

    public void setLib_exs1(String lib_exs1) {
        this.lib_exs1 = lib_exs1;
    }

    public String getLib_exs2() {
        return lib_exs2;
    }

    public void setLib_exs2(String lib_exs2) {
        this.lib_exs2 = lib_exs2;
    }

    public String getLib_exs3() {
        return lib_exs3;
    }

    public void setLib_exs3(String lib_exs3) {
        this.lib_exs3 = lib_exs3;
    }

    public String getLib_exs4() {
        return lib_exs4;
    }

    public void setLib_exs4(String lib_exs4) {
        this.lib_exs4 = lib_exs4;
    }

    public String getLib_exs5() {
        return lib_exs5;
    }

    public void setLib_exs5(String lib_exs5) {
        this.lib_exs5 = lib_exs5;
    }

    public boolean isLib_exb1() {
        return lib_exb1;
    }

    public void setLib_exb1(boolean lib_exb1) {
        this.lib_exb1 = lib_exb1;
    }

    public boolean isLib_exb2() {
        return lib_exb2;
    }

    public void setLib_exb2(boolean lib_exb2) {
        this.lib_exb2 = lib_exb2;
    }

    public boolean isLib_exb3() {
        return lib_exb3;
    }

    public void setLib_exb3(boolean lib_exb3) {
        this.lib_exb3 = lib_exb3;
    }

    public boolean isLib_exb4() {
        return lib_exb4;
    }

    public void setLib_exb4(boolean lib_exb4) {
        this.lib_exb4 = lib_exb4;
    }

    public boolean isLib_exb5() {
        return lib_exb5;
    }

    public void setLib_exb5(boolean lib_exb5) {
        this.lib_exb5 = lib_exb5;
    }
}
