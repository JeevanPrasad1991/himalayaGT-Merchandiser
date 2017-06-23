package com.cpm.xmlGetterSetter;

import java.util.ArrayList;

/**
 * Created by jeevanp on 22-06-2017.
 */

public class AssetMasterNewGetterSetter {
    String  assetMasternewTable;

    public String getAssetMasternewTable() {
        return assetMasternewTable;
    }

    public void setAssetMasternewTable(String assetMasternewTable) {
        this.assetMasternewTable = assetMasternewTable;
    }

    public ArrayList<String> getAsset_cd() {
        return asset_cd;
    }

    public void setAsset_cd(String asset_cd) {
        this.asset_cd.add(asset_cd);
    }

    public ArrayList<String> getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset.add(asset);
    }

    ArrayList<String>asset_cd=new ArrayList<>();
    ArrayList<String>asset=new ArrayList<>();
}
