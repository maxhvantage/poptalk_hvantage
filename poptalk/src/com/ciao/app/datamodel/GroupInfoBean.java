package com.ciao.app.datamodel;

import java.util.List;
/**
 * Created by rajat on 14/4/15.
 * This class used to hold the info about a particular group .
 */
public class GroupInfoBean {
	String groupOwnerID,groupJID,groupName,groupImage,groupCreationTime;
	List<MemberBean> memberList;
	List<String> membersJIDList;
	public List<String> getMembersJIDList() {
		return membersJIDList;
	}
	public void setMembersJIDList(List<String> membersJIDList) {
		this.membersJIDList = membersJIDList;
	}
	public String getGroupOwnerID() {
		return groupOwnerID;
	}
	public void setGroupOwnerID(String groupOwnerID) {
		this.groupOwnerID = groupOwnerID;
	}
	public String getGroupJID() {
		return groupJID;
	}
	public void setGroupJID(String groupJID) {
		this.groupJID = groupJID;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getGroupImage() {
		return groupImage;
	}
	public void setGroupImage(String groupImage) {
		this.groupImage = groupImage;
	}
	public String getGroupCreationTime() {
		return groupCreationTime;
	}
	public void setGroupCreationTime(String groupCreationTime) {
		this.groupCreationTime = groupCreationTime;
	}
	public List<MemberBean> getMemberList() {
		return memberList;
	}
	public void setMemberList(List<MemberBean> memberList) {
		this.memberList = memberList;
	}
}
