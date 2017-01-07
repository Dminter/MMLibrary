package com.zncm.library.data;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by MX on 3/20 0020.
 */
public class Items extends BaseData {
    @DatabaseField(generatedId = true)
    private int item_id;
    @DatabaseField
    private int lib_id;
    @DatabaseField
    private String item_json;
    @DatabaseField
    private Long item_time;
    @DatabaseField
    private Long item_modify_time;


    @DatabaseField
    private int item_exi1;
    @DatabaseField
    private int item_exi2;
    @DatabaseField
    private int item_exi3;
    @DatabaseField
    private int item_exi4;
    @DatabaseField
    private int item_exi5;

    @DatabaseField
    private String item_exs1;
    @DatabaseField
    private String item_exs2;
    @DatabaseField
    private String item_exs3;
    @DatabaseField
    private String item_exs4;
    @DatabaseField
    private String item_exs5;


    @DatabaseField
    private boolean item_exb1;
    @DatabaseField
    private boolean item_exb2;
    @DatabaseField
    private boolean item_exb3;
    @DatabaseField
    private boolean item_exb4;
    @DatabaseField
    private boolean item_exb5;


    public Items() {
    }

    public Items(int lib_id, String item_json) {
        this.lib_id = lib_id;
        this.item_json = item_json;
        this.item_time = System.currentTimeMillis();
        this.item_modify_time = System.currentTimeMillis();
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public int getLib_id() {
        return lib_id;
    }

    public void setLib_id(int lib_id) {
        this.lib_id = lib_id;
    }

    public String getItem_json() {
        return item_json;
    }

    public void setItem_json(String item_json) {
        this.item_json = item_json;
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

    public int getItem_exi1() {
        return item_exi1;
    }

    public void setItem_exi1(int item_exi1) {
        this.item_exi1 = item_exi1;
    }

    public int getItem_exi2() {
        return item_exi2;
    }

    public void setItem_exi2(int item_exi2) {
        this.item_exi2 = item_exi2;
    }

    public int getItem_exi3() {
        return item_exi3;
    }

    public void setItem_exi3(int item_exi3) {
        this.item_exi3 = item_exi3;
    }

    public int getItem_exi4() {
        return item_exi4;
    }

    public void setItem_exi4(int item_exi4) {
        this.item_exi4 = item_exi4;
    }

    public int getItem_exi5() {
        return item_exi5;
    }

    public void setItem_exi5(int item_exi5) {
        this.item_exi5 = item_exi5;
    }

    public String getItem_exs1() {
        return item_exs1;
    }

    public void setItem_exs1(String item_exs1) {
        this.item_exs1 = item_exs1;
    }

    public String getItem_exs2() {
        return item_exs2;
    }

    public void setItem_exs2(String item_exs2) {
        this.item_exs2 = item_exs2;
    }

    public String getItem_exs3() {
        return item_exs3;
    }

    public void setItem_exs3(String item_exs3) {
        this.item_exs3 = item_exs3;
    }

    public String getItem_exs4() {
        return item_exs4;
    }

    public void setItem_exs4(String item_exs4) {
        this.item_exs4 = item_exs4;
    }

    public String getItem_exs5() {
        return item_exs5;
    }

    public void setItem_exs5(String item_exs5) {
        this.item_exs5 = item_exs5;
    }

    public boolean isItem_exb1() {
        return item_exb1;
    }

    public void setItem_exb1(boolean item_exb1) {
        this.item_exb1 = item_exb1;
    }

    public boolean isItem_exb2() {
        return item_exb2;
    }

    public void setItem_exb2(boolean item_exb2) {
        this.item_exb2 = item_exb2;
    }

    public boolean isItem_exb3() {
        return item_exb3;
    }

    public void setItem_exb3(boolean item_exb3) {
        this.item_exb3 = item_exb3;
    }

    public boolean isItem_exb4() {
        return item_exb4;
    }

    public void setItem_exb4(boolean item_exb4) {
        this.item_exb4 = item_exb4;
    }

    public boolean isItem_exb5() {
        return item_exb5;
    }

    public void setItem_exb5(boolean item_exb5) {
        this.item_exb5 = item_exb5;
    }
}
