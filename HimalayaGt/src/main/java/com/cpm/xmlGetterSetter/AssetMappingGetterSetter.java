package com.cpm.xmlGetterSetter;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jeevanp on 21-06-2017.
 */

public class AssetMappingGetterSetter implements Serializable {
    String tableassetmapping;

    public String getAsset_image() {
        return asset_image;
    }

    public void setAsset_image(String asset_image) {
        this.asset_image = asset_image;
    }

    String asset_image;
    public boolean islisted() {
        return islisted;
    }

    public void setIslisted(boolean islisted) {
        this.islisted = islisted;
    }

    boolean islisted=true;
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    String remark="";

    public ArrayList<String> getCategorycd_mapping() {
        return categorycd_mapping;
    }

    public void setCategorycd_mapping(String categorycd_mapping) {
        this.categorycd_mapping.add(categorycd_mapping);
    }

    ArrayList<String>categorycd_mapping=new ArrayList<>();
    ArrayList<String>store_cd=new ArrayList<>();
    public ArrayList<String> getKey_id() {
        return key_id;
    }

    public void setKey_id(String key_id) {
        this.key_id.add(key_id);
    }

    ArrayList<String>key_id=new ArrayList<>();
    ArrayList<String>asset_cd=new ArrayList<>();

    public ArrayList<String> getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset.add(asset);
    }

    ArrayList<String>asset=new ArrayList<>();
    public String getTableassetmapping() {
        return tableassetmapping;
    }

    public void setTableassetmapping(String tableassetmapping) {
        this.tableassetmapping = tableassetmapping;
    }

    public ArrayList<String> getStore_cd() {
        return store_cd;
    }

    public void setStore_cd(String store_cd) {
        this.store_cd.add(store_cd);
    }

    public ArrayList<String> getAsset_cd() {
        return asset_cd;
    }

    public void setAsset_cd(String asset_cd) {
        this.asset_cd.add(asset_cd);
    }

    public ArrayList<String> getPlanogram() {
        return planogram;
    }

    public void setPlanogram(String planogram) {
        this.planogram.add(planogram);
    }

    ArrayList<String>planogram=new ArrayList<>();
    ArrayList<String>category=new ArrayList<>();
    ArrayList<String>category_id=new ArrayList<>();
    ArrayList<String>toglvale=new ArrayList<>();
    ArrayList<String>additional_image=new ArrayList<>();
    public ArrayList<String> getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category.add(category);
    }
    public ArrayList<String> getCategory_id() {
        return category_id;
    }
    public void setCategory_id(String category_id) {
        this.category_id.add(category_id);
    }
    public ArrayList<String> getToglvale() {
        return toglvale;
    }
    public void setToglvale(String toglvale) {
        this.toglvale.add(toglvale);
    }
    public ArrayList<String> getAdditional_image() {
        return additional_image;
    }
    public void setAdditional_image(String additional_image) {
        this.additional_image.add(additional_image);
    }
}
