package com.ciao.app.datamodel;

import java.util.List;

/**
 * Created by rajat on 11/2/15.
 */
public class CiaoContactBean {
    String id;
    String name;
    String imagePath;
    String isCiaoUser;
    String commUser;
    boolean isSelected;
    String isFavContact;
    List<PhoneNumberBean> userPhoneList;
    List<EmailBean> userEmailBeanList;

    public CiaoContactBean(){}

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getIsFavContact() {
        return isFavContact;
    }

    public void setIsFavContact(String isFavContact) {
        this.isFavContact = isFavContact;
    }


    public String getCommUser() {
        return commUser;
    }

    public void setCommUser(String commUser) {
        this.commUser = commUser;
    }

    public List<PhoneNumberBean> getUserPhoneList() {
        return userPhoneList;
    }

    public void setUserPhoneList(List<PhoneNumberBean> userPhoneList) {
        this.userPhoneList = userPhoneList;
    }

    public List<EmailBean> getUserEmailBeanList() {
        return userEmailBeanList;
    }

    public void setUserEmailBeanList(List<EmailBean> userEmailBeanList) {
        this.userEmailBeanList = userEmailBeanList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getIsCiaoUser() {
        return isCiaoUser;
    }

    public void setIsCiaoUser(String isCiaoUser) {
        this.isCiaoUser = isCiaoUser;
    }
}
