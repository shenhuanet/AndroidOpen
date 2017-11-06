package com.shenhua.citypicker.model;

import java.io.Serializable;

/**
 * author shenhua on 2016/4/11.
 */
public class City implements Serializable{
    private static final long serialVersionUID = -1547150453814884047L;
    private String name;
    private String pinyin;


    public City(String name, String pinyin) {
        this.name = name;
        this.pinyin = pinyin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }
}
