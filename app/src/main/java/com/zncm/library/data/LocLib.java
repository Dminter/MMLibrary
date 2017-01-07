package com.zncm.library.data;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by MX on 3/20 0020.
 */
public class LocLib extends BaseData {
    @DatabaseField(generatedId = true)
    private int loc_id;
    @DatabaseField
    private String loc_name;
    @DatabaseField
    private String loc_tag;
    @DatabaseField
    private String loc_content;
    @DatabaseField
    private String loc_desc;
    @DatabaseField
    private String loc_icon;
    @DatabaseField
    private String loc_sort;
    @DatabaseField
    private String loc_date;
    @DatabaseField
    private int loc_status;//是否已经添加 0 没有 1添加
    @DatabaseField
    private Long loc_time;




    @DatabaseField
    private int loc_exi1;
    @DatabaseField
    private int loc_exi2;
    @DatabaseField
    private int loc_exi3;
    @DatabaseField
    private int loc_exi4;
    @DatabaseField
    private int loc_exi5;

    @DatabaseField
    private String loc_exs1;
    @DatabaseField
    private String loc_exs2;
    @DatabaseField
    private String loc_exs3;
    @DatabaseField
    private String loc_exs4;
    @DatabaseField
    private String loc_exs5;


    @DatabaseField
    private boolean loc_exb1;
    @DatabaseField
    private boolean loc_exb2;
    @DatabaseField
    private boolean loc_exb3;
    @DatabaseField
    private boolean loc_exb4;
    @DatabaseField
    private boolean loc_exb5;




    public LocLib() {
    }

    public LocLib(String loc_name, String loc_sort, String loc_desc, String loc_tag, String loc_content, String loc_date, String loc_icon) {
        this.loc_name = loc_name;
        this.loc_tag = loc_tag;
        this.loc_content = loc_content;
        this.loc_desc = loc_desc;
        this.loc_icon = loc_icon;
        this.loc_sort = loc_sort;
        this.loc_date = loc_date;
        this.loc_status = 0;
        this.loc_time = System.currentTimeMillis();
    }


    public String getLoc_sort() {
        return loc_sort;
    }

    public void setLoc_sort(String loc_sort) {
        this.loc_sort = loc_sort;
    }

    public String getLoc_date() {
        return loc_date;
    }

    public void setLoc_date(String loc_date) {
        this.loc_date = loc_date;
    }

    public int getLoc_id() {
        return loc_id;
    }

    public void setLoc_id(int loc_id) {
        this.loc_id = loc_id;
    }

    public String getLoc_name() {
        return loc_name;
    }

    public void setLoc_name(String loc_name) {
        this.loc_name = loc_name;
    }

    public String getLoc_tag() {
        return loc_tag;
    }

    public void setLoc_tag(String loc_tag) {
        this.loc_tag = loc_tag;
    }

    public String getLoc_content() {
        return loc_content;
    }

    public void setLoc_content(String loc_content) {
        this.loc_content = loc_content;
    }

    public int getLoc_status() {
        return loc_status;
    }

    public void setLoc_status(int loc_status) {
        this.loc_status = loc_status;
    }

    public Long getLoc_time() {
        return loc_time;
    }

    public void setLoc_time(Long loc_time) {
        this.loc_time = loc_time;
    }


    public String getLoc_desc() {
        return loc_desc;
    }

    public void setLoc_desc(String loc_desc) {
        this.loc_desc = loc_desc;
    }

    public String getLoc_icon() {
        return loc_icon;
    }

    public void setLoc_icon(String loc_icon) {
        this.loc_icon = loc_icon;
    }


    public int getLoc_exi1() {
        return loc_exi1;
    }

    public void setLoc_exi1(int loc_exi1) {
        this.loc_exi1 = loc_exi1;
    }

    public int getLoc_exi2() {
        return loc_exi2;
    }

    public void setLoc_exi2(int loc_exi2) {
        this.loc_exi2 = loc_exi2;
    }

    public int getLoc_exi3() {
        return loc_exi3;
    }

    public void setLoc_exi3(int loc_exi3) {
        this.loc_exi3 = loc_exi3;
    }

    public int getLoc_exi4() {
        return loc_exi4;
    }

    public void setLoc_exi4(int loc_exi4) {
        this.loc_exi4 = loc_exi4;
    }

    public int getLoc_exi5() {
        return loc_exi5;
    }

    public void setLoc_exi5(int loc_exi5) {
        this.loc_exi5 = loc_exi5;
    }

    public String getLoc_exs1() {
        return loc_exs1;
    }

    public void setLoc_exs1(String loc_exs1) {
        this.loc_exs1 = loc_exs1;
    }

    public String getLoc_exs2() {
        return loc_exs2;
    }

    public void setLoc_exs2(String loc_exs2) {
        this.loc_exs2 = loc_exs2;
    }

    public String getLoc_exs3() {
        return loc_exs3;
    }

    public void setLoc_exs3(String loc_exs3) {
        this.loc_exs3 = loc_exs3;
    }

    public String getLoc_exs4() {
        return loc_exs4;
    }

    public void setLoc_exs4(String loc_exs4) {
        this.loc_exs4 = loc_exs4;
    }

    public String getLoc_exs5() {
        return loc_exs5;
    }

    public void setLoc_exs5(String loc_exs5) {
        this.loc_exs5 = loc_exs5;
    }

    public boolean isLoc_exb1() {
        return loc_exb1;
    }

    public void setLoc_exb1(boolean loc_exb1) {
        this.loc_exb1 = loc_exb1;
    }

    public boolean isLoc_exb2() {
        return loc_exb2;
    }

    public void setLoc_exb2(boolean loc_exb2) {
        this.loc_exb2 = loc_exb2;
    }

    public boolean isLoc_exb3() {
        return loc_exb3;
    }

    public void setLoc_exb3(boolean loc_exb3) {
        this.loc_exb3 = loc_exb3;
    }

    public boolean isLoc_exb4() {
        return loc_exb4;
    }

    public void setLoc_exb4(boolean loc_exb4) {
        this.loc_exb4 = loc_exb4;
    }

    public boolean isLoc_exb5() {
        return loc_exb5;
    }

    public void setLoc_exb5(boolean loc_exb5) {
        this.loc_exb5 = loc_exb5;
    }
}
