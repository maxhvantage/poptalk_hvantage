package com.ciao.app.datamodel;

/**
 * Created by rajat on 14/4/15.
 * This class used to hold the info media file for group chats.
 */
public class MediaBean
{
	String mediaType;
	String MediaFilePath;
	
	
	public String getMediaType() {
		return mediaType;
	}
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}
	public String getMediaFilePath() {
		return MediaFilePath;
	}
	public void setMediaFilePath(String mediaFilePath) {
		MediaFilePath = mediaFilePath;
	}
	
}
