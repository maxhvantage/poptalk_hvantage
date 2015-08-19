package com.ciao.app.datamodel;

/**
 * Created by vinod on 15/4/15.
 * Updated by Simon 6/27/15
 */
public class CountryListBean extends BaseResponseBean{
    int countryFlag;
    String countryCode;
    String countryName;
    String countryPrefix;

    public CountryListBean()
    {
    }
    public CountryListBean(String countryName,int countryFlag) {
        this.countryFlag = countryFlag;
        this.countryName = countryName;
    }

    public CountryListBean( String countryName, String countryCode,int countryFlag) {
        this.countryFlag = countryFlag;
        this.countryCode = countryCode;
        this.countryName = countryName;
    }

    public CountryListBean(String countryName, String countryCode, String countryPrefix, int countryFlag) {
        this.countryFlag = countryFlag;
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.countryPrefix = countryPrefix;
    }


    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }



    public int getCountryFlag() {
        return countryFlag;
    }

    public void setCountryFlag(int countryFlag) {
        this.countryFlag = countryFlag;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryPrefix() {
        return countryPrefix;
    }

    public void setCountryPrefix(String countryPrefix) {
        this.countryPrefix = countryPrefix;
    }
}
