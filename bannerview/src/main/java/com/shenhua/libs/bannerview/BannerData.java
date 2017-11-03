package com.shenhua.libs.bannerview;

import java.io.Serializable;
import java.util.List;

/**
 * Created by shenhua on 3/28/2017.
 * Email shenhuanet@126.com
 */
public class BannerData implements Serializable {

    private static final long serialVersionUID = 5623655139616890383L;
    private String[] aTitle;
    private List<String> bTitle;
    private String[] aImage;
    private List<String> bImage;
    private String[] aHref;
    private List<String> bHref;
    private int[] a;
    private List<Integer> b;

    public int[] getA() {
        return a;
    }

    public void setA(int[] a) {
        this.a = a;
    }

    public String[] getaHref() {
        return aHref;
    }

    public void setaHref(String[] aHref) {
        this.aHref = aHref;
    }

    public String[] getaImage() {
        return aImage;
    }

    public void setaImage(String[] aImage) {
        this.aImage = aImage;
    }

    public String[] getaTitle() {
        return aTitle;
    }

    public void setaTitle(String[] aTitle) {
        this.aTitle = aTitle;
    }

    public List<Integer> getB() {
        return b;
    }

    public void setB(List<Integer> b) {
        this.b = b;
    }

    public List<String> getbHref() {
        return bHref;
    }

    public void setbHref(List<String> bHref) {
        this.bHref = bHref;
    }

    public List<String> getbImage() {
        return bImage;
    }

    public void setbImage(List<String> bImage) {
        this.bImage = bImage;
    }

    public List<String> getbTitle() {
        return bTitle;
    }

    public void setbTitle(List<String> bTitle) {
        this.bTitle = bTitle;
    }
}
