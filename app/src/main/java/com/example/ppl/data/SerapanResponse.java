package com.example.ppl.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SerapanResponse{
    @SerializedName("paguNilai")
    private String paguNilai;
    @SerializedName("serapan")
    private List<Serapan> serapan;
    @SerializedName("sumberNama")
    private String sumberNama; // Hapus `static`

    public String getSumberNama() {
        return sumberNama;
    }

    public void setSumberNama(String sumberNama) {
        this.sumberNama = sumberNama;
    }

    public String getPaguNilai() {
        return paguNilai;
    }

    public void setPaguNilai(String paguNilai) {
        this.paguNilai = paguNilai;
    }

    public List<Serapan> getSerapan() {
        return serapan;
    }

    public void setSerapan(List<Serapan> serapan) {
        this.serapan = serapan;
    }

}
