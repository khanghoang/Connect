package com.shonnect.shonnect.model;

import java.io.Serializable;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/4/15.
 */
public class Shop implements Serializable {
    private String id;
    private String shopName;
    private String bannerImage;
    private String url;

    public Shop(String id, String shopName, String bannerImage, String url) {
        this.id = id;
        this.shopName = shopName;
        this.bannerImage = bannerImage;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
