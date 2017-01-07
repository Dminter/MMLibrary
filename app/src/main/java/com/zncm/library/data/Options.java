package com.zncm.library.data;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by MX on 3/20 0020.
 */
public class Options extends BaseData {
    @DatabaseField(generatedId = true)
    private int options_id;
    @DatabaseField
    private int lib_id;
    @DatabaseField
    private int fields_id;
    @DatabaseField
    private String options_name;
    @DatabaseField
    private Long item_time;
    @DatabaseField
    private Long item_modify_time;


    @DatabaseField
    private int options_exi1;
    @DatabaseField
    private int options_exi2;
    @DatabaseField
    private int options_exi3;
    @DatabaseField
    private int options_exi4;
    @DatabaseField
    private int options_exi5;

    @DatabaseField
    private String options_exs1;
    @DatabaseField
    private String options_exs2;
    @DatabaseField
    private String options_exs3;
    @DatabaseField
    private String options_exs4;
    @DatabaseField
    private String options_exs5;


    @DatabaseField
    private boolean options_exb1;
    @DatabaseField
    private boolean options_exb2;
    @DatabaseField
    private boolean options_exb3;
    @DatabaseField
    private boolean options_exb4;
    @DatabaseField
    private boolean options_exb5;


    public Options() {
    }

    public Options(int lib_id, int fields_id, String options_name) {
        this.lib_id = lib_id;
        this.fields_id = fields_id;
        this.options_name = options_name;
    }

    public int getOptions_id() {
        return options_id;
    }

    public void setOptions_id(int options_id) {
        this.options_id = options_id;
    }

    public int getLib_id() {
        return lib_id;
    }

    public void setLib_id(int lib_id) {
        this.lib_id = lib_id;
    }

    public int getFields_id() {
        return fields_id;
    }

    public void setFields_id(int fields_id) {
        this.fields_id = fields_id;
    }

    public String getOptions_name() {
        return options_name;
    }

    public void setOptions_name(String options_name) {
        this.options_name = options_name;
    }

    public Long getItem_time() {
        return item_time;
    }

    public void setItem_time(Long item_time) {
        this.item_time = item_time;
    }

    public Long getItem_modify_time() {
        return item_modify_time;
    }

    public void setItem_modify_time(Long item_modify_time) {
        this.item_modify_time = item_modify_time;
    }


    public int getOptions_exi1() {
        return options_exi1;
    }

    public void setOptions_exi1(int options_exi1) {
        this.options_exi1 = options_exi1;
    }

    public int getOptions_exi2() {
        return options_exi2;
    }

    public void setOptions_exi2(int options_exi2) {
        this.options_exi2 = options_exi2;
    }

    public int getOptions_exi3() {
        return options_exi3;
    }

    public void setOptions_exi3(int options_exi3) {
        this.options_exi3 = options_exi3;
    }

    public int getOptions_exi4() {
        return options_exi4;
    }

    public void setOptions_exi4(int options_exi4) {
        this.options_exi4 = options_exi4;
    }

    public int getOptions_exi5() {
        return options_exi5;
    }

    public void setOptions_exi5(int options_exi5) {
        this.options_exi5 = options_exi5;
    }

    public String getOptions_exs1() {
        return options_exs1;
    }

    public void setOptions_exs1(String options_exs1) {
        this.options_exs1 = options_exs1;
    }

    public String getOptions_exs2() {
        return options_exs2;
    }

    public void setOptions_exs2(String options_exs2) {
        this.options_exs2 = options_exs2;
    }

    public String getOptions_exs3() {
        return options_exs3;
    }

    public void setOptions_exs3(String options_exs3) {
        this.options_exs3 = options_exs3;
    }

    public String getOptions_exs4() {
        return options_exs4;
    }

    public void setOptions_exs4(String options_exs4) {
        this.options_exs4 = options_exs4;
    }

    public String getOptions_exs5() {
        return options_exs5;
    }

    public void setOptions_exs5(String options_exs5) {
        this.options_exs5 = options_exs5;
    }

    public boolean isOptions_exb1() {
        return options_exb1;
    }

    public void setOptions_exb1(boolean options_exb1) {
        this.options_exb1 = options_exb1;
    }

    public boolean isOptions_exb2() {
        return options_exb2;
    }

    public void setOptions_exb2(boolean options_exb2) {
        this.options_exb2 = options_exb2;
    }

    public boolean isOptions_exb3() {
        return options_exb3;
    }

    public void setOptions_exb3(boolean options_exb3) {
        this.options_exb3 = options_exb3;
    }

    public boolean isOptions_exb4() {
        return options_exb4;
    }

    public void setOptions_exb4(boolean options_exb4) {
        this.options_exb4 = options_exb4;
    }

    public boolean isOptions_exb5() {
        return options_exb5;
    }

    public void setOptions_exb5(boolean options_exb5) {
        this.options_exb5 = options_exb5;
    }
}
