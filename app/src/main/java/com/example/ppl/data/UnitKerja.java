package com.example.ppl.data;

public class UnitKerja {
    private String code;
    private String name;

    // Constructor
    public UnitKerja(String code, String name) {
        this.code = code;
        this.name = name;
    }

    // Getters and Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "OrganizationUnit{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

