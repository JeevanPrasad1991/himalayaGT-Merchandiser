package com.cpm.xmlGetterSetter;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yadavendras on 25-08-2016.
 */
public class WindowListGetterSetter implements Serializable{

    ArrayList<String> window_cd = new ArrayList<>();
    public ArrayList<SkuQwantityGetterSetter> getSkuQuantityList() {
        return skuQuantityList;
    }

    public void setSkuQuantityList(ArrayList<SkuQwantityGetterSetter> skuQuantityList) {
        this.skuQuantityList = skuQuantityList;
    }

    ArrayList<SkuQwantityGetterSetter> skuQuantityList = new ArrayList<>();

    public ArrayList<String> getExitOrNot() {
        return exitOrNot;
    }

    public void setExitOrNot(String exitOrNot) {
        this.exitOrNot.add(exitOrNot);
    }

    ArrayList<String>exitOrNot=new ArrayList<>();


    public int getKey_id() {
        return key_id;
    }

    public void setKey_id(int key_id) {
        this.key_id = key_id;
    }

    int key_id=0;
    String window;
    String brand_cd;
    String sku_hold;
    String planogram_image;
    String window_image;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;

    }
    String remark="";
    boolean islisted=true;
    public ArrayList<String> getWindow_cd() {
        return window_cd;
    }

    public void setWindow_cd(String window_cd) {
        this.window_cd.add(window_cd);
    }

    public String getWindow() {
        return window;
    }

    public void setWindow(String window) {
        this.window = window;
    }

    public boolean islisted() {
        return islisted;
    }

    public void setIslisted(boolean islisted) {
        this.islisted = islisted;
    }

    public String getSku_hold() {
        return sku_hold;
    }

    public void setSku_hold(String sku_hold) {
        this.sku_hold = sku_hold;
    }

    public String getBrand_cd() {
        return brand_cd;
    }

    public void setBrand_cd(String brand_cd) {
        this.brand_cd = brand_cd;
    }

    public String getPlanogram_image() {
        return planogram_image;
    }

    public void setPlanogram_image(String planogram_image) {
        this.planogram_image = planogram_image;
    }

    public String getWindow_image() {
        return window_image;
    }

    public void setWindow_image(String window_image) {
        this.window_image = window_image;
    }
}
