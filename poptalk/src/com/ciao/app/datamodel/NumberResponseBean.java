package com.ciao.app.datamodel;

import java.util.ArrayList;
import java.util.List;

public class NumberResponseBean extends BaseResponseBean {


	private List<String> beanlist= new ArrayList<String>();

	public List<String> getBeanlist() {
		return beanlist;
	}

	public void setBeanlist(List<String> beanlist) {
		this.beanlist = beanlist;
	}
}
