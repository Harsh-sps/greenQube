package com.aistream.greenqube.mvp.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class WifiInfo {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("routerid")
    @Expose
    private String routerid;
    @SerializedName("mac")
    @Expose
    private String mac;
    @SerializedName("mac_5g")
    @Expose
    private String mac5g;
    @SerializedName("ssid")
    @Expose
    private String ssid;
    @SerializedName("ssid5g")
    @Expose
    private String ssid5g;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("region")
    @Expose
    private String region;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("postal_code")
    @Expose
    private String postalCode;
    @SerializedName("hotspot_id")
    @Expose
    private String hotspotId;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("hw_brand")
    @Expose
    private String hwBrand;
    @SerializedName("hw_model")
    @Expose
    private String hwModel;
    @SerializedName("address1")
    @Expose
    private String address1;
    @SerializedName("address2")
    @Expose
    private String address2;
    @SerializedName("notes")
    @Expose
    private String notes;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("status")
    @Expose
    private Integer status;

    @SerializedName("location")
    @Expose
    private String location;

    private Integer visibleonapps;

    private Double distance;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRouterid() {
        return routerid;
    }

    public void setRouterid(String routerid) {
        this.routerid = routerid;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getMac5g() {
        return mac5g;
    }

    public void setMac5g(String mac5g) {
        this.mac5g = mac5g;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getSsid5g() {
        return ssid5g;
    }

    public void setSsid5g(String ssid5g) {
        this.ssid5g = ssid5g;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getHotspotId() {
        return hotspotId;
    }

    public void setHotspotId(String hotspotId) {
        this.hotspotId = hotspotId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHwBrand() {
        return hwBrand;
    }

    public void setHwBrand(String hwBrand) {
        this.hwBrand = hwBrand;
    }

    public String getHwModel() {
        return hwModel;
    }

    public void setHwModel(String hwModel) {
        this.hwModel = hwModel;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getVisibleonapps() {
        return visibleonapps;
    }

    public void setVisibleonapps(Integer visibleonapps) {
        this.visibleonapps = visibleonapps;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}


