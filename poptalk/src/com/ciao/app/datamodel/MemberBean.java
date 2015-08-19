package com.ciao.app.datamodel;

/**
 * Created by rajat on 14/4/15.
 * This class used to hold the info about a particular group member.
 */
public class MemberBean extends BaseResponseBean{
    String name;
    String status;
    int image;
    boolean isAdmin;
    String profilePic;
    String memberJID;
    
  
	public String getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public MemberBean(){}

    public MemberBean(String name, String status, int image) {
        this.name = name;
        this.status = status;
        this.image = image;
    }
    public MemberBean(String name, String status) {
    	this.name = name;
    	this.status = status;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
    	this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
    	this.status = status;
    }

    public String getMemberJID() {
		return memberJID;
	}

	public void setMemberJID(String memberJID) {
		this.memberJID = memberJID;
	}

	public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
