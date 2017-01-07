package com.zncm.library.data;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by MX on 3/20 0020.
 */
public class Fields extends BaseData {
    @DatabaseField(generatedId = true)
    private int fields_id;
    @DatabaseField
    private String fields_name;
    @DatabaseField
    private boolean fields_notnull;
    @DatabaseField
    private int lib_id;
    @DatabaseField
    private int fields_type;
    @DatabaseField
    private Long fields_time;
    @DatabaseField
    private Long fields_modify_time;

    @DatabaseField
    private int fields_exi1; //排序用
    @DatabaseField
    private int fields_exi2;
    @DatabaseField
    private int fields_exi3;
    @DatabaseField
    private int fields_exi4;
    @DatabaseField
    private int fields_exi5;

    @DatabaseField
    private String fields_exs1;
    @DatabaseField
    private String fields_exs2;
    @DatabaseField
    private String fields_exs3;
    @DatabaseField
    private String fields_exs4;
    @DatabaseField
    private String fields_exs5;


    @DatabaseField
    private boolean fields_exb1;
    @DatabaseField
    private boolean fields_exb2;
    @DatabaseField
    private boolean fields_exb3;
    @DatabaseField
    private boolean fields_exb4;
    @DatabaseField
    private boolean fields_exb5;


    public Fields() {
    }

    public Fields(String fields_name, boolean fields_notnull, int lib_id, int fields_type) {
        this.fields_name = fields_name;
        this.fields_notnull = fields_notnull;
        this.lib_id = lib_id;
        this.fields_type = fields_type;
        this.fields_time = System.currentTimeMillis();
        this.fields_modify_time = System.currentTimeMillis();
    }

    public Fields(String fields_name, int lib_id) {
        this.fields_name = fields_name;
        this.fields_notnull = false;
        this.lib_id = lib_id;
        this.fields_type = EnumData.FieldsTypeEnum.FIELDS_TEXT.getValue();
        this.fields_time = System.currentTimeMillis();
        this.fields_modify_time = System.currentTimeMillis();
    }


    public int getFields_id() {
        return fields_id;
    }

    public void setFields_id(int fields_id) {
        this.fields_id = fields_id;
    }

    public String getFields_name() {
        return fields_name;
    }

    public void setFields_name(String fields_name) {
        this.fields_name = fields_name;
    }

    public boolean isFields_notnull() {
        return fields_notnull;
    }

    public void setFields_notnull(boolean fields_notnull) {
        this.fields_notnull = fields_notnull;
    }

    public int getLib_id() {
        return lib_id;
    }

    public void setLib_id(int lib_id) {
        this.lib_id = lib_id;
    }

    public int getFields_type() {
        return fields_type;
    }

    public void setFields_type(int fields_type) {
        this.fields_type = fields_type;
    }

    public Long getFields_time() {
        return fields_time;
    }

    public void setFields_time(Long fields_time) {
        this.fields_time = fields_time;
    }

    public Long getFields_modify_time() {
        return fields_modify_time;
    }

    public void setFields_modify_time(Long fields_modify_time) {
        this.fields_modify_time = fields_modify_time;
    }


    public int getFields_exi1() {
        return fields_exi1;
    }

    public void setFields_exi1(int fields_exi1) {
        this.fields_exi1 = fields_exi1;
    }

    public int getFields_exi2() {
        return fields_exi2;
    }

    public void setFields_exi2(int fields_exi2) {
        this.fields_exi2 = fields_exi2;
    }

    public int getFields_exi3() {
        return fields_exi3;
    }

    public void setFields_exi3(int fields_exi3) {
        this.fields_exi3 = fields_exi3;
    }

    public int getFields_exi4() {
        return fields_exi4;
    }

    public void setFields_exi4(int fields_exi4) {
        this.fields_exi4 = fields_exi4;
    }

    public int getFields_exi5() {
        return fields_exi5;
    }

    public void setFields_exi5(int fields_exi5) {
        this.fields_exi5 = fields_exi5;
    }

    public String getFields_exs1() {
        return fields_exs1;
    }

    public void setFields_exs1(String fields_exs1) {
        this.fields_exs1 = fields_exs1;
    }

    public String getFields_exs2() {
        return fields_exs2;
    }

    public void setFields_exs2(String fields_exs2) {
        this.fields_exs2 = fields_exs2;
    }

    public String getFields_exs3() {
        return fields_exs3;
    }

    public void setFields_exs3(String fields_exs3) {
        this.fields_exs3 = fields_exs3;
    }

    public String getFields_exs4() {
        return fields_exs4;
    }

    public void setFields_exs4(String fields_exs4) {
        this.fields_exs4 = fields_exs4;
    }

    public String getFields_exs5() {
        return fields_exs5;
    }

    public void setFields_exs5(String fields_exs5) {
        this.fields_exs5 = fields_exs5;
    }

    public boolean isFields_exb1() {
        return fields_exb1;
    }

    public void setFields_exb1(boolean fields_exb1) {
        this.fields_exb1 = fields_exb1;
    }

    public boolean isFields_exb2() {
        return fields_exb2;
    }

    public void setFields_exb2(boolean fields_exb2) {
        this.fields_exb2 = fields_exb2;
    }

    public boolean isFields_exb3() {
        return fields_exb3;
    }

    public void setFields_exb3(boolean fields_exb3) {
        this.fields_exb3 = fields_exb3;
    }

    public boolean isFields_exb4() {
        return fields_exb4;
    }

    public void setFields_exb4(boolean fields_exb4) {
        this.fields_exb4 = fields_exb4;
    }

    public boolean isFields_exb5() {
        return fields_exb5;
    }

    public void setFields_exb5(boolean fields_exb5) {
        this.fields_exb5 = fields_exb5;
    }
}
