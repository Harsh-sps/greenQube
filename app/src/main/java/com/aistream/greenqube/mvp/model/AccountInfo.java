package com.aistream.greenqube.mvp.model;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by PhuDepTraj on 5/9/2018.
 */

public class AccountInfo {
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("surname")
    @Expose
    private String surname;
    @SerializedName("headshot")
    @Expose
    private String headshot;
    @SerializedName("register_date")
    @Expose
    private String registerDate;
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("balance")
    @Expose
    private String balance;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("gender")
    @Expose
    private Integer gender;
    @SerializedName("nickname")
    @Expose
    private String nickname;
    @SerializedName("occupation")
    @Expose
    private String occupation;
    @SerializedName("age")
    @Expose
    private Integer age;
    @SerializedName("birthday")
    @Expose
    private String birthday;
    @SerializedName("plan_name")
    @Expose
    private String planName;
    @SerializedName("planid")
    @Expose
    private Integer planid;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("next_billing_date")
    @Expose
    private long next_billing_date;

    @SerializedName("max_downloads")
    @Expose
    private int maxDownloads;

    @SerializedName("available_downloads")
    @Expose
    private int availableDownloads;

    public String getUserName() {
        String userName = "";
        if (!TextUtils.isEmpty(name)) {
            userName += name + " ";
        }

        if (!TextUtils.isEmpty(surname)) {
            userName += surname;
        }
        return userName;
    }

    public AccountInfo(AccountLogin accountLogin) {
        this.phone = accountLogin.getPhoneNumber();
        this.email = "";
        this.surname = "";
        this.name = "";
        String userName = accountLogin.getUserName();
        if (!TextUtils.isEmpty(userName)) {
            String[] strs = userName.split(" ");
            this.name = strs[0];
            this.surname = strs[0];
            if (strs.length == 2) {
                this.surname = strs[1];
            }
        }

        this.gender = accountLogin.getGender();
        if (this.gender == 0) {
            this.gender = 1;
        }
        this.birthday = accountLogin.getBirthday();
        if (TextUtils.isEmpty(this.birthday)) {
            this.birthday = "1970-01-01";
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getHeadshot() {
        return headshot;
    }

    public void setHeadshot(String headshot) {
        this.headshot = headshot;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public Integer getPlanid() {
        return planid;
    }

    public void setPlanid(Integer planid) {
        this.planid = planid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getNext_billing_date() {
        return next_billing_date;
    }

    public void setNext_billing_date(long next_billing_date) {
        this.next_billing_date = next_billing_date;
    }

    public int getMaxDownloads() {
        return maxDownloads;
    }

    public void setMaxDownloads(int maxDownloads) {
        this.maxDownloads = maxDownloads;
    }

    public int getAvailableDownloads() {
        return availableDownloads;
    }

    public void setAvailableDownloads(int availableDownloads) {
        this.availableDownloads = availableDownloads;
    }
}
