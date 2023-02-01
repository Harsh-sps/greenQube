package com.aistream.greenqube.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DetectFMS {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("uuid")
    @Expose
    private String uuid;
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
    @SerializedName("password_5g")
    @Expose
    private String password5g;
    @SerializedName("hidden")
    @Expose
    private Integer hidden;
    @SerializedName("hidden_5g")
    @Expose
    private Integer hidden5g;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("latitude")
    @Expose
    private String latitude;
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
    @SerializedName("visibleonapps")
    @Expose
    private Integer visibleonapps;
    @SerializedName("groupid")
    @Expose
    private String groupid;
    @SerializedName("max_sessions")
    @Expose
    private Integer maxSessions;
    @SerializedName("max_dl_speed")
    @Expose
    private Integer maxDlSpeed;
    @SerializedName("ul_speed")
    @Expose
    private String ulSpeed;
    @SerializedName("dl_speed")
    @Expose
    private String dlSpeed;
    @SerializedName("sessions")
    @Expose
    private Integer sessions;
    @SerializedName("local_ip")
    @Expose
    private String localIp;
    @SerializedName("public_ip")
    @Expose
    private String publicIp;

    /**
     * No args constructor for use in serialization
     *
     */
    public DetectFMS() {
    }

    /**
     *
     * @param region
     * @param hwBrand
     * @param address1
     * @param mac
     * @param address2
     * @param password
     * @param ssid5g
     * @param city
     * @param maxDlSpeed
     * @param id
     * @param hwModel
     * @param postalCode
     * @param dlSpeed
     * @param name
     * @param ssid
     * @param longitude
     * @param mac5g
     * @param groupid
     * @param localIp
     * @param icon
     * @param sessions
     * @param hidden5g
     * @param password5g
     * @param status
     * @param hotspotId
     * @param maxSessions
     * @param publicIp
     * @param visibleonapps
     * @param country
     * @param hidden
     * @param ulSpeed
     * @param latitude
     * @param uuid
     * @param notes
     */
    public DetectFMS(Integer id, String uuid, String mac, String mac5g, String ssid, String ssid5g, String password, String password5g, Integer hidden, Integer hidden5g, String longitude, String latitude, String region, String city, String postalCode, String hotspotId, String country, String name, String hwBrand, String hwModel, String address1, String address2, String notes, String icon, Integer status, Integer visibleonapps, String groupid, Integer maxSessions, Integer maxDlSpeed, String ulSpeed, String dlSpeed, Integer sessions, String localIp, String publicIp) {
        super();
        this.id = id;
        this.uuid = uuid;
        this.mac = mac;
        this.mac5g = mac5g;
        this.ssid = ssid;
        this.ssid5g = ssid5g;
        this.password = password;
        this.password5g = password5g;
        this.hidden = hidden;
        this.hidden5g = hidden5g;
        this.longitude = longitude;
        this.latitude = latitude;
        this.region = region;
        this.city = city;
        this.postalCode = postalCode;
        this.hotspotId = hotspotId;
        this.country = country;
        this.name = name;
        this.hwBrand = hwBrand;
        this.hwModel = hwModel;
        this.address1 = address1;
        this.address2 = address2;
        this.notes = notes;
        this.icon = icon;
        this.status = status;
        this.visibleonapps = visibleonapps;
        this.groupid = groupid;
        this.maxSessions = maxSessions;
        this.maxDlSpeed = maxDlSpeed;
        this.ulSpeed = ulSpeed;
        this.dlSpeed = dlSpeed;
        this.sessions = sessions;
        this.localIp = localIp;
        this.publicIp = publicIp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getPassword5g() {
        return password5g;
    }

    public void setPassword5g(String password5g) {
        this.password5g = password5g;
    }

    public Integer getHidden() {
        return hidden;
    }

    public void setHidden(Integer hidden) {
        this.hidden = hidden;
    }

    public Integer getHidden5g() {
        return hidden5g;
    }

    public void setHidden5g(Integer hidden5g) {
        this.hidden5g = hidden5g;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
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

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public Integer getMaxSessions() {
        return maxSessions;
    }

    public void setMaxSessions(Integer maxSessions) {
        this.maxSessions = maxSessions;
    }

    public Integer getMaxDlSpeed() {
        return maxDlSpeed;
    }

    public void setMaxDlSpeed(Integer maxDlSpeed) {
        this.maxDlSpeed = maxDlSpeed;
    }

    public String getUlSpeed() {
        return ulSpeed;
    }

    public void setUlSpeed(String ulSpeed) {
        this.ulSpeed = ulSpeed;
    }

    public String getDlSpeed() {
        return dlSpeed;
    }

    public void setDlSpeed(String dlSpeed) {
        this.dlSpeed = dlSpeed;
    }

    public Integer getSessions() {
        return sessions;
    }

    public void setSessions(Integer sessions) {
        this.sessions = sessions;
    }

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public String getPublicIp() {
        return publicIp;
    }

    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

}
