package com.ciao.app.datamodel;

/**
 * Created by rajat on 11/2/15.
 */
public class PhoneNumberBean {
    String id,phoneNumber,type,isCiaoUser;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsCiaoUser() {
        return isCiaoUser;
    }

    public void setIsCiaoUser(String isCiaoUser) {
        this.isCiaoUser = isCiaoUser;
    }
}
