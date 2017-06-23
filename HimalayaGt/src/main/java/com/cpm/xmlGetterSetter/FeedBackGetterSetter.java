package com.cpm.xmlGetterSetter;

/**
 * Created by jeevanp on 03-04-2017.
 */

public class FeedBackGetterSetter {
    public String getFeedBack() {
        return feedBack;
    }

    public void setFeedBack(String feedBack) {
        this.feedBack = feedBack;
    }

    String feedBack="";
    String store_cd;

    public String getVisit_date() {
        return visit_date;
    }

    public void setVisit_date(String visit_date) {
        this.visit_date = visit_date;
    }

    public String getStore_cd() {
        return store_cd;
    }

    public void setStore_cd(String store_cd) {
        this.store_cd = store_cd;
    }

    String visit_date;

}
