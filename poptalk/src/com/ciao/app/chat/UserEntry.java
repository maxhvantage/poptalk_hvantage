package com.ciao.app.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.jivesoftware.smackx.packet.VCard;
/*
 * This class is used to store the Chat user info
 */
public class UserEntry {
	private String name = "";
	private int presence = 3;
	private String status,dbId,uId, image;

	private String userName = "unkown";
	private String by="";
	private VCard vCard;
	
	
	public VCard getvCard() {
		return vCard;
	}

	public void setvCard(VCard vCard) {
		this.vCard = vCard;
	}

	private HashMap< String, String> hashMap = new HashMap<String, String>();
	public ArrayList<UserPresences> resources = new ArrayList<UserPresences>();
	
	
	
	public ArrayList getResources() {
		

		 Set keys = hashMap.keySet();
	        Iterator itr = keys.iterator();
	 
	        String key;
	        String value;
	        while(itr.hasNext())
	        {
	            key = (String)itr.next();
	            value = (String)hashMap.get(key);
	        }
		
		
		return resources;
	}

	public void setResources(String resource, int presence) {
//		this.resources = resources;
		hashMap.put(resource, ""+presence);
		
		
		
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDbId() {
		return dbId;
	}

	public void setDbId(String dbId) {
		this.dbId = dbId;
	}
	
	public String getUId() {
		return uId;
	}

	public void setUId(String uId) {
		this.uId = uId;
	}
	public void setUserImage(String image) {
		this.image = image;
	}
	public String getUserImage() {
		return image;
	}
	public int getPresence() {
		return presence;
	}

	public void setPresence(int presence) {
		this.presence = presence;
	}

	public void setStatus(String string) {

		status=string;
	}

	public String getStatus() {
		
		
		
		return status;

	}

	public void setUserName(String replace) {
		 
		userName =replace;
	}
	
public String getUserName() {
	return userName;
		 
		
	}

public String getBy() {
	 
	return by;
}
public void setBy(String by) {
	this.by=by;
	
}

 class UserPresences{
	 public String resource = "";
		public int presence = 3;
}
 
/* DownloadInfo info;


public DownloadInfo getInfo() {
	return info;
}

public void setInfo(DownloadInfo info) {
	this.info = info;
}*/

}
