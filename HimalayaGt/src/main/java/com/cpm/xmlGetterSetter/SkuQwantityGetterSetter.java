package com.cpm.xmlGetterSetter;

import java.util.ArrayList;

/**
 * Created by jeevanp on 20-06-2017.
 */

public class SkuQwantityGetterSetter {
    ArrayList<String>sku=new ArrayList<>();

    public ArrayList<String> getBrand_cd() {
        return brand_cd;
    }

    public void setBrand_cd(String brand_cd) {
        this.brand_cd.add(brand_cd);
    }

    ArrayList<String>brand_cd=new ArrayList<>();

    public String getQwantity() {
        return qwantity;
    }

    public void setQwantity(String qwantity) {
        this.qwantity = qwantity;
    }

    String qwantity ;
    public ArrayList<String> getCommon_id() {
        return Common_id;
    }

    public void setCommon_id(String common_id) {
        Common_id.add(common_id);
    }

    ArrayList<String>Common_id=new ArrayList<>();
    public ArrayList<String> getSku() {
        return sku;
    }
    public void setSku(String sku) {
        this.sku.add(sku);
    }

    public ArrayList<String> getSku_cd() {
        return sku_cd;
    }

    public void setSku_cd(String sku_cd) {
        this.sku_cd.add(sku_cd);
    }
    ArrayList<String>sku_cd=new ArrayList<>();

}
