package com.example.ppl.data;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("account")
    private String account;

    @SerializedName("group")
    private int group;

    @SerializedName("nama")
    private String nama;

    @SerializedName("unitkerja")
    private String unitkerja;

    // Constructor
    public User(String account, int group, String nama, String unitkerja) {
        this.account = account;
        this.group = group;
        this.nama = nama;
        this.unitkerja = unitkerja;
    }

    // Getter dan Setter
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getUnitkerja() {
        return unitkerja;
    }

    public void setUnitkerja(String unitkerja) {
        this.unitkerja = unitkerja;
    }

    // Menampilkan informasi user
    @Override
    public String toString() {
        return "User{" +
                "account='" + account + '\'' +
                ", group=" + group +
                ", nama='" + nama + '\'' +
                ", unitkerja='" + unitkerja + '\'' +
                '}';
    }
}