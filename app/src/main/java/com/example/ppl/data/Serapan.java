package com.example.ppl.data;

import com.google.gson.annotations.SerializedName;

public class Serapan extends SerapanResponse {
    @SerializedName("bulan")
    private int bulan;
    @SerializedName("nilai")
    private String nilai;

    public Serapan(int bulan, String nilai) {
        this.bulan = bulan;
        this.nilai = nilai;
    }

    public int getBulan() {
        return bulan;
    }

    public void setBulan(int bulan) {
        this.bulan = bulan;
    }

    public String getNilai() {
        return nilai;
    }

    public void setNilai(String nilai) {
        this.nilai = nilai;
    }
}
