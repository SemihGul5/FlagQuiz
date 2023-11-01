package com.example.flagquiz;

public class Country {
    private String countryName;
    private int flag;

    public Country(String countryName, int flag) {
        this.countryName = countryName;
        this.flag = flag;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
