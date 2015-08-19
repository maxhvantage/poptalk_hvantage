package com.ciao.app.datamodel;



/**
 * Created by vinod on 16/4/15.
 */
public class NumbersListBean extends BaseResponseBean {
    String number;


    public String getNumber() {
        return number;
    }

    public NumbersListBean(String number) {
        this.number = number;


    }
    public void setNumber(String number) {
        this.number = number;
    }


}